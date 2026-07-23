package wfg.native_ui.ui.util;

import java.util.List;

import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;

import wfg.native_ui.internal.ui.core.UIElement;
import wfg.native_ui.ui.MethodFields;

/**
 * Gets injected to vanilla UI hierarchies. Its presence indicates that the UI has yet to be refreshed.
 * Used to prevent constant UI-replacement.
 */
public class IdentityMarker extends UIElement {
    private IdentityMarker() {};
    private static final IdentityMarker element = new IdentityMarker();

    /** attaches to the provided parent an {@link IdentityMarker}. */
    public static final void attach(UIPanelAPI parent) {
        parent.addComponent(element);
    }

    /** @return if the provided object is an instace of {@link IdentityMarker}. */
    public static final boolean isMarker(Object obj) {
        return obj instanceof IdentityMarker;
    }

    /** @return if the direct children of the {@code parent} contain an {@link IdentityMarker}. */
    public static final boolean isPresent(UIPanelAPI parent) {
        final List<UIComponentAPI> children = MethodFields.getChildrenNonCopy(parent);
        
        for (UIComponentAPI child : children) {
            if (isMarker(child)) return true;
        }
        return false;
    }
}