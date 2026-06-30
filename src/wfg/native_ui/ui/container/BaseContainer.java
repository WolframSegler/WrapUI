package wfg.native_ui.ui.container;

import com.fs.starfarer.api.ui.UIPanelAPI;

import wfg.native_ui.ui.component.BackgroundComp;
import wfg.native_ui.ui.component.NativeComponents;
import wfg.native_ui.ui.core.UIElementFlags.HasBackground;
import wfg.native_ui.ui.panel.CustomPanel;

// TODO make this inherit UIContainer
/**
 * An empty implementation of {@link CustomPanel}
 */
public class BaseContainer extends CustomPanel implements HasBackground {
    public final BackgroundComp bg = comp().get(NativeComponents.BACKGROUND);

    public BaseContainer(UIPanelAPI parent, int width, int height) {
        super(parent, width, height);
    }
}