package net.artux.pda.map.di;

import com.badlogic.gdx.assets.AssetManager;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.map.GameMap;

import java.util.Properties;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final DataRepository dataRepository;

    public AppModule(PlatformInterface platformInterface) {
        this.dataRepository = new DataRepository(platformInterface);
    }

    @Provides
    public Properties getProperties() {
        return dataRepository.getProperties();
    }

    @Provides
    @Singleton
    public DataRepository getDataRepository() {
        return dataRepository;
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
    public GameMap getGameMap() {
        return dataRepository.getGameMap();
    }

}
