package wfg.wrap_ui.internal.ui.dialogs;

import static wfg.wrap_ui.util.UIConstants.*;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.FaderUtil;

import wfg.wrap_ui.internal.ui.plugins.FoldingPanelPlugin;
import wfg.wrap_ui.internal.util.BorderRenderer;
import wfg.wrap_ui.internal.util.NoiseRenderer;
import wfg.wrap_ui.internal.util.PanelFillRenderer;
import wfg.wrap_ui.ui.Attachments;
import wfg.wrap_ui.ui.panels.CustomPanel;

public class FoldingPanel extends CustomPanel<FoldingPanelPlugin, FoldingPanel, UIPanelAPI> {
    public boolean renderBackground = true;
    public boolean transitionEnabled = true;
    public boolean isAlwaysScissor = false;
    public float borderThickness = 7f;
    public float noiseAlpha = 1f;

    private UIComponentAPI currentPanel;
    private UIComponentAPI nextPanel;
    private BorderRenderer borderRenderer;
    private PanelFillRenderer backgroundLayer;
    private PanelFillRenderer foregroundLayer;
    private NoiseRenderer noiseRenderer;
    private FaderUtil fader = new FaderUtil(1f, 0f);
    private int backgroundAlphaMin = 125;
    private int backgroundAlphaMax = 175;

    public FoldingPanel(UIPanelAPI parent, int width, int height, String borderPrefix,
        int borderThickness
    ) {
        super(parent, width, height, new FoldingPanelPlugin());
        getPlugin().init(this);
        this.borderThickness = borderThickness;
        borderRenderer = new BorderRenderer(borderPrefix, width, height);
        initializeBackground();
        noiseRenderer.fadeOut(0.5f);
    }

    public FoldingPanel(int width, int height, String borderPrefix, int borderThickness) {
        this(Attachments.getScreenPanel(), width, height, borderPrefix, borderThickness);
    }

    public FoldingPanel(int width, int height, String borderPrefix) {
        this(width, height, borderPrefix, 7);
    }

    public FoldingPanel(int width, int height) {
        this(width, height, "ui_border1", 7);
    }

    public FoldingPanel() {
        this(100, 100, "ui_border1", 7);
    }

    public void createPanel() {}

    public void setBorder(String prefix) {
        borderRenderer = new BorderRenderer(prefix, getPos().getWidth(), getPos().getHeight());
    }

    public void setBackgroundAlpha(int min, int max) {
        backgroundAlphaMin = min;
        backgroundAlphaMax = max;
        initializeBackground();
    }

    private void initializeBackground() {
        final PositionAPI pos = getPos();
        final SettingsAPI settings = Global.getSettings();
        backgroundLayer = new PanelFillRenderer(settings.getSprite("ui", "scanline11"),
            pos.getWidth() - pad * 2, pos.getHeight() - pad * 2
        );
        backgroundLayer.setColors(
            new Color(0, 0, 0, 125), new Color(0, 0, 0, backgroundAlphaMin));
        backgroundLayer.setOverlayColors(
            new Color(0, 0, 0, 175), new Color(0, 0, 0, backgroundAlphaMax));

        backgroundLayer.useAdditiveBlend = false;
        backgroundLayer.edgeSize = 7f;

        foregroundLayer = new PanelFillRenderer(settings.getSprite("ui", "scanline11"),
            pos.getWidth() - pad * 2, pos.getHeight() - pad * 2
        );
        foregroundLayer.useAdditiveBlend = true;
        foregroundLayer.setColors(new Color(10, 38, 44, 0), new Color(10, 38, 44, 0));
        noiseRenderer = new NoiseRenderer(settings.getSprite("ui", "noise"),
            pos.getWidth() - pad * 2, pos.getHeight() - pad * 2
        );
    }

    public void foldOut(float var1) {
        fader.setDurationIn(var1);
        fader.fadeIn();
    }

    public void forceFoldIn() {
        fader.forceOut();
    }

    public void forceFoldOut() {
        fader.forceIn();
    }

    public void foldIn(float var1) {
        fader.setDurationOut(var1);
        fader.fadeOut();
    }

    /**
     * The component must be created as a child of {@link FoldingPanel}
     */
    public void setNext(UIComponentAPI comp) {
        if (comp == null) return;
        noiseRenderer.fadeIn(0.2f, 0.2f);
        if (!transitionEnabled) noiseRenderer.fader.forceIn();

        nextPanel = comp;
        final float w = getContentWidth();
        final float h = getContentHeight();
        remove(currentPanel);
        comp.getPosition().setSize(w, h);
        addPositionOnly(comp).inTL(borderThickness, borderThickness);
        if (!transitionEnabled) {
            currentPanel = nextPanel;
            nextPanel = null;
            noiseRenderer.fader.forceOut();
        }
    }

    public PositionAPI setSize(float width, float height) {
        if (currentPanel != null) {
            currentPanel.getPosition().setSize(
                width - borderThickness * 2f, height - borderThickness * 2f
            );
        }

        final PositionAPI pos = getPos().setSize(width, height);
        initializeBackground();
        return pos;
    }

    public float getContentWidth() {
        return getPos().getWidth() - borderThickness * 2f;
    }

    public float getContentHeight() {
        return getPos().getHeight() - borderThickness * 2f;
    }

    public void flickerNoise(float var1, float var2) {
        noiseRenderer.fadeIn(var1, var2);
    }

    public void advanceImpl(float var1) {
        if (nextPanel != null && noiseRenderer.isMaxBrightness()) {
            currentPanel = nextPanel;
            nextPanel = null;
            if (!transitionEnabled) {
                noiseRenderer.fader.forceOut();
            }
        }

        if (fader.getBrightness() != 0.0F || !fader.isIdle()) {
            backgroundLayer.advance(var1);
            foregroundLayer.advance(var1);
            noiseRenderer.advance(var1);
            fader.advance(var1);
            if (currentPanel != null) {
                currentPanel.advance(var1);
            }
        }
    }

    public FaderUtil getFanOut() {
        return fader;
    }

    public void processInputImpl(List<InputEventAPI> events) {
        if (currentPanel != null && fader.getBrightness() >= 0.75f && nextPanel == null) {
            currentPanel.processInput(events);
        }
    }

    public void renderImpl(float alphaMult) {
        if (fader.getBrightness() == 0f && fader.isIdle()) return;

        final PositionAPI pos = getPos();
        final float brightness = fader.getBrightness();
        final float heightScale = Math.min(1f, brightness / 0.75f);
        final float transitionAlpha = Math.max(0f, (brightness - 0.75f) / 0.25f);
        final float borderAlphaFactor = Math.min(1f, brightness / 0.25f);
        final float panelWidth = pos.getWidth();
        final float panelHeight = pos.getHeight() * heightScale;

        final float x = pos.getX();
        final float y = pos.getY() + pos.getHeight() / 2f - panelHeight / 2f;
        if (renderBackground) {
            borderRenderer.setSize(panelWidth, panelHeight);
            borderRenderer.render(x, y, alphaMult * borderAlphaFactor);
        }

        if (brightness != 1f || isAlwaysScissor) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            
            final float scale = Global.getSettings().getScreenScaleMult();
            final int scissorX = (int) (x + borderThickness * scale);
            final int scissorY = (int) (y + borderThickness * scale);
            final int scissorW = (int) ((panelWidth - borderThickness * 2f) * scale);
            final int scissorH = (int) ((panelHeight - borderThickness * 2f) * scale);
            GL11.glScissor(scissorX, scissorY, scissorW, scissorH);
        }

        if (renderBackground) {
            backgroundLayer.renderVerticalGradient(pos.getX() + pad, pos.getY() + pad,
                alphaMult * borderAlphaFactor
            );
        }

        if (currentPanel != null) {
            if (transitionEnabled && renderBackground) {
            currentPanel.render(transitionAlpha * alphaMult * (1f - noiseRenderer.getBrightness()));
            } else {
            currentPanel.render(transitionAlpha * alphaMult);
            }
        }

        if (renderBackground) {
            foregroundLayer.renderVerticalGradient(pos.getX() + pad, pos.getY() + pad, 
                alphaMult * borderAlphaFactor
            );
            noiseRenderer.render(pos.getX() + pad, pos.getY() + pad,
                alphaMult * noiseAlpha
            );
        }

        if (brightness != 1f || isAlwaysScissor) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    public UIComponentAPI getCurr() {
        return currentPanel;
    }

    public UIComponentAPI getNext() {
        return nextPanel;
    }
}