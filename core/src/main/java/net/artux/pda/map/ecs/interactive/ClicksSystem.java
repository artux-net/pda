package net.artux.pda.map.ecs.interactive;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.di.scope.PerGameMap;

import javax.inject.Inject;

@PerGameMap
public class ClicksSystem extends IteratingSystem implements GestureDetector.GestureListener{

    private final ComponentMapper<ClickComponent> cm = ComponentMapper.getFor(ClickComponent.class);
    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);

    @Inject
    public ClicksSystem() {
        super(Family.all(ClickComponent.class, BodyComponent.class).exclude(PassivityComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    public boolean clicked(float x, float y) {
        for (int i = 0; i < getEntities().size(); i++) {
            ClickComponent clickComponent = cm.get(getEntities().get(i));
            BodyComponent bodyComponent = pm.get(getEntities().get(i));

            if (bodyComponent.getPosition().epsilonEquals(x, y, clickComponent.clickRadius)) {
                clickComponent.clickListener.clicked();
                return true;
            }
        }


        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
