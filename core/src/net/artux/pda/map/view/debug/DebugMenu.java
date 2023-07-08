package net.artux.pda.map.view.debug;

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
import net.artux.pda.map.view.debug.widgets.MusicWidget;
import net.artux.pda.map.view.debug.widgets.SoundsWidget;
import net.artux.pda.map.view.view.bars.Utils;

import javax.inject.Inject;

public class DebugMenu extends Table {

    private final VerticalGroup buttons;
    private final ScrollPane selectPane;
    private final ScrollPane contentPane;

    @Inject
    public DebugMenu(Skin skin, Label.LabelStyle labelStyle, CheckBoxWidget checkBoxWidget,
                     Logger logger, ItemsWidget itemsWidget, MapsWidget mapsWidget,
                     ActionsWidget actionsWidget, MusicWidget musicWidget,
                     SoundsWidget soundsWidget,
                     SlotTextButton loggerButton,
                     SlotTextButton mapsButton,
                     SlotTextButton soundButton,
                     SlotTextButton musicButton,
                     SlotTextButton actionsButton,
                     SlotTextButton checks, SlotTextButton items) {
        super(skin);
        setFillParent(true);
        top();
        left();

        Label label = new Label("Консоль тестирования", labelStyle);
        top();
        add(label).colspan(3);
        row();
        buttons = new VerticalGroup();
        buttons.left().top();

        checks.setText("Настройки");
        checks.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setWidget(checkBoxWidget);
            }
        });
        loggerButton.setText("Логгер");
        loggerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setWidget(logger);
            }
        });
        items.setText("Выдача предметов");
        items.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setWidget(itemsWidget);
            }
        });
        mapsButton.setText("Карты");
        mapsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setWidget(mapsWidget);
            }
        });
        soundButton.setText("Звуки");
        soundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setWidget(soundsWidget);
            }
        });

        musicButton.setText("Музыка");
        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setWidget(musicWidget);
            }
        });
        actionsButton.setText("Действия");
        actionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setWidget(actionsWidget);
            }
        });

        buttons.addActor(checks);
        buttons.addActor(loggerButton);
        buttons.addActor(items);
        buttons.addActor(mapsButton);
        buttons.addActor(soundButton);
        buttons.addActor(musicButton);
        buttons.addActor(actionsButton);

        selectPane = new ScrollPane(buttons, skin);
        selectPane.setScrollingDisabled(true, false);
        contentPane = new ScrollPane(checkBoxWidget, skin);
        contentPane.setScrollingDisabled(true, false);
        contentPane.setClamp(false);

        add(selectPane).colspan(1).growY();
        add(contentPane).top().align(Align.top).colspan(2).grow();

        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));
    }

    public void setWidget(Actor actor) {
        contentPane.setActor(actor);
    }

}
