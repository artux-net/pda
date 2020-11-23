package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.artux.pda.map.GdxAdapter;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.model.Text;

import java.util.HashMap;

import static net.artux.pda.map.GdxAdapter.RUSSIAN_CHARACTERS;
import static net.artux.pda.map.GdxAdapter.RUSSIAN_FONT_NAME;

public class PauseState extends State {

    Stage stage;
    Texture background;
    public static final int FBO_SIZE = 1024;


    public PauseState(final GameStateManager gsm, Map map) {
        super(gsm);
        stage = new Stage();

        BitmapFont font = GdxAdapter.generateFont(RUSSIAN_FONT_NAME, RUSSIAN_CHARACTERS);
        font.setColor(0,0,0,1);
        Skin skinButton=new Skin();
        TextureAtlas buttonAtlas = new TextureAtlas("button");
        skinButton.addRegions(buttonAtlas);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;

        textButtonStyle.up = skinButton.getDrawable("rounded_rectangle_button");
        textButtonStyle.down = skinButton.getDrawable("rounded_rectangle_button");
        textButtonStyle.checked = skinButton.getDrawable("rounded_rectangle_button");
        TextButton button=new TextButton("Finish",textButtonStyle);
        button.setText("Достать пда");
        button.scaleBy(4);
        button.setHeight(200);
        button.setWidth(500);
        button.setPosition(w/2-w/4,h/2);
        button.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                HashMap<String, String> data = new HashMap<>();
                data.put("openPda", "");
                gsm.getPlatformInterface().send(data);
            }
        });
        stage.addActor(button);

        Skin buttonSkin = new Skin();
        buttonSkin.add("pause", new Texture("pause.png"));

        Button.ButtonStyle pauseButtonStyle = new Button.ButtonStyle();
        pauseButtonStyle.up = buttonSkin.getDrawable("pause");
        Button pauseButton = new Button(pauseButtonStyle);
        pauseButton.setPosition(w - w/11, h - h/11);
        pauseButton.setSize(h/12,h/12);
        pauseButton.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gsm.pop();
            }
        });
        stage.addActor(pauseButton);
        stage.addActor(new Text("Пауза - " + map.getTitle(), new Vector2(10,Gdx.graphics.getHeight()-10)));
        stage.addActor(new Text(getMember().getData().toString(), new Vector2(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()-10)));
    }

    @Override
    protected void handleInput() {
        gsm.addInputProcessor(stage);
    }

    @Override
    protected void stop() {
        gsm.removeInputProcessor(stage);
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(50, 0, 0, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {
    }

    public void setBackground(Texture background){
        this.background = background;
    }
}
