package wfg.native_ui.example.widget;

import com.fs.starfarer.api.ui.ButtonAPI.UICheckboxSize;

import static wfg.native_ui.util.UIConstants.*;

import org.lwjgl.input.Keyboard;

import com.fs.starfarer.api.ui.Fonts;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.core.UIBuildableAPI;
import wfg.native_ui.ui.widget.CheckboxButton;
import wfg.native_ui.util.NativeUiUtils;
import wfg.native_ui.util.NativeUiUtils.AnchorType;

public final class CheckboxButtonExample extends UIContainer implements UIBuildableAPI {
    private boolean someSharedData = false;

    public CheckboxButtonExample(float width, float height) {
        super(width, height);

        buildUI();
    }

    @Override
    public void buildUI() {
        // Basic example
        final CheckboxButton btn = new CheckboxButton(22, "basic", Fonts.DEFAULT_SMALL, null, UICheckboxSize.SMALL, false);
        btn.onClicked = (button) -> {
            button.setChecked(!button.isChecked());
            someSharedData = button.isChecked();
        };
        btn.setChecked(someSharedData);
        add(btn).inTL(hpad, hpad);

        // With tp and shortcut
        final CheckboxButton footer = new CheckboxButton(20, "important footnote text",
            Fonts.ORBITRON_12, (button) -> {
                button.setChecked(!button.isChecked());
                createSomeUI(button.isChecked());
            },
            UICheckboxSize.SMALL, false
        );
        footer.setShortcutAndAppendToText(Keyboard.KEY_Q);
        footer.tooltip.width = getWidth() * 0.5f;
        footer.tooltip.builder = (tp, exp) -> {
            tp.addPara("further details regarding important footnote text", pad);
        };
        footer.tooltip.positioner = (tp, exp) -> {
            NativeUiUtils.anchorPanel(tp, footer, AnchorType.TopLeft, pad);
        };
    }

    private final void createSomeUI(boolean someState) {
        clearChildren();

        // add UI
    }
}