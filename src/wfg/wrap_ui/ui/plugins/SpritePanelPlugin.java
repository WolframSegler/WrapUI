package wfg.wrap_ui.ui.plugins;

import com.fs.starfarer.api.ui.PositionAPI;

import wfg.wrap_ui.ui.panels.SpritePanel;
import wfg.wrap_ui.util.RenderUtils;

public class SpritePanelPlugin<
    PanelType extends SpritePanel<PanelType>
> extends CustomPanelPlugin<PanelType, SpritePanelPlugin<PanelType>> {

    private boolean isDrawFilledQuad = false;

    @Override
    public void init(PanelType panel) {
        super.init(panel);
        if (m_panel.fillColor != null) {
            isDrawFilledQuad = true;
        }
    }

    public void setDrawFilledQuad(boolean a) {
        isDrawFilledQuad = a;
    }

    @Override
    public void renderBelow(float alphaMult) {
        super.renderBelow(alphaMult);
        if (m_panel.m_sprite == null) {
            return;
        }

        if (m_panel.color != null) {
            m_panel.m_sprite.setColor(m_panel.color);
        }

        final PositionAPI pos = m_panel.getPos();
        final float x = pos.getX();
        final float y = pos.getY();
        final float w = pos.getWidth();
        final float h = pos.getHeight();

        if (isDrawFilledQuad && m_panel.fillColor != null) {
            m_panel.m_sprite.setColor(m_panel.fillColor);
            RenderUtils.drawQuad(x, y, w, h, m_panel.fillColor, alphaMult, false);
        }

        if (m_panel.drawTexOutline && m_panel.texOutlineColor != null) {
            RenderUtils.drawSpriteOutline(
                m_panel.m_sprite, m_panel.texOutlineColor, x, y, w, h, alphaMult, 2
            );
        }

        m_panel.m_sprite.setSize(w, h);
        m_panel.m_sprite.render(x, y);
    }
}