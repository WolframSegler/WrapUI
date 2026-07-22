package wfg.native_ui.example.container;

import static wfg.native_ui.util.UIConstants.*;

import java.awt.Color;

import com.fs.starfarer.api.ui.Fonts;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.container.BaseContainer;
import wfg.native_ui.ui.core.UIBuildableAPI;
import wfg.native_ui.ui.widget.Button;

/**
 * In the following example, {@link BaseContainer} is used to draw a gray separator line between UI elements.
 */
public final class BaseContainerExample extends UIContainer implements UIBuildableAPI {
    public BaseContainerExample(float width, float height) {
        super(width, height);

        buildUI();
    }

    @Override
    public void buildUI() {
        final Button btn1 = new Button(100, 20, "Some Content", Fonts.DEFAULT_SMALL, null);
        add(btn1).inTL(0f, 0f);

        final BaseContainer separator = new BaseContainer(getWidth() - pad*2, 1f);
        add(separator).inTL(pad, 20 + hpad);
        separator.bg.color = Color.GRAY;

        final Button btn2 = new Button(100, 20, "More Content", Fonts.DEFAULT_SMALL, null);
        add(btn2).inBL(0f, 0f);
    }
}