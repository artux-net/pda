package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.QuestComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.model.items.MedicineModel;

import javax.inject.Inject;

@PerGameMap
public class PlayerSystem extends BaseSystem implements Disposable {

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<PlayerComponent> pmm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<VelocityComponent> vcm = ComponentMapper.getFor(VelocityComponent.class);

    private final float pixelsPerMeter = 3f;
    private InteractionSystem interactionSystem;


    @Inject
    public PlayerSystem(InteractionSystem interactionSystem) {
        super(Family.all(PositionComponent.class, QuestComponent.class).get());
        this.interactionSystem = interactionSystem;
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);

        //todo rthis
        interactionSystem.getUserInterface().getGameZone().setVisible(false);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        loadPreferences();
    }

    public Entity getPlayer() {
        return super.getPlayer();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (getPlayer() != null) {
            MoodComponent moodComponent = mm.get(getPlayer());
            PositionComponent playerPosition = pm.get(getPlayer());
            SpriteComponent spriteComponent = sm.get(getPlayer());
            Entity enemy = moodComponent.enemy;
            Vector2 direction;
            if (enemy == null) {
                direction = vcm.get(getPlayer()).getVelocity();
            } else {
                PositionComponent enemyPosition = pm.get(enemy);
                direction = enemyPosition.getPosition().cpy().sub(playerPosition.getPosition());
            }

            float degrees = (float) (Math.atan2(
                    -direction.x,
                    direction.y
            ) * 180.0d / Math.PI);

            float currentRotation = spriteComponent.getRotation() - 90;
            float alternativeRotation;
            if (currentRotation > 0)
                alternativeRotation = currentRotation - 360;
            else
                alternativeRotation = currentRotation + 360;

            float difference = currentRotation - degrees;
            if (Math.abs(alternativeRotation - degrees) < Math.abs(difference))
                difference = alternativeRotation - degrees;

            if (direction.x != 0 && direction.y != 0) {
                float step = difference * deltaTime * 20;
                spriteComponent.setRotation(spriteComponent.getRotation() - step);
            }

        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    public PlayerComponent getPlayerComponent() {
        return pmm.get(getPlayer());
    }

    public HealthComponent getHealthComponent() {
        return hm.get(getPlayer());
    }

    public Vector2 getPosition() {
        PositionComponent playerPosition = pm.get(getPlayer());
        return playerPosition.getPosition();
    }

    public int getDistance() {
        if (getEntities() != null && getEntities().size() > 0) {
            return (int) (getPosition().dst(pm.get(getEntities().first()).getPosition()) / pixelsPerMeter);//TODO select target
        } else return -1;
    }

    @Override
    public void dispose() {
        savePreferences();
    }

    public void savePreferences() {
        if (getPlayer() != null) {
            Preferences preferences = Gdx.app.getPreferences("player");

            HealthComponent healthComponent = getHealthComponent();
            preferences.putFloat("health", healthComponent.value);
            preferences.putFloat("radiation", healthComponent.radiation);

            preferences.flush();
        }
    }

    private void loadPreferences() {
        if (getPlayer() != null) {
            Preferences preferences = Gdx.app.getPreferences("player");

            HealthComponent healthComponent = getHealthComponent();
            healthComponent.value = preferences.getFloat("health", 100);
            if (healthComponent.isDead())
                healthComponent.value = 50;
            healthComponent.radiation = preferences.getFloat("radiation", 0);
        }
    }


    public interface MedicineListener {
        void treat(MedicineModel model);
    }
}
