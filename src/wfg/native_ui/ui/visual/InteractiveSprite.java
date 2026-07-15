package wfg.native_ui.ui.visual;

import static wfg.native_ui.util.Globals.settings;

import java.awt.Color;

import com.fs.starfarer.api.graphics.SpriteAPI;

import wfg.native_ui.ui.component.AudioFeedbackComp;
import wfg.native_ui.ui.component.HoverGlowComp;
import wfg.native_ui.ui.component.NativeComponents;
import wfg.native_ui.ui.component.TooltipComp;
import wfg.native_ui.ui.core.UIElementFlags.HasAudioFeedback;
import wfg.native_ui.ui.core.UIElementFlags.HasHoverGlow;
import wfg.native_ui.ui.core.UIElementFlags.HasTooltip;

/**
 * An {@link AbstractSpriteElement} with tooltip, hover glow, and audio feedback support.
 *
 * <p>Usage example:
 * <pre>{@code
 * InteractiveSprite panel = new InteractiveSprite(64, 64, "iconPath", null, null);
 * panel.tooltip.builder = (tooltip, expanded) -> {
 *     tooltip.addPara("...", pad);
 * };
 * panel.tooltip.positioner = (tooltip, expanded) -> {
 *     // default if not overridden is NativeUiUtils.mouseCornerPos(tooltip)
 *     NativeUiUtils.anchorPanel(tooltip, anchor, AnchorType.LeftTop, pad);
 * };
 * }</pre>
 */
public class InteractiveSprite extends AbstractSpriteElement<InteractiveSprite>
    implements HasTooltip, HasHoverGlow, HasAudioFeedback
{
    public final TooltipComp tooltip = comp().get(NativeComponents.TOOLTIP);
    public final HoverGlowComp glow = comp().get(NativeComponents.HOVER_GLOW);
    public final AudioFeedbackComp audio = comp().get(NativeComponents.AUDIO_FEEDBACK);

    public InteractiveSprite(float width, float height, String spriteID,
        Color color, Color fillColor
    ) {
        this(width, height, settings.getSprite(spriteID), color, fillColor);
    }

    public InteractiveSprite(float width, float height, SpriteAPI sprite,
        Color color, Color fillColor
    ) {
        super(width, height, sprite, color, fillColor);

        glow.additiveSprite = mSprite;
    }
}