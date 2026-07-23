package wfg.native_ui.ui.widget;

import static wfg.native_ui.util.Globals.settings;
import static wfg.native_ui.util.UIConstants.*;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.graphics.util.Fader;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TextFieldAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI.ActionListenerDelegate;
import com.fs.starfarer.api.util.FaderUtil;
import com.fs.starfarer.api.util.Misc;

import rolflectionlib.util.RolfLectionUtil;
import wfg.native_ui.example.widget.TextFieldExample;
import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.MethodFields;
import wfg.native_ui.util.NativeUiUtils;
import wfg.native_ui.util.RenderUtils;

// TODO release TextField after new update.
/**
 * Not complete yet!!!
 * <p><strong>Example: </strong> {@link TextFieldExample}</p>
 */
public class TextField extends UIContainer implements TextFieldAPI {
    private static final char NULL_CHAR = '\u0000';
    private static final String TYPER_BUZZER_SOUND_ID = "ui_typer_buzz";
    private static final String TYPER_TYPE_SOUND_ID = "ui_typer_type";
    private static final Color DISABLED_COLOR = new Color(100, 100, 100, 255);

    private static final Class<?> labelClass = settings.createLabel("", null).getClass();
    private static final Object isBlinkingMethod = RolfLectionUtil.getMethodDeclared("isBlinking", labelClass, 0);
    private static final Object stopBlinkingMethod = RolfLectionUtil.getMethodDeclared("stopBlinking", labelClass, 0);
    private static final Object getFlasherMethod = RolfLectionUtil.getMethodDeclared("getFlasher", labelClass, 0);
    private static final Object blinkMethod = RolfLectionUtil.getMethodDeclared("blink", labelClass, 2);

    private final ActionListenerDelegate actionListener;

    private LabelAPI textLabel;
    private LabelAPI cursorLabel;
    private LabelAPI blankTextLabel;
    private LabelAPI descLabel;
    private Color bgColor = NativeUiUtils.setAlpha(btnBgColorDark, 150);
    private Color borderColor = bgColor;
    private String lastTextBeforeFocus = null;
    private TextFieldAPI nextTextField;
    private TextFieldListener fieldListener = null;
    private int maxCharacters = -1;
    private float leftPad = 2f;
    
    public boolean goToNextOnEnter = false;
    public boolean callTabPressedOnEnter = false;
    public boolean limitByStringWidth = true;
    public boolean undoOnEscape = true;
    public boolean handleCtrlV = true;
    public boolean isEnabled = true;
    public boolean minimalMode = false;

    public TextField(float w, float h, String text, String font, ActionListenerDelegate listener) {
        this(w, h, text, font, false, listener);
    }

    public TextField(float w, float h, String text, String font, boolean useSmallInsignia, ActionListenerDelegate listener) {
        super(w, h);

        actionListener = listener;
        textLabel = settings.createLabel(text, useSmallInsignia ? Fonts.INSIGNIA_LARGE : font);
        textLabel.setAlignment(Alignment.LMID);
        textLabel.autoSizeToWidth(w);
        add(textLabel).inLMid(leftPad * 2f);
        cursorLabel = settings.createLabel("_", font);
        cursorLabel.autoSizeToWidth(w);
        cursorLabel.setAlignment(Alignment.MID);
        final Fader cursorFader = MethodFields.getPanelFader((UIComponentAPI) cursorLabel);
        cursorFader.setDuration(0.05f, 0.25f);
        cursorFader.forceOut();
        setColor(btnTxtColor);
        add(cursorLabel).rightOfBottom((UIComponentAPI) textLabel, 0f);
    }

    public void setHint(String hint) {
        blankTextLabel = settings.createLabel(hint, Fonts.DEFAULT_SMALL);
        blankTextLabel.setAlignment(Alignment.MID);
        blankTextLabel.setColor(gray);
        blankTextLabel.autoSizeToWidth(getWidth());
        add(blankTextLabel).inMid();
    }

    public void setDesc(String desc) {
        descLabel = settings.createLabel(desc, Fonts.VICTOR_10);
        descLabel.setColor(btnTxtColor);
        descLabel.autoSizeToWidth(getWidth());
        add(descLabel).inTR(6f, -6f);
        setPad(4f);
    }

    public boolean isMinimalMode() {
        return minimalMode;
    }

    public void setMinimalMode(boolean bl) {
        minimalMode = bl;
    }

    public void setPad(final float pad) {
        leftPad = pad;
        remove(textLabel);
        add(textLabel).inLMid(pad * 2f);
    }

    public LabelAPI getTextLabel() {
        return textLabel;
    }

    public LabelAPI getTextLabelAPI() {
        return textLabel;
    }

    public TextFieldListener getFieldListener() {
        return fieldListener;
    }

    public void setFieldListener(TextFieldListener listener) {
        fieldListener = listener;
    }

    public void setMidAlignment() {
        remove(textLabel);
        add(textLabel).inMid();
    }

    public void setColor(Color color) {
        textLabel.setColor(color);
        cursorLabel.setColor(color);
    }

    public void setBgColor(Color color) {
        bgColor = color;
    }

    public TextFieldAPI getNext() {
        return nextTextField;
    }

    public void setNext(TextFieldAPI b2) {
        nextTextField = b2;
    }

    public final String getText() {
        return textLabel.getText();
    }

    public void setText(String text) {
        final String prevText = getText();
        textLabel.setText(text);
        textLabel.autoSizeToWidth(getWidth());
        MethodFields.recomputePos(mPos);

        if (fieldListener != null && !prevText.equals(getText())) {
            fieldListener.textChanged(this, prevText);
        }
    }

    public boolean isValidChar(char input) {
        if (input == NULL_CHAR) {
            return false;
        }
        if (input == '%') {
            return false;
        }
        if (input == '$') {
            return false;
        }
        // oOOO[] oOOOArray = textLabel.getRenderer().return().\u00d600000();
        // boolean bl = oOOOArray.length > input && oOOOArray[input] != null;
        // return bl;
        return true; // TODO handle
    }

    public final boolean appendCharIfPossible(char c2) {
        return appendCharIfPossible(c2, true);
    }

    public final boolean appendCharIfPossible(char character, boolean playSound) {
        final String newText = String.valueOf(textLabel.getText()) + character;
        final boolean isValid = isValidChar(character);
        final float newWidth = textLabel.computeTextWidth(newText);
        final float cursorWidth = textLabel.computeTextWidth("_");
        final boolean exceedsCharLimit = newText.length() > maxCharacters && maxCharacters >= 0;

        if (isValid && (newWidth <= getWidth() - leftPad * 2f - cursorWidth - leftPad || !limitByStringWidth) && !exceedsCharLimit) {
            setText(newText);
            if (playSound) {
                Global.getSoundPlayer().playUISound(TYPER_TYPE_SOUND_ID, 1f, 1f);
            }
            if (fieldListener != null) {
                fieldListener.charTyped(this, character);
            }
            return true;

        } else {
            if (playSound) {
                Global.getSoundPlayer().playUISound(TYPER_BUZZER_SOUND_ID, 1f, 1f);
            }
            if (fieldListener != null) {
                fieldListener.charTypeFailed(this, character);
            }
            return false;
        }
    }

    public final int getMaxChars() {
        return maxCharacters;
    }

    public final void setMaxChars(int count) {
        maxCharacters = count;
    }

    private final void deleteLastChar() {
        final String string = textLabel.getText();
        if (string.length() <= 0) {
            Global.getSoundPlayer().playUISound(TYPER_BUZZER_SOUND_ID, 1f, 1f);
            return;
        }
        Global.getSoundPlayer().playUISound(TYPER_TYPE_SOUND_ID, 1f, 1f);
        setText(string.substring(0, string.length() - 1));
    }

    public final void deleteAll() {
        deleteAll(true);
    }

    public final void deleteAll(boolean playSound) {
        final boolean hasText = textLabel.getText().length() > 0;
        if (playSound) Global.getSoundPlayer().playUISound(hasText ? TYPER_TYPE_SOUND_ID : TYPER_BUZZER_SOUND_ID, 1f, 1f);
        if (hasText) setText("");
    }

    public void deleteLastWord() {
        final String string = textLabel.getText();
        if (string.length() == 0) {
            Global.getSoundPlayer().playUISound(TYPER_BUZZER_SOUND_ID, 1f, 1f);
        }

        int index = string.lastIndexOf(" ");
        if (index == string.length() - 1 && index > 0) {
            index = string.substring(0, index).lastIndexOf(" ");
        }
        if (index == -1) {
            deleteAll();
        } else {
            Global.getSoundPlayer().playUISound(TYPER_TYPE_SOUND_ID, 1f, 1f);
            setText(string.substring(0, index + 1));
        }
    }

    public void grabFocus() {
        grabFocus(true);
    }

    public void grabFocus(boolean withSound) {
        if (withSound) Global.getSoundPlayer().playUISound(TYPER_BUZZER_SOUND_ID, 1f, 1f);

        lastTextBeforeFocus = getText();
        // TODO handle UIManager
        // O0Oo.\u00d500000(this);
    }

    public void releaseFocus(Object object) {
        if (object != null && actionListener != null) {
            actionListener.actionPerformed(object, this);
        }
        // TODO handle UIManager
        // O0Oo.super(this);
    }

    @Override
    public void advanceImpl(float delta) {
        super.advanceImpl(delta);
        
        if (blankTextLabel != null) blankTextLabel.setOpacity(getText().isEmpty() ? 1f : 0f);
        
        final Fader cursorFader = MethodFields.getPanelFader((UIComponentAPI) cursorLabel);
        final boolean isBlinking = (boolean) RolfLectionUtil.invokeMethodDirectly(isBlinkingMethod, cursorLabel);
        final boolean hasFocus = hasFocus();
        
        if ((isBlinking || cursorFader.getBrightness() > 0f && !cursorFader.isFadingOut()) && !hasFocus) {
            RolfLectionUtil.invokeMethodDirectly(stopBlinkingMethod, cursorLabel);
            cursorFader.fadeOut();
        } else if ((!isBlinking || cursorFader.getBrightness() < 1f && !cursorFader.isFadingIn()) && hasFocus) {
            RolfLectionUtil.invokeMethodDirectly(blinkMethod, cursorLabel, 2f, Float.MAX_VALUE);
            ((FaderUtil)RolfLectionUtil.invokeMethodDirectly(getFlasherMethod, cursorLabel)).forceIn();
            cursorFader.fadeIn();
        }
    }

    public boolean hasFocus() {
        // return O0Oo.\u00d300000() == this;
        return false; // TODO handle with FocusManager
    }

    @Override
    public void processInputImpl(List<InputEventAPI> events) {
        super.processInputImpl(events);

        final boolean hasFocus = hasFocus();

        if (!isEnabled()) {
            if (hasFocus) releaseFocus(null);
            return;
        }

        for (InputEventAPI event : events) {
            if (event.isConsumed() || event.isMouseMoveEvent()) continue;
            if (!hasFocus && event.isLMBDownEvent() && mPos.containsEvent(event)) {
                grabFocus();
                clearKeyboardEvents(events);
                event.consume();
                continue;
            }
            if (hasFocus && event.isMouseDownEvent()) {
                releaseFocus(event);
                break;
            }
            if (!hasFocus || !event.isKeyboardEvent() || !event.isKeyDownEvent() && !event.isRepeat()) continue;
            if (event.getEventValue() == Keyboard.KEY_RETURN || event.getEventValue() == Keyboard.KEY_NUMPADENTER) {
                if (callTabPressedOnEnter && fieldListener != null) {
                    fieldListener.tabPressed(this, event);
                }
                if (goToNextOnEnter && nextTextField != null) {
                    releaseFocus(event);
                    nextTextField.grabFocus();
                    clearKeyboardEvents(events);
                    event.consume();
                    break;
                }
                if (!callTabPressedOnEnter) {
                    Global.getSoundPlayer().playUISound(TYPER_BUZZER_SOUND_ID, 1f, 1f);
                }
                releaseFocus(event);
                event.consume();
                break;
            }
            if (event.getEventValue() == Keyboard.KEY_TAB) {
                if (fieldListener != null) {
                    fieldListener.tabPressed(this, event);
                }
                if (nextTextField == null) continue;
                releaseFocus(event);
                nextTextField.grabFocus();
                clearKeyboardEvents(events);
                event.consume();
                break;
            }
            if (event.getEventValue() == Keyboard.KEY_ESCAPE) {
                Global.getSoundPlayer().playUISound(TYPER_TYPE_SOUND_ID, 1f, 1f);
                if (undoOnEscape) {
                    setText(lastTextBeforeFocus);
                }
                releaseFocus(null);
                if (fieldListener != null) {
                    fieldListener.escapePressed(this, event);
                }
                event.consume();
                break;
            }
            if (event.getEventValue() == Keyboard.KEY_V && event.isCtrlDown() && handleCtrlV) {
                final String string = getClipboardText();
                int charIndex = 0;
                while (charIndex < string.length()) {
                    appendCharIfPossible(string.charAt(charIndex));
                    ++charIndex;
                }
                event.consume();
                continue;
            }
            if (event.getEventValue() == Keyboard.KEY_BACK) {
                final boolean isEmpty = getText().isEmpty();
                if (event.isShiftDown()) {
                    deleteAll();
                } else if (event.isCtrlDown()) {
                    deleteLastWord();
                } else {
                    deleteLastChar();
                }
                if (fieldListener != null) {
                    fieldListener.backspacePressed(this, !isEmpty);
                }
                event.consume();
                continue;
            }
            final char inputChar = event.getEventChar();
            if (inputChar == NULL_CHAR) continue;
            appendCharIfPossible(inputChar);
            event.consume();
        }
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color color) {
        borderColor = color;
    }

    public void hideCursor() {
        cursorLabel.setOpacity(0f);
    }

    public void showCursor() {
        cursorLabel.setOpacity(1f);
    }

    @Override
    public void renderImpl(float alpha) {
        final float x = getX();
        final float y = getY();
        final float w = getWidth();
        final float h = getHeight();
        final Fader cursorFader = MethodFields.getPanelFader((UIComponentAPI) cursorLabel);

        final Color adjustedBorderColor = NativeUiUtils.setAlpha(borderColor, 127f + 128f * cursorFader.getBrightness());
        final Color adjustedBgColor = NativeUiUtils.adjustBrightness(bgColor, 0.4f);
        final Color adjustedGradientColor = NativeUiUtils.setAlpha(adjustedBgColor, 200);
        if (minimalMode) {
            RenderUtils.drawQuad(x, y, w, 2f, adjustedBorderColor, alpha, false);

            RenderUtils.drawGradientQuad(x + 1f, y + 1f, w - 2f, h - 2f, adjustedBorderColor, Misc.zeroColor, Misc.zeroColor, adjustedGradientColor, alpha);
        } else {
            if (descLabel != null) {
                RenderUtils.drawFramedBorder(x, y, w, h, 1f, adjustedBorderColor, alpha, true);
                final float f7 = descLabel.getPosition().getX();
                final float f8 = descLabel.getPosition().getX() + descLabel.getPosition().getWidth();
                RenderUtils.drawQuad(x + 1f, y + h - 1f, f7 - x - 1f, 1f, adjustedBorderColor, alpha, false);
                RenderUtils.drawQuad(f8 + 1f, y + h - 1f, x + w - f8 - 2f, 1f, adjustedBorderColor, alpha, false);
            } else {
                RenderUtils.drawQuad(x, y, w, h, adjustedBorderColor, alpha, false);
            }
            RenderUtils.drawQuad(x + 1f, y + 1f, w - 2f, h - 2f, adjustedGradientColor, alpha, false);
        }

        super.render(alpha);

        if (!isEnabled()) {
            RenderUtils.drawQuad(x + 1f, y + 1f, w - 2f, h - 2f, DISABLED_COLOR, alpha * 0.4f, false);
        }
    }

    private static final String getClipboardText() {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final Transferable transferable = clipboard.getContents(null);
        final boolean validClipboard = transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor);

        String string = "";
        if (validClipboard) {
            try {
                string = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        return string;
    }

    private static final void clearKeyboardEvents(final List<InputEventAPI> events) {
        for (InputEventAPI event : events) {
            if (event.isConsumed() || event.isMouseEvent() || !event.isKeyboardEvent()) continue;
            event.consume();
        }
    }

    public final void setLimitByStringWidth(boolean bool) { limitByStringWidth = bool; }
    public final void setUndoOnEscape(boolean bool) { undoOnEscape = bool; }
    public final void setHandleCtrlV(boolean bool) { handleCtrlV = bool; }
    public final void setEnabled(boolean bool) { isEnabled = bool; }
    public final void setVerticalCursor(boolean bool) {}
    public final boolean isLimitByStringWidth() { return limitByStringWidth; }
    public final boolean isHandleCtrlV() { return handleCtrlV; }
    public final boolean isUndoOnEscape() { return undoOnEscape; }
    public final boolean isVerticalCursor() { return false; }
    public final boolean isEnabled() { return isEnabled; }

    public static interface TextFieldListener {
        public void backspacePressed(TextFieldAPI field, boolean character);

        public void charTyped(TextFieldAPI field, char character);

        public void charTypeFailed(TextFieldAPI field, char character);

        public void tabPressed(TextFieldAPI field, InputEventAPI event);

        default public void escapePressed(TextFieldAPI field, InputEventAPI event) {
        }

        default public void textChanged(TextFieldAPI field, String prevText) {
        }
    }
}