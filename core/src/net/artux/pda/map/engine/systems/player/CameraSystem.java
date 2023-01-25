package net.artux.pda.map.engine.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.Position;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.engine.systems.BaseSystem;
import net.artux.pda.map.utils.Mappers;
import net.artux.pda.model.map.GameMap;

import javax.inject.Inject;

@PerGameMap
public class CameraSystem extends BaseSystem implements GestureDetector.GestureListener {

    private OrthographicCamera camera;
    private ClicksSystem clicksSystem;
    private GameMap map;

    private ComponentMapper<PlayerComponent> cm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    private Vector2 cameraV2Position = new Vector2();
    private Vector2 cameraSpeed = new Vector2();

    private float initialZoom = 0.5f;
    private static float maxZoom = 1f;
    private static float minZoom = 0.1f;
    private static float maxLimit = 0.8f;
    private static float minLimit = 0.4f;
    private static float zoomingSpeed = 2f;
    private static float specialZoomValue = 0.2f;
    boolean detached;
    boolean specialZoom = false;

    @Inject
    public CameraSystem(Camera camera, ClicksSystem clicksSystem, GameMap map) {
        super(Family.all().get());
        this.camera = (OrthographicCamera) camera;
        this.clicksSystem = clicksSystem;
        this.map = map;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        camera.zoom = initialZoom;

        camera.position.x = Mappers.vector2(map.getDefPos()).x;
        camera.position.y = Mappers.vector2(map.getDefPos()).y;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (isPlayerActive()) {
            Position position = pm.get(getPlayer());
            VelocityComponent velocityComponent = vm.get(getPlayer());

            if (!detached) {
                cameraV2Position.x = camera.position.x;
                cameraV2Position.y = camera.position.y;
                Vector2 unit = position.getPosition().cpy().sub(cameraV2Position);

                float speed = cameraV2Position.dst(position.getPosition()) * 3f * (1 / camera.zoom);

                if (1 / unit.len() < 1.5) {
                    unit.scl((1 / unit.len()) * deltaTime * speed);
                }

                internalMoveBy(unit.x, unit.y);
            }else{
                cameraSpeed.scl(0.9f);
                moveBy(cameraSpeed.x, cameraSpeed.y);
                if (cameraSpeed.len2() < 0.1)
                    cameraSpeed.set(0,0);
            }
            if (!velocityComponent.isZero()) {
                detached = false;
                cameraSpeed.set(0,0);
            }

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
    }

    public boolean isDetached() {
        return detached;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

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
        if (isPlayerActive()) {
            if (newX <= GlobalData.mapWidth && newX >= 0)
                camera.position.set(newX, camera.position.y, 0);
            if (newY <= GlobalData.mapHeight && newY >= 0)
                camera.position.set(camera.position.x, newY, 0);
        }
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
        if (newZoom < maxZoom && newZoom > minZoom && isPlayerActive()) {
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

    public OrthographicCamera getCamera() {
        return camera;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        setDetached(true);
        moveBy(-deltaX, deltaY);
        return true;
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
    public boolean touchDown(float x, float y, int pointer, int button) {
        cameraSpeed.set(0,0);
        if (clicksSystem != null)
            return clicksSystem.clicked(x, y);
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
        cameraSpeed.set(-velocityX, velocityY);
        cameraSpeed.scl(0.01f);
        return false;
    }

    private Vector2 lastPointer;
    private float lastZoom;

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        setZooming(true);
        setDetached(true);

        float panAmount = pointer1.dst(pointer2);
        float initAmount = initialPointer1.dst(initialPointer2);
        float newZoom = (initAmount / panAmount) * getCurrentZoom();

        Vector2 centerPoint = new Vector2((pointer1.x + pointer2.x) / 2, (pointer1.y + pointer2.y) / 2);

        if (lastPointer != null) {
            Vector2 cameraPosition = getPosition();
            Vector2 cameraShift = new Vector2(centerPoint.x - cameraPosition.x, cameraPosition.y - centerPoint.y);
            Vector2 cameraShiftValue = cameraShift.cpy().scl(lastZoom - newZoom);

            if (setZoom(newZoom))
                moveBy(cameraShiftValue.x, cameraShiftValue.y);

            moveBy(lastPointer.x - centerPoint.x, centerPoint.y - lastPointer.y);
        }

        lastPointer = centerPoint;
        lastZoom = newZoom;

        return false;
    }

    @Override
    public void pinchStop() {
        setZooming(false);
        lastPointer = null;
    }
}
