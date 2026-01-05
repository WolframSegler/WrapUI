package wfg.wrap_ui.ui;

import java.util.Collections;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.campaign.CampaignEngine;
import com.fs.starfarer.campaign.CampaignState;
import com.fs.starfarer.combat.CombatState;
import com.fs.starfarer.title.TitleScreenState;
import com.fs.state.AppDriver;

import rolflectionlib.util.RolfLectionUtil;
// import com.fs.starfarer.ui.oo0O;
/**
 * Utility class providing access points to UI elements across different game contexts:
 * campaign, interaction, and combat. Allows retrieving core panels, tab panels,
 * dialogs, and overlays depending on the current mode. Is not null-safe.
 */
public class Attachments {

    private static final Object getWarRoomObject;
    private static Object setParentMethod;
    
    static {
        getWarRoomObject = RolfLectionUtil.getMethod("getWarroom",
            CombatState.class);
    }

    /**
     * Must be interacting with an entity.
     */
    public static final InteractionDialogAPI getInteractionDialog() {
        return Global.getSector().getCampaignUI().getCurrentInteractionDialog();
    }

    /**
     * Must be in campaign mode.
     */
    public static final InteractionDialogAPI createInteractionDialog() {
        return createInteractionDialog(Global.getSector().getPlayerFleet());
    }

    /**
     * Must be in campaign mode.
     */
    public static final InteractionDialogAPI createInteractionDialog(UIPanelAPI parent) {
        return createInteractionDialog(parent, Global.getSector().getPlayerFleet());
    }

    /**
     * Must be in campaign mode.
     */
    public static final InteractionDialogAPI createInteractionDialog(SectorEntityToken target) {
        return createInteractionDialog(getCoreUI(), target);
    }

    /**
     * Must be in campaign mode.
     */
    public static final InteractionDialogAPI createInteractionDialog(
        UIPanelAPI parent, SectorEntityToken target
    ) {
        final CampaignState state = getCampaignState();
        final InteractionDialogPlugin plugin = new InteractionDialogPlugin() {
            public void init(InteractionDialogAPI dialog) {
                if (setParentMethod == null) {
                    setParentMethod = RolfLectionUtil.getMethodFromSuperClass(
                        "setParent", dialog.getClass());
                }
                // The default parent is undesirable
                RolfLectionUtil.invokeMethodDirectly(setParentMethod,
                    dialog, parent);
                dialog.setPromptText("");
            }
            public void optionSelected(String optionText, Object optionData) {}
            public void optionMousedOver(String optionText, Object optionData) {}
            public void advance(float amount) {}
            public void backFromEngagement(EngagementResultAPI result) {}
            public Object getContext() { return null;}
            public Map<String, MemoryAPI> getMemoryMap() { return Collections.emptyMap();}
        };
        state.setDialogType(null);
        state.showInteractionDialog(plugin, target);
        CampaignEngine.getInstance().getCampaignUI().getCore().getFader().fadeIn();
        return state.getCurrentInteractionDialog();
    }

    /**
     * Returns the core UI panel of the current interaction.
     * Must be interacting with an entity.
     */
    public static final UIPanelAPI getInteractionCoreUI() {
        if (getCampaignState().getEncounterDialog() == null) return null;
        return getCampaignState().getEncounterDialog().getCoreUI();
    }

    /**
     * Must be in campaign mode. Retrieves campaign core UI
     */
    public static final UIPanelAPI getCampaignCoreUI() {
        return CampaignEngine.getInstance().getCampaignUI().getCore();
    }

    /**
     * Works in-general in campaign mode.
     */
    public static final UIPanelAPI getCoreUI() {
        return getInteractionCoreUI() == null ? getCampaignCoreUI() : getInteractionCoreUI();
    }

    /**
     * Works in-general.
     */
    public static final UIPanelAPI getScreenPanel() {
        switch (Global.getSettings().getCurrentState()) {
            case CAMPAIGN: return getCoreUI();
            case COMBAT: getCombatScreenPanel();
            case TITLE: default: return getTitleScreenPanel();
        }
    }

    /**
     * Returns the panel for the current tab (FLEET, CHARACTER, COMMAND etc.) within an interaction.
     * Must be interacting with an entity.
     */
    public static final UIPanelAPI getInteractionCurrentTab() {
        if (getCampaignState().getEncounterDialog() == null) return null;
        if (getCampaignState().getEncounterDialog().getCoreUI() == null) return null;
        return getCampaignState().getEncounterDialog().getCoreUI().getCurrentTab();
    }

    /**
     * Returns the panel for the current tab (FLEET, CHARACTER, COMMAND etc.) in the campaign screen.
     * Must be in campaign mode.
     */
    public static final UIPanelAPI getCampaignCurrentTab() {
        return CampaignEngine.getInstance().getCampaignUI().getCore().getCurrentTab();
    }

    /**
     * Works in-general
     */
    public static final UIPanelAPI getCurrentTab() {
        return getInteractionCurrentTab() == null ? getCampaignCurrentTab() : getInteractionCurrentTab();
    }

    /**
     * Also known as {@code getDialogParent()}.
     * Must be in campaign mode.
     */
    public static final UIPanelAPI getCampaignScreenPanel() {
        return CampaignEngine.getInstance().getCampaignUI().getDialogParent();
    }

    /**
     * Must be in combat mode.
     */
    public static final UIPanelAPI getCombatScreenPanel() {
        return getCombatState().getWidgetPanel();
    }

    /**
     * Must be in combat mode.
     */
    public static final UIPanelAPI getTutorialOverlay() {
        return getCombatState().getTutorialOverlay();
    }

    /**
     * Must be in combat mode.
     */
    public static final UIPanelAPI getWarroomPanel() {
        return (UIPanelAPI) RolfLectionUtil.invokeMethodDirectly(
            getWarRoomObject, getCombatState());
    }

    /**
     * Must be in Title mode.
     */
    public static final UIPanelAPI getTitleScreenPanel() {
        return (UIPanelAPI) getTitleState().getScreenPanel();
    }


    /**
     * @return The instance of CampaignState if the currentState is Campaign state. Fails otherwise.
     */
    public static final CampaignState getCampaignState() {
        return ((CampaignState)AppDriver.getInstance().getCurrentState());
    }

    /**
     * @return The instance of CombatState if the current state is CombatState. Fails otherwise.
     */
    public static final CombatState getCombatState() {
        return ((CombatState)AppDriver.getInstance().getCurrentState());
    }

    /**
     * @return The instance of TitleScreenState if the current state is TitleState. Fails otherwise.
     */
    public static final TitleScreenState getTitleState() {
        return ((TitleScreenState)AppDriver.getInstance().getCurrentState());
    }
}