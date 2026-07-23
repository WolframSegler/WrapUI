package wfg.native_ui.example.widget;

import static wfg.native_ui.util.UIConstants.opad;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.core.UIBuildableAPI;
import wfg.native_ui.ui.widget.RadioPanel;
import wfg.native_ui.ui.widget.RadioPanel.LayoutMode;

/**
 * {@link RadioPanel} used as a filter.
 */
public final class RadioPanelExample extends UIContainer implements UIBuildableAPI {
    public int directionMode = 0;

    public RadioPanelExample(float w, float h) {
        super(w, h);

        buildUI();
    }

    @Override
    public void buildUI() {
        clearChildren();

        final RadioPanel modeRadio = new RadioPanel(getWidth() - opad*2, 30, LayoutMode.HORIZONTAL)
            .addOption("All")
            .addOption("Exporters")
            .addOption("Importers")
            .addOption("In-Faction");
        modeRadio.optionSelected = (index) -> {
            directionMode = index;
            // rebuild elements that depend on the filter
        };
        modeRadio.setSelectedIndex(directionMode);
        modeRadio.buildUI(); // This must be called explicitly
        add(modeRadio).inTL(0f, 0f);
    }
}