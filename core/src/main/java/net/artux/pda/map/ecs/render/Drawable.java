package net.artux.pda.map.ecs.render;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Drawable {
    void draw (Batch batch, float parentAlpha);
}
