package net.artux.engine.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

import net.artux.pda.map.scenes.ErrorScene;
import net.artux.pda.map.di.components.CoreComponent;

import java.util.Stack;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Менеджер сцен
 */
@Singleton
public class SceneManager implements Screen {

    private final Stack<Scene> scenes;
    private final InputMultiplexer multiplexer = new InputMultiplexer();
    private final CoreComponent coreComponent;
    //private final AssetsController assetsController;

    @Inject
    public SceneManager(CoreComponent coreComponent) {
        scenes = new Stack<>();
        this.coreComponent = coreComponent;
        //assetsController = new AssetsController(coreComponent.getAssetsManager());
    }

    public void push(Scene scene) {
        if (!scenes.empty())
            scenes.peek().stop();
        scenes.push(scene);
        scenes.peek().handleInput();
    }

    public void resume() {
        scenes.peek().resume();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public Scene peek() {
        return scenes.peek();
    }

    /**
     * Установка новой сцены
     * @param scene
     */
    public void set(Scene scene) {
        Scene current = scenes.pop();
        current.stop();
        current.dispose();
        //assetsController.unload(current);
        clearInputProcessors();
        scenes.push(scene);
        scenes.peek().handleInput();
        scenes.peek().resume();
    }

    public void clear() {
        for (Scene scene : scenes) {
                      scene.dispose();
        }
        scenes.removeAllElements();
    }

    public void update(float dt) {
        try {
            if (scenes.size() > 0)
                scenes.peek().update(dt);
        } catch (Exception e) {
            ErrorScene errorState = coreComponent.getErrorState();
            errorState.setThrowable(e);
            set(errorState);
        }
    }

    public void resize(int width, int height) {
        if (scenes.size() > 0)
            scenes.peek().resize(width, height);
    }

    @Override
    public void show() {

    }

    public void render(float dt) {
        try {
            if (scenes.size() > 0)
                scenes.peek().render(dt);
        } catch (Exception e) {
            ErrorScene errorState = coreComponent.getErrorState();
            errorState.setThrowable(e);
            set(errorState);
        }
    }

    public void addInputProcessor(InputProcessor inputProcessor) {
        if (!multiplexer.getProcessors().contains(inputProcessor, false))
            multiplexer.addProcessor(inputProcessor);
        if (multiplexer.size() >= 1)
            Gdx.input.setInputProcessor(multiplexer);
    }

    public void removeInputProcessor(InputProcessor inputProcessor) {
        if (multiplexer.getProcessors().contains(inputProcessor, false))
            multiplexer.removeProcessor(inputProcessor);
    }

    public void clearInputProcessors() {
        multiplexer.clear();
    }

    public void pause() {
        for (int i = 0; i < multiplexer.getProcessors().size; i++) {
            for (int j = 0; j < Gdx.input.getMaxPointers(); j++) {
                multiplexer.getProcessors().get(i).touchUp(0, 0, j, 0); //resets all inputs after pause;
            }
        }
    }
}
