package wfg.native_ui.example.container;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.core.UIBuildableAPI;

public final class VanillaScrollPanelExample extends UIContainer implements UIBuildableAPI {
    public VanillaScrollPanelExample(float w, float h) {
        super(w, h);

        buildUI();
    }

    @Override
    public void buildUI() {
        clearChildren();

        // TODO add example
    }
}