package wfg.native_ui.example.widget;

import static wfg.native_ui.util.UIConstants.*;

import java.awt.Color;

import com.fs.starfarer.api.util.Misc;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.core.UIBuildableAPI;
import wfg.native_ui.ui.widget.Slider;

public final class SliderExample extends UIContainer implements UIBuildableAPI {
    private static final Color depositColor = new Color(90, 150, 110);

    public float value = 0f;
    public float credits = 10000f;

    public SliderExample(float w, float h) {
        super(w, h);

        buildUI();
    }

    @Override
    public void buildUI() {
        clearChildren();

        final float min = 100;
        final float max = 500;
        final float sliderW = 300;
        final float sliderH = 32;

        // some value slider
        final Slider slider = new Slider(null, min, max, sliderW - opad*2, sliderH);
        slider.customText = () -> {
            value = slider.getProgress(); // using the custom text setter as a listener.
            return String.format("%.1f", slider.getProgress());
        };
        slider.setProgress(value);
        add(slider).inTL(hpad, hpad);

        // credits deposit slider
        final Slider depositSlider = new Slider(
            null, 0f, credits, sliderW - opad*2, sliderH
        );
        depositSlider.setHighlightOnMouseover(true);
        depositSlider.setBarColor(depositColor);
        depositSlider.showValueOnly = true;
        depositSlider.customText = () -> Misc.getDGSCredits(depositSlider.getProgressInterpolated());
        add(depositSlider).inTL(hpad, hpad * 2 + sliderH);
    }
}