package wfg.native_ui;

import java.util.List;

import com.fs.graphics.util.Fader;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;

import rolflectionlib.util.RolfLectionUtil;

public abstract class CustomPanel implements CustomUIPanelPlugin {
    private static final Object clearChildrenMethod;
    private static final Object getChildrenCopyMethod;
    private static final Object getChildrenNonCopyMethod;
    private static final Object getFaderMethod;
    private static final Object addToPositionMethod;
    private static final Object removeFromPositionMethod;
    private static final Object positionSetParentMethod;

    static {
        final UIPanelAPI panelIns = Global.getSettings().createCustom(0f, 0f, null);
        final Class<?> panelClazz = panelIns.getClass();
        final Class<?> posClazz = panelIns.getPosition().getClass();

        clearChildrenMethod = RolfLectionUtil.getMethod(
            "clearChildren", panelClazz);
        getChildrenCopyMethod = RolfLectionUtil.getMethod(
            "getChildrenCopy", panelClazz);
        getChildrenNonCopyMethod = RolfLectionUtil.getMethod(
            "getChildrenNonCopy", panelClazz);
        getFaderMethod = RolfLectionUtil.getMethod("getFader", panelClazz);
        addToPositionMethod = RolfLectionUtil.getMethod("add", posClazz, 1);
        removeFromPositionMethod = RolfLectionUtil.getMethod("remove", posClazz, 1);
        positionSetParentMethod = RolfLectionUtil.getMethod("setParent", posClazz, 1);
    }

    protected final CustomPanelAPI mPanel;

    public CustomPanel() {
        this(0f, 0f);
    }

    public CustomPanel(float width, float height) {
        mPanel = Global.getSettings().createCustom(width, height, this);
    }

    public void buttonPressed(Object buttonID) {}
    public void positionChanged(PositionAPI position) {}

    public CustomPanelAPI getPanel() { return mPanel; }
    public CustomPanelAPI panel() { return mPanel; }

    public PositionAPI getPos() { return mPanel.getPosition(); }
    public PositionAPI pos() { return mPanel.getPosition(); }

    public PositionAPI add(LabelAPI a) {
        return add((UIComponentAPI) a);
    }

    public TooltipMakerAPI getTooltip(float width, float height, boolean withScroller) {
        return mPanel.createUIElement(width, height, withScroller);
    }

    /** Note: the tooltip must be created using {@link #getTooltip}. */
    public PositionAPI add(TooltipMakerAPI a) {
        return mPanel.addUIElement(a);
    }

    public PositionAPI add(UIComponentAPI a) {
        mPanel.addComponent(a);

        return a.getPosition();
    }

    public PositionAPI add(CustomPanel a) {
        mPanel.addComponent(a.getPanel());

        return a.getPos();
    }

    public void remove(LabelAPI a) {
        remove((UIComponentAPI) a);
    }

    public void remove(UIComponentAPI a) {
        mPanel.removeComponent(a);
    }

    public void remove(CustomPanel a) {
        mPanel.removeComponent(a.getPanel());
    }

    public final Fader getPanelFader() {
        return getPanelFader(mPanel);
    }

    public PositionAPI addPositionOnly(UIComponentAPI comp) {
        return addPositionOnly(pos(), comp);
    }

    public PositionAPI removePositionOnly(UIComponentAPI comp) {
        return removePositionOnly(pos(), comp);
    }

    public void clearChildren() {
        clearChildren(mPanel);
    }

    public List<UIComponentAPI> getChildrenNonCopy() {
        return getChildrenNonCopy(mPanel);
    }

    public List<UIComponentAPI> getChildrenCopy() {
        return getChildrenCopy(mPanel);
    }

    public final void setSize(int width, int height) {
        pos().setSize(width, height);
    }

    public final void setWidth(int width) {
        pos().setSize(width, pos().getHeight());
    }

    public final void setHeight(int height) {
        pos().setSize(pos().getWidth(), height);
    }

    /** Forces a recompute of the position hierarchy by setting the size to its current value. */
    public final void posRecompute() {
        pos().setSize(pos().getWidth(), pos().getHeight());
    }

    @SuppressWarnings("unchecked")
    public static final List<UIComponentAPI> clearChildren(UIPanelAPI panel) {
        return (List<UIComponentAPI>) RolfLectionUtil.invokeMethodDirectly(clearChildrenMethod, panel);
    }

    @SuppressWarnings("unchecked")
    public static final List<UIComponentAPI> getChildrenNonCopy(UIPanelAPI panel) {
        return (List<UIComponentAPI>) RolfLectionUtil.invokeMethodDirectly(getChildrenNonCopyMethod, panel);
    }

    @SuppressWarnings("unchecked")
    public static final List<UIComponentAPI> getChildrenCopy(UIPanelAPI panel) {
        return (List<UIComponentAPI>) RolfLectionUtil.invokeMethodDirectly(getChildrenCopyMethod, panel);
    }

    public static final Fader getPanelFader(UIPanelAPI panel) {
        return (Fader) RolfLectionUtil.invokeMethodDirectly(getFaderMethod, panel);
    }

    public static final PositionAPI addPositionOnly(PositionAPI parent, UIComponentAPI comp) {
        final PositionAPI position = comp.getPosition();
        RolfLectionUtil.invokeMethodDirectly(positionSetParentMethod, position, parent);
        RolfLectionUtil.invokeMethodDirectly(addToPositionMethod, parent, position);
        return position;
    }

    public static final PositionAPI removePositionOnly(PositionAPI parent, UIComponentAPI comp) {
        final PositionAPI position = comp.getPosition();
        RolfLectionUtil.invokeMethodDirectly(positionSetParentMethod, position, (Object)null);
        RolfLectionUtil.invokeMethodDirectly(removeFromPositionMethod, parent, position);
        return position;
    }
}