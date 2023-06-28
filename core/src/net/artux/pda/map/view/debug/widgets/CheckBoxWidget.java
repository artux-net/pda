package net.artux.pda.map.view.debug.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.view.view.bars.Utils;

import javax.inject.Inject;

@PerGameMap
public class CheckBoxWidget extends Table {

    @Inject
    public CheckBoxWidget(Skin skin) {
        super(skin);
        left();
        defaults().left();
        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));
    }

    public void addCheckBox(String title, ChangeListener changeListener, boolean checked) {
        CheckBox checkBox = new CheckBox(title, getSkin());
        row();
        add(checkBox);
        checkBox.getImage().setScaling(Scaling.fit);
        checkBox.getImageCell().size(60, 60);
        checkBox.addListener(changeListener);
        checkBox.setChecked(checked);
    }

}
