package net.artux.pda.map.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Bot {

    private static float MOVEMENT = 0.3f;
    private boolean run;

    private Vector2 position;
    private Vector2 velocity;
    private Sprite sprite;
    private Vector2 target;
    boolean waiting;
    boolean timerStarted;
    private Spawn spawn;

    public Bot(Vector2 playerPosition, Spawn spawn) {
        position = playerPosition;
        velocity = new Vector2(0,0);
        sprite = new Sprite(new Texture("quest.png"), 0, 0, 23, 23);
        sprite.setSize(32, 32);
        sprite.setPosition(playerPosition.x, playerPosition.y);
        sprite.setOriginCenter();
        this.spawn = spawn;
    }

    public void setDestination(Vector2 point){
        target = point;
    }

    public void draw(Batch batch){
        sprite.draw(batch);
    }

    public void update(float dt){
        if (!waiting&&target!=null) {
            float x = target.x - position.x;
            float y = target.y - position.y;
            if (Math.abs(x) > 0) position.add(x/Math.abs(x)* MOVEMENT, 0);
            if (Math.abs(y) > 0) position.add(0, y/Math.abs(y)* MOVEMENT);
            if (Math.abs(x) <=5 && Math.abs(y)<=5) waiting = true;
            sprite.setPosition(position.x, position.y);
        }else{
            if (!timerStarted){
                final Random random = new Random();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        setDestination(spawn.getRandomPoint(random));
                        waiting = false;
                        timerStarted = false;
                    }
                }, 1000*(Math.abs(random.nextLong()%30)));
            }
            timerStarted = true;
        }
    }

}
