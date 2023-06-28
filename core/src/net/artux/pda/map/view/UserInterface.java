package net.artux.pda.map.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.view.view.bars.Utils;

import javax.inject.Inject;

@PerGameMap
public class UserInterface extends Group {

    private final BitmapFont font;
    private final Stack stack;

    private final Group gameZone;
    private final UIFrame uiFrame;
    private final Skin skin;
    private final AssetsFinder assetsFinder;

    @Inject
    public UserInterface(Skin skin, AssetsFinder assetsFinder, UIFrame uiFrame) {
        super();
        this.assetsFinder = assetsFinder;
        this.stack = new OneStack();
        this.skin = skin;
        this.uiFrame = uiFrame;
        updateSkin(skin);

        font = assetsFinder.getFontManager().getFont(24);
        uiFrame.setFillParent(true);
        addActor(uiFrame);

        float w = uiFrame.getW();
        float h = uiFrame.getH();

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

    private void updateSkin(Skin skin) {
        Window.WindowStyle windowStyle = skin.get(Window.WindowStyle.class);
        windowStyle.titleFont = assetsFinder.getFontManager().getFont(38);
        windowStyle.background = Utils.getColoredDrawable(1, 1, Color.BLACK);
    }

}
