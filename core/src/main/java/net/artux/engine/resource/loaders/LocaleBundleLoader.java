package net.artux.engine.resource.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import net.artux.engine.utils.LocaleBundle;

import java.util.Locale;

public class LocaleBundleLoader extends AsynchronousAssetLoader<LocaleBundle, LocaleBundleLoader.LocaleBundleParameter> {

    public LocaleBundleLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    LocaleBundle bundle;

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, LocaleBundleLoader.LocaleBundleParameter parameter) {
        this.bundle = null;
        Locale locale;
        boolean simpleFormat;
        if (parameter == null) {
            locale = Locale.getDefault();
            simpleFormat = false;
        } else {
            locale = parameter.locale == null ? Locale.getDefault() : parameter.locale;
            simpleFormat = parameter.simpleFormat;
        }
        this.bundle = new LocaleBundle(file, locale, simpleFormat);

    }

    @Override
    public LocaleBundle loadSync(AssetManager manager, String fileName, FileHandle file, LocaleBundleLoader.LocaleBundleParameter parameter) {
        LocaleBundle bundle = this.bundle;
        this.bundle = null;
        return bundle;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, LocaleBundleLoader.LocaleBundleParameter parameter) {
        return null;
    }

    static public class LocaleBundleParameter extends AssetLoaderParameters<LocaleBundle> {
        public final Locale locale;
        public final boolean simpleFormat;

        public LocaleBundleParameter() {
            this(null, false);
        }

        public LocaleBundleParameter(Locale locale) {
            this(locale, false);
        }

        public LocaleBundleParameter(Locale locale, boolean simpleFormat) {
            this.locale = locale;
            this.simpleFormat = simpleFormat;
        }
    }
}
