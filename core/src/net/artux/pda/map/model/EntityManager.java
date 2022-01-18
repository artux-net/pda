package net.artux.pda.map.model;

import static com.badlogic.gdx.math.MathUtils.random;

import static net.artux.pda.map.states.GameStateManager.getLabelStyle;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.artux.pda.map.model.components.HealthComponent;
import net.artux.pda.map.model.components.InteractiveComponent;
import net.artux.pda.map.model.components.PlayerComponent;
import net.artux.pda.map.model.components.ClickComponent;
import net.artux.pda.map.model.components.UserVelocityInput;
import net.artux.pda.map.model.components.MoodComponent;
import net.artux.pda.map.model.components.PositionComponent;
import net.artux.pda.map.model.components.SpriteComponent;
import net.artux.pda.map.model.components.StatesComponent;
import net.artux.pda.map.model.components.TargetMovingComponent;
import net.artux.pda.map.model.components.VelocityComponent;
import net.artux.pda.map.model.components.WeaponComponent;
import net.artux.pda.map.model.components.states.BotStatesAshley;
import net.artux.pda.map.model.systems.BattleSystem;
import net.artux.pda.map.model.systems.CameraFollowingSystem;
import net.artux.pda.map.model.systems.ClicksSystem;
import net.artux.pda.map.model.systems.InteractionSystem;
import net.artux.pda.map.model.systems.LogSystem;
import net.artux.pda.map.model.systems.MoodSystem;
import net.artux.pda.map.model.systems.MovingSystem;
import net.artux.pda.map.model.systems.RenderSystem;
import net.artux.pda.map.model.systems.StatesSystem;
import net.artux.pda.map.model.systems.TargetingSystem;
import net.artux.pda.map.states.State;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pdalib.Checker;
import net.artux.pdalib.Member;
import net.artux.pdalib.profile.Story;
import net.artux.pdalib.profile.items.Armor;
import net.artux.pdalib.profile.items.Weapon;

import java.util.HashMap;

public class EntityManager extends InputListener implements Disposable {

    Stage stage;
    AssetManager assetManager;
    Map map;
    Member member;

    Engine engine;

    RenderSystem renderSystem;
    BattleSystem battleSystem;
    ClicksSystem clicksSystem;

    public EntityManager(Engine engine, AssetManager assetManager, Stage stage, Map map, Member member, UserInterface userInterface) {
        this.engine = engine;
        this.stage = stage;
        this.assetManager = assetManager;
        this.map = map;
        this.member = member;

        clicksSystem = new ClicksSystem();

        //player
        Entity player = new Entity();
        UserVelocityInput velocityComponent = new UserVelocityInput();
        player.add(new PositionComponent(map.getPlayerPosition()))
                .add(new VelocityComponent())
                .add(new SpriteComponent(velocityComponent, assetManager.get("gg.png", Texture.class), 32, 32))
                .add(new WeaponComponent(member))
                .add(new MoodComponent(member))
                .add(new HealthComponent())
                .add(velocityComponent)
                .add(new PlayerComponent(stage.getViewport().getCamera(), member));
        engine.addEntity(player);

        createControlPointsEntities();
        createQuestPointsEntities();

        renderSystem = new RenderSystem(stage.getBatch());
        battleSystem = new BattleSystem(stage.getBatch());
        engine.addSystem(renderSystem);
        engine.addSystem(clicksSystem);
        engine.addSystem(battleSystem);
        engine.addSystem(new LogSystem(userInterface));
        engine.addSystem(new InteractionSystem(stage, userInterface));
        engine.addSystem(new StatesSystem());
        engine.addSystem(new TargetingSystem());
        engine.addSystem(new MoodSystem());
        engine.addSystem(new MovingSystem());
        engine.addSystem(new CameraFollowingSystem());
    }

    private void createControlPointsEntities(){
        JsonReader reader = new JsonReader(Gdx.files.internal("mobs.json").reader());
        Mobs mobs = new Gson().fromJson(reader, Mobs.class);

        for (final Spawn spawn : map.getSpawns()) {
            Mob mob = mobs.getMob(spawn.getId());

            Entity controlPoint = new Entity();
            float size = spawn.getR() * 2 * 0.9f;
            final Mob finalMob = mob;

            controlPoint.add(new PositionComponent(spawn.getPosition()))
                    .add(new SpriteComponent(assetManager.get("controlPoint.png", Texture.class),size, size))
                    .add(new ClickComponent(new ClickComponent.ClickListener() {
                        @Override
                        public void clicked() {
                            final Label text = new Label(finalMob.name, getLabelStyle());
                            text.setPosition(spawn.getPosition().x, spawn.getPosition().y);
                            stage.addActor(text);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(5000);
                                        text.remove();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }));
            engine.addEntity(controlPoint);


            TargetMovingComponent.Targeting targeting = new TargetMovingComponent.Targeting() {
                @Override
                public Vector2 getTarget() {
                    double r = (double) spawn.getR()/2 + random.nextInt(spawn.getR());

                    double angle = random.nextInt(360);

                    Vector2 basePosition = spawn.getPosition();
                    float x = (float) (Math.cos(angle) * r);
                    float y = (float) (Math.sin(angle) * r);
                    return new Vector2(basePosition.x+x,basePosition.y+y);
                }
            };

            for (int i = 0; i < spawn.getN(); i++) {
                Entity entity = new Entity();

                Armor armor = new Armor();

                Weapon w = new Weapon();
                w.speed=14;
                w.damage=2;
                w.precision=1;

                MoodComponent moodComponent = new MoodComponent(mob.group, mobs.getRelations(mob.group).toArray(new Integer[0]), spawn.isAngry());
                moodComponent.ignorePlayer = spawn.isIgnorePlayer();

                entity.add(new PositionComponent(targeting.getTarget()))
                        .add(new VelocityComponent())
                        .add(new HealthComponent())
                        .add(moodComponent)
                        .add(new WeaponComponent(armor, w, w))
                        .add(new StatesComponent<>(entity, BotStatesAshley.STANDING, BotStatesAshley.GUARDING))
                        .add(new TargetMovingComponent(targeting));


                Texture texture;
                if (member != null) {
                    if (mob.group < 0 || member.relations.get(mob.group) < -2)
                        texture = assetManager.get("red.png", Texture.class);
                    else if (member.relations.get(mob.group) > 2)
                        texture = assetManager.get("green.png", Texture.class);
                    else
                        texture = assetManager.get("yellow.png", Texture.class);
                } else texture = assetManager.get("yellow.png", Texture.class);

                entity.add(new SpriteComponent(texture,8,8));
                engine.addEntity(entity);
            }
        }
    }

    private void createQuestPointsEntities(){
        for (Point point : map.getPoints()) {
            if (member != null && Checker.check(point.getCondition(), member))
                if (point.getData().containsKey("chapter")) {
                    int storyId = Integer.parseInt(member.getData().getTemp().get("currentStory"));
                    for (Story story : member.getData().getStories()) {
                        if (story.getStoryId() == storyId
                                && (Integer.parseInt(point.getData().get("chapter")) == story.getLastChapter()
                                || Integer.parseInt(point.getData().get("chapter")) == 0))
                            addPoint(point);
                    }
                } else addPoint(point);

        }

        for (Transfer transfer : map.getTransfers()) {
            if (member!=null && Checker.check(transfer.condition, member))
                addTransferPoint(transfer);
        }

    }

    private void addPoint(final Point point) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(point.getPosition()))
                .add(new InteractiveComponent(point.getTitle(), point.type, new InteractiveComponent.InteractListener() {
                    @Override
                    public void interact() {
                        State.gsm.getPlatformInterface().send(point.getData());
                    }
                }));

        Texture texture = null;
        switch (point.type){
            case 0:
            case 1:
                texture = assetManager.get("quest.png", Texture.class);
                break;
            case 4:
                texture = assetManager.get("seller.png", Texture.class);
                break;
            case 5:
                texture = assetManager.get("cache.png", Texture.class);
                break;
            case 6:
                texture = assetManager.get("quest1.png", Texture.class);
                break;
        }
        if (texture!=null)
            entity.add(new SpriteComponent(texture));

        engine.addEntity(entity);
    }

    private void addTransferPoint(final Transfer point) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(point.getPosition()))
                .add(new InteractiveComponent(point.getMessage(), -1, new InteractiveComponent.InteractListener() {
                    @Override
                    public void interact() {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("map", String.valueOf(point.getTo()));
                        data.put("pos", point.getToPosition());
                        State.gsm.getPlatformInterface().send(data);
                    }
                }));

        entity.add(new SpriteComponent(assetManager.get("transfer.png", Texture.class), 32, 32));

        engine.addEntity(entity);
    }


    public void update(float dt){
        engine.update(dt);
    }

    public void draw(float dt){
        battleSystem.drawObjects(dt);
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        return clicksSystem.clicked(x, y);
    }

    @Override
    public void dispose() {
        battleSystem.dispose();
    }
}
