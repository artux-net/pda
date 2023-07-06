package net.artux.pda.map.ecs.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Timer;

import net.artux.engine.graphics.postprocessing.PostProcessing;
import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.systems.BaseSystem;
import net.artux.pda.map.ecs.vision.FogOfWarComponent;
import net.artux.pda.map.ecs.ai.LeaderComponent;
import net.artux.pda.map.ecs.battle.MoodComponent;
import net.artux.pda.map.ecs.interactive.PassivityComponent;
import net.artux.pda.map.engine.entities.model.entities.RelationType;
import net.artux.pda.map.di.scope.PerGameMap;

import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Named;

@PerGameMap
public class RenderSystem extends BaseSystem implements Drawable {

    private final Stage stage;

    private final Label.LabelStyle labelStyle;
    private final Label label;
    private Timer.Task task;

    private final ComponentMapper<LeaderComponent> lc = ComponentMapper.getFor(LeaderComponent.class);
    private final ComponentMapper<FogOfWarComponent> fog = ComponentMapper.getFor(FogOfWarComponent.class);
    private final ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);

    private ImmutableArray<Entity> relationalEntities;
    private final EnumMap<RelationType, Sprite> relationalSprites;
    private final EnumMap<RelationType, Sprite> relationalLeaderSprites;
    private final PostProcessing.ShaderGroup blurGroup;
    private final PostProcessing.ShaderGroup redEjectGroup;
    private final PostProcessing.ShaderGroup redDamageGroup;

    public static boolean showAll = false;

    @Inject
    public RenderSystem(@Named("gameStage") Stage stage, AssetsFinder assetsFinder, PostProcessing postProcessing) {
        super(Family.all(SpriteComponent.class, BodyComponent.class).exclude(PassivityComponent.class).get());
        this.stage = stage;

        BitmapFont font = assetsFinder.getFontManager().getFont(16);
        labelStyle = new Label.LabelStyle(font, Color.WHITE);
        label = new Label("", labelStyle);
        relationalSprites = new EnumMap<>(RelationType.class);
        relationalLeaderSprites = new EnumMap<>(RelationType.class);

        AssetManager assetManager = assetsFinder.getManager();
        Sprite redSprite = new Sprite(assetManager.get("textures/icons/entity/red.png", Texture.class));
        Sprite yellowSprite = new Sprite(assetManager.get("textures/icons/entity/yellow.png", Texture.class));
        Sprite greenSprite = new Sprite(assetManager.get("textures/icons/entity/green.png", Texture.class));

        Sprite redStarSprite = new Sprite(assetManager.get("textures/icons/entity/star-red.png", Texture.class));
        Sprite yellowStarSprite = new Sprite(assetManager.get("textures/icons/entity/star-yellow.png", Texture.class));
        Sprite greenStarSprite = new Sprite(assetManager.get("textures/icons/entity/star-green.png", Texture.class));

        Consumer<Sprite> spriteConsumer = sprite -> {
            sprite.setSize(8, 8);
            sprite.setOriginCenter();
        };

        relationalSprites.put(RelationType.ENEMY, redSprite);
        relationalSprites.put(RelationType.NEUTRAL, yellowSprite);
        relationalSprites.put(RelationType.FRIEND, greenSprite);

        relationalLeaderSprites.put(RelationType.ENEMY, redStarSprite);
        relationalLeaderSprites.put(RelationType.NEUTRAL, yellowStarSprite);
        relationalLeaderSprites.put(RelationType.FRIEND, greenStarSprite);

        relationalLeaderSprites.values().forEach(spriteConsumer);
        relationalSprites.values().forEach(spriteConsumer);

        ShaderProgram shaderProgram = assetManager.get("shaders/blur.frag");
        blurGroup = postProcessing.loadShaderGroup("blur",
                List.of(Pair.of(shaderProgram, shaderProgram1 -> {
                            shaderProgram1.setUniformf("dir", 1f, 0);
                            shaderProgram1.setUniformf("radius", blurEffect);
                            shaderProgram1.setUniformf("resolution", Gdx.graphics.getWidth());
                        }),
                        Pair.of(shaderProgram, shaderProgram12 -> {
                            shaderProgram12.setUniformf("dir", 0, 1f);
                            shaderProgram12.setUniformf("radius", blurEffect);
                            shaderProgram12.setUniformf("resolution", Gdx.graphics.getHeight());
                        })));

        shaderProgram = assetManager.get("shaders/red.frag");
        redEjectGroup = postProcessing.loadShaderGroup("red",
                List.of(Pair.of(shaderProgram, shaderProgram1 -> {
                    shaderProgram1.setUniformf("red_value",
                            (float) Math.sin(redEffectAccumulator += 0.005f));
                })));

        redDamageGroup = postProcessing.loadShaderGroup("redDamage",
                List.of(Pair.of(shaderProgram, shaderProgram1 -> {
                    if (damageAccumulator > 3) {
                        shaderProgram1.setUniformf("red_value", 1);
                        damageAccumulator = 3;
                    }
                    else if (damageAccumulator < 0)
                        shaderProgram1.setUniformf("red_value", 0);
                    else shaderProgram1.setUniformf("red_value", damageAccumulator / 3f);
                })));
    }

    float redEffectAccumulator = 0;
    public float damageAccumulator = 0;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        relationalEntities = engine.getEntitiesFor(Family.all(MoodComponent.class, BodyComponent.class).exclude(SpriteComponent.class).get());
    }


    public void showText(String text, float x, float y) {
        label.setText(text);
        label.invalidate();
        x -= label.getPrefWidth() / 2;
        y += 23;
        label.setPosition(x, y);
        if (!stage.getActors().contains(label, true))
            stage.addActor(label);
        if (task != null && task.isScheduled()) {
            task.cancel();
        }
        task = new Timer.Task() {
            @Override
            public void run() {
                if (stage.getActors().contains(label, true))
                    label.remove();
            }
        };
        Timer.schedule(task, 5);
    }

    public void showText(String text, Vector2 position) {
        showText(text, position.x, position.y);
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (blurEffect > 0) {
            blurEffect -= deltaTime;
        }
        if (redEffect > 0) {
            redEffect -= deltaTime;
        }
        if (damageAccumulator > 0) {
            damageAccumulator -= deltaTime;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ImmutableArray<Entity> entities = getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            BodyComponent bodyComponent = pm.get(entity);
            SpriteComponent sprite = sm.get(entity);
            if (!showAll && fog.has(entity)) {
                sprite.setAlpha(fog.get(entity).visionCoefficient);
                batch.setColor(sprite.getColor());
            }

            batch.draw(sprite, bodyComponent.getX() - sprite.getOriginX(), bodyComponent.getY() - sprite.getOriginY(), sprite.getOriginX(),
                    sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation());
            batch.setColor(Color.WHITE);
        }

        for (int i = 0; i < relationalEntities.size(); i++) {
            Entity entity = relationalEntities.get(i);
            BodyComponent bodyComponent = pm.get(entity);

            MoodComponent moodComponent = mm.get(getPlayer());
            MoodComponent entityMoodComponent = mm.get(entity);
            Sprite sprite;

            if (lc.has(entity) && !relationalLeaderSprites.isEmpty()) {
                sprite = relationalLeaderSprites.get(RelationType.by(moodComponent.getRelation(entityMoodComponent)));
            } else
                sprite = relationalSprites.get(RelationType.by(moodComponent.getRelation(entityMoodComponent)));

            if (!showAll && fog.has(entity)) {
                sprite.setAlpha(fog.get(entity).visionCoefficient);
                batch.setColor(sprite.getColor());
            }
            batch.draw(sprite, bodyComponent.getX() - sprite.getOriginX(), bodyComponent.getY() - sprite.getOriginY(), sprite.getOriginX(),
                    sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), 0);
            batch.setColor(Color.WHITE);
        }
        blurGroup.setEnabled(blurEffect > 0);
        redEjectGroup.setEnabled(redEffect > 0);
        redDamageGroup.setEnabled(damageAccumulator > 0);
    }

    float redEffect = 0;
    float blurEffect = 0;

    public void setBlurEffect(int seconds) {
        blurEffect += seconds;
    }

    public void setRedEffect(float redEffect) {
        this.redEffect = redEffect;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
