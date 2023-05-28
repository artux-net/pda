package net.artux.engine.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.Stack;


public abstract class Scene implements Screen {

    protected final SceneController sceneController;
    private final Stack<Stage> stages;

    protected float w = Gdx.graphics.getWidth();
    protected float h = Gdx.graphics.getHeight();

    public Scene(SceneController sceneController) {
        this.sceneController = sceneController;
        stages = new Stack<>();
    }

    public void addStage(Stage stage) {
        stages.push(stage);
    }

    public abstract void resume();

    protected void handleInput() {
        for (Stage stage : stages) {
            sceneController.addInputProcessor(stage);
        }
    }

    protected void stop(){
        for (Stage stage : stages) {
            sceneController.removeInputProcessor(stage);
        }
    }

    public void update(float dt){
        for (Stage stage : stages) {
            stage.act(dt);
        }
    }

    public void render(float dt){
        for (Stage stage : stages) {
            stage.draw();
        }
    }

    @Override
    public void show() {

    }

    public abstract void resize(int width, int height);

    public void dispose() {
        for (Stage stage : stages) {
            stage.dispose();
        }
    }

}