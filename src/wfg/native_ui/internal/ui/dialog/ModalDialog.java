package wfg.native_ui.internal.ui.dialog;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

import static wfg.native_ui.util.UIConstants.screenH;
import static wfg.native_ui.util.UIConstants.screenW;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.FaderUtil;
import com.fs.starfarer.codex2.CodexDialog;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.Attachments;
import wfg.native_ui.ui.functional.RunnableWithCode;
import wfg.native_ui.util.RenderUtils;

public class ModalDialog extends UIContainer {
    public RunnableWithCode delegate;
    public int optionOnKeyboardCancel = 1;
    public int optionOnKeyboardConfirm = 0;
    public int dismissOption = -1;
    public float backgroundDimAmount = 0.66f;
    public boolean RMBAnywhereCancels = false;
    public boolean LMBOutsideCancels = false;
    public boolean RMBOutsideCancels = true;
    public boolean interceptAllContainedEvents = true;
    public boolean fadeInAndOut = true;
    public boolean keyboardShortcutsToAcceptEnabled = true;

    protected boolean useCustomCenter = false;
    protected boolean suspendEventInterception = false;
    protected float centerX, centerY;

    protected final UIComponentAPI inputInterceptor;
    protected final Set<Integer> optionSet = new HashSet<>();
    protected final FaderUtil fader = new FaderUtil(0f, 0.5f, 0.2f);
    protected final UIPanelAPI interceptorParent;

    public ModalDialog() {
        this(Attachments.getScreenPanel(), 500, 200, null);
    }

    public ModalDialog(float width, float height) {
        this(Attachments.getScreenPanel(), width, width, null);
    }

    public ModalDialog(float width, float height, RunnableWithCode dialogDismissed) {
        this(Attachments.getScreenPanel(), width, width, dialogDismissed);
    }

    public ModalDialog(UIPanelAPI interceptorParent, float width, float height, RunnableWithCode dialogDismissed) {
        super(width, height);
        this.interceptorParent = interceptorParent;

        delegate = dialogDismissed;
        inputInterceptor = new ModalInterceptor(this);
    }

    public UIComponentAPI getInterceptor() { return inputInterceptor; }

    public void setCenter(float cx, float cy) {
        centerX = cx;
        centerY = cy;
        useCustomCenter = true;
    }

    public void show(float durIn, float durOut) {
        fader.setDuration(durIn, durOut);
        interceptorParent.removeComponent(inputInterceptor);
        interceptorParent.addComponent(inputInterceptor);

        final PositionAPI pos = interceptorParent.getPosition();
        inputInterceptor.getPosition().setSize(pos.getWidth(), pos.getHeight()).inMid();
        if (useCustomCenter) {
            mPos.inBL(
                centerX - pos.getX() - pos.getWidth() / 2f,
                centerY - pos.getY() - pos.getHeight() / 2f);
        } else mPos.inMid();

        if (fadeInAndOut) fader.fadeIn();
        else fader.forceIn();

        dismissOption = -1;
    }

    public void resetOption() { dismissOption = -1;}

    public void dismiss(int option) {
        dismissOption = option;
        if (fadeInAndOut) {
            if (optionSet.contains(option)) fader.forceOut();
            else fader.fadeOut();
        }

        if (delegate != null) delegate.run(option);
    }

    public void makeOptionInstant(int option) {
        optionSet.add(option);
    }

    public final float getFaderBrightness() {
        return fader.getBrightness();
    }

    protected boolean isFullyShown() {
        return fader.getBrightness() == 1f;
    }

    protected boolean isBeingDismissed() {
        return dismissOption >= 0;
    }

    public void setSuspendEventInterception(boolean bool) {
        if (bool && !suspendEventInterception) {
            interceptorParent.removeComponent(inputInterceptor);
        } else if (!bool && suspendEventInterception) {
            interceptorParent.removeComponent(inputInterceptor);
            interceptorParent.addComponent(inputInterceptor);
            final PositionAPI pos = interceptorParent.getPosition();
            inputInterceptor.getPosition().setSize(pos.getWidth(), pos.getHeight()).inMid();
        }

        suspendEventInterception = bool;
    }

    @Override
    public void processInputImpl(List<InputEventAPI> events) {
        if (suspendEventInterception || isBeingDismissed()) return;

        for (InputEventAPI e : events) {
            if (e.isConsumed()) continue;

            final boolean inside = mPos.containsEvent(e);

            if (e.isLMBDownEvent() && LMBOutsideCancels && !inside) {
                dismiss(optionOnKeyboardCancel);
                e.consume();
                return;
            }

            if (optionOnKeyboardCancel != -1 &&
                ((e.isRMBDownEvent() && RMBOutsideCancels && (!inside || RMBAnywhereCancels))
                    || (e.isKeyDownEvent() && e.getEventValue() == Keyboard.KEY_ESCAPE
                ))
            ) {
                dismiss(optionOnKeyboardCancel);
                e.consume();
                return;
            }

            if (e.isKeyDownEvent()
                && keyboardShortcutsToAcceptEnabled
                && optionOnKeyboardConfirm != -1
                && (e.getEventValue() == Keyboard.KEY_RETURN
                    || e.getEventValue() == Keyboard.KEY_SPACE
            )) {
                dismiss(optionOnKeyboardConfirm);
                e.consume();
                return;
            }

            if (e.isKeyboardEvent() && e.getEventValue() == Keyboard.KEY_F2) {
                CodexDialog.show();
                e.consume();
                continue;
            }

            if (e.isKeyboardEvent() || (inside && interceptAllContainedEvents)) {
                e.consume();
            }
        }
    }

    @Override
    public void advanceImpl(float delta) {
        fader.advance(delta);
    }

    @Override
    public void renderImpl(float alpha) {
        RenderUtils.drawQuad(
            0f, 0f, screenW, screenH, Color.BLACK,
            alpha * backgroundDimAmount * getFaderBrightness(),
            false
        );
    }

    public void outsideClickAbsorbed(InputEventAPI event) {};
}