package wfg.native_ui.example.visual;

import static wfg.native_ui.util.Globals.settings;
import static wfg.native_ui.util.UIConstants.*;

import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.ComponentFactory;
import wfg.native_ui.ui.core.UIBuildableAPI;
import wfg.native_ui.ui.visual.TextWrapper;
import wfg.native_ui.util.NativeUiUtils;
import wfg.native_ui.util.NativeUiUtils.AnchorType;
import wfg.native_ui.util.NumFormat;

/**
 * This class provides an example for how {@link TextWrapper} is supposed to be subclassed.
 */
public final class TextWrapperExample extends UIContainer implements UIBuildableAPI {
    private static final int LABEL_W = 150;
    private static final int LABEL_H = 50;

    public TextWrapperExample(float w, float h) {
        super(w, h);

        buildUI();
    }

    @Override
    public void buildUI() {
        final CommoditySpecAPI com = settings.getCommoditySpec(Commodities.SUPPLIES);

        final TextWrapper textPanel1 = new TextWrapper(LABEL_W, LABEL_H) {

            public void buildUI() {
                final long value = Math.round(Math.random() * 7000d); // some data
                final String txt = "Global Production";
                final String valueTxt = value < 1 ? "---" : NumFormat.engNotate(value);

                ComponentFactory.addCaptionValueBlock(this, txt, valueTxt, base, LABEL_W);

                tooltip.width = 460f;
                tooltip.builder = (tp, exp) -> {
                    tp.addPara("The combined daily output of %1$s across all colonies and the informals in the Sector. Represents active industrial and informal production, excluding existing stockpiles.",
                        pad, highlight, com.getName()
                    );
                };
            }
        };

        add(textPanel1).inTL(0f, 0f);

        final TextWrapper textPanel2 = new TextWrapper(170, 0) {
            @Override
            public void buildUI() {
                final double value = Math.round(Math.random() * 3000d); // some data
                final String valueTxt = value < 1d ? "---" :  NumFormat.engNotate(value);

                ComponentFactory.addCaptionValueBlock(
                    this, "Global Exports",
                    valueTxt, base
                );

                tooltip.width = 460f;
                tooltip.builder = (tp, exp) -> {
                    tp.addPara(String.format("Shows the percentage of total global and informal exports controlled by each faction and the informal sector. In-faction trade is excluded from this total.", com.getName()), pad);
                };
                tooltip.positioner = (tp, exp) -> {
                    NativeUiUtils.anchorPanel(tp, this, AnchorType.RightTop, opad);
                };
            }
        };

        add(textPanel2).inTL(0f, 50f);
    }
}