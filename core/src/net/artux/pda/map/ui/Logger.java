package net.artux.pda.map.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A nicer class for showing framerate that doesn't spam the console
 * like Logger.log()
 *
 * @author William Hartman
 */
public class Logger implements Disposable{
    long lastTimeCounted;
    private float sinceChange;
    private float frameRate;
    private final BitmapFont font;
    private final SpriteBatch batch;
    private OrthographicCamera cam;
    private final Player player;
    private final int x;
    private final int y;

    List<String> texts =  new ArrayList<>();

    public Logger(Player player, int x, int y) {
        this.player = player;
        this.x = x;
        this.y = y;

        lastTimeCounted = TimeUtils.millis();
        sinceChange = 0;
        frameRate = Gdx.graphics.getFramesPerSecond();
        font = new BitmapFont();
        batch = new SpriteBatch();
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        texts.add("Member null: " + (player.getMember() == null));
    }

    public void resize(int screenWidth, int screenHeight) {
        cam = new OrthographicCamera(screenWidth, screenHeight);
        cam.translate(screenWidth / 2, screenHeight / 2);
        cam.update();
        batch.setProjectionMatrix(cam.combined);
    }

    public void addText(String text){
        texts.add(text);
    }

    public void removeText(String text){
        texts.remove(text);
    }

    public void update() {
        long delta = TimeUtils.timeSinceMillis(lastTimeCounted);
        lastTimeCounted = TimeUtils.millis();

        sinceChange += delta;
        if(sinceChange >= 1000) {
            sinceChange = 0;
            frameRate = Gdx.graphics.getFramesPerSecond();
        }
    }

    public void render() {
        batch.begin();

        font.draw(batch, (int)frameRate + " fps", x, y - 3);
        font.draw(batch, "x: " + player.getPosition().x + ", y: " + player.getPosition().y, x, y - 15);
        font.draw(batch, "Player health: " + player.health, x, y - 30);
        font.draw(batch, "Native Heap: " + Gdx.app.getNativeHeap(), x, y - 60);
        font.draw(batch, "Screen: " + Gdx.app.getGraphics().getWidth() + ":" + Gdx.app.getGraphics().getHeight(), x, y - 75);
        font.draw(batch, "Density: " + Gdx.app.getGraphics().getDensity(), x, y - 90);
        font.draw(batch, "Version: " + Gdx.app.getVersion(), x, y - 105);
        for (int i = 0; i<texts.size(); i++){
            font.draw(batch, texts.get(i), x, y - 130 - i*15);
        }

        batch.end();
    }

    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}
