package net.artux.pda.map.utils.di.components;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Logger;

import net.artux.engine.scenes.SceneManager;
import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.scenes.ErrorScene;
import net.artux.pda.map.scenes.PlayScene;
import net.artux.pda.map.scenes.PreloadScene;
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

    PreloadScene getPreloadState();

    PlayScene getPlayState();

    ErrorScene getErrorState();

    SceneManager getGSC();

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
