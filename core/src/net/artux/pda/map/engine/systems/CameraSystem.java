package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.VelocityComponent;

public class CameraSystem extends BaseSystem {

    private OrthographicCamera camera;

    private ComponentMapper<PlayerComponent> cm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    private Vector2 cameraV2Position = new Vector2();

    private float initialZoom = 0.5f;
    private static float maxZoom = 2f;
    private static float minZoom = 0.1f;
    private static float maxLimit = 1.4f;
    private static float minLimit = 0.4f;
    private static float zoomingSpeed = 2f;
    private static float specialZoomValue = 0.2f;
    boolean detached;
    boolean specialZoom = false;

    public CameraSystem() {
        super(null);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        PlayerComponent playerComponent = cm.get(player);
        camera = (OrthographicCamera) playerComponent.camera;
        camera.zoom = initialZoom;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        PositionComponent positionComponent = pm.get(player);
        VelocityComponent velocityComponent = vm.get(player);

        if (!detached) {
            cameraV2Position.x = camera.position.x;
            cameraV2Position.y = camera.position.y;
            Vector2 unit = positionComponent.getPosition().cpy().sub(cameraV2Position);

            float speed = cameraV2Position.dst(positionComponent.getPosition()) * 3f * (1 / camera.zoom);

            if (1 / unit.len() < 1.5) {
                unit.scl((1 / unit.len()) * deltaTime * speed);
            }

            internalMoveBy(unit.x, unit.y);
        }
        if (!velocityComponent.velocity.isZero())
            detached = false;

        if (specialZoom && !detached) {
            if (camera.zoom > specialZoomValue) {
                float delta = camera.zoom - specialZoomValue;

                camera.zoom -= delta * deltaTime * zoomingSpeed;
            } else if (camera.zoom != specialZoomValue) {
                float delta = specialZoomValue - camera.zoom;

                camera.zoom += delta * deltaTime * zoomingSpeed;
            }
        } else if (!zooming) {
            if (camera.zoom < minLimit) {
                float delta = minLimit - camera.zoom;

                camera.zoom += delta * deltaTime * zoomingSpeed;
            }

            if (camera.zoom > maxLimit) {
                float delta = camera.zoom - maxLimit;
                camera.zoom -= delta * deltaTime * zoomingSpeed;
            }
        }
        updateData();
    }

    private void updateData() {
        GlobalData.cameraPosX = camera.position.x;
        GlobalData.cameraPosY = camera.position.y;
        GlobalData.zoom = camera.zoom;
    }

    public void setDetached(boolean detached) {
        this.detached = detached;
        specialZoom = false;
    }

    public void setSpecialZoom(boolean specialZoom) {
        this.specialZoom = specialZoom;
    }

    private void internalMoveBy(float x, float y) {
        camera.position.x += x * camera.zoom;
        camera.position.y += y * camera.zoom;
    }

    public void moveBy(float x, float y) {
        float newX = camera.position.x + x * camera.zoom;
        float newY = camera.position.y + y * camera.zoom;
        if (newX <= GlobalData.mapWidth && newX >= 0)
            camera.position.set(newX, camera.position.y, 0);
        if (newY <= GlobalData.mapHeight && newY >= 0)
            camera.position.set(camera.position.x, newY, 0);
    }

    float currentZoom;
    boolean zooming = false;

    public void setZooming(boolean zooming) {
        if (!this.zooming) {
            currentZoom = camera.zoom;
        }
        this.zooming = zooming;
    }

    public boolean setZoom(float newZoom) {
        if (newZoom < maxZoom && newZoom > minZoom) {
            camera.zoom = newZoom;
            return true;
        } else return false;
    }

    public Vector2 getPosition() {
        return new Vector2(camera.position.x, camera.position.y);
    }


    public float getCurrentZoom() {
        return currentZoom;
    }
}
