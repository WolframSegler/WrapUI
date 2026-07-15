package wfg.native_ui.internal.ui.core;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

import wfg.native_ui.ui.ComponentFactory;
import wfg.native_ui.ui.core.UIContainerAPI;
import wfg.native_ui.ui.core.UIElementAPI;
import wfg.native_ui.ui.event.IdentifiedPanel;
import wfg.native_ui.ui.system.BaseSystem;

public class UIContainer extends UIEntity implements UIContainerAPI {
    private final ArrayList<UIComponentAPI> children = new ArrayList<>();

    public UIContainer(float width, float height) {
        super(width, height);
    }

    public UIContainer() {
        super();
    }

    public UIContainer(PositionAPI pos) {
        super(pos);
    }

    public PositionAPI add(UIComponentAPI comp) {
        if (!children.contains(comp)) {
            children.add(comp);
            final PositionAPI compPos = comp.getPosition();
            // TODO
            // mPos.add(compPos);
            // compPos.setParent(mPos);
            // comp.setParent(this);

            if (comp instanceof UIElementAPI element) {
                element.reportAttached();
            }
        }
        return comp.getPosition();
    }

    public PositionAPI add(TooltipMakerAPI tooltip) {
        return ComponentFactory.addTooltip(tooltip, 0f, false, this);
    }

    public void remove(UIComponentAPI comp) {
        if (children.remove(comp)) {
            final PositionAPI compPos = comp.getPosition();
            // TODO
            // mPos.remove(compPos);
            // compPos.setParent(null);
            // comp.setParent(null);

            if (comp instanceof UIElementAPI element) {
                element.reportDetached();
            }
        }
    }

    public List<UIComponentAPI> getChildren() {
        return children;
    }

    public List<UIComponentAPI> getChildrenCopy() {
        return new ArrayList<>(children);
    }

    public void clearChildren() {
        for (UIComponentAPI child : getChildrenCopy()) {
            removeComponent(child);
        }
        children.clear();
    }

    public <T extends UIComponentAPI> T getChild(Class<T> type) {
        for (UIComponentAPI child : children) {
            if (type.isInstance(child)) return type.cast(child);
        }
        return null;
    }

    public UIComponentAPI getChild(String panelId) {
        if (panelId == null) return null;
        for (UIComponentAPI child : children) {
            if (child instanceof IdentifiedPanel ip && panelId.equals(ip.getPanelId())) {
                return child;
            }
        }
        return null;
    }

    public PositionAPI addComponent(UIComponentAPI comp) { return add(comp); }
    public void removeComponent(UIComponentAPI comp) { remove(comp); }

    public PositionAPI addPos(UIComponentAPI comp) {
        final PositionAPI compPos = comp.getPosition();
        // mPos.addChild(compPos);
        // compPos.setParent(mPos);

        return comp.getPosition();
    }

    public void removePos(UIComponentAPI comp) {
        final PositionAPI compPos = comp.getPosition();
        // TODO
        // mPos.removeChild(compPos);
        // compPos.setParent(null);
    }

    public void bringComponentToTop(UIComponentAPI comp) { bringToTop(comp); }
    public void bringToTop(UIComponentAPI comp) {
        if (children.remove(comp)) {
            children.add(comp);
            if (mParent != null) mParent.bringComponentToTop(this);
        }
    }

    public void bringToTopWithinItself(UIComponentAPI comp) {
        if (children.remove(comp)) {
            children.add(comp);
        }
    }

    public void sendToBottom(UIComponentAPI comp) {
        if (children.remove(comp)) {
            children.add(0, comp);
            sendToBack();
        }
    }

    public void sendToBottomWithinItself(UIComponentAPI comp) {
        if (children.remove(comp)) {
            children.add(0, comp);
        }
    }

    @Override
    public void renderImpl(float alpha) {
        for (BaseSystem system : system().getAll()) {
            system.renderBelow(this, alpha);
        }
        renderBelowImpl(alpha);

        children.forEach(c -> c.render(alpha));

        for (BaseSystem system : system().getAll()) {
            system.renderAbove(this, alpha);
        }
        renderAboveImpl(alpha);
    }

    @Override
    public void advanceImpl(float delta) {
        super.advanceImpl(delta);
        children.forEach(c -> c.advance(delta));
    }

    @Override
    public void processInputImpl(List<InputEventAPI> events) {
        super.processInputImpl(events);
        children.forEach(c -> c.processInput(events));
    }


    // TODO remove after update
    @Deprecated
    public PositionAPI add(LabelAPI comp) {
        return add((UIComponentAPI) comp);
    }

    // TODO remove after update
    @Deprecated
    public void remove(LabelAPI comp) {
        remove((UIComponentAPI) comp);
    }
}