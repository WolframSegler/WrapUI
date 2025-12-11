package wfg.wrap_ui.ui.plugins;

import java.util.List;

import com.fs.starfarer.api.input.InputEventAPI;

import wfg.wrap_ui.ui.panels.ScrollPanel;

public class ScrollPanelPlugin extends CustomPanelPlugin<ScrollPanel, ScrollPanelPlugin> {
    @Override
    public void processInput(List<InputEventAPI> events) {
        super.processInput(events);
        m_panel.processInputImpl(events, inputSnapshot);
    }

    @Override
    public void render(float alpha) {
        super.render(alpha);
        m_panel.renderImpl(alpha);
    }
}