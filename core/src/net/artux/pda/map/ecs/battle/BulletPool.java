package net.artux.pda.map.ecs.battle;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.ecs.global.WorldSystem;
import net.artux.pda.map.ecs.interactive.PassivityComponent;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.render.SpriteComponent;
import net.artux.pda.map.ecs.vision.FogOfWarComponent;

import javax.inject.Inject;

@PerGameMap
public class BulletPool extends Pool<Entity> {

    private final ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);
    private final Texture simpleBulletTexture;
    private final World world;
    private final WorldSystem worldSystem;
    private final PassivityComponent passivityComponent;
    private final ApplicationLogger logger;

    @Inject
    public BulletPool(AssetManager assetManager, World world, WorldSystem worldSystem, ApplicationLogger logger) {
        super(64);
        simpleBulletTexture = assetManager.get("textures/icons/entity/bullet.png", Texture.class);
        this.world = world;
        this.worldSystem = worldSystem;
        this.logger = logger;
        passivityComponent = new PassivityComponent();
    }

    @Override
    protected Entity newObject() {
        logger.log("BulletPool", "newObject");
        Entity entity = new Entity()
                .add(new BodyComponent(new Vector2(), BodyDef.BodyType.KinematicBody, world)
                        .velocity((double) 10000, (double) 10000))
                .add(new SpriteComponent(simpleBulletTexture, 15, 2))
                .add(new FogOfWarComponent())
                .add(new BulletComponent());
        worldSystem.addEntity(entity);
        return entity;
    }

    @Override
    public Entity obtain() {
        logger.log("BulletPool", "bullet obtained");
        Entity entity = super.obtain();
        bm.get(entity).body.setActive(true);
        entity.remove(PassivityComponent.class);
        return entity;
    }

    @Override
    public void free(Entity object) {
        logger.log("BulletPool", "bullet freed");
        super.free(object);
    }

    @Override
    protected void reset(Entity object) {
        super.reset(object);
        bm.get(object).body.setActive(false);
        object.add(passivityComponent);
    }

    @Override
    protected void discard(Entity object) {
        logger.log("BulletPool", "bullet discarded");
        super.discard(object);
        worldSystem.removeEntity(object);
    }
}
