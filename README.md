# WrapUI
A basic UI framework I created while working with Starsector's UI. Not feature-complete.  
Feel free to create forks or contribute. Bug reports are also appreciated.

# Features
- CustomPanel and CustomPanelPlugin for direct injection into vanilla UI hierarchies
- Extensive use of generics for early error prevention
- Global UI attachment points via `Attachments.java`
- Barebones UI state tracking using `UIState.java`
- Simple dialog wrapper for accessing the dialog panel instance
- Systems for tooltips and fader management to reduce boilerplate, with a more composition-oriented approach
-Basic number formatter for large numbers

# UI Elements
- Button implementation similar to vanilla, without using `ButtonAPI`
- Simple pie chart
- Carbon copy of the vanilla slider used in settings. The blue one.
- Sortable table similar to `UITable` in functionality, avoiding the obfuscated vanilla table
- `SpritePanel` with optional tooltip; essentially a `SpriteAPI` wrapper.

# Usage
- All panels that wish to use Plugins or Systems must extend `CustomPanel`.
- To access the actual `CustomPanelAPI` instance, the getPanel() method can be used.
- Do not forget to call getPlugin().init() inside the panel constructor.

# Possible Questions
- Why is it called Wrap UI?
    - Because `CustomPanel` is a wrapper for `CustomPanelAPI` and is itself not the actual panel.