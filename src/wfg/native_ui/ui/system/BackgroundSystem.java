package wfg.native_ui.ui.system;

import wfg.native_ui.ui.component.BackgroundComp;
import wfg.native_ui.ui.component.NativeComponents;
import wfg.native_ui.ui.core.UIEntityAPI;
import wfg.native_ui.util.RenderUtils;

public final class BackgroundSystem extends BaseSystem {

    private static final BackgroundSystem INSTANCE = new BackgroundSystem();
    public static BackgroundSystem get() { return INSTANCE; }
    private BackgroundSystem() {}

    @Override
    public void init(UIEntityAPI element) {
        element.comp().setIfNotPresent(NativeComponents.BACKGROUND, new BackgroundComp());
    }

    @Override
    public void renderBelow(final UIEntityAPI element, float alpha) {
        final BackgroundComp bg = element.comp().get(NativeComponents.BACKGROUND);
        if (!bg.enabled) return;

        final var pos = element.pos();

        final int x = (int) pos.getX() + bg.offset.x;
        final int y = (int) pos.getY() + bg.offset.y;
        final int w = (int) pos.getWidth() + bg.offset.w;
        final int h = (int) pos.getHeight() + bg.offset.h;

        RenderUtils.drawQuad(x, y, w, h, bg.color, bg.alpha * alpha, false);
    }
}