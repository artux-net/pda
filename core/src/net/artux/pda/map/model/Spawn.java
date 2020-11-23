package net.artux.pda.map.model;

import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.states.PlayState;

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
    Entity enemy;
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

    public void create(){
        getPosition();

        final Random random = new Random();
        for(int i=0;i<n;i++) {
            final Bot bot = new Bot(id, getRandomPoint(random), this);
            bots.add(bot);
            PlayState.entities.add(bot);
            PlayState.stage.addActor(bot);
        }
    }

    Vector2 getRandomPoint(Random random){
        float x = (random.nextFloat() % 1)*r*2-r;
        int n = random.nextInt(2+1)-1;
        float y = n*(float) Math.sqrt(r*r - x*x);
        return new Vector2(position.x+x,position.y+y);
    }

    public void setEnemy(Spawn spawn) {
        setEnemy(spawn, 0,0);
    }

    void setEnemy(Spawn spawn, int i, int j) {
        bots.get(i).setEnemy(spawn.bots.get(j));
        if (i < bots.size() && j < spawn.bots.size())
            setEnemy(spawn, ++i, ++j);
        else if (i < bots.size())
            setEnemy(spawn, ++i, --j);
        else if (j < spawn.bots.size())
            setEnemy(spawn, --i, ++j);
    }
}
