package wfg.native_ui.example.widget;

import static wfg.native_ui.util.Globals.settings;
import static wfg.native_ui.util.UIConstants.hpad;
import static wfg.native_ui.util.UIConstants.opad;

import java.awt.Color;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.core.UIBuildableAPI;
import wfg.native_ui.ui.widget.Button;
import wfg.native_ui.ui.widget.MultiSelect;
import wfg.native_ui.ui.widget.RadioPanel.LayoutMode;

/**
 * This example has a weapon size filter made using {@link MultiSelect}. 
 */
public final class MultiSelectExample extends UIContainer implements UIBuildableAPI {
    public EnumSet<WeaponSize> weaponSizeFilters = EnumSet.allOf(WeaponSize.class);

    public MultiSelectExample(float w, float h) {
        super(w, h);

        buildUI();
    }

    @Override
    public void buildUI() {
        clearChildren();

        final LabelAPI weaponLbl = settings.createLabel("Weapon", Fonts.ORBITRON_16);
        add(weaponLbl).inTL(0f, 0f);

        final int btnW = 100;
        final int btnH = 20;

        final List<String> weaponSizeStrings = Arrays.asList(WeaponSize.SMALL.getDisplayName(), WeaponSize.MEDIUM.getDisplayName(), WeaponSize.LARGE.getDisplayName());
        final MultiSelect weaponSizeButtons = new MultiSelect(btnW*3 + opad*2, btnH, weaponSizeStrings, LayoutMode.HORIZONTAL);
        weaponSizeButtons.onSelected = (button) -> {

            final WeaponSize marker = WeaponSize.values()[(Integer) button.customData];
            if (button.isChecked()) {
                weaponSizeFilters.add(marker);
            } else {
                weaponSizeFilters.remove(marker);
            }
        };
        weaponSizeButtons.buildUI();

        for (Button btn : weaponSizeButtons.getButtons()) {
            final int index = (Integer) btn.customData;
            final boolean contains = weaponSizeFilters.contains(WeaponSize.values()[index]);

            if (contains) weaponSizeButtons.select(index);
            btn.setHighlightBrightness(0f);
            btn.bgColor = Color.BLACK;
        }
        add(weaponSizeButtons).rightOfMid((UIComponentAPI) weaponLbl, hpad);
    }
}