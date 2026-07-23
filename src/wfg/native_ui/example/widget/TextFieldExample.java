package wfg.native_ui.example.widget;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.core.UIBuildableAPI;

public final class TextFieldExample extends UIContainer implements UIBuildableAPI {
    public TextFieldExample(float w, float h) {
        super(w, h);

        buildUI();
    }

    @Override
    public void buildUI() {
        clearChildren();

        // TODO add example
    }
} 