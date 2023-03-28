package net.artux.pda.map.engine.ecs.systems;

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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;

import net.artux.engine.graphics.postprocessing.PostProcessing;
import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.FogOfWarComponent;
import net.artux.pda.map.engine.ecs.components.MoodComponent;
import net.artux.pda.map.engine.ecs.components.PassivityComponent;
import net.artux.pda.map.engine.ecs.components.SpriteComponent;
import net.artux.pda.map.engine.ecs.entities.model.entities.RelationType;
import net.artux.pda.map.utils.di.scope.PerGameMap;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@PerGameMap
public class RenderSystem extends BaseSystem implements Drawable {

    private final Stage stage;

    private final Label.LabelStyle labelStyle;

    private final ComponentMapper<FogOfWarComponent> fog = ComponentMapper.getFor(FogOfWarComponent.class);
    private final ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);

    private ImmutableArray<Entity> relationalEntities;
    private final HashMap<RelationType, Sprite> relationalSprites;
    private final PostProcessing.ShaderGroup blurGroup;

    public static boolean showAll = false;

    @Inject
    public RenderSystem(@Named("gameStage") Stage stage, AssetsFinder assetsFinder, PostProcessing postProcessing) {
        super(Family.all(SpriteComponent.class, BodyComponent.class).exclude(PassivityComponent.class).get());
        this.stage = stage;

        BitmapFont font = assetsFinder.getFontManager().getFont(16);
        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        AssetManager assetManager = assetsFinder.getManager();
        relationalSprites = new HashMap<>();
        Sprite redSprite = new Sprite(assetManager.get("red.png", Texture.class));
        Sprite yellowSprite = new Sprite(assetManager.get("yellow.png", Texture.class));
        Sprite greenSprite = new Sprite(assetManager.get("green.png", Texture.class));
        redSprite.setSize(8, 8);
        yellowSprite.setSize(8, 8);
        greenSprite.setSize(8, 8);
        redSprite.setOriginCenter();
        yellowSprite.setOriginCenter();
        greenSprite.setOriginCenter();

        relationalSprites.put(RelationType.ENEMY, redSprite);
        relationalSprites.put(RelationType.NEUTRAL, yellowSprite);
        relationalSprites.put(RelationType.FRIEND, greenSprite);

        ShaderProgram shaderProgram = assetManager.get("shaders/blur.frag");
        blurGroup = postProcessing.loadShaderGroup("blur",
                List.of(Pair.of(shaderProgram, shaderProgram1 -> {
                            shaderProgram1.setUniformf("dir", 1f, 0);
                            shaderProgram1.setUniformf("radius", effect);
                            shaderProgram1.setUniformf("resolution", Gdx.graphics.getWidth());
                        }),
                        Pair.of(shaderProgram, shaderProgram12 -> {
                            shaderProgram12.setUniformf("dir", 0, 1f);
                            shaderProgram12.setUniformf("radius", effect);
                            shaderProgram12.setUniformf("resolution", Gdx.graphics.getHeight());
                        })));
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        relationalEntities = engine.getEntitiesFor(Family.all(MoodComponent.class, BodyComponent.class).exclude(SpriteComponent.class).get());
    }

    public void showText(String text, float x, float y) {
        if (!hasStageActorWithName(stage, text)) {
            final Label label = new Label(text, labelStyle);
            label.setName(text);
            label.setOrigin(Align.center);
            label.setPosition(x, y);
            stage.addActor(label);
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (stage.getActors().contains(label, true))
                        label.remove();
                }
            }, 5);
        }
    }

    private boolean hasStageActorWithName(Stage stage, String name) {
        for (Actor actor : stage.getActors()) {
            if (actor.getName() != null && actor.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void showText(String text, Vector2 position) {
        showText(text, position.x, position.y);
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (effect > 0) {
            effect -= deltaTime;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ImmutableArray<Entity> entities = getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            BodyComponent bodyComponent = pm.get(entity);
            SpriteComponent spriteComponent = sm.get(entity);
            Sprite sprite = spriteComponent.sprite;
            if (!showAll && fog.has(entity)) {
                sprite.setAlpha(fog.get(entity).visionCoefficient);
                batch.setColor(sprite.getColor());
            }

            batch.draw(sprite, bodyComponent.getX() - sprite.getOriginX(), bodyComponent.getY() - sprite.getOriginY(), sprite.getOriginX(),
                    sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), spriteComponent.getRotation());
            batch.setColor(Color.WHITE);
        }

        for (int i = 0; i < relationalEntities.size(); i++) {
            Entity entity = relationalEntities.get(i);
            BodyComponent bodyComponent = pm.get(entity);

            MoodComponent moodComponent = mm.get(getPlayer());
            MoodComponent entityMoodComponent = mm.get(entity);

            Sprite sprite = relationalSprites.get(RelationType.by(moodComponent.getRelation(entityMoodComponent)));
            if (!showAll && fog.has(entity)) {
                sprite.setAlpha(fog.get(entity).visionCoefficient);
                batch.setColor(sprite.getColor());
            }
            batch.draw(sprite, bodyComponent.getX() - sprite.getOriginX(), bodyComponent.getY() - sprite.getOriginY(), sprite.getOriginX(),
                    sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), 0);
            batch.setColor(Color.WHITE);
        }
        blurGroup.setEnabled(effect > 0);

    }

    float set = 0;
    float effect = 0;

    public void setEffect(int seconds) {
        effect += seconds;
        set = seconds;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
