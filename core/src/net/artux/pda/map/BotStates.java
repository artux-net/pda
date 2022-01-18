package net.artux.pda.map;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

import net.artux.pda.map.model.Bot;
import net.artux.pda.map.model.Entity;
import net.artux.pdalib.profile.items.Weapon;

import java.util.Timer;
import java.util.TimerTask;

public enum BotStates implements State<Bot> {

    FIND_TARGET(){
        @Override
        public void update(Bot entity) {
            com.badlogic.ashley.core.Entity entity1 = new com.badlogic.ashley.core.Entity();
            entity.setMovementTarget(entity.getNewTarget());
            entity.getStateMachine().changeState(MOVING);
        }
    },

    STANDING(){

        @Override
        public void enter(final Bot entity) {
            entity.setMovementTarget(null);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    entity.getStateMachine().changeState(FIND_TARGET);
                }
            }, 1000 * (Math.abs(random.nextLong() % 30)));

        }

        @Override
        public void update(Bot entity) {
            entity.stand();
        }
    },

    MOVING(){
        @Override
        public void update(Bot entity) {
            if (entity.getPosition().dst(entity.movementTarget)<3f){
                entity.getStateMachine().changeState(STANDING);
            }else{
                entity.moveToTarget();
            }
        }
    },

    MOVING_FOR_SHOOT(){
        @Override
        public void update(Bot entity) {
            if (entity.getEnemy()!=null) {
                if (entity.getWeapon()!=null) {
                    Weapon weapon = entity.getWeapon();
                    if (entity.getEnemy().getPosition().dst(entity.getPosition()) < weapon.precision*100) {
                        entity.getStateMachine().changeState(SHOOT);
                    } else {
                        entity.setMovementTarget(entity.getEnemy().getPosition());
                        entity.moveToTarget();
                    }
                }
            }else entity.getStateMachine().changeState(GUARDING);
        }
    },

    SHOOT(){
        @Override
        public void update(Bot entity) {
            if (entity.getEnemy()!=null) {
                if (entity.getWeapon()!=null) {
                    Weapon weapon = entity.getWeapon();

                    if (entity.getEnemy().getPosition().dst(entity.getPosition()) < weapon.precision*100) {
                        //shooting
                    } else {
                        entity.getStateMachine().changeState(MOVING_FOR_SHOOT);
                    }

                }
            }else entity.getStateMachine().changeState(GUARDING);
        }
    },

    ATTACKING(){
        //global
        @Override
        public void update(Bot entity) {
            entity.getVelocity().scl(1/entity.getVelocity().len());
            //entity.getStateMachine().setGlobalState();
        }
    },

    GUARDING(){
        //global

        @Override
        public void enter(Bot entity) {

        }

        @Override
        public void update(Bot entity) {

        }
    };

    @Override
    public void enter(Bot entity) {

    }


    @Override
    public void exit(Bot entity) {

    }

    @Override
    public boolean onMessage(Bot entity, Telegram telegram) {
        if (telegram.message==1){
            entity.setEnemy((Entity) telegram.extraInfo);
        }
        return true;
    }

}
