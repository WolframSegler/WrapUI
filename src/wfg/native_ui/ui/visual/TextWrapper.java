package wfg.native_ui.ui.visual;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.LabelAPI;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.component.AudioFeedbackComp;
import wfg.native_ui.ui.component.NativeComponents;
import wfg.native_ui.ui.component.TooltipComp;
import wfg.native_ui.ui.core.UIBuildableAPI;
import wfg.native_ui.ui.core.UIElementFlags.HasAudioFeedback;
import wfg.native_ui.ui.core.UIElementFlags.HasTooltip;

/**
 * A text wrapper with tooltip, audio feedback, and UI context support.
 *
 * <p>This element is intended to be subclassed anonymously for ad-hoc UI creation. 
 * Subclasses override {@link #buildUI()} to define UI elements and layout. 
 * Internal fields (checkbox, labels, text positions) are exposed publicly so that 
 * external code can read element state from the anonymous subclass.</p>
 *
 * <p>Usage example:
 * <pre>{@code
 * TextPanel panel = new TextPanel(300, 50) {
 *     @Override
 *     public void buildUI() {
 *         mCheckbox = addCheckbox("Enable", 10, 10);
 *         label1 = addLabel("Hello", 20, 10);
 *         textX1 = 0; textY1 = 0; textW1 = 100; textH1 = 20;
 *     }
 * };
 *
 * // External code can now inspect the fields:
 * if (panel.m_checkbox.isChecked()) { ... }
 *
 * // Tooltip setup:
 * panel.tooltip.builder = (tooltip, expanded) -> tooltip.addPara("Example text", 3f);
 * panel.tooltip.positioner = (tooltip, expanded) -> NativeUiUtils.anchorPanel(
 *     tooltip, anchorPanel, AnchorType.LeftTop, pad
 * );
 * }</pre>
 */
public abstract class TextWrapper extends UIContainer implements
    HasTooltip, HasAudioFeedback, UIBuildableAPI
{
    public final TooltipComp tooltip = comp().get(NativeComponents.TOOLTIP);
    public final AudioFeedbackComp audio = comp().get(NativeComponents.AUDIO_FEEDBACK);

    // Shared state for anonymous subclasses to modify.
    public ButtonAPI mCheckbox;
    public float textX1, textX2 = 0;
    public float textY1, textY2 = 0;
    public float textW1, textH1 = 0;
    public LabelAPI label1, label2 = null;

    public TextWrapper(int width, int height) {
        super(width, height);

        buildUI();
    }
}