package net.artux.pda.map.model.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.model.components.player.PlayerComponent;
import net.artux.pda.map.model.components.PositionComponent;
import net.artux.pda.map.model.components.VelocityComponent;

public class CameraSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private OrthographicCamera camera;

    private ComponentMapper<PlayerComponent> cm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class, VelocityComponent.class).get());
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            PlayerComponent playerComponent = cm.get(entity);
            camera = (OrthographicCamera) playerComponent.camera;
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

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

            if (!zooming){
                if (camera.zoom<0.4f){
                    float delta = 0.4f - camera.zoom;

                    camera.zoom += delta*deltaTime*zoomingSpeed;
                }

                if (camera.zoom>1.3f){
                    float delta = camera.zoom - 1.3f;
                    camera.zoom -= delta*deltaTime*zoomingSpeed;
                }
            }
        }
    }

    boolean detached;
    public void setDetached(boolean detached){
        this.detached = detached;
    }

    public void moveBy(float x, float y){
        camera.position.add(x*camera.zoom, y*camera.zoom, 0);
    }

    boolean zooming = false;
    float currentZoom;
    float zoomingSpeed = 2f;

    public void setZooming(boolean zooming) {
        if (!this.zooming){
            currentZoom = camera.zoom;
        }
        this.zooming = zooming;

    }

    public void setZoom(float newZoom){
        if (newZoom<2f && newZoom>0.2f)
            camera.zoom = newZoom;
    }

    public float getCurrentZoom(){
        return currentZoom;
    }
}
