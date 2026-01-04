package wfg.wrap_ui.internal.util;

import java.util.Random;
import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.FaderUtil;
import com.fs.starfarer.api.util.Misc;

import wfg.wrap_ui.util.RenderUtils;

public class NoiseRenderer {
    public final FaderUtil fader = new FaderUtil(0f, 1f, 1f);
    public Color noiseColor = Global.getSettings().getColor("noiseColor");
    private float renderW;
    private float renderH;

    private SpriteAPI m_sprite;
    private NoiseGenerator noise = new NoiseGenerator();
    private long randomNum = 0L;

    public NoiseRenderer(SpriteAPI sprite, float width, float height) {
        m_sprite = sprite;
        renderW = width;
        renderH = height;
        fader.setBounceDown(true);
    }

    public float getNoiseBrightness() {
        return (0.5F + 0.5F * noise.getIntensity()) * fader.getBrightness();
    }

    public float getBrightness() {
        return fader.getBrightness();
    }

    public void fadeIn(float inDuration) {
        fader.setDurationIn(inDuration);
        fader.fadeIn();
    }

    public void fadeOut(float inDuration) {
        fader.setDurationOut(inDuration);
        fader.fadeOut();
    }

    public void fadeIn(float inDuration, float outDuration) {
        fader.setDuration(inDuration, outDuration);
        fader.fadeIn();
    }

    public boolean isMaxBrightness() {
        return fader.getBrightness() == 1f;
    }

    public void advance(float delta) {
        if (fader.getBrightness() > 0f) {
            noise.advance(delta);
        }

        fader.advance(delta);
        randomNum = Misc.random.nextLong();
    }

    public void render(float x, float y, float intensityMultiplier) {
        final float intensity = getNoiseBrightness() * intensityMultiplier;
        if (intensity > 0f) {
            final Random rand = randomNum == 0L ? new Random() : new Random(randomNum);

            render(m_sprite, intensity, x, y, rand.nextFloat(), rand.nextFloat(), 1f * renderW / m_sprite.getWidth(), 1f * renderH / m_sprite.getHeight());
        }
    }

    private void render(SpriteAPI sprite, float alpha, float x, float y, float uStart,
        float vStart, float uWidth, float vHeight
    ) {
        sprite.bindTexture();
        final float texW = sprite.getTexWidth();
        final float texH = sprite.getTexHeight();

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0f);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        RenderUtils.setGlColor(noiseColor, alpha);
        final float epsilon = 0.001f;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(uStart * texW + epsilon, vStart * texH + epsilon);
        GL11.glVertex2f(0f, 0f);
        GL11.glTexCoord2f(uStart * texW + epsilon, (vStart + vHeight) * texH - epsilon);
        GL11.glVertex2f(0f, renderH);
        GL11.glTexCoord2f((uStart + uWidth) * texW - epsilon, (vStart + vHeight) * texH - epsilon);
        GL11.glVertex2f(renderW, renderH);
        GL11.glTexCoord2f((uStart + uWidth) * texW - epsilon, vStart * texH + epsilon);
        GL11.glVertex2f(renderW, 0f);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}