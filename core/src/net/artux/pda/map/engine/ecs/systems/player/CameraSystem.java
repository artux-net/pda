package net.artux.pda.map.engine.ecs.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.VelocityComponent;
import net.artux.pda.map.engine.ecs.systems.BaseSystem;
import net.artux.pda.map.utils.MapInfo;
import net.artux.pda.map.utils.Mappers;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.model.map.GameMap;

import javax.inject.Inject;

@PerGameMap
public class CameraSystem extends BaseSystem implements GestureDetector.GestureListener {

    private final OrthographicCamera camera;
    private final ClicksSystem clicksSystem;
    private final PlayerMovingSystem playerMovingSystem;
    private final GameMap map;
    private final MapInfo mapInfo;

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);

    private final Vector2 cameraV2Position = new Vector2();
    private final Vector2 cameraSpeed = new Vector2();

    private static final float maxZoom = 1f;
    private static final float minZoom = 0.1f;
    private static final float maxLimit = 0.8f;
    private static final float minLimit = 0.4f;
    private static final float zoomingSpeed = 2f;
    private static final float specialZoomValue = 0.2f;
    boolean detached;
    boolean specialZoom = false;

    @Inject
    public CameraSystem(Camera camera, MapInfo mapInfo, ClicksSystem clicksSystem, PlayerMovingSystem playerMovingSystem, GameMap map) {
        super(Family.all().get());
        this.camera = (OrthographicCamera) camera;
        this.mapInfo = mapInfo;
        this.clicksSystem = clicksSystem;
        this.playerMovingSystem = playerMovingSystem;
        this.map = map;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        camera.zoom = 0.5f;

        camera.position.x = Mappers.vector2(map.getDefPos()).x;
        camera.position.y = Mappers.vector2(map.getDefPos()).y;
    }

    Vector2 unit = new Vector2();

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (isPlayerActive()) {
            BodyComponent bodyComponent = pm.get(getPlayer());
            Vector2 velocity = playerMovingSystem.getVelocity();

            if (!detached) {
                cameraV2Position.x = camera.position.x;
                cameraV2Position.y = camera.position.y;
                unit.set(bodyComponent.getPosition()).sub(cameraV2Position);

                float speed = cameraV2Position.dst(bodyComponent.getPosition()) * 3f * (1 / camera.zoom);

                if (1 / unit.len() < 1.5) {
                    unit.scl((1 / unit.len()) * deltaTime * speed);
                }

                moveBy(unit.x, unit.y);
            } else {
                cameraSpeed.scl(0.9f);
                moveBy(cameraSpeed.x, cameraSpeed.y);
                if (cameraSpeed.len2() < 0.1)
                    cameraSpeed.set(0, 0);
            }
            if (!velocity.isZero()) {
                detached = false;
                cameraSpeed.set(0, 0);
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
        }
    }

    public boolean isDetached() {
        return detached;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    public void setDetached(boolean detached) {
        this.detached = detached;
        specialZoom = false;
    }

    public void setSpecialZoom(boolean specialZoom) {
        this.specialZoom = specialZoom;
    }

    public void moveBy(float x, float y) {
        float offsetX = x * camera.zoom;
        float offsetY = y * camera.zoom;

        Vector3 leftBottom = camera.frustum.planePoints[0];
        Vector3 topRight = camera.frustum.planePoints[2];

        if (topRight.x + offsetX > mapInfo.getMapWidth()) {
            offsetX = mapInfo.getMapWidth() - topRight.x;
        }
        if (leftBottom.x + offsetX < 0) {
            offsetX = -leftBottom.x;
        }

        if (leftBottom.y + offsetY < 0) {
            offsetY = -leftBottom.y;
        }

        if (topRight.y + offsetY > mapInfo.getMapHeight()) {
            offsetY = mapInfo.getMapHeight() - topRight.y;
        }

        if (isPlayerActive()) {
            camera.translate(offsetX, offsetY);
        }
        camera.update(true);
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
        cameraSpeed.set(0, 0);
        return true;
    }

    Vector3 cords = new Vector3();

    @Override
    public boolean tap(float x, float y, int count, int button) {
        cords.set(x, y, 0);
        camera.unproject(cords);
        return clicksSystem.clicked(cords.x, cords.y);
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

    Vector2 centerPoint = new Vector2();
    Vector2 cameraShift = new Vector2();

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        setZooming(true);
        setDetached(true);

        float panAmount = pointer1.dst(pointer2);
        float initAmount = initialPointer1.dst(initialPointer2);
        float newZoom = (initAmount / panAmount) * getCurrentZoom();

        centerPoint.set((pointer1.x + pointer2.x) / 2, (pointer1.y + pointer2.y) / 2);

        if (lastPointer != null) {
            Vector2 cameraPosition = getPosition();
            cameraShift.set(centerPoint.x - cameraPosition.x, cameraPosition.y - centerPoint.y);
            float mul = lastZoom - newZoom;

            if (setZoom(newZoom))
                moveBy(cameraShift.x * mul, cameraShift.y * mul);

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
