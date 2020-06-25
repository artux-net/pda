package net.artux.pda.map.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class Spawn {
    private int id;
    private int r;
    private int n;
    private String pos;
    private Vector2 position = null;
    private HashMap<String, String> data;
    List<Bot> bots = new ArrayList<>();
    public HashMap<String, String> getData() {
        return data;
    }

    public Vector2 getPosition() {
        if (position==null) {
            String[] s = pos.split(":");
            position = new Vector2(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
        }
        return position;
    }

    public void create(Batch batch){
        getPosition();
        final Random random = new Random();
        for(int i=0;i<n;i++) {
            final Bot bot = new Bot(getRandomPoint(random), this);
            bots.add(bot);
        }
    }

    public void draw(Batch batch){
        for (Bot bot : bots)
            bot.draw(batch);
    }

    public void update(float dt){
        for (Bot bot : bots)
            bot.update(dt);
    }

    Vector2 getRandomPoint(Random random){

        float x = (random.nextFloat() % 1)*r*2-r;
        int n = random.nextInt(2+1)-1;
        float y = n*(float) Math.sqrt(r*r - x*x);
        System.out.println(x + "      "+y);
        return new Vector2(position.x+x,position.y+y);
    }
}
