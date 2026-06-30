## Number Suffixes
- **int** – no suffix: `42`
- **long** – always lowercase `l` suffix: `1234567l`
- **float** – always lowercase `f` suffix, whole numbers without `.0`: `6f` **not** `6.0f`
- **double** – always lowercase `d` suffix, whole numbers without `.0`: `7d` **not** `7.0d`

## Conditionals & Early Returns
Prefer returning early when a condition is **not** met. This avoids unnecessary nesting and keeps the happy path flat.

```java
// ❌ Avoid
public void process(Player player) {
    if (player != null) {
        if (player.isAlive()) {
            doSomething();
        }
    }
}

// ✅ Good
public final void process(final Player player) {
    if (player == null) return;
    if (!player.isAlive()) return;
    doSomething();
}
```

## Single-Line Conditionals
If the body is short and fits on one line, place it on the same line without braces.

```java
if (value == 0) return;
if (list.isEmpty()) throw new IllegalStateException();
```

## Explicit `final` on Methods
Even when a method is already implicitly `final` (`private` or `static`), mark it `final` for readability.

```java
private final void helper() { … }
public static final int calculate() { … }
```

## Constants
Values used multiple times inside a method should be extracted to `private static final` fields at the top of the class.

```java
private static final int PADDING = 8;
private static final int CONTAINER_WIDTH = 240;
```

## Switch over if-else
Prefer `switch` statements (or switch expressions) wherever possible.

```java
// ❌
String name;
if (type == A) {
    name = "Alpha";
} else if (type == B) {
    name = "Beta";
} else {
    name = "Unknown";
}

// ✅
final String name = switch (type) {
    case A -> "Alpha";
    case B -> "Beta";
    default -> "Unknown";
};
```

## Naming Conventions
- **Members** use camelCase. A prefix may indicate scope: `mPanel` (member), `gPanel` (global), `sPanel` (static). No dash after the prefix (`mPanel`, not `m_Panel`).
- **Constants** (`private static final`) use UPPER_SNAKE_CASE: `MAX_HEALTH`, `DEFAULT_COLOR`.

## Encapsulation (Setters / Getters)
If a field has no side-effect logic, make it `public` instead of adding trivial getters/setters.
Having getters/setters for future flexibility is acceptable but not preferred.

```java
// ✅ Preferred
public int health;
```

## Lambdas over Anonymous Classes
Always use lambdas and functional interfaces instead of anonymous classes (if applicable).

```java
list.forEach(item -> process(item));
```

## Compile-Time Computations
Anything that can be computed at compile time must be computed at compile time (`static final` constants, string concatenation of constants).

## DRY with Static Methods
Code used in multiple places should be extracted into reusable `static` methods.

## Final Locals and Restructuring
- Mark local variables `final` whenever possible (exceptions: loop indices like `i`).
- Restructure code to allow `final`. Use `switch` expressions or ternary operators to initialise a variable with a single assignment.
- If a variable must be set inside branches, refactor to a single assignment so it can be declared `final`.

```java
// ❌ Avoid: manual bound checking prevents final
int clamped = rawValue;
if (rawValue < min) {
    clamped = min;
} else if (rawValue > max) {
    clamped = max;
}

// ✅ Using clamp for a final variable
final int clamped = Math.clamp(rawValue, min, max);

// ❌ Avoid: if-else chain with eager initialization prevents final
String display = "Unknown";
if (state == ACTIVE) {
    display = "Active";
} else if (state == PAUSED) {
    display = "Paused";
}

// ✅ switch expression with final assignment
final String display = switch (state) {
    case ACTIVE -> "Active";
    case PAUSED -> "Paused";
    default -> "Unknown";
};

// ✅ if-else chain without eager initialization
final String display;
if (state == ACTIVE) {
    display = "Active";
} else if (state == PAUSED) {
    display = "Paused";
} else {
    display = "Unknown";
}
```

## Modifier Order
Field and method modifiers must follow this order if applicable:
1. visibility (`public` / `private` / `protected`)
2. `static`
3. `final`
4. `abstract`
5. `volatile`
6. `synchronized`

```java
private static final int TIMEOUT = 5000;
public final synchronized void reset() { … }
```

## Method Header Formatting
Opening braces belong on the same line as the closing parenthesis. If the signature is too long, break after the opening parenthesis, align parameters on the next line, and keep the closing parenthesis and opening brace together.

```java
// ✅ Good – short signature
public final void process(final Player player) {
    ...
}

// ✅ Good – long signature, clean break
public final void buildUI(
    final Container parent,
    final String title,
    final boolean resizable
) {
    ...
}

// ❌ Avoid – brace on its own line
public void process(final Player player)
{
    ...
}

// ❌ Avoid – orphaned closing parenthesis
public void buildUI(
    final Container parent,
    final String title,
    final boolean resizable) {
    ...
}
```