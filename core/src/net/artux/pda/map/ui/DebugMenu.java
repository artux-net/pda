package net.artux.pda.map.ui;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.ui.view.bars.Utils;
import net.artux.pda.map.utils.Colors;

public class DebugMenu extends Table {

    private Label label;
    private Table content;

    public DebugMenu(Skin skin, Label.LabelStyle labelStyle) {
        super(skin);

        top();
        left();

        label = new Label("Режим тестирования", labelStyle);
        top();
        add(label);
        row();
        content = new Table();
        content.defaults().align(Align.left);
        content.left();
        ScrollPane scrollPane = new ScrollPane(content, skin);
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane).growX();

        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));
    }

    public void addCheckBox(String title, ChangeListener changeListener, boolean checked) {
        CheckBox checkBox = new CheckBox(title, getSkin());
        content.row();
        content.add(checkBox);
        checkBox.getImage().setScaling(Scaling.fit);
        checkBox.getImageCell().size(60, 60);
        checkBox.addListener(changeListener);
        checkBox.setChecked(checked);
    }

}
