# Using NativeUI in Your Mod - AI Rules

# Setup & Initialization
Declare the dependency in `mod_info.json` (for the mod loader), version might change:

```json
"dependencies": [
    {
        "id": "wfg_native_ui",
        "name": "NativeUI",
        "version": "0.5.0"
    }
]
```

The IDE needs to know the path to `native_ui.jar`. Assuming the parent dir of the root is `mods/`, here is the `pom.xml` entry:
```xml
<dependency>
    <groupId>wfg</groupId>
    <artifactId>native_ui</artifactId>
    <version>1.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/../NativeUI/jars/main/native_ui.jar</systemPath>
</dependency>
```

Source files are under `NativeUI/src/wfg/native_ui/`.

# Style Rules

## Number Suffixes
- **int** – no suffix: `42`
- **long** – always lowercase `l` suffix: `1234567l`
- **float** – always lowercase `f` suffix, whole numbers without `.0`: `6f` **not** `6.0f`
- **double** – always lowercase `d` suffix, whole numbers without `.0`: `7d` **not** `7.0d`

## Early Returns
Return immediately when a precondition fails. Keep the happy path unindented.

```java
public final void process(final Player player) {
    if (player == null) return;
    if (!player.isAlive()) return;
    doSomething();
}
```

## Single-Line Conditionals
If the body is short, omit braces and keep it on one line:

```java
if (value == 0) return;
if (list.isEmpty()) throw new IllegalStateException();
```

## `final` on Everything Possible
- **Methods**: always mark as `final`, even `private` or `static`.
- **Classes**: declare `final` unless explicitly designed for extension (utility classes are `final`).
- **Local variables**: mark as `final` whenever possible (except loop indices). Refactor to allow single assignment. Use `switch` expressions or `Math.clamp()` to avoid mutable intermediates.

```java
// multiple assignment, wrong
int clamped = rawValue;
if (rawValue < min) clamped = min;
else if (rawValue > max) clamped = max;

// single assignment, correct
final int clamped = Math.clamp(rawValue, min, max);


// eager default, wrong
String display = "Unknown";
if (state == ACTIVE) display = "Active";

// final from branch, correct
final String display;
if (state == ACTIVE) display = "Active";
else display = "Unknown";

// switch expression, correct (preferred)
final String display = switch (state) {
    case ACTIVE -> "Active";
    default -> "Unknown";
};
```

## Constants
- Extract reused values into `private static final` fields at the top of the class.
- Use `UPPER_SNAKE_CASE`: `BG_COLOR`, `MAX_HEALTH`.
- Compute compile-time constants (`static final` strings, math) in the field declaration.

## Naming
- **Members**: `camelCase`. Use prefixes: `m` for instance, `s` for static, `g` for global (e.g., `mPanel`, `gPanel`, `sPanel`). No underscore after prefix.
- **Constants**: `UPPER_SNAKE_CASE`.

## Encapsulation
Make fields `public` unless they need side-effect logic. **No** trivial getters/setters.

## Lambdas
Always use lambdas over anonymous classes:
```java
list.forEach(item -> process(item));
```

## Switch Expressions
Prefer `switch` expressions over `if-else` chains for value assignments:
```java
final String name = switch (type) {
    case A -> "Alpha";
    case B -> "Beta";
    default -> "Unknown";
};
```

## DRY
Extract reused logic into reusable `static final` methods.

## Modifier Order
Follow this exact sequence: `visibility` `static` `final` `abstract` `volatile` `synchronized`
```java
private static final int TIMEOUT = 5000;
public final synchronized void reset() { … }
```

## Method Header Formatting
- Opening brace on same line as closing parenthesis.
- For long signatures: break after `(`, align parameters on next line, and keep `)` `{` together.
```java
public final void process(Player player) {
    ...
}

public final void buildUI(
    Container parent,
    String title,
    boolean resizable,
    boolean addOutline,
    int outlineThickness
) {
    ...
}
```

- Never put brace on its own line.
- Never leave a closing parenthesis orphaned at the end of the last parameter line.

## UIContants
Always import all constants statically:
```java
import static wfg.native_ui.util.UIConstants.*;
```
Then use the constants directly by name—no qualification.

### Spacing (mandatory)
Never hardcode 3, 5, or 10 for layout; always use these constants.
- `pad` (3) - tight spacing, often used for paragraphs or dense grids.
- `hpad` (5) – half of opad. Used when doing layout calculations.
- `opad` (10) – outer section padding. Used for inter-panel padding and significant gaps.

For semantic correctness, do `n * pad` and similar when layouting:
```java
public void buildUI() {
    final int panelSize = 37;
    final UIElement elem1 = new UIElement(panelSize, panelSize);
    final UIElement elem2 = new UIElement(panelSize, panelSize);
    final UIElement elem3 = new UIElement(panelSize, panelSize);

    add(elem1).inTL(pad, pad);
    add(elem2).inTL(pad, 2 * pad + panelSize);
    add(elem3).inTL(pad, 3 * pad + 2 * panelSize);
}
```

### Button defaults
- `BUTTON_W` and `BUTTON_H` are suggested defaults. Use them if no explicit size is specified by the user.

### Colors
Always use the precomputed color constants (if present). Do not call `Misc.getTextColor()` etc. directly in UI code:
- `text_color`, `highlight`, `positive`, `negative`, `base`, `dark`, `grid`, `bright`, `gray`, `glowHighlight`, `btnTxtColor`, `btnBgColorDark`
- `bgAlpha` (0.85f) – standard background opacity.

### Screen dimensions & scale
- `screenW`, `screenH` are the current screen size (pixels).

### Not exhaustive
- The class contains additional constants (e.g.,`UI_BORDER_1–4`). If you need a value that isn't listed here, check the UIConstants source before hardcoding anything.

## NumFormat

Use the `NumFormat` utility class for all number formatting. Import the class normally:
```java
import wfg.native_ui.NumFormat;
```

Then call the methods directly on the class:

- `NumFormat.engNotate(long)` — engineer notation (3 significant digits) for large positive numbers:
  - `924` → `"924"`
  - `9245` → `"9.25K"`
  - `79245` → `"79.2K"`
  - `1024000000` → `"1.02B"`
  Automatically prepends `MINUS` ("−") for negative values.

- `NumFormat.formatCredit(long)` — comma‑grouped credit string with credit symbol (`¢`), e.g. `"1,234¢"`.
  - `NumFormat.formatCreditAbs(long)` for absolute value.

- `NumFormat.reverseEngNotate(float)` — readable multiplier display when value is barely above 1 (tiny deviations):
  - e.g., `1.002f` → `"1.0m"`.

- `NumFormat.formatMagnitudeAware(double)` — adaptive precision: small values get precise formatting, large values use engineer notation.

- `NumFormat.formatAdaptivePrecision(double)` — 1‑2 decimal places with rounding.

- `NumFormat.MINUS` — the correct minus sign character (`"−"`). Use this instead of `"-"` when you need to display a negative sign manually.

**Never** write your own number‑to‑string logic, delegate to `NumFormat`.