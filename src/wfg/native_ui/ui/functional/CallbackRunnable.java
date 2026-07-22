package wfg.native_ui.ui.functional;

@FunctionalInterface
public interface CallbackRunnable<CallerType> {
    void run(CallerType caller);
}