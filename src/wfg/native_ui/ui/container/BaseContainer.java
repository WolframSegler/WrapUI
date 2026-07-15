package wfg.native_ui.ui.container;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.component.BackgroundComp;
import wfg.native_ui.ui.component.NativeComponents;
import wfg.native_ui.ui.core.UIElementFlags.HasBackground;

/**
 * An empty implementation of {@link UIContainer}
 */
public class BaseContainer extends UIContainer implements HasBackground {
    public final BackgroundComp bg = comp().get(NativeComponents.BACKGROUND);

    public BaseContainer(float width, float height) {
        super(width, height);
    }
}