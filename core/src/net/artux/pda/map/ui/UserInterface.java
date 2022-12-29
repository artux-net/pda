package net.artux.pda.map.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.utils.Colors;

import javax.inject.Inject;

@PerGameMap
public class UserInterface extends Group implements Disposable {

    public float w = Gdx.graphics.getWidth();
    public float h = Gdx.graphics.getHeight();

    private BitmapFont font;
    private Stack stack;

    private final Group gameZone;
    private UIFrame uiFrame;
    private BackpackMenu backpackMenu;
    private final Skin skin;

    private AssetManager assetManager;

    @Inject
    public UserInterface(AssetsFinder assetsFinder, Camera camera) {
        super();
        this.stack = new Stack();
        this.assetManager = assetsFinder.getManager();
        skin = new Skin(Gdx.files.internal("data/skin/uiskin.json"));
        long loadTime = TimeUtils.millis();

        font = assetsFinder.getFontManager().getFont(24);
        uiFrame = new UIFrame(camera, font, Colors.primaryColor, Colors.backgroundColor);
        addActor(uiFrame);

        float gameZoneWidth = w - uiFrame.standartFrameSize - uiFrame.headerLeftX;
        float gameZoneHeight = h - uiFrame.standartFrameSize - uiFrame.topFrameHeight;
        stack.setSize(gameZoneWidth, gameZoneHeight);
        stack.setX(uiFrame.getHeaderLeftX());
        stack.setY(uiFrame.standartFrameSize);

        gameZone = new Group();
        gameZone.setSize(gameZoneWidth, gameZoneHeight);
        stack.add(gameZone);

        addActor(stack);
        backpackMenu = new BackpackMenu(this, assetManager.get("skins/cloud/cloud-form-ui.json"));
        backpackMenu.setPosition(w / 4, h / 3);
        backpackMenu.setSize(w / 2, h / 2);

        Gdx.app.log("UI", "Loading took " + (TimeUtils.millis() - loadTime) + " ms.");
    }

    public Stack getStack() {
        return stack;
    }

    public Label.LabelStyle getLabelStyle() {
        return new Label.LabelStyle(font, Color.WHITE);
    }

    public void switchBackpack() {
        if (getChildren().indexOf(backpackMenu, false) == -1)
            addActor(backpackMenu);
        else
            removeActor(backpackMenu);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void dispose() {
        skin.dispose();
        uiFrame.dispose();
    }

    public Group getGameZone() {
        return gameZone;
    }

    public UIFrame getUIFrame() {
        return uiFrame;
    }
}
