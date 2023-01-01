package net.artux.pda.map.ui.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.ui.FontManager;
import net.artux.pda.model.quest.CheckpointModel;
import net.artux.pda.model.quest.MissionModel;

public class MissionBlock extends Table {

    public MissionBlock(Skin skin, MissionModel mission, AssetsFinder assetsFinder,String[] params) {
        super(skin);
        left();
        FontManager fontManager = assetsFinder.getFontManager();
        Label.LabelStyle titleLabelStyle = fontManager.getLabelStyle(32, Color.WHITE);

        Label title = new Label(mission.getTitle(), titleLabelStyle);
        title.setWrap(true);
        add(title)
                .growX();
        row();

        boolean actualGone = false;
        for (int i = 0; i < mission.getCheckpoints().size(); i++) {
            CheckpointModel checkpoint = mission.getCheckpoints().get(i);
            if (checkpoint.isActual(params))
                actualGone = true;

            CheckBox checkBox = new CheckBox(checkpoint.getTitle(), getSkin());
            checkBox.getLabel().setWrap(true);
            checkBox.getLabelCell().grow();
            checkBox.getImage().setScaling(Scaling.fit);
            checkBox.getImageCell().size(50, 50);
            checkBox.setChecked(!actualGone);
            checkBox.setDisabled(true);

            add(checkBox)
                    .left()
                    .growX();
            row();
        }

    }

}
