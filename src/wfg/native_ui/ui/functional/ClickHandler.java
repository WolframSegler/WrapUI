package wfg.native_ui.ui.functional;

@FunctionalInterface
public interface ClickHandler<SourceType> {
    void handle(SourceType source, boolean isLeftClick);
}