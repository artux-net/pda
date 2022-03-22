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

public class CameraSystem extends EntitySystem {

    private ImmutableArray<Entity> players;
    private OrthographicCamera camera;

    private ComponentMapper<PlayerComponent> cm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    private float initialZoom = 0.5f;
    private static float maxZoom = 2f;
    private static float minZoom = 0.1f;
    private static float maxLimit = 1.4f;
    private static float minLimit = 0.4f;
    private static float zoomingSpeed = 2f;
    private static float specialZoomValue = 0.2f;
    boolean detached;
    boolean specialZoom = true;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class, VelocityComponent.class).get());
        for (int i = 0; i < players.size(); i++) {
            Entity entity = players.get(i);

            PlayerComponent playerComponent = cm.get(entity);
            camera = (OrthographicCamera) playerComponent.camera;
        }
        camera.zoom = initialZoom;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < players.size(); i++) {
            Entity entity = players.get(i);

            PositionComponent positionComponent = pm.get(entity);
            VelocityComponent velocityComponent = vm.get(entity);

            if (!detached){
                Vector2 cameraPos = new Vector2(camera.position.x, camera.position.y);
                Vector2 unit = positionComponent.getPosition().cpy().sub(cameraPos);

                float speed = cameraPos.dst(positionComponent.getPosition())*3f*(1/camera.zoom);

                if (1/unit.len() < 1.5){
                    unit.scl((1/unit.len())*deltaTime*speed);
                }

                moveBy(unit.x, unit.y);
            }
            if (!velocityComponent.velocity.isZero())
                detached = false;

            if (specialZoom){
                if (camera.zoom > specialZoomValue) {
                    float delta = camera.zoom - specialZoomValue;

                    camera.zoom -= delta * deltaTime * zoomingSpeed;
                }else if(camera.zoom != specialZoomValue){
                    float delta = specialZoomValue - camera.zoom;

                    camera.zoom += delta * deltaTime * zoomingSpeed;
                }
            } else if (!zooming){
                if (camera.zoom<minLimit){
                    float delta = minLimit - camera.zoom;

                    camera.zoom += delta*deltaTime*zoomingSpeed;
                }

                if (camera.zoom>maxLimit){
                    float delta = camera.zoom - maxLimit;
                    camera.zoom -= delta*deltaTime*zoomingSpeed;
                }
            }
        }
    }

    
    public void setDetached(boolean detached){
        this.detached = detached;
        specialZoom = false;
    }

    public void setSpecialZoom(boolean specialZoom) {
        this.specialZoom = specialZoom;
    }

    public void moveBy(float x, float y){
        float newX = camera.position.x + x*camera.zoom;
        float newY = camera.position.y + y*camera.zoom;
        if (newX <= GlobalData.mapWidth && newX >=0)
            camera.position.set(newX, camera.position.y, 0);
        if(newY <= GlobalData.mapHeight && newY >=0)
            camera.position.set(camera.position.x, newY, 0);
    }

    boolean zooming = false;

    public void setZooming(boolean zooming) {
        if (!this.zooming){
            specialZoomValue = camera.zoom;
        }
        this.zooming = zooming;
    }

    public boolean setZoom(float newZoom){
        if (newZoom<maxZoom && newZoom>minZoom){
            camera.zoom = newZoom;
            return true;
        } else return false;
    }

    public Vector2 getPosition(){
        return new Vector2(camera.position.x, camera.position.y);
    }


    public float getCurrentZoom(){
        return specialZoomValue;
    }
}
