package net.artux.pda.map.ecs.ai.statemachine;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.ai.StatesComponent;
import net.artux.pda.map.ecs.render.Drawable;
import net.artux.pda.map.di.scope.PerGameMap;

import javax.inject.Inject;

@PerGameMap
public class StatesSystem extends IteratingSystem implements Drawable {

    private final ComponentMapper<StatesComponent> sm = ComponentMapper.getFor(StatesComponent.class);
    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);

    private final MessageManager messageManager = MessageManager.getInstance();
    private final BitmapFont font;
    private boolean debugStates;

    @Inject
    public StatesSystem(AssetsFinder assetsFinder) {
        super(Family.all(StatesComponent.class).get());
        this.font = assetsFinder.getFontManager().getFont(8);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        for (int i = 0; i < getEntities().size(); i++) {
            Entity entity = getEntities().get(i);
            sm.get(entity).getCurrentState().enter(entity);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        messageManager.update();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        sm.get(entity).update();
    }

    public void setDebugStates(boolean debugStates) {
        this.debugStates = debugStates;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (debugStates)
            for (int i = 0; i < getEntities().size(); i++) {
                Entity entity = getEntities().get(i);
                Vector2 position = pm.get(entity).getPosition();
                StatesComponent statesComponent = sm.get(entity);
                font.draw(batch, statesComponent.getStateTitle(), position.x - 16, position.y + 12);
            }
    }
}
