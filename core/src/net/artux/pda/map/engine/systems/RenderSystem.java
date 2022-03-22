package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.ui.Fonts;

public class RenderSystem extends EntitySystem implements Disposable {

    private Array<Entity> entities;
    private Batch batch;
    private Stage stage;

    private BitmapFont font;
    private Label.LabelStyle labelStyle;

    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    public RenderSystem(Stage stage) {
        this.batch = stage.getBatch();
        this.stage = stage;

        font = Fonts.generateFont(Fonts.Language.RUSSIAN, 16);
        labelStyle = new Label.LabelStyle(font, Color.WHITE);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = new Array<>(engine.getEntitiesFor(Family.all(SpriteComponent.class, PositionComponent.class).get()).toArray());

        engine.addEntityListener(Family.all(SpriteComponent.class, PositionComponent.class).get(), new EntityListener() {
            @Override
            public void entityAdded(Entity entity) {
                entities.add(entity);
            }

            @Override
            public void entityRemoved(Entity entity) {
                entities.removeValue(entity, true);
            }
        });

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);

            SpriteComponent spriteComponent = sm.get(entity);
            PositionComponent positionComponent = pm.get(entity);

            Sprite sprite = spriteComponent.sprite;
            batch.draw(sprite, positionComponent.getX()-sprite.getOriginX(), positionComponent.getY()-sprite.getOriginY(), sprite.getOriginX(),
                    sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), spriteComponent.getRotation());
        }
    }

    public void showText(String text, float x, float y){
        final Label label = new Label(text, labelStyle);
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

    public void showText(String text, int x, int y, int offsetX, int offsetY){
        showText(text, x + offsetX, y + offsetY);
    }

    public void showText(String text, Vector2 position){
        showText(text, position.x, position.y);
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
