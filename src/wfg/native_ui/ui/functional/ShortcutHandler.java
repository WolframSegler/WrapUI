package wfg.native_ui.ui.functional;

import com.fs.starfarer.api.input.InputEventAPI;

@FunctionalInterface
public interface ShortcutHandler<CallerType> {
    /**
     * @param event nullable
     */
    void run(CallerType caller, InputEventAPI event);
}