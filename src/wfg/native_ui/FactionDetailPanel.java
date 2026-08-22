package wfg.native_ui;

import java.util.List;
import java.util.function.Consumer;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionSpecAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.loading.Description.Type;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class FactionDetailPanel extends CustomPanel implements Consumer<FactionSpecAPI> {
    private FactionSpecAPI currentFaction;

    public FactionDetailPanel(float width, float height) {
        super(width, height);

        showDefaultState();
    }

    private void showDefaultState() {
        clearChildren();

        /** Please avoid using the tooltip for a singular label. The tool is overqualified for the job. */
        final LabelAPI defaultLbl = Global.getSettings().createLabel("Select a faction from the list.", Fonts.DEFAULT_SMALL);
        /** Should stand in the middle. */
        add(defaultLbl).inMid();
    }

    @Override
    public void accept(FactionSpecAPI faction) {
        currentFaction = faction;
        buildUI();
    }

    private void buildUI() {
        clearChildren();
        if (currentFaction == null) {
            showDefaultState();
            return;
        }

        /** Vanilla uses these values quite often. I also do for visual consistency. */
        final float pad = 3f;
        final float opad = 10f;

        final float width = pos().getWidth();
        final float height = pos().getHeight();

        /** It is sort of justified here, because it provides a sprite wrapper using addImage. */
        final TooltipMakerAPI content = getTooltip(width, height, false);

        final String logoId = currentFaction.getLogo();
        if (logoId != null) {
            /** The logo is usually 410*256 px, so scale the width/height by that. */
            content.addImage(logoId, 200f, 125f, pad);
        }

        /** Do not use addTitle, as that would place the title at the top left, which is where the logo sits. */
        final LabelAPI nameLabel = content.addPara(currentFaction.getDisplayName(), currentFaction.getBaseUIColor(), pad);
        /** Remember that the width of the label is the width of the tooltip, so centering it at the middle works. */
        nameLabel.setAlignment(Alignment.MID);
        /** A section heading could have also been used. Personal taste. */

        /** Let me be honest, I have no idea if this is the correct way to retrieve the faction desc, but this is a GUI guide, so don't care. */
        final Description desc = Global.getSettings().getDescription(currentFaction.getId(), Type.FACTION);
        content.addPara(desc.getText1(), opad);

        /** Default is inBL(0f, 0f), which is fine, because the tooltip covers the entire panel. */
        add(content);
    }

    @Override public void renderBelow(float alpha) {}
    @Override public void render(float alpha) {}
    @Override public void advance(float delta) {}
    @Override public void processInput(List<InputEventAPI> events) {}
}