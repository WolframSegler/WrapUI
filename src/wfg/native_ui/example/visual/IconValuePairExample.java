package wfg.native_ui.example.visual;

import static wfg.native_ui.util.UIConstants.*;

import java.util.Collections;
import java.util.List;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.core.UIBuildableAPI;
import wfg.native_ui.ui.visual.IconValuePairTp;
import wfg.native_ui.util.NativeUiUtils;
import wfg.native_ui.util.NativeUiUtils.AnchorType;

/**
 * This example contains three icon value pairs with tooltips.
 */
public final class IconValuePairExample extends UIContainer implements UIBuildableAPI {
    public IconValuePairExample(float w, float h) {
        super(w, h);

        buildUI();
    }

    @Override
    public void buildUI() {
        clearChildren();

        final int titleW = 220;
        final int entryW = 100;
        final int entryH = 32;

        // some data
        final List<Object> orders = Collections.emptyList();
        final int estimatedTime = 400;
        final int prodLines = 5;

        final IconValuePairTp ordersPair = new IconValuePairTp(entryW, entryH, "checklistIconId", orders.size(), true, null);
        final IconValuePairTp timePair = new IconValuePairTp(entryW, entryH, "stopwatchIconId", estimatedTime, true, null);
        final IconValuePairTp prodPair = new IconValuePairTp(entryW, entryH, "productionIconId", prodLines, true, null);

        add(ordersPair).inTL(hpad + titleW, hpad);
        add(timePair).inTL(hpad + titleW + entryW, hpad);
        add(prodPair).inTL(hpad + titleW + entryW*2, hpad);

        ordersPair.tooltip.builder = (tp, expanded) -> {
            tp.addTitle("Active Orders", base);
            tp.addPara("Number of ships on the production queue.", pad);
        };
        timePair.tooltip.builder = (tp, expanded) -> {
            tp.addTitle("Total Build Time", base);
            tp.addPara("Further details about the build time.", pad, highlight, Integer.toString(prodLines));
        };
        prodPair.tooltip.builder = (tp, expanded) -> {
            tp.addTitle("Assembly Lines", base);
            tp.addPara("Further details about the assembly lines.", pad, highlight, Integer.toString(prodLines));
        };

        ordersPair.tooltip.positioner = (tp, exp) -> NativeUiUtils.anchorPanel(tp, ordersPair, AnchorType.RightTop, hpad);
        timePair.tooltip.positioner = (tp, exp) -> NativeUiUtils.anchorPanel(tp, timePair, AnchorType.RightTop, hpad);
        prodPair.tooltip.positioner = (tp, exp) -> NativeUiUtils.anchorPanel(tp, prodPair, AnchorType.RightTop, hpad);
    }
}