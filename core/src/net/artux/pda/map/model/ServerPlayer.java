package net.artux.pda.map.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.states.ArenaState;
import net.artux.pda.map.states.State;
import net.artux.pdalib.Member;

public class ServerPlayer extends Player {
    Vector2 lastPosition = new Vector2();
    Vector2 nextPosition = new Vector2();
    Vector2 serverVelocity = null;


    public ServerPlayer(State state, Vector2 playerPosition, Member member, AssetManager skin) {
        super(state, playerPosition, member, skin);
    }

    public void setNextPosition(float x, float y) {
        lastPosition.x = getX();
        lastPosition.y = getY();
        this.nextPosition.x = x;
        this.nextPosition.y = y;
        serverVelocity = nextPosition.sub(lastPosition).cpy();
    }

    @Override
    public void act(float delta) {
        if(ArenaState.getPing()!=0) {

            float targetX = getX(); //Player's X
            float targetY = getY(); //Player's Y
            float spriteX = nextPosition.x; //Enemy's X
            float spriteY = nextPosition.y; //Enemy's Y
            float x2 = spriteX; //Enemy's new X
            float y2 = spriteY; //Enemy's new Y
            float angle; // We use a triangle to calculate the new trajectory
            if (Math.abs(targetY - spriteY) > 2 || Math.abs(targetX - spriteX) > 2) {
                angle = (float) Math
                        .atan2(targetY - spriteY, targetX - spriteX);
                x2 += (float) Math.cos(angle) * 125
                        * Gdx.graphics.getDeltaTime();
                y2 += (float) Math.sin(angle) * 125
                        * Gdx.graphics.getDeltaTime();
                setPosition(x2, y2); //Set enemy's new positions.
            }
        }

    }
}
