package net.artux.engine.ui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

/**
 * See {@link InputProcessor} and
 * {@link GestureDetector.GestureListener} implementations.
 *
 * @author prynovx
 */

public class InputListener extends com.badlogic.gdx.scenes.scene2d.InputListener {

    private final static int maxPointers = 10;
    private float tapRectangleWidth;
    private float tapRectangleHeight;
    private boolean inTapRectangle;
    private long tapCountInterval = 400L;
    boolean longPressFired;
    private static final float longPressSeconds = 1.1f;

    private Array<Vector2> initialPointers = new Array<>(maxPointers);
    private Array<Vector2> pointers = new Array<>(maxPointers);

    private final Timer.Task longPressTask = new Timer.Task() {
        @Override
        public void run() {
            if (!longPressFired)
                longPressFired = longPress(pointers.get(0).x, pointers.get(0).y);
        }
    };

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (pointer >= maxPointers) return false;
        initialPointers.get(pointer).set(x, y);
        /*if (pointer == 0) {
            pointer1.set(x, y);
            longPressFired = false;
            tapRectangleCenterX = x;
            tapRectangleCenterY = y;
        } else
            pointer2.set(x, y);*/

        if (pointer > 0)
            longPressTask.cancel();

        if (!longPressTask.isScheduled())
            Timer.schedule(longPressTask, longPressSeconds);
        return true;
    }

    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        return super.mouseMoved(event, x, y);
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
/*
        // check if we are still tapping.
        if (inTapRectangle && !isWithinTapRectangle(x, y, tapRectangleCenterX, tapRectangleCenterY))
            inTapRectangle = false;*/

        longPressTask.cancel();
        super.touchUp(event, x, y, pointer, button);
    }

    private boolean isWithinTapRectangle(float x, float y, float centerX, float centerY) {
        return Math.abs(x - centerX) < tapRectangleWidth && Math.abs(y - centerY) < tapRectangleHeight;
    }
}
