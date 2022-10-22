package net.artux.pda.map.engine.ai.states.stalker;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.WeaponComponent;

public class GlobalAttackingState extends StalkerState {
    public GlobalAttackingState(StateMachine<Entity, BotState> stateMachine) {
        super(stateMachine);
    }

    @Override
    public void enter(Entity entity) {

    }

    @Override
    public void update(Entity entity) {
        MoodComponent moodComponent = mm.get(entity);
        PositionComponent positionComponent = pm.get(entity);
        WeaponComponent weaponComponent = wm.get(entity);
        if (moodComponent.hasEnemy()) {
            HealthComponent healthComponent = hcm.get(moodComponent.getEnemy());
            PositionComponent enemyPosition = pm.get(moodComponent.getEnemy());
            if (healthComponent.isDead() || enemyPosition.getPosition().dst(positionComponent.getPosition()) > 200) {
                moodComponent.setEnemy(null);
                stateMachine.setGlobalState(new GlobalGuardingState(stateMachine));
            }else{
                if (weaponComponent.shoot()){

                }
            }
        } else {
            moodComponent.setEnemy(null);
            stateMachine.setGlobalState(new GlobalGuardingState(stateMachine));
        }
    }

    @Override
    public void exit(Entity entity) {

    }

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        return false;
    }
}
