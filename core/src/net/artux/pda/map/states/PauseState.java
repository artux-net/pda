package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.artux.pda.map.model.Map;
import net.artux.pda.map.model.Player;
import net.artux.pda.map.model.Point;
import net.artux.pda.map.model.Text;
import net.artux.pdalib.Checker;
import net.artux.pdalib.profile.Story;

import java.util.HashMap;

import static com.badlogic.gdx.graphics.Texture.TextureWrap.Repeat;
import static net.artux.pda.map.GdxAdapter.RUSSIAN_CHARACTERS;
import static net.artux.pda.map.GdxAdapter.RUSSIAN_FONT_NAME;
import static net.artux.pda.map.GdxAdapter.generateFont;

public class PauseState extends State {

    private final AssetManager assetManager;
    Stage stage;
    Texture background;
    BitmapFont font;
    Skin skin;

    public PauseState(final GameStateManager gsm, final Player player) {
        super(gsm);
        long before = Gdx.app.getNativeHeap();
        stage = new Stage();

        font = generateFont(RUSSIAN_FONT_NAME, RUSSIAN_CHARACTERS, 72);
        font.setColor(0,0,0,1);

        Text button = new Text("В меню", font);
        button.setPosition(50,Gdx.graphics.getHeight()-200);
        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                HashMap<String, String> data = new HashMap<>();
                data.put("openPda", "");
                gsm.getPlatformInterface().send(data);
            }
        });
        final Map map = (Map) gsm.get("map");
        if (map.getBlurTextureUri()!=null)
            if (Gdx.files.absolute(map.getBlurTextureUri()).exists()) {
                background = new Texture(Gdx.files.absolute(map.getBlurTextureUri()));
                background.setWrap(Repeat, Repeat);
            }



        stage.addActor(button);

        assetManager = new AssetManager();
        assetManager.load("pause.png", Texture.class);
        assetManager.finishLoading();

        Button.ButtonStyle pauseButtonStyle = new Button.ButtonStyle();
        pauseButtonStyle.up = new TextureRegionDrawable(assetManager.get("pause.png", Texture.class));
        final Button pauseButton = new Button(pauseButtonStyle);
        pauseButton.setPosition(w - w/11, h - h/11);
        pauseButton.setSize(h/12,h/12);
        pauseButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("play touched - pause");
                gsm.pop();
            }
        });

        skin = new Skin(Gdx.files.internal("data/assets/uiskin.json"));

        Table container = new Table();
        stage.addActor(container);
        container.setPosition(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f);
        container.setSize(500, 300);

        Table table = new Table();

        final ScrollPane scroll = new ScrollPane(table, skin);
        scroll.setScrollingDisabled(true,false);

        table.pad(10).defaults().expandX().space(4);
        for (final Point point : map.getPoints()) {
            if (point.type < 2 || point.type > 3)
            if (getMember()!=null && Checker.check(point.getCondition(), getMember())){
                table.row().width(500);
                if (point.getData().containsKey("chapter") && getMember()!=null) {
                    int storyId = Integer.parseInt(getMember().getData().getTemp().get("currentStory"));
                    for (Story story : getMember().getData().getStories()) {
                        if (story.getStoryId() == storyId
                                && (Integer.parseInt(point.getData().get("chapter")) == story.getLastChapter()
                                || Integer.parseInt(point.getData().get("chapter")) == 0)) {
                            Text label = new Text(point.getTitle(), font);
                            label.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    super.clicked(event, x, y);
                                    player.setDirection(point.getPosition());
                                }
                            });
                            table.add(label);
                        }
                    }
                } else {
                    Text label = new Text(point.getTitle(), font);
                    label.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);
                            player.setDirection(point.getPosition());
                        }
                    });
                    table.add(label);
                }
            }
        }

        container.add(scroll).expand().fill();

        stage.addActor(pauseButton);
        Text title = new Text("Навигация - " + ((Map)gsm.get("map")).getTitle(), font);
        title.setPosition(50,Gdx.graphics.getHeight()-80);
        stage.addActor(title);
        long after = Gdx.app.getNativeHeap();
        System.out.println("Pause init " + (after-before));
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
        Gdx.gl.glClearColor(120/255f, 120/255f, 120/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        /*stage.getBatch().begin();
        if (background!=null)
            stage.getBatch().draw(background, 0, 0);
        stage.getBatch().end();*/
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {
        long before = Gdx.app.getNativeHeap();
        stage.dispose();
        if (background!=null)
            background.dispose();
        font.dispose();
        skin.dispose();
        assetManager.dispose();
        long after = Gdx.app.getNativeHeap();
        System.out.println("Pause dispose " + (after-before));
    }

}
