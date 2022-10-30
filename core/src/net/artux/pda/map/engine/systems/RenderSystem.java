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

import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.components.BodyComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.RelationalSpriteComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.entities.RelationType;

import java.util.HashMap;

public class RenderSystem extends BaseSystem implements Drawable {

    private Stage stage;

    private BitmapFont font;
    private Label.LabelStyle labelStyle;

    private ComponentMapper<RelationalSpriteComponent> rsm = ComponentMapper.getFor(RelationalSpriteComponent.class);
    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);

    private ImmutableArray<Entity> relationalEntities;
    private final HashMap<RelationType, Sprite> relationalSprites;

    public static boolean showAll = false;

    public RenderSystem(Stage stage, AssetsFinder assetsFinder) {
        super(Family.all(SpriteComponent.class, BodyComponent.class).get());
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
        relationalEntities = engine.getEntitiesFor(Family.all(RelationalSpriteComponent.class, MoodComponent.class, BodyComponent.class).get());
    }

    public void showText(String text, float x, float y) {
        if (!hasStageActorWithName(stage, text)) {
            final Label label = new Label(text, labelStyle);
            label.setName(text);
            label.setOrigin(Align.center);
            label.setPosition(x, y);
            stage.addActor(label);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        if (stage.getActors().contains(label, true))
                            label.remove();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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

    public void showText(String text, int x, int y, int offsetX, int offsetY) {
        showText(text, x + offsetX, y + offsetY);
    }

    public void showText(String text, Vector2 position) {
        showText(text, position.x, position.y);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {

        for (int i = 0; i < getEntities().size(); i++) {
            Entity entity = getEntities().get(i);
            Vector2 positionComponent = pm.get(entity).getBody().getPosition();
            SpriteComponent spriteComponent = sm.get(entity);
            Sprite sprite = spriteComponent.sprite;
            if (!showAll)
                batch.setColor(sprite.getColor());

            batch.draw(sprite, positionComponent.x - sprite.getOriginX(), positionComponent.y - sprite.getOriginY(), sprite.getOriginX(),
                    sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), spriteComponent.getRotation());
            batch.setColor(Color.WHITE);
        }

        for (int i = 0; i < relationalEntities.size(); i++) {
            Entity entity = relationalEntities.get(i);
            Vector2 positionComponent = pm.get(entity).getBody().getPosition();

            MoodComponent moodComponent = mm.get(player);
            MoodComponent entityMoodComponent = mm.get(entity);

            Sprite sprite = relationalSprites.get(RelationType.by(moodComponent.getRelation(entityMoodComponent)));
            RelationalSpriteComponent relationalSpriteComponent = rsm.get(entity);
            sprite.setAlpha(relationalSpriteComponent.getAlpha());
            if (!showAll)
                batch.setColor(sprite.getColor());

            batch.draw(sprite, positionComponent.x - sprite.getOriginX(), positionComponent.y - sprite.getOriginY(), sprite.getOriginX(),
                    sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), 0);
            batch.setColor(Color.WHITE);
        }

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
