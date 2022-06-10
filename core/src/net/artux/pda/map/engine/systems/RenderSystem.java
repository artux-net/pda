package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.ui.Fonts;

public class RenderSystem extends BaseSystem implements Drawable, Disposable {

    private Stage stage;

    private BitmapFont font;
    private Label.LabelStyle labelStyle;

    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    public static boolean showAll = false;

    public RenderSystem(Stage stage) {
        super(Family.all(SpriteComponent.class, PositionComponent.class).get());
        this.stage = stage;

        font = Fonts.generateFont(Fonts.Language.RUSSIAN, 16);
        labelStyle = new Label.LabelStyle(font, Color.WHITE);
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

    @Override
    public void draw(Batch batch, float parentAlpha) {

        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);

            SpriteComponent spriteComponent = sm.get(entity);
            PositionComponent positionComponent = pm.get(entity);

            Sprite sprite = spriteComponent.sprite;
            if (!showAll)
                batch.setColor(sprite.getColor());

            batch.draw(sprite, positionComponent.getX()-sprite.getOriginX(), positionComponent.getY()-sprite.getOriginY(), sprite.getOriginX(),
                    sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), spriteComponent.getRotation());
            batch.setColor(Color.WHITE);

        }

    }
}
