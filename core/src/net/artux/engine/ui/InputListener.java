package net.artux.engine.ui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Timer;

/**
 * See {@link InputProcessor} and
 * {@link GestureDetector.GestureListener} implementations.
 *
 * @author prynovx
 */

public class InputListener extends com.badlogic.gdx.scenes.scene2d.InputListener {

    boolean longPressFired;
    private static final float longPressSeconds = 1.1f;

    Vector2 pointer1 = new Vector2();
    private final Vector2 pointer2 = new Vector2();

    private final Timer.Task longPressTask = new Timer.Task() {
        @Override
        public void run() {
            if (!longPressFired) longPressFired = longPress(pointer1.x, pointer1.y);
        }
    };

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (pointer == 0)
            pointer1.set(x, y);
        else
            pointer2.set(x, y);

        if (pointer == 1)
            longPressTask.cancel();

        if (!longPressTask.isScheduled())
            Timer.schedule(longPressTask, longPressSeconds);
        return super.touchDown(event, x, y, pointer, button);
    }

    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        longPressTask.cancel();
        return super.mouseMoved(event, x, y);
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        super.touchUp(event, x, y, pointer, button);
    }
}
