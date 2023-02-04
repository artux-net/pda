package net.artux.pda.map.di.modules.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.view.FontManager;
import net.artux.pda.map.view.UIFrame;
import net.artux.pda.map.view.UserInterface;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;


@Module
public class RootInterfaceModule {

    @Provides
    public UIFrame uiFrame(UserInterface userInterface) {
        return userInterface.getUIFrame();
    }

    @Provides
    public BitmapFont getFont(AssetsFinder assetsFinder) {
        return assetsFinder.getFontManager().getFont(24);
    }

    @Provides
    public Label.LabelStyle getLabelStyle(BitmapFont font) {
        return new Label.LabelStyle(font, Color.WHITE);
    }

    @Provides
    public TextButton.TextButtonStyle getTextButtonStyle(AssetsFinder assetsFinder) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        FontManager fontManager = assetsFinder.getFontManager();
        style.font = fontManager.getFont(24);
        style.fontColor = Color.WHITE;
        style.up = new TextureRegionDrawable(assetsFinder.getManager().get("ui/slots/slot_wide.png", Texture.class));
        return style;
    }

    @Provides
    @Named("assistantTable")
    public Table getAssistantTable(@Named("gameZone") Group gameZone) {
        Table assistantBlock = new Table();
        assistantBlock.setFillParent(true);
        assistantBlock.right().top();
        gameZone.addActor(assistantBlock);

        return assistantBlock;
    }

    @Provides
    @Named("gameZone")
    public Group getGameZone(UserInterface userInterface) {
        return userInterface.getGameZone();
    }

    @Provides
    @Named("hudTable")
    public Table getHudTable(@Named("gameZone") Group gameZone) {
        Table hudTable = new Table();
        hudTable.setFillParent(true);
        hudTable.left().top();

        gameZone.addActor(hudTable);

        return hudTable;
    }

    @Provides
    @Named("controlTable")
    public Table getControlTable(@Named("gameZone") Group gameZone) {
        Table controlBlock = new Table();
        controlBlock.setFillParent(true);
        controlBlock.right().bottom();
        controlBlock.defaults()
                .pad(10)
                .right();

        Color color = gameZone.getColor();
        color.a = 0.7f;
        controlBlock.setColor(color);
        color.a = 1f;
        gameZone.setColor(color);

        gameZone.addActor(controlBlock);
        return controlBlock;
    }

    @Provides
    @Named("joyTable")
    public Table getJoyTable(@Named("gameZone") Group gameZone) {
        Table table = new Table();
        table.setWidth(Gdx.graphics.getWidth()/3f);
        table.setFillParent(true);
        table.left().bottom();
        gameZone.addActor(table);
        return table;
    }

}
