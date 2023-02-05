package net.artux.pda.map.view;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

import net.artux.pda.map.engine.AssetsFinder;
import net.artux.engine.pathfinding.MapBorder;

public class UserInterface extends Group {

    private final BitmapFont font;
    private final Stack stack;

    private final Group gameZone;
    private final UIFrame uiFrame;
    private final Skin skin;

    public UserInterface(Skin skin, AssetsFinder assetsFinder, MapBorder mapBorder, Camera uiCamera, Camera camera) {
        super();
        this.stack = new Stack();
        this.skin = skin;

        font = assetsFinder.getFontManager().getFont(24);
        uiFrame = new UIFrame(assetsFinder.getManager(), camera, uiCamera, mapBorder, font);
        uiFrame.setFillParent(true);
        addActor(uiFrame);

        float w = uiCamera.viewportWidth;
        float h = uiCamera.viewportHeight;

        setSize(w, h);

        float gameZoneWidth = w - uiFrame.standardFrameSize - uiFrame.headerLeftX;
        float gameZoneHeight = h - uiFrame.standardFrameSize - uiFrame.topFrameHeight;
        stack.setSize(gameZoneWidth, gameZoneHeight);
        stack.setX(uiFrame.getHeaderLeftX());
        stack.setY(uiFrame.standardFrameSize);
        stack.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                uiFrame.setSlidersVisible(stack.getChildren().size < 2);
                return false;
            }
        });

        gameZone = new Group();
        gameZone.setSize(gameZoneWidth, gameZoneHeight);
        stack.add(gameZone);

        addActor(stack);
    }

    public Skin getSkin() {
        return skin;
    }

    public Stack getStack() {
        return stack;
    }

    public Label.LabelStyle getLabelStyle() {
        return new Label.LabelStyle(font, Color.WHITE);
    }

    public Group getGameZone() {
        return gameZone;
    }

    public UIFrame getUIFrame() {
        return uiFrame;
    }

}
