package wfg.wrap_ui.internal.ui.plugins;

import java.util.List;

import com.fs.starfarer.api.input.InputEventAPI;

import wfg.wrap_ui.internal.ui.dialogs.FoldingPanel;
import wfg.wrap_ui.ui.plugins.CustomPanelPlugin;

public class FoldingPanelPlugin extends CustomPanelPlugin<FoldingPanel, FoldingPanelPlugin> {
    @Override
    public void render(float alphaMult) {
        super.render(alphaMult);
        getPanel().renderImpl(alphaMult);
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        super.processInput(events);
        getPanel().processInputImpl(events);
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        getPanel().advanceImpl(amount);
    }
}