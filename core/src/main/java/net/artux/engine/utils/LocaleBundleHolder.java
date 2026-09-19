package net.artux.engine.utils;

/**
 * Static access point to the current LocaleBundle for code Dagger can't reach directly:
 * ECS components and enum constants are plain data, created by Ashley (components, reflectively
 * pooled) or by the JVM's own class-loading (enum constants) rather than by the Dagger graph, so
 * they can't take a constructor-injected LocaleBundle.
 * <p>
 * {@link #init(LocaleBundle)} is called once, from the same Dagger provider that hands out the
 * real LocaleBundle (see AppModule#getLocaleBundle), so every consumer - injected or static -
 * ends up reading the same instance. Callers should only resolve strings lazily (e.g. from a
 * getter invoked at render/display time), never at construction/class-init time, since enum
 * constants are built the first time their class is touched, which can happen before the locale
 * asset has finished loading.
 */
public final class LocaleBundleHolder {

    private static volatile LocaleBundle bundle;

    private LocaleBundleHolder() {
    }

    public static void init(LocaleBundle localeBundle) {
        bundle = localeBundle;
    }

    public static String get(String key) {
        return requireBundle().get(key);
    }

    public static String get(String key, Object... args) {
        return requireBundle().get(key, args);
    }

    private static LocaleBundle requireBundle() {
        LocaleBundle current = bundle;
        if (current == null) {
            throw new IllegalStateException(
                    "LocaleBundleHolder used before AppModule#getLocaleBundle ran - " +
                            "resolve strings lazily (on display), not at construction/class-init time."
            );
        }
        return current;
    }
}
