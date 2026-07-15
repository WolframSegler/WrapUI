package wfg.native_ui.ui.visual;

import java.awt.Color;

import com.fs.starfarer.api.graphics.SpriteAPI;

import wfg.native_ui.ui.component.NativeComponents;
import wfg.native_ui.ui.component.TooltipComp;
import wfg.native_ui.ui.core.UIElementFlags.HasTooltip;

public class IconValuePairTp extends IconValuePair implements HasTooltip {

    public final TooltipComp tooltip = comp().get(NativeComponents.TOOLTIP);
    
    public IconValuePairTp(float w, float h, String iconID, double value, boolean withX, Color color) {
        super(w, h, iconID, value, withX, color);
    }
    
    public IconValuePairTp(float w, float h, SpriteAPI icon, double value, boolean withX, Color color) {
        super(w, h, icon, value, withX, color);
    }

    public IconValuePairTp(float w, float h, String iconID, double value, boolean withX, Color color,
        String font
    ) {
        super(w, h, iconID, value, withX, color, font);
    }
    
    public IconValuePairTp(float w, float h, SpriteAPI icon, double value, boolean withX, Color color,
        String font
    ) {
        super(w, h, icon, value, withX, color, font);
    }
}