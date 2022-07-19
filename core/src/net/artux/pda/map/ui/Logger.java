package net.artux.pda.map.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.engine.Triple;
import net.artux.pda.map.engine.systems.PlayerSystem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Logger extends VerticalGroup implements Disposable {
    private final Label.LabelStyle labelStyle;
    long lastTimeCounted;
    private float sinceChange;
    private float frameRate;

    private final List<Triple<Method, Object, String>> dataCollection = new ArrayList<>();
    public static boolean visible = true;

    public Logger(Engine engine, Skin skin) {
        super();

        lastTimeCounted = TimeUtils.millis();
        sinceChange = 0;
        frameRate = Gdx.graphics.getFramesPerSecond();
        BitmapFont font = skin.getFont("font");
        labelStyle = new Label.LabelStyle(font, Color.WHITE);
        columnAlign(Align.right);

        PlayerSystem playerSystem = engine.getSystem(PlayerSystem.class);

        put("FPS", this, "getFrameRate");
        put("Native Heap", Gdx.app, "getNativeHeap");
        put("Java Heap", Gdx.app, "getJavaHeap");
        put("Player position", playerSystem, "getPosition");
        put("Здоровье", playerSystem, "getHealth");
        put("Params", playerSystem.getPlayerComponent().gdxData, "getParameters");
        put("Temp", playerSystem.getPlayerComponent().gdxData.getCurrent(), "toString");
        //put("Stories stat", Arrays.toString(playerSystem.getPlayerMember().getData().getStories().toArray()), "toString");

        put("Screen width", Gdx.app.getGraphics(), "getWidth");
        put("Height", Gdx.app.getGraphics(), "getHeight");
        put("Density", Gdx.app.getGraphics(), "getDensity");
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        update();
        for (Actor l : getChildren()) {
            for (Triple<Method, Object, String> tr :
                    dataCollection) {
                if (tr.getThird().equals(l.getName())) {
                    try {
                        if (tr.getSecond() != null && tr.getFirst() != null)
                            ((Label) l).setText(tr.getThird() + ": " + tr.getFirst().invoke(tr.getSecond()).toString());

                        else
                            ((Label) l).setText(tr.getThird() + ": null");
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (visible)
            super.draw(batch, parentAlpha);
    }

    public void update() {
        long delta = TimeUtils.timeSinceMillis(lastTimeCounted);
        lastTimeCounted = TimeUtils.millis();

        sinceChange += delta;
        if (sinceChange >= 1000) {
            sinceChange = 0;
            frameRate = Gdx.graphics.getFramesPerSecond();
        }
    }

    public void put(String title, Object o, String nameOfMethod) {
        Method method = null;
        try {
            if (nameOfMethod == null || nameOfMethod.equals(""))
                throw new NoSuchMethodException();
            method = o.getClass().getDeclaredMethod(nameOfMethod);

        } catch (NoSuchMethodException e) {
            try {
                method = o.getClass().getDeclaredMethod("toString");
            } catch (NoSuchMethodException ignored) {
                try {
                    method = o.getClass().getSuperclass().getDeclaredMethod(nameOfMethod);
                } catch (NoSuchMethodException noSuchMethodException) {
                    noSuchMethodException.printStackTrace();
                }
            }
        } finally {
            if (method != null) {
                dataCollection.add(new Triple<>(method, o, title));
                Label label = new Label(title, labelStyle);
                //label.setWrap(true);
                label.setName(title);
                addActor(label);
            }
        }
    }

    public void dispose() {
        dataCollection.clear();
    }

    public float getFrameRate() {
        return frameRate;
    }
}
