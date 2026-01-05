package wfg.wrap_ui.internal.ui.plugins;

import java.util.List;
import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.input.InputEventAPI;

import wfg.wrap_ui.internal.ui.dialogs.ModalDialog;
import wfg.wrap_ui.ui.plugins.CustomPanelPlugin;
import wfg.wrap_ui.util.RenderUtils;

public class ModalDialogPlugin extends CustomPanelPlugin<ModalDialog, ModalDialogPlugin>{
    @Override
    public void processInput(List<InputEventAPI> events) {
        super.processInput(events);

        getPanel().processInputImpl(events);
    }

    @Override
    public void advance(float delta) {
        super.advance(delta);

        getPanel().advanceImpl(delta);
    }

    @Override
    public void renderBelow(float alphaMult) {
        super.renderBelow(alphaMult);

        final SettingsAPI set = Global.getSettings();
        RenderUtils.drawQuad(
            0f, 0f, set.getScreenWidth(), set.getScreenHeight(),
            Color.BLACK,
            alphaMult * m_panel.backgroundDimAmount * m_panel.getFaderBrightness(),
            false
        );
    }
}