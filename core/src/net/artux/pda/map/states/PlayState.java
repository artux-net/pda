package net.artux.pda.map.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import net.artux.pda.map.GdxAdapter;
import net.artux.pda.map.model.Entity;
import net.artux.pda.map.model.Hit;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.model.Mob;
import net.artux.pda.map.model.Player;
import net.artux.pda.map.model.Point;
import net.artux.pda.map.model.Quest;
import net.artux.pda.map.model.Spawn;
import net.artux.pda.map.model.Text;
import net.artux.pda.map.model.Transfer;
import net.artux.pda.map.model.TransferPoint;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pdalib.Checker;
import net.artux.pdalib.profile.Story;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.Texture.TextureWrap.Repeat;
import static net.artux.pda.map.GdxAdapter.RUSSIAN_CHARACTERS;
import static net.artux.pda.map.GdxAdapter.RUSSIAN_FONT_NAME;


public class PlayState extends State {

    private Player player;
    public static List<Entity> entities = new ArrayList<>();

    public static Stage stage;
    public static Stage uistage;

    private final Texture background;
    private Texture blur;
    private Texture bounds;

    int wb;
    int hb;

    public static AssetManager assetManager;

    long heapLoss;

    private final Button.ButtonStyle textButtonStyle;
    public static BitmapFont font;

    private final UserInterface userInterface;

    public PlayState(final GameStateManager gsm, Batch batch) {
        super(gsm);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        long before = Gdx.app.getNativeHeap();


        Type REVIEW_TYPE = new TypeToken<List<Mob>>() {
        }.getType();
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(Gdx.files.internal("mobs").reader());
        List<Mob> data = gson.fromJson(reader, REVIEW_TYPE);
        System.out.println("Mobs data size: " + data.size());
        font = GdxAdapter.generateFont(RUSSIAN_FONT_NAME, RUSSIAN_CHARACTERS);

        assetManager = new AssetManager();
        assetManager.load("dialog.png", Texture.class);
        assetManager.load("beg2.png", Texture.class);
        assetManager.load("beg1.png", Texture.class);
        assetManager.load("pause.png", Texture.class);
        assetManager.load("quest.png", Texture.class);
        assetManager.load("seller.png", Texture.class);
        assetManager.load("quest1.png", Texture.class);
        assetManager.load("cache.png", Texture.class);
        assetManager.load("direction.png", Texture.class);
        assetManager.load("transfer.png", Texture.class);
        assetManager.load("gg1.png", Texture.class);
        assetManager.load("red.png", Texture.class);
        assetManager.load("green.png", Texture.class);
        assetManager.load("yellow.png", Texture.class);
        assetManager.load("direction.png", Texture.class);
        assetManager.load("touchpad/knob.png", Texture.class);
        assetManager.load("touchpad/back.png", Texture.class);
        assetManager.load("occupations.png", Texture.class);
        assetManager.finishLoading();

        Map map = (Map) gsm.get("map");
        background = new Texture(Gdx.files.absolute(map.getTextureUri()));
        if (map.getBlurTextureUri()!=null)
            if (Gdx.files.absolute(map.getBlurTextureUri()).exists()) {
                blur = new Texture(Gdx.files.absolute(map.getBlurTextureUri()));
                blur.setWrap(Repeat, Repeat);
                wb = background.getWidth()/2 - blur.getWidth()/2;
                hb = background.getHeight()/2 - blur.getHeight()/2;
            }

        Viewport viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage(viewport, batch);
        uistage = new Stage();

        textButtonStyle = new Button.ButtonStyle();
        textButtonStyle.up  =  new TextureRegionDrawable(assetManager.get("dialog.png", Texture.class));
        textButtonStyle.down = new TextureRegionDrawable(assetManager.get("dialog.png", Texture.class));
        textButtonStyle.checked = new TextureRegionDrawable(assetManager.get("dialog.png", Texture.class));
        textButtonStyle.over = new TextureRegionDrawable(assetManager.get("dialog.png", Texture.class));

        ((OrthographicCamera)stage.getCamera()).zoom -= 0.5f;

        initPlayer(map);

        userInterface = new UserInterface(gsm, player, assetManager, font);
        uistage.addActor(userInterface);

        for (Spawn spawn : map.getSpawns()){
            spawn.create(assetManager, data, player);
        }

        for(final Point point: map.getPoints()) {
            if (getMember()!=null && Checker.check(point.getCondition(), getMember()))
                if (point.getData().containsKey("chapter")){
                    int storyId = Integer.parseInt(getMember().getData().getTemp().get("currentStory"));
                    for (Story story : getMember().getData().getStories()) {
                        if (story.getStoryId() == storyId
                                && (Integer.parseInt(point.getData().get("chapter")) == story.getLastChapter()
                                || Integer.parseInt(point.getData().get("chapter")) == 0))
                                addPoint(point);
                    }
                 }else addPoint(point);

        }
        for (Transfer transfer : map.getTransfers()){
            stage.addActor(new TransferPoint(transfer, assetManager));
        }

        long after = Gdx.app.getNativeHeap();
        heapLoss = after-before;
        System.out.println("Play init " + (after-before));
    }

    private void addPoint(Point point){
        stage.addActor(new Quest(point, assetManager));
        if (point.type < 2 || point.type > 3)
            player.setDirection(point.getPosition());
    }

    private void initPlayer(Map map){
        System.out.println("Heap init:" + Gdx.app.getNativeHeap());
        player = new Player(map.getPlayerPosition(), getMember(), assetManager);
        System.out.println("Heap player:" + Gdx.app.getNativeHeap());
        bounds = new Texture(Gdx.files.absolute(map.getBoundsTextureUri()));
        if (Gdx.files.absolute(map.getBoundsTextureUri()).exists())
            player.setBoundsTexture(bounds);
        System.out.println("Heap bounds:" + Gdx.app.getNativeHeap());
        entities.add(player);
        stage.addActor(player);
        System.out.println("Heap 2:" + Gdx.app.getNativeHeap());

    }

    public static void registerHit(Hit hit){
        stage.addActor(hit);
    }

    boolean checkEnemy(int id1, int id2){
        //TODO
        return false;
    }

    @Override
    protected void handleInput() {
        gsm.addInputProcessor(stage);
        gsm.addInputProcessor(uistage);
    }

    @Override
    protected void stop() {
        gsm.removeInputProcessor(stage);
        gsm.removeInputProcessor(uistage);
    }

    @Override
    public void update(float dt) {
        uistage.act(dt);
        stage.act(dt);

        for (int i = 0; i<stage.getActors().size; i++){
            Actor actor = stage.getActors().get(i);

            if (actor instanceof Hit){
                Hit hit = (Hit) actor;
                for (Actor actor1 : stage.getActors()){
                    if (actor1 instanceof Entity){
                        Entity entity = (Entity) actor1;
                        if (entity.getSprite().getBoundingRectangle().overlaps(hit.getSprite().getBoundingRectangle())
                                && !hit.author.equals(entity)){
                            entity.damage(hit.getDamage());
                            stage.getActors().removeIndex(i);
                            break;
                        }
                    }
                }
            }else if (actor instanceof Quest){
                final Quest point  = (Quest) actor;
                String name = "q-"+point.hashCode();
                if (point.getPosition().dst(player.getPosition()) < 35f) {
                    if (point.type!=1 && point.type!=3) {
                        if (!userInterface.contains(name)) {
                            Button button = new Button(textButtonStyle);
                            button.setPosition(w - w / 12, 2.5f * h / 12);
                            button.setSize(h / 10, h / 10);
                            button.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    gsm.getPlatformInterface().send(point.getData());
                                }
                            });
                            button.setName(name);
                            Text text =new Text(point.title, font);
                            text.setPosition(point.getPosition().x, point.getPosition().y);
                            text.setName(name);
                            stage.addActor(text);
                            userInterface.addActor(button);
                        }
                    }else {
                        gsm.getPlatformInterface().send(point.getData());
                    }
                } else {
                    for(Actor buttons : userInterface.getChildren()){
                        if(buttons.getName()!=null && buttons.getName().equals(name)) {
                            buttons.remove();
                        }
                    }
                    for(Actor buttons : stage.getActors()){
                        if(buttons.getName()!=null && buttons.getName().equals(name)) {
                            buttons.remove();
                        }
                    }
                }

            }else if (actor instanceof TransferPoint){
                final TransferPoint point  = (TransferPoint) actor;
                String name = "t-"+point.hashCode();
                if (point.getPosition().dst(player.getPosition()) < 35f) {
                    if (!userInterface.contains(name)) {
                        Button button = new Button(textButtonStyle);
                        button.setPosition(w - w/12, 2.5f * h / 12);
                        button.setSize(h/10,h/10);
                        button.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                gsm.getPlatformInterface().send(point.getData());
                            }
                        });
                        button.setName(name);
                        Text text =new Text(point.getTitle(), font);
                        text.setPosition(point.getPosition().x, point.getPosition().y);
                        text.setName(name);
                        stage.addActor(text);
                        userInterface.addActor(button);
                    }
                } else {
                    for(Actor buttons : userInterface.getChildren()){
                        if(buttons.getName()!=null && buttons.getName().equals(name)) {
                            buttons.remove();
                        }
                    }
                    for(Actor buttons : stage.getActors()){
                        if(buttons.getName()!=null && buttons.getName().equals(name)) {
                            buttons.remove();
                        }
                    }
                }
            }
        }

        for (int i = 0; i<entities.size(); i++)
            for (int j=0; j<entities.size(); j++)
                if (entities.get(i).getPosition().dst(entities.get(j).getPosition())<50f)
                    if (checkEnemy(entities.get(i).id, entities.get(j).id)){
                        Entity entity = entities.get(i);
                        entity.setEnemy(entities.get(j));
                        entities.set(i, entity);

                        entity = entities.get(j);
                        entity.setEnemy(entities.get(i));
                        entities.set(j, entity);
                    }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(130, 169, 130, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();

        stage.getViewport().getCamera().position.set(player.getPosition(), 0);
        if (blur!=null)
            stage.getBatch().draw(blur,wb, hb);
        stage.getBatch().draw(background, 0, 0);

        stage.getBatch().end();
        stage.draw();

        uistage.draw();
    }

    boolean contains(String name, Stage stage){
        for (Actor actor : stage.getActors()) {
            if (actor.getName()!=null && actor.getName().equals(name))
                return true;
        }
        return false;
    }

    @Override
    public void resize(int w, int h) {
        System.out.println("Resized: " + w + " : " + h);
        stage.getViewport().update(w, h, true);

    }

    @Override
    public void dispose() {
        System.out.println("Dispose PlayState");
        long before = Gdx.app.getNativeHeap();
        System.out.println("Before: " );
        stage.dispose();
        uistage.dispose();
        background.dispose();
        if (bounds!=null)
            bounds.dispose();
        if (blur!=null)
            blur.dispose();
        font.dispose();
        assetManager.dispose();
        player.dispose();
        userInterface.dispose();

        long after = Gdx.app.getNativeHeap();
        System.out.println("Play dispose " + (after-before));
        System.out.println("Total heap loss " + heapLoss);
    }

}
