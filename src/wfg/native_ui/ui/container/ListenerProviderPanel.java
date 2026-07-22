package wfg.native_ui.ui.container;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.component.InteractionComp;
import wfg.native_ui.ui.component.NativeComponents;
import wfg.native_ui.ui.core.UIElementFlags.HasInteraction;

/**
 * Wrapper panel for providing a listener in overlays.
 */
public class ListenerProviderPanel extends UIContainer implements HasInteraction {
    public final InteractionComp<ListenerProviderPanel> interaction = comp().get(NativeComponents.INTERACTION);

    public ListenerProviderPanel(int width, int height) {
        super(width, height);
    }
}