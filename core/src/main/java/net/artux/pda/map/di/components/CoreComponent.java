package net.artux.pda.map.di.components;

import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.assets.AssetManager;
import com.google.gson.Gson;

import net.artux.engine.scenes.SceneManager;
import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.scenes.ErrorScene;
import net.artux.pda.map.scenes.PlayScene;
import net.artux.pda.map.scenes.PreloadScene;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.map.di.modules.AppModule;
import net.artux.pda.map.view.root.FontManager;
import net.artux.pda.model.items.ItemsContainerModel;
import net.artux.pda.model.map.GameMap;

import org.luaj.vm2.LuaTable;

import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Основополагающие компоненты движка, не зависимые от запущенной карты
 */
@Singleton
@Component(modules = {AppModule.class})
public interface CoreComponent {

    /**
     *
     * @return сцена загрузки
     */
    PreloadScene getPreloadState();

    /**
     * весь геймплей здесь
     * @return сцена с картой
     */
    PlayScene getPlayState();

    ErrorScene getErrorState();

    /**
     * менеджер сцен, позволяет сменить текущую сцену
     * @return
     */
    SceneManager getSceneManager();

    AssetsFinder getAssetsFinder();

    AssetManager getAssetsManager();

    LocaleBundle getLocaleBundle();

    DataRepository getDataRepository();

    PlatformInterface getPlatformInterface();

    GameMap getGameMap();

    Properties getProperties();

    FontManager getFontManager();

    @Named("testerMode")
    Boolean isTesterMode();

    ApplicationLogger getLogger();

    Gson getGson();

    ItemsContainerModel getItemsContainerModel();
}
