package wfg.native_ui.ui.container;

import java.awt.Color;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.input.InputEventType;
import com.fs.starfarer.api.ui.UIComponentAPI;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.component.BackgroundComp;
import wfg.native_ui.ui.component.InputSnapshotComp;
import wfg.native_ui.ui.component.NativeComponents;
import wfg.native_ui.ui.core.UIElementFlags.HasBackground;
import wfg.native_ui.ui.core.UIElementFlags.HasInputSnapshot;
import wfg.native_ui.util.Arithmetic;

/**
 * Do not add children directly to ScrollPanel, but to the contentPanel
 */
public class ScrollPanel extends UIContainer implements HasBackground, HasInputSnapshot {
    public final BackgroundComp bg = comp().get(NativeComponents.BACKGROUND);
    protected final InputSnapshotComp input = comp().get(NativeComponents.INPUT_SNAPSHOT);

    public enum ScrollType { HORIZONTAL, VERTICAL, BOTH }
    public ScrollType scrollType = ScrollType.VERTICAL;
    public float scrollSpeed = 0.5f;

    protected final UIContainer contentPanel;

    protected float contentWidth, contentHeight;
    protected float scrollOffsetX, scrollOffsetY;

    public ScrollPanel(float viewportWidth, float viewportHeight) {
        super(viewportWidth, viewportHeight);

        bg.color = new Color(100, 100, 100);
        bg.enabled = false;

        contentWidth = viewportWidth;
        contentHeight = viewportHeight;

        contentPanel = new UIContainer(viewportWidth, viewportHeight);
        add(contentPanel).inBL(0f, 0f);
    }

    public UIContainer getContentPanel() { return contentPanel; }
    public void addToContent(UIComponentAPI comp) {
        contentPanel.add(comp);
    }

    public void setContentWidth(int width) {
        contentWidth = width;
        contentPanel.setWidth(width);
    }

    public void setContentHeight(int height) {
        contentHeight = height;
        contentPanel.setHeight(height);
    }

    @Override
    public final void renderImpl(float alpha) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());

        super.renderImpl(alpha);
        
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void processInputImpl(List<InputEventAPI> events) {
        super.processInputImpl(events);
        if (!input.hoveredLastFrame) return;
        
        int scrollValue = 0;
        boolean shiftDown = false;

        for (InputEventAPI e : events) {
            if (e.isConsumed()) { continue; }

            if (e.getEventType() == InputEventType.MOUSE_SCROLL) {
                scrollValue = e.getEventValue();
                e.consume();
                continue;
            }

            if ((e.getEventType() == InputEventType.KEY_DOWN || e.getEventType() == InputEventType.KEY_REPEAT) &&
                (e.getEventValue() == Keyboard.KEY_LSHIFT || e.getEventValue() == Keyboard.KEY_RSHIFT)) {
                shiftDown = true;
            }
        }

        if (scrollValue == 0) return;

        final float delta = -scrollValue * scrollSpeed;

        switch (scrollType) {
        case VERTICAL:
            scrollOffsetY = clampScrollY(scrollOffsetY + delta);
            break;
        case HORIZONTAL:
            scrollOffsetX = clampScrollX(scrollOffsetX + delta);
            break;
        case BOTH:
            if (shiftDown) {
                scrollOffsetX = clampScrollX(scrollOffsetX + delta);
            } else {
                scrollOffsetY = clampScrollY(scrollOffsetY + delta);
            }
            break;
        }
        contentPanel.pos().inTL(-scrollOffsetX, scrollOffsetY);
    }

    private final float clampScrollX(float x) {
        return Arithmetic.clamp(x, 0f, Math.max(0f, contentWidth - pos().getWidth()));
    }

    private final float clampScrollY(float y) {
        return Arithmetic.clamp(y, 0f, Math.max(0f, contentHeight - pos().getHeight()));
    }
}