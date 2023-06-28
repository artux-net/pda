package net.artux.pda.map.view.debug.widgets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import net.artux.pda.map.ecs.sound.SoundsSystem;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.view.FontManager;
import net.artux.pda.map.view.view.bars.Utils;

import javax.inject.Inject;

@PerGameMap
public class SoundsWidget extends Table {

    @Inject
    public SoundsWidget(Skin skin, AssetManager assetManager, SoundsSystem soundsSystem, FontManager fontManager) {
        super(skin);
        left();
        defaults().left();
        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));

        Label.LabelStyle labelStyle = new Label.LabelStyle(fontManager.getFont(48), Color.GRAY);
        Array<Sound> out = new Array<>();
        assetManager.getAll(Sound.class, out);
        for (int i = 0; i < out.size; i++) {
            Sound sound = out.get(i);
            String name = assetManager.getAssetFileName(sound);
            Label label = new Label(name, labelStyle);
            add(label);
            row();
            label.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    soundsSystem.playSound(sound);
                }
            });
        }
    }


}
