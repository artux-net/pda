package net.artux.pda.map.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.utils.Colors;

public class UserInterface extends Group implements Disposable {

    private final BitmapFont font;
    private final Stack stack;

    private final Group gameZone;
    private final UIFrame uiFrame;
    private final Skin skin;

    public UserInterface(AssetsFinder assetsFinder, Camera camera) {
        super();
        this.stack = new Stack();
        skin = new Skin(Gdx.files.internal("data/skin/uiskin.json")); //todo asset manager

        font = assetsFinder.getFontManager().getFont(24);
        uiFrame = new UIFrame(camera, font, Colors.primaryColor, Colors.backgroundColor);
        addActor(uiFrame);

        float w = camera.viewportWidth;
        float h = camera.viewportHeight;

        setSize(w,h);

        float gameZoneWidth = w - uiFrame.standartFrameSize - uiFrame.headerLeftX;
        float gameZoneHeight = h - uiFrame.standartFrameSize - uiFrame.topFrameHeight;
        stack.setSize(gameZoneWidth, gameZoneHeight);
        stack.setX(uiFrame.getHeaderLeftX());
        stack.setY(uiFrame.standartFrameSize);

        gameZone = new Group();
        gameZone.setSize(gameZoneWidth, gameZoneHeight);
        stack.add(gameZone);

        addActor(stack);
    }

    public Stack getStack() {
        return stack;
    }

    public Label.LabelStyle getLabelStyle() {
        return new Label.LabelStyle(font, Color.WHITE);
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
