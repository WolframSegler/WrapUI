package wfg.native_ui.internal.util;

import org.lwjgl.opengl.GL11;

import com.fs.starfarer.api.graphics.SpriteAPI;

public class NineSliceUtils {
    private static final float epsilon = 0.001f;

    /** Assumes no texture offset. */
    public static final void drawCorner(
        boolean flipU, boolean flipV, float texW, float texH,
        float x, float y, float w, float h
    ) {
        final float uLeft = flipU ? texW : 0f;
        final float uRight = flipU ? 0f : texW;
        final float vBottom = flipV ? texH : 0f;
        final float vTop = flipV ? 0f : texH;

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(uLeft, vBottom);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(uLeft, vTop);
        GL11.glVertex2f(x, y + h);
        GL11.glTexCoord2f(uRight, vTop);
        GL11.glVertex2f(x + w, y + h);
        GL11.glTexCoord2f(uRight, vBottom);
        GL11.glVertex2f(x + w, y);
        GL11.glEnd();
    }

    /** Assumes no texture offset. */
    public static final void drawStretch(
        boolean flipU, boolean flipV, float texW, float texH,
        float x, float y, float w, float h
    ) {
        final float uLeft = flipU ? texW - epsilon : epsilon;
        final float uRight = flipU ? epsilon : texW - epsilon;
        final float vBottom = flipV ? texH : epsilon;
        final float vTop = flipV ? epsilon : texH - epsilon;

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(uLeft, vBottom);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(uLeft, vTop);
        GL11.glVertex2f(x, y + h);
        GL11.glTexCoord2f(uRight, vTop);
        GL11.glVertex2f(x + w, y + h);
        GL11.glTexCoord2f(uRight, vBottom);
        GL11.glVertex2f(x + w, y);
        GL11.glEnd();
    }

    /**
     * Draws a corner with texture dimensions equal to render dimensions.
     * Assumes no texture offset.
     */
    public static final void drawCorner(
        boolean flipU, boolean flipV,
        float x, float y, float w, float h
    ) {
        drawCorner(flipU, flipV, w, h, x, y, w, h);
    }

    /**
     * Draws a corner from a SpriteAPI, using its texture dimensions.
     * Render size equals texture size.
     * Assumes no texture offset.
     */
    public static final void drawCorner(
        SpriteAPI sprite, boolean flipU, boolean flipV, float x, float y
    ) {
        drawCorner(
            flipU, flipV,
            sprite.getTexWidth(), sprite.getTexHeight(),
            x, y,
            sprite.getWidth(), sprite.getHeight()
        );
    }

    /**
     * Draws a corner from a SpriteAPI with separate render size.
     * Assumes no texture offset.
     */
    public static final void drawCorner(
        SpriteAPI sprite, boolean flipU, boolean flipV,
        float x, float y, float w, float h
    ) {
        drawCorner(
            flipU, flipV,
            sprite.getTexWidth(), sprite.getTexHeight(),
            x, y, w, h
        );
    }

    /**
     * Draws a stretch section with texture dimensions equal to render dimensions.
     * Assumes no texture offset.
     */
    public static final void drawStretch(
        boolean flipU, boolean flipV,
        float x, float y, float w, float h
    ) {
        drawStretch(flipU, flipV, w, h, x, y, w, h);
    }

    /**
     * Draws a stretch section from a SpriteAPI, using its texture dimensions.
     * Render size equals texture size.
     * Assumes no texture offset.
     */
    public static final void drawStretch(
        SpriteAPI sprite, boolean flipU, boolean flipV, float x, float y
    ) {
        drawStretch(
            flipU, flipV,
            sprite.getTexWidth(), sprite.getTexHeight(),
            x, y,
            sprite.getWidth(), sprite.getHeight()
        );
    }

    /**
     * Draws a stretch section from a SpriteAPI with separate render size.
     * Assumes no texture offset.
     */
    public static final void drawStretch(
        SpriteAPI sprite, boolean flipU, boolean flipV,
        float x, float y, float w, float h
    ) {
        drawStretch(
            flipU, flipV,
            sprite.getTexWidth(), sprite.getTexHeight(),
            x, y, w, h
        );
    }
}