package wfg.native_ui.ui.functional;

import java.util.function.Supplier;

import wfg.native_ui.ui.container.DockPanel;

public class DockButton<T extends DockPanel> extends Button {
    private T dock;
    private final Supplier<T> dockFactory;

    public DockButton(int width, int height, String text, String font, Supplier<T> dockFactory) {
        super(width, height, text, font, null);
        this.dockFactory = dockFactory;
        onClicked = btn -> {
            if (dock == null) createDock();
            if (dock == null) return;
            
            if (dock.isOpen()) dock.close();
            else dock.open(true);
        };
        
        setQuickMode(true);
    }

    private void createDock() {
        dock = dockFactory.get();
        dock.removeWhenClosed = true;
        dock.onRemoved = d -> dock = null;
    }
}