package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.components.FogOfWarComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PassivityComponent;
import net.artux.pda.map.engine.components.Position;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.entities.RelationType;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

@PerGameMap
public class RenderSystem extends BaseSystem implements Drawable {

    private Stage stage;

    private BitmapFont font;
    private Label.LabelStyle labelStyle;

    private ComponentMapper<FogOfWarComponent> fog = ComponentMapper.getFor(FogOfWarComponent.class);
    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);

    private ImmutableArray<Entity> relationalEntities;
    private final HashMap<RelationType, Sprite> relationalSprites;

    public static boolean showAll = false;

    @Inject
    public RenderSystem(@Named("gameStage") Stage stage, AssetsFinder assetsFinder) {
        super(Family.all(SpriteComponent.class, Position.class).exclude(PassivityComponent.class).get());
        this.stage = stage;

        font = assetsFinder.getFontManager().getFont(16);
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
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        relationalEntities = engine.getEntitiesFor(Family.all(MoodComponent.class, Position.class).exclude(SpriteComponent.class).get());
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
    public void draw(Batch batch, float parentAlpha) {
        ImmutableArray<Entity> entities = getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            Position position = pm.get(entity);
            SpriteComponent spriteComponent = sm.get(entity);
            Sprite sprite = spriteComponent.sprite;
            if (!showAll && fog.has(entity)) {
                sprite.setAlpha(fog.get(entity).visionCoefficient);
                batch.setColor(sprite.getColor());
            }

            batch.draw(sprite, position.getX() - sprite.getOriginX(), position.getY() - sprite.getOriginY(), sprite.getOriginX(),
                    sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), spriteComponent.getRotation());
            batch.setColor(Color.WHITE);
        }

        for (int i = 0; i < relationalEntities.size(); i++) {
            Entity entity = relationalEntities.get(i);
            Position position = pm.get(entity);

            MoodComponent moodComponent = mm.get(getPlayer());
            MoodComponent entityMoodComponent = mm.get(entity);

            Sprite sprite = relationalSprites.get(RelationType.by(moodComponent.getRelation(entityMoodComponent)));
            if (!showAll && fog.has(entity)) {
                sprite.setAlpha(fog.get(entity).visionCoefficient);
                batch.setColor(sprite.getColor());
            }
            batch.draw(sprite, position.getX() - sprite.getOriginX(), position.getY() - sprite.getOriginY(), sprite.getOriginX(),
                    sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), 0);
            batch.setColor(Color.WHITE);
        }

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
