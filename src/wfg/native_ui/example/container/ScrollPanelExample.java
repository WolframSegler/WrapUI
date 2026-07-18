package wfg.native_ui.example.container;

import static wfg.native_ui.util.Globals.settings;
import static wfg.native_ui.util.UIConstants.*;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.LabelAPI;

import wfg.native_ui.internal.ui.core.UIContainer;
import wfg.native_ui.ui.container.ScrollPanel;
import wfg.native_ui.ui.container.ScrollPanel.ScrollType;
import wfg.native_ui.ui.core.UIBuildableAPI;

/**
 * In this example, widgets are created from a list of data and stacked horizontally.
 * Should the total width of the widgets exceed the height, the panel will use the scroll input to move the content panel. 
 */
public final class ScrollPanelExample extends UIContainer implements UIBuildableAPI {
    
    public ScrollPanelExample(float width, float height) {
        super(width, height);

        buildUI();
    }

    @Override
    public void buildUI() {
        clearChildren();

        final ScrollPanel scrollPanel = new ScrollPanel(getWidth(), getHeight());
        scrollPanel.scrollType = ScrollType.HORIZONTAL;
        add(scrollPanel).inBL(0f, 0f);

        final List<DataClass> data = getData();
        for (int i = 0; i < data.size(); i++) {
            scrollPanel.add(new WidgetClass(100f, 200f, data.get(i)))
                .inLMid(pad + i*(100f + pad));
        }
    }

    private final List<DataClass> getData() {
        final List<DataClass> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            final int val = i + 1;
            list.add(new DataClass(7 * val, 13 * val, "SomeData_" + Integer.toString(val)));
        }
        return list;
    }

    public static class DataClass {
        public int data1;
        public int data2;
        public String dataId;

        public DataClass(int data1, int data2, String dataId) {
            this.data1 = data1;
            this.data2 = data2;
            this.dataId = dataId;
        }
    }

    public static class WidgetClass extends UIContainer {

        public WidgetClass(float width, float height, DataClass data) {
            super(width, height);

            final LabelAPI title = settings.createLabel(data.dataId, Fonts.INSIGNIA_LARGE);
            title.autoSizeToWidth(width);
            title.setAlignment(Alignment.MID);
            add(title).inTL(0f, pad);

            final LabelAPI dataLbl = settings.createLabel(Integer.toString(data.data1) + " - " + Integer.toString(data.data2), Fonts.DEFAULT_SMALL);
            add(dataLbl).inBMid(opad);
        }
    }
}