package net.artux.pda.map.utils.di.components;

import com.badlogic.gdx.assets.AssetManager;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.states.ErrorState;
import net.artux.pda.map.states.GameStateController;
import net.artux.pda.map.states.PlayState;
import net.artux.pda.map.states.PreloadState;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.map.utils.di.modules.AppModule;
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

    ErrorState getErrorState();

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
