package net.artux.pda.map.view.view;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import net.artux.pda.map.di.scope.PerGameMap;

import javax.inject.Inject;

@PerGameMap
public class SelectorMenu extends Table {

    private final Label.LabelStyle labelStyle;

    @Inject
    public SelectorMenu(Label.LabelStyle labelStyle) {
        super();
        this.labelStyle = labelStyle;

        align(Align.left);
        defaults()
                .pad(10);
    }

    public Cell<Actor> add(Actor actor, boolean asButton) {
        if (asButton && getCells().size > 0) {
            add(new Label("//", labelStyle));
        }
        return add(actor);
    }

}
