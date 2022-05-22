package net.artux.pda.map.engine;

import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class RandomPosition {

    Random random = new Random();

    public Vector2 getRandomAround(Vector2 point, int r){
        int min = -r;
        float offsetX =  random.nextInt(r - min) + min;
        float offsetY =  random.nextInt(r - min) + min;
        return point.cpy().add(offsetX, offsetY);
    }

}
