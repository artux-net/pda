package net.artux.engine.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;

public class LocaleBundle {

    private Locale locale;
    private final ObjectMap<String, String> properties;
    private LocaleBundle parent;

    private final boolean simpleFormat;

    public LocaleBundle(FileHandle fileHandle, Locale locale, boolean simpleFormat) {
        this.simpleFormat = simpleFormat;
        this.locale = locale;
        properties = new ObjectMap<>();

        Properties temp = new Properties();
        try {
            temp.load(resolveForLocale(fileHandle, locale).reader(StandardCharsets.UTF_8.name()));
            // Properties.forEach() takes a BiConsumer, which RoboVM's runtime only has as
            // a phantom (compile-only) class - using it here throws NoClassDefFoundError
            // on iOS, so this has to iterate directly instead.
            for (String key : temp.stringPropertyNames()) {
                properties.put(key, temp.getProperty(key));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Bundle files follow the convention "name.properties" (base, historically
     * Russian) / "name_en.properties" (English), sitting next to each other.
     * Falls back to the base file whenever a translated sibling for the
     * requested locale doesn't exist, so untranslated locales keep working.
     */
    private static FileHandle resolveForLocale(FileHandle fileHandle, Locale locale) {
        if (locale == null) return fileHandle;

        String language = locale.getLanguage();
        if (language == null || language.isEmpty()) return fileHandle;

        String extension = fileHandle.extension();
        String nameWithoutExtension = fileHandle.nameWithoutExtension();
        String localizedName = extension.isEmpty()
                ? nameWithoutExtension + "_" + language
                : nameWithoutExtension + "_" + language + "." + extension;

        FileHandle localized = fileHandle.sibling(localizedName);
        return localized.exists() ? localized : fileHandle;
    }

    public String get(String key) {
        String result = properties.get(key);
        if (result == null) {
            if (parent != null) result = parent.get(key);

            if (result == null) {
                throw new MissingResourceException("Can't find bundle key " + key, this.getClass().getName(), key);
                /*    if (exceptionOnMissingKey)
                        throw new MissingResourceException("Can't find bundle key " + key, this.getClass().getName(), key);
                    else
                        return "???" + key + "???";*/
            }
        }
        return result;
    }

    public String get(String key, Object... args) {
        return String.format(get(key), args);
    }


}
