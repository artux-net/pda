package net.artux.pda.map.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.engine.Pair;
import net.artux.pda.map.engine.Triple;
import net.artux.pda.map.engine.systems.LogSystem;
import net.artux.pdalib.Member;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Logger extends VerticalGroup implements Disposable {
    private final Label.LabelStyle labelStyle;
    long lastTimeCounted;
    private float sinceChange;
    private float frameRate;
    private final BitmapFont font;

    public static List<Triple<Method, Object, String>> dataCollection = new ArrayList<>();

    public abstract static class LogData {
        public static Vector2 position = new Vector2();
        public static float health;
        public static Member member;
        public static Vector2 logPoint;
    }

    public Logger(Engine engine) {
        super();

        lastTimeCounted = TimeUtils.millis();
        sinceChange = 0;
        frameRate = Gdx.graphics.getFramesPerSecond();
        font = Fonts.generateFont(Fonts.Language.RUSSIAN, Fonts.ARIAL_FONT, 16);
        labelStyle = new Label.LabelStyle(font, Color.WHITE);
        columnAlign(Align.left);


        LogSystem logSystem = engine.getSystem(LogSystem.class);

        put("FPS", this, "getFrameRate");
        put("Native Heap",  Gdx.app,"getNativeHeap");
        put("Java Heap", Gdx.app, "getJavaHeap");
        put("Player position", logSystem, "getPlayerPosition");
        put("Params", logSystem.getPlayerMember().getData(), "getParameters");

        put("Screen width", Gdx.app.getGraphics(),"getWidth");
        put("Height", Gdx.app.getGraphics(),"getHeight");
        put("Density", Gdx.app.getGraphics(),"getDensity");
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        update();
        for (Actor l : getChildren()) {
            for (Triple<Method, Object, String> tr :
                    dataCollection) {
                if (tr.getThird().equals(l.getName())){
                    try {
                        if (tr.getSecond()!=null && tr.getFirst()!=null)
                            ((Label) l).setText(tr.getThird() + ": " + tr.getFirst().invoke(tr.getSecond()).toString());

                        else
                            ((Label) l).setText(tr.getThird() + ": null");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            Label label = (Label)l;
            label.setFillParent(false);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
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

    public void put(String title, Object o, String nameOfMethod){
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
                label.setName(title);
                addActor(label);
            }
        }
    }

    public void dispose() {
        font.dispose();
    }

    public float getFrameRate() {
        return frameRate;
    }
}
