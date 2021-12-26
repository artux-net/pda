package net.artux.pda.map.states;

import static com.badlogic.gdx.graphics.Texture.TextureWrap.Repeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

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
import java.util.HashMap;
import java.util.List;


public class PlayState extends State {

    private final Player player;
    public List<Entity> entities = new ArrayList<>();

    public Stage stage;
    public Stage uistage;

    private Texture background;
    private Texture blur;
    private Texture bounds;

    int wb;
    int hb;

    public static AssetManager assetManager;

    private final Button.ButtonStyle textButtonStyle;
    private final UserInterface userInterface;

    private static final String tag = "PlayState";

    public PlayState(final GameStateManager gsm, Batch batch) {
        super(gsm);
        Gdx.app.debug(tag, "Start play state init");

        Gdx.app.debug(tag, "Before loading textures, heap " + Gdx.app.getNativeHeap());
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

        Gdx.app.debug(tag, "After loading textures, heap " + Gdx.app.getNativeHeap());

        Viewport viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage(viewport, batch);
        uistage = new Stage();

        player = initPlayer();

        Map map = (Map) gsm.get("map");
        if (map!=null) {
            player.setPosition(map.getPlayerPosition().x, map.getPlayerPosition().y);

            background = (Texture) gsm.get("texture");

            if (map.getBlurTextureUri() != null)
                if (gsm.get("blur")!=null) {
                    blur = (Texture) gsm.get("blur");
                    blur.setWrap(Repeat, Repeat);
                    if (background != null) {
                        wb = background.getWidth() / 2 - blur.getWidth() / 2;
                        hb = background.getHeight() / 2 - blur.getHeight() / 2;
                    }
                }

            Type REVIEW_TYPE = new TypeToken<List<Mob>>() {
            }.getType();
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(Gdx.files.internal("mobs").reader());
            List<Mob> data = gson.fromJson(reader, REVIEW_TYPE);

            for (Spawn spawn : map.getSpawns()){
                spawn.create(this, assetManager, data, player, gsm);
            }

           for(Point point: map.getPoints()) {
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

            Gdx.app.debug(tag, "Number of transfers " + map.getTransfers().size());

            for (Transfer transfer : map.getTransfers()){
                stage.addActor(new TransferPoint(transfer, assetManager));
            }
        }
        Gdx.app.debug(tag, "After loading map, heap " + Gdx.app.getNativeHeap());

        textButtonStyle = new Button.ButtonStyle();
        textButtonStyle.up  =  new TextureRegionDrawable(assetManager.get("dialog.png", Texture.class));
        textButtonStyle.down = new TextureRegionDrawable(assetManager.get("dialog.png", Texture.class));
        textButtonStyle.checked = new TextureRegionDrawable(assetManager.get("dialog.png", Texture.class));
        textButtonStyle.over = new TextureRegionDrawable(assetManager.get("dialog.png", Texture.class));

        ((OrthographicCamera)stage.getCamera()).zoom -= 0.5f;


        userInterface = new UserInterface(gsm, player, assetManager);
        uistage.addActor(userInterface);
    }

    private void addPoint(Point point){
        stage.addActor(new Quest(point, assetManager));
        if (point.type < 2 || point.type > 3)
            player.setDirection(point.getPosition());
    }

    private Player initPlayer(){
        Map map = (Map) gsm.get("map");
        Player player = new Player(this, getMember(), assetManager);

        if (gsm.get("bounds")!=null) {
            bounds = (Texture) gsm.get("bounds");
            player.setBoundsTexture(bounds);
        }
        entities.add(player);
        stage.addActor(player);
        return player;
    }

    public void registerHit(Hit hit){
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

    HashMap<String, String> preferData = null;
    ChangeListener dataListener = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if (preferData !=null)
                gsm.getPlatformInterface().send(preferData);
        }
    };

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
                Quest point  = (Quest) actor;
                String name = "q-"+point.hashCode();
                if (point.getPosition().dst(player.getPosition()) < 35f) {
                    if (point.getType()!=1 && point.getType()!=3) {
                        if (!userInterface.contains(name)) {
                            preferData = point.getData();
                            Button button = new Button(textButtonStyle);
                            button.setPosition(w - w / 12, 2.5f * h / 12);
                            button.setSize(h / 10, h / 10);
                            button.addListener(dataListener);
                            button.setName(name);
                            Text text = new Text(point.getTitle(), gsm.getRussianFont());
                            text.setPosition(point.getPosition().x, point.getPosition().y);
                            text.setName(name);
                            stage.addActor(text);
                            userInterface.addActor(button);
                        }
                    }else {
                        gsm.getPlatformInterface().send(point.getData());
                    }
                } else {
                    removeActor(stage.getActors(), name);
                    removeActor(userInterface.getChildren(), name);
                }

            }else if (actor instanceof TransferPoint){
                TransferPoint point  = (TransferPoint) actor;
                String name = "t-"+point.hashCode();
                if (point.getPosition().dst(player.getPosition()) < 35f) {
                    if (!userInterface.contains(name)) {
                        preferData = point.getData();
                        Button button = new Button(textButtonStyle);
                        button.setPosition(w - w/12, 2.5f * h / 12);
                        button.setSize(h/10,h/10);
                        button.addListener(dataListener);
                        button.setName(name);
                        Text text =new Text(point.getTitle(), gsm.getRussianFont());
                        text.setPosition(point.getX(), point.getY());
                        text.setName(name);
                        stage.addActor(text);
                        userInterface.addActor(button);
                    }
                } else {
                    removeActor(stage.getActors(), name);
                    removeActor(userInterface.getChildren(), name);
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

    void removeActor(Array<Actor> actors, String actorName){
        for(Actor actor : actors){
            if(actor.getName()!=null && actor.getName().equals(actorName)) {
                actor.remove();
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
       stage.getBatch().begin();

        if (player!=null)
            stage.getViewport().getCamera().position.set(player.getPosition(), 0);
        if (blur!=null)
            stage.getBatch().draw(blur,wb, hb);
        if (background!=null)
            stage.getBatch().draw(background, 0, 0);
        else if (gsm.get("texture")!=null){
            background = (Texture) gsm.get("texture");
        }

        stage.getBatch().end();
        stage.draw();

        uistage.draw();
    }

    @Override
    public void resize(int w, int h) {
        System.out.println("Resized: " + w + " : " + h);
        stage.getViewport().update(w, h, true);

    }

    @Override
    public void dispose() {
        stage.dispose();
        uistage.dispose();
        Gdx.app.debug(tag,"after dispose stages, heap " + Gdx.app.getNativeHeap());
        if (background!=null)
            background.dispose();
        if (bounds!=null)
            bounds.dispose();
        if (blur!=null)
            blur.dispose();
        Gdx.app.debug(tag,"after dispose textures, heap " + Gdx.app.getNativeHeap());
        Gdx.app.debug(tag,"after dispose font, heap " + Gdx.app.getNativeHeap());
        assetManager.dispose();
        Gdx.app.debug(tag,"after dispose asset manager and font, heap " + Gdx.app.getNativeHeap());
        if (player!=null)
            player.dispose();
        Gdx.app.debug(tag,"after dispose player, heap " + Gdx.app.getNativeHeap());
        if (userInterface!=null)
         userInterface.dispose();
        Gdx.app.debug(tag,"after dispose ui, heap " + Gdx.app.getNativeHeap());

    }

}
