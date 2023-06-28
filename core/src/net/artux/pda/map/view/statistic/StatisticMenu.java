package net.artux.pda.map.view.statistic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.view.Logger;
import net.artux.pda.map.view.blocks.SlotTextButton;
import net.artux.pda.map.view.debug.widgets.ActionsWidget;
import net.artux.pda.map.view.debug.widgets.CheckBoxWidget;
import net.artux.pda.map.view.debug.widgets.ItemsWidget;
import net.artux.pda.map.view.debug.widgets.MapsWidget;
import net.artux.pda.map.view.debug.widgets.SoundsWidget;
import net.artux.pda.map.view.view.bars.Utils;

import javax.inject.Inject;

public class StatisticMenu extends Table {


    @Inject
    public StatisticMenu(Skin skin, Label.LabelStyle labelStyle, CheckBoxWidget checkBoxWidget,
                         Logger logger, ItemsWidget itemsWidget, MapsWidget mapsWidget,
                         SlotTextButton checks, SlotTextButton items) {
        super(skin);
        setFillParent(true);
        top();
        left();

        Label label = new Label("В разработке", labelStyle);
        top();
        add(label).colspan(3);
        row();
        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));
    }

}
