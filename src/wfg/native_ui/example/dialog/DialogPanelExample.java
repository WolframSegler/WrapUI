package wfg.native_ui.example.dialog;

import static wfg.native_ui.util.Globals.settings;
import static wfg.native_ui.util.UIConstants.*;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.ui.LabelAPI;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.core.UIBuildableAPI;
import wfg.native_ui.ui.dialog.DialogPanel;
import wfg.native_ui.ui.visual.AbstractSpriteElement.SpriteElement;

/** 
 * In this example, the dialog is used for instant text display and subclassed for an additional UI element.
 */
public class DialogPanelExample extends UIContainer implements UIBuildableAPI {

    public DialogPanelExample(float width, float height) {
        super(width, height);

        buildUI();
    }

    @Override
    public void buildUI() {
        // Displaying text instantly.
        new DialogPanel(400, 100, null, "Some text", "dismiss")
            .show(0.3f, 0.3f);


        // Giving the first button the shortcut "G", in this case the "accept" button.
        final DialogPanel someDialog = new DialogPanel(400, 100, null, "Some important decision", "accept", "decline", "postpone");
        someDialog.setConfirmShortcut();
        someDialog.show(0.3f, 0.3f);


        // Subclass example
        final FactionAPI heg = Global.getSector().getFaction(Factions.HEGEMONY);
        final DialogPanel subclassDialog = new DialogPanel(400, 300, (code) -> {

            if (code == 0) { // accepted
                heg.adjustRelationship(Factions.PLAYER, 0.05f);
            } else if (code == 1) { // declined
                heg.adjustRelationship(Factions.PLAYER, -0.05f);
            }

        }, null, "accept", "decline") {

            @Override
            public void buildUI() {
                final SpriteElement crest = new SpriteElement(40, 40, heg.getCrest(), null, null);
                add(crest).inTL(0f, 0f);

                final LabelAPI title = settings.createLabel("Could you spare some food for me? Sincerely, " + heg.getDisplayNameWithArticle(), null);
                title.setHighlight(heg.getDisplayNameWithArticle());
                title.setHighlightColor(highlight);
                title.autoSizeToWidth(getWidth());
                add(title).belowLeft(crest, opad);      
            }
        };
        subclassDialog.setConfirmShortcut();
        subclassDialog.show(0.3f, 0.3f);
    }
}