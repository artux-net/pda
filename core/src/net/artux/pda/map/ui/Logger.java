package net.artux.pda.map.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pdalib.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Logger implements Disposable {
    long lastTimeCounted;
    private float sinceChange;
    private float frameRate;
    private final BitmapFont font;
    private final SpriteBatch batch;
    private final float x;
    private final float y;

    public static List<String> dataCollection = new ArrayList<>();

    public abstract static class LogData {
        public static float posX;
        public static float posY;
        public static float health;
        public static Member member;
        public static Vector2 logPoint;
    }

    public Logger(float x, float y) {
        this.x = x;
        this.y = y;

        lastTimeCounted = TimeUtils.millis();
        sinceChange = 0;
        frameRate = Gdx.graphics.getFramesPerSecond();
        font = Fonts.generateFont(Fonts.Language.RUSSIAN, Fonts.ARIAL_FONT, 16);
        batch = new SpriteBatch();
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

    public void addRow(String row){
        dataCollection.add(row);
    }

    public void render() {
        batch.begin();

        dataCollection.add("Player position, x: " + LogData.posX + ", y: " + LogData.posY);
        dataCollection.add("Player health: " + LogData.health);
        dataCollection.add("Money: " + LogData.member.getMoney());
        dataCollection.add("Player keys: " + Arrays.toString(LogData.member.getData().parameters.keys.toArray(new String[0])));
        dataCollection.add("Player values: " + LogData.member.getData().parameters.values.toString());
        dataCollection.add("");
        dataCollection.add((int) frameRate + " FPS");
        dataCollection.add("Native Heap: " + Gdx.app.getNativeHeap() + " Java Heap: " + Gdx.app.getJavaHeap());
        dataCollection.add("Screen: " + Gdx.app.getGraphics().getWidth() + ":" + Gdx.app.getGraphics().getHeight());
        dataCollection.add("Density: " + Gdx.app.getGraphics().getDensity());

        float step = 25;

        for (int i = 0; i < dataCollection.size(); i++) {
            font.draw(batch, dataCollection.get(i), x, y - i * step);
        }
        batch.end();

        dataCollection.clear();
    }

    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}
