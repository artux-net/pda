package net.artux.pda.map.di.modules;

import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.assets.AssetManager;
import com.google.gson.Gson;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.common.PropertyFields;
import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.map.view.root.FontManager;
import net.artux.pda.model.items.ItemsContainerModel;
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
    public Gson gson() {
        return new Gson();
    }

    @Provides
    @Singleton
    public DataRepository getDataRepository() {
        return dataRepository;
    }

    @Provides
    public ApplicationLogger getAppLogger() {
        return dataRepository.getApplicationLogger();
    }

    @Provides
    public PlatformInterface getPlatformInterface() {
        return dataRepository.getPlatformInterface();
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

    @Provides
    public ItemsContainerModel getItemsContainerModel() {
        return dataRepository.getItems();
    }

}
