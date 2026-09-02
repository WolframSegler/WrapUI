package wfg.native_ui.internal.util;

import static wfg.native_ui.util.Globals.settings;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.fs.starfarer.api.graphics.SpriteAPI;

import wfg.native_ui.util.RenderUtils;

public final class ToastRenderer {
    private ToastRenderer() {};
    private static final SpriteAPI HANDLE_BL = settings.getSprite("ui", "toast_handle_bottom_left");
    private static final SpriteAPI HANDLE_CL = settings.getSprite("ui", "toast_handle_center_left");

    private static final float HANDLE_W = HANDLE_BL.getTextureWidth();
    private static final float CORNER_H = HANDLE_BL.getTextureHeight();

    public static final void render(float x, float y, float w, float h, boolean mirror, float alpha, Color color, int blendSrc, int blendDest) {
        final float handleX = x + (mirror ? w - HANDLE_W : 0f);
        final float blY = y;
        final float tlY = y + h - CORNER_H;
        final float centerY = y + CORNER_H;
        final float innerH = h - 2f * CORNER_H;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(blendSrc, blendDest);
        RenderUtils.setGlColor(color, alpha);

        HANDLE_BL.bindTexture();
        NineSliceUtils.drawCorner(HANDLE_BL, mirror, false, handleX, blY);

        HANDLE_BL.bindTexture();
        NineSliceUtils.drawCorner(HANDLE_BL, mirror, true, handleX, tlY);

        HANDLE_CL.bindTexture();
        NineSliceUtils.drawStretch(HANDLE_CL, mirror, false, handleX, centerY, HANDLE_W, innerH);

        GL11.glDisable(GL11.GL_BLEND);
    }
}