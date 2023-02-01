package net.artux.pda.map.di;

import com.badlogic.gdx.assets.AssetManager;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.states.GameStateController;
import net.artux.pda.map.states.PlayState;
import net.artux.pda.map.states.PreloadState;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.map.GameMap;

import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface CoreComponent {

    PreloadState getPreloadState();

    PlayState getPlayState();

    GameStateController getGSC();

    AssetsFinder getAssetsFinder();

    AssetManager getAssetsManager();

    LocaleBundle getLocaleBundle();

    DataRepository getDataRepository();

    PlatformInterface getPlatformInterface();

    GameMap getGameMap();

    Properties getProperties();

    @Named("testerMode")
    Boolean isTesterMode();

}
