package wfg.wrap_ui.internal.ui.plugins;

import java.util.List;

import com.fs.starfarer.api.input.InputEventAPI;

import wfg.wrap_ui.internal.ui.dialogs.ModalDialog;
import wfg.wrap_ui.ui.plugins.CustomPanelPlugin;

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
}