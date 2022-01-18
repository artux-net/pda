package net.artux.pda.map;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

import net.artux.pda.map.model.Bot;
import net.artux.pda.map.model.Player;

import java.util.Timer;
import java.util.TimerTask;

public enum PlayerStates implements State<Player> {

    FIND_TARGET(){
        @Override
        public void update(Player entity) {

        }
    },

    STANDING(){

        @Override
        public void enter(final Player entity) {

        }

        @Override
        public void update(Player entity) {


        }
    },

    MOVING(){
        @Override
        public void update(Player entity) {



        }
    },

    ATTACK(){
        @Override
        public void update(Player entity) {
            //entity.getStateMachine().setGlobalState();
        }
    };

    @Override
    public void enter(Player entity) {

    }


    @Override
    public void exit(Player entity) {

    }

    @Override
    public boolean onMessage(Player entity, Telegram telegram) {
        return false;
    }

}
