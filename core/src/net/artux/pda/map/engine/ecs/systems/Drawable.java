package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Drawable {
    void draw (Batch batch, float parentAlpha);
}
