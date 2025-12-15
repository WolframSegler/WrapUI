package wfg.wrap_ui.ui;

import static wfg.wrap_ui.util.UIConstants.*;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;

import wfg.wrap_ui.ui.panels.Button;
import wfg.wrap_ui.util.CallbackRunnable;

public class ComponentFactory {
    public static final Button createCheckboxWithText(
        UIPanelAPI parent, int btnSize, String text, String font, CallbackRunnable<Button> onClick, 
        Color txtColor, int textAndBtnGap
    ) {
        final SettingsAPI settings = Global.getSettings();
        final Button checkbox = new Button(
            parent, btnSize, btnSize, null, null, onClick
        );
        checkbox.bgAlpha = 1f;
        checkbox.bgDisabledAlpha = 1f;
        checkbox.bgSelectedColor = new Color(60, 230, 250);
        checkbox.quickMode = false;

        final LabelAPI label = settings.createLabel(
            text, font == null ? Fonts.DEFAULT_SMALL : font
        );
        final int lblH = (int) label.computeTextHeight(text);
        label.setColor(txtColor == null ? base : txtColor);

        final int maxH = Math.max(lblH, btnSize);

        checkbox.add(label).inBL(btnSize + textAndBtnGap, (maxH - lblH) / 2f);

        return checkbox;
    }
}