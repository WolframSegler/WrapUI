package wfg.wrap_ui.internal.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;

public class BorderRenderer {
    public boolean renderCenter = true;

    private SpriteAPI bottom_left;
    private SpriteAPI bottom_right;
    private SpriteAPI bottom_mid;
    private SpriteAPI center;
    private SpriteAPI left_mid;
    private SpriteAPI right_mid;
    private SpriteAPI top_left;
    private SpriteAPI top_right;
    private SpriteAPI top_mid;
    private float width;
    private float height;
    private float tilesWide;
    private float tilesHigh;
    private float corner_width;

    public BorderRenderer(String prefix, float var2, float var3) {
        this(prefix);
        this.setSize(var2, var3);
    }

    /**
     * The texture size should match the actual size of the sprites.
     * <pre>
     * Available prefixes:
     * "ui_border1"
     * "ui_border2"
     * "ui_border3"
     * "ui_border4"
     * </pre>
     */
    public BorderRenderer(String prefix) {
        final SettingsAPI settings = Global.getSettings();

        bottom_left = settings.getSprite("ui", prefix + "_bot_left");
        bottom_right = settings.getSprite("ui", prefix + "_bot_right");
        bottom_mid = settings.getSprite("ui", prefix + "_bot");
        center = settings.getSprite("ui", "panel00_center");
        left_mid = settings.getSprite("ui", prefix + "_left");
        right_mid = settings.getSprite("ui", prefix + "_right");
        top_left = settings.getSprite("ui", prefix + "_top_left");
        top_right = settings.getSprite("ui", prefix + "_top_right");
        top_mid = settings.getSprite("ui", prefix + "_top");
        corner_width = bottom_left.getTexWidth();
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        this.tilesWide = (width - this.corner_width * 2.0F) / this.corner_width;
        this.tilesHigh = (height - this.corner_width * 2.0F) / this.corner_width;
    }

    public void render(float x, float y, float alpha) {
        {
            bottom_left.setAlphaMult(alpha);
            bottom_right.setAlphaMult(alpha);
            top_left.setAlphaMult(alpha);
            top_right.setAlphaMult(alpha);
            left_mid.setAlphaMult(alpha);
            right_mid.setAlphaMult(alpha);
            top_mid.setAlphaMult(alpha);
            bottom_mid.setAlphaMult(alpha);
        }
        
        bottom_left.render(x, y);
        bottom_right.render(x + width - corner_width, y);
        top_left.render(x, y + height - corner_width);
        top_right.render(x + width - corner_width, y + height - corner_width);
        left_mid.renderRegion(x, y + corner_width, 0f, 0f, 1f, tilesHigh);
        right_mid.renderRegion(x + width - corner_width, y + corner_width, 0f, 0f, 1f, tilesHigh);
        top_mid.renderRegion(x + corner_width, y + height - corner_width, 0f, 0f, tilesWide, 1f);
        bottom_mid.renderRegion(x + corner_width, y, 0f, 0f, tilesWide, 1f);
        if (renderCenter) {
            center.setAlphaMult(alpha);
            center.renderRegion(x + corner_width, y + corner_width, 0f, 0f, tilesWide, tilesHigh);
        }
    }
}