package wfg.wrap_ui.ui.dialogs;

import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.campaign.CampaignEngine;

public interface WrapDialogDelegate extends CustomDialogDelegate {
    /**
     * Allows the CustomDialogDelegate to access its InteractionDialog 
     */
    default void setInteractionDialog(InteractionDialogAPI a) {}

    /**
     * If created manually to display a CustomDialog, then it must also be dismissed manually.
     * This method lets The CustomDialog differenciate between the scenarios.
     */
    void setWasInteractionDialogCreated(boolean a);

    default void handleClosingForDialogCreated(InteractionDialogAPI dialog) {
        dialog.dismiss();
        final CampaignEngine engine = CampaignEngine.getInstance();
        engine.getCampaignUI().getCore().hideAbilityBar();
        engine.getCampaignUI().getCore().hideLogistics();
        engine.setPaused(true);
    }
}