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
        properties = new ObjectMap<>();

        Properties temp = new Properties();
        try {
            temp.load(fileHandle.reader(StandardCharsets.UTF_8.name()));//todo locale specific-load
            temp.forEach((key, value) -> {
                properties.put((String) key, (String) value);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
