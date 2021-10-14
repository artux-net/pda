package net.artux.pda.map.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.artux.pda.map.states.GameStateManager;
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

    public void create(final PlayState playState, AssetManager skin, List<Mob> mobs, Player player, final GameStateManager gsm){
        getPosition();

        final Random random = new Random();
        Mob mob = new Mob();
        for (Mob m:mobs){
            if (id==m.id){
                mob = m;
                System.out.println("Mob found "+ id +" " + mob.name);
            }
        }
        for(int i=0;i<n;i++) {
            final Bot bot = new Bot(id, getRandomPoint(random), this, skin, mob, player);
            bots.add(bot);
            playState.entities.add(bot);
            final Mob finalMob = mob;
            bot.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    final Text text = new Text(finalMob.name, gsm.getRussianFont());
                    text.setPosition(position.x, position.y);
                    System.out.println("Add text " + text);
                    playState.stage.addActor(text);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);
                                text.remove();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
            playState.stage.addActor(bot);
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
