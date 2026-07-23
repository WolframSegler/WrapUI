package wfg.native_ui.example.visual;

import static wfg.native_ui.util.UIConstants.*;
import static wfg.native_ui.util.Globals.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.LabelAPI;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.core.UIBuildableAPI;
import wfg.native_ui.ui.visual.PieChart;
import wfg.native_ui.ui.visual.PieChart.PieSlice;
import wfg.native_ui.util.NumFormat;

/**
 * The example provides a pie chart breakdown of the sector population.
 */
public final class PieChartExample extends UIContainer implements UIBuildableAPI {
    private static final int PIECHART_S = 250;
    private static final int TITLE_H = 35;

    public PieChartExample(float w, float h) {
        super(w, h);

        buildUI();
    }

    @Override
    public void buildUI() {
        clearChildren();

        final List<FactionAPI> factionList = Global.getSector().getAllFactions();
        final ArrayList<PieSlice> data = new ArrayList<>(factionList.size());
        final long globalPop = getGlobalPopulationCount();

        if (globalPop > 0l) {
            for (FactionAPI faction : factionList) {
                final long factionPop = getFactionPopulationCount(faction.getId());
                final float share = (float) (factionPop / (double) globalPop);
                if (share > 0f) {
                    data.add(new PieSlice(faction.getId(), faction.getBaseUIColor(), share));
                }
            }
        }
        Collections.sort(data, (a, b) -> Float.compare(b.fraction, a.fraction));

        final LabelAPI title = settings.createLabel("Sector Population", Fonts.ORBITRON_24AABOLD);
        title.setColor(base);
        title.setAlignment(Alignment.TMID);
        add(title).inTL(opad*2, opad*2).setSize(PIECHART_S, TITLE_H);

        final PieChart chart = new PieChart(PIECHART_S, PIECHART_S, data);
        add(chart).inTL(opad*2, opad*2 + TITLE_H + hpad);

        chart.tooltip.width = 360;
        chart.tooltip.builder = (tp, exp) -> {
            tp.addTitle("Sector Population Breakdown", base);
            
            tp.addPara("Shows the population of the sector with the share of each faction.", pad);

            tp.beginTable(
                base, dark, highlight, 20, true, true, new Object[] {
                    "faction", 200, "Population", 100
                }
            );

            for (PieSlice slice : data) {
                tp.addRow(new Object[] {
                    slice.color,
                    Global.getSector().getFaction(slice.uniqueID).getDisplayName(),
                    highlight,
                    NumFormat.engNotate(slice.fraction * globalPop)
                });
            }
            tp.addTable("", 0, opad);
        };
    }

    private static final long getGlobalPopulationCount() {
        long total = 0l;

        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            total += Math.pow(10d, market.getSize());
        }

        return total;
    }

    private static final long getFactionPopulationCount(String factionID) {
        long total = 0l;

        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (market.getFactionId().equals(factionID)) {
                total += Math.pow(10d, market.getSize());
            }
        }

        return total;
    }
}