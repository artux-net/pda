package net.artux.tests.pathfind;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.artux.pda.map.model.Map;
import net.artux.tests.GdxAiTestUtils;

public class PathFinderAdapter extends ApplicationAdapter {

    public static void main (String[] argv) {
        GdxAiTestUtils.launch(new PathFinderAdapter());
    }

    private static final boolean DEBUG_STAGE = false;


    Table testsTable;
    PathFinderTestBase currentTest;

    public Skin skin;
    public float stageWidth;
    public float stageHeight;
    public Stage stage;

    @Override
    public void create () {
        Gdx.gl.glClearColor(.3f, .3f, .3f, 1);

        skin = new Skin(Gdx.files.local("data/uiskin.json"));


        // Enable color markup
        BitmapFont font = skin.get("default-font", BitmapFont.class);
        font.getData().markupEnabled = true;

        stage = new Stage();
        stage.setDebugAll(DEBUG_STAGE);
        stageWidth = stage.getWidth();
        stageHeight = stage.getHeight();

        Gdx.input.setInputProcessor(new InputMultiplexer(stage));

        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(stageWidth, stageHeight);
        testsTable = new Table();
        stack.add(testsTable);

        Map map = new Map();
        map.setTexture("maps/map_escape.png");
        map.setBoundsTexture("maps/test.png");
        // Set selected test
        currentTest = new PathFinderTest(this, map);
        currentTest.create();

        InputMultiplexer im = (InputMultiplexer)Gdx.input.getInputProcessor();
        if (im.size() > 1) im.removeProcessor(1);
        if (currentTest.getInputProcessor() != null) im.addProcessor(currentTest.getInputProcessor());
    }

    @Override
    public void render () {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update time
        GdxAI.getTimepiece().update(Gdx.graphics.getDeltaTime());

        if (currentTest != null) currentTest.render();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize (int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
        stageWidth = width;
        stageHeight = height;
    }


}
