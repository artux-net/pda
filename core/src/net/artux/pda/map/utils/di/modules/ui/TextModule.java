package net.artux.pda.map.utils.di.modules.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.map.view.FontManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class TextModule {

    @Provides
    @Named("titleStyle")
    public Label.LabelStyle getTitleLabelStyle(FontManager fontManager) {
        return fontManager.getLabelStyle(38, Color.WHITE);
    }

    @Provides
    @PerGameMap
    public BitmapFont getFont(AssetsFinder assetsFinder) {
        return assetsFinder.getFontManager().getFont(24);
    }

    @Provides
    @Named("descStyle")
    public Label.LabelStyle getLabelStyle(BitmapFont font) {
        return new Label.LabelStyle(font, Color.WHITE);
    }

    @Provides
    public Label.LabelStyle getDefaultLabelStyle(BitmapFont font) {
        return new Label.LabelStyle(font, Color.WHITE);
    }

    @Provides
    public TextButton.TextButtonStyle getTextButtonStyle(AssetsFinder assetsFinder) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        FontManager fontManager = assetsFinder.getFontManager();
        style.font = fontManager.getFont(24);
        style.fontColor = Color.WHITE;
        style.up = new TextureRegionDrawable(assetsFinder.getManager().get("textures/ui/slots/slot_wide.png", Texture.class));
        return style;
    }

}
