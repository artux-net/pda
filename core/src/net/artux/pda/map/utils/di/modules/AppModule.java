package net.artux.pda.map.utils.di.modules;

import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.common.PropertyFields;
import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.map.view.FontManager;
import net.artux.pda.model.map.GameMap;

import org.luaj.vm2.LuaTable;

import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final DataRepository dataRepository;

    public AppModule(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Provides
    public Properties getProperties() {
        return dataRepository.getProperties();
    }

    @Provides
    public LuaTable getLuaTable() {
        return dataRepository.getLuaTable();
    }

    @Provides
    @Named("testerMode")
    public boolean isInTesterMode() {
        return getProperties()
                .getProperty(PropertyFields.TESTER_MODE, "false")
                .equals("true");
    }

    @Provides
    @Singleton
    public FontManager fontManager(AssetsFinder assetsFinder) {
        return assetsFinder.getFontManager();
    }

    @Provides
    @Singleton
    public DataRepository getDataRepository() {
        return dataRepository;
    }

    @Provides
    public ApplicationLogger getAppLogger() {
        return Gdx.app.getApplicationLogger();
    }

    @Provides
    public PlatformInterface getPlatformInterface() {
        return dataRepository.getPlatformInterface();
    }

    @Provides
    @Singleton
    public AssetsFinder getAssetsFinder(Properties properties) {
        return new AssetsFinder(properties);
    }

    @Provides
    public AssetManager getAssetManager(AssetsFinder assetsFinder) {
        return assetsFinder.getManager();
    }

    @Provides
    public LocaleBundle getLocaleBundle(AssetsFinder assetsFinder) {
        return assetsFinder.getLocaleBundle();
    }

    @Provides
    public GameMap getGameMap() {
        return dataRepository.getGameMap();
    }

    @Provides
    public AppModule getAppModule() {
        return this;
    }

}
