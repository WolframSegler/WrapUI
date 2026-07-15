package wfg.native_ui.ui.visual;

import static wfg.native_ui.util.Globals.settings;
import static wfg.native_ui.util.UIConstants.*;

import java.awt.Color;

import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.LabelAPI;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.visual.AbstractSpriteElement.SpriteElement;
import wfg.native_ui.util.NumFormat;

public class IconValuePair extends UIContainer {
    private final SpriteElement iconBase;
    private final LabelAPI valueLbl;

    public IconValuePair(float w, float h, String iconID, double value, boolean withX, Color color) {
        this(w, h, settings.getSprite(iconID), value, withX, color);
    }

    public IconValuePair(float w, float h, SpriteAPI icon, double value, boolean withX, Color color) {
        this(w, h, icon, value, withX, color, null);
    }

    public IconValuePair(float w, float h, String iconID, double value, boolean withX, Color color,
        String font
    ) {
        this(w, h, settings.getSprite(iconID), value, withX, color, font);
    }
    
    public IconValuePair(float w, float h, SpriteAPI icon, double value, boolean withX, Color color,
        String font
    ) {
        super(w, h);

        iconBase = new SpriteElement(h, h, icon, null, null);
        final String valueStr = (withX ? Strings.X : "") + NumFormat.engNotate(value);
        valueLbl = settings.createLabel(valueStr, font == null ? Fonts.DEFAULT_SMALL : font);
        valueLbl.setColor(color == null ? highlight : color);
        valueLbl.getPosition().setSize(w - h - pad, h);
        valueLbl.setAlignment(Alignment.LMID);

        add(iconBase).inBL(0f, 0f);
        add(valueLbl).inBR(0f, 0f);
    }

    public final SpriteElement icon() {
        return iconBase;
    }

    public final LabelAPI label() {
        return valueLbl;
    }
}