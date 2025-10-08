package net.artux.pda.map.view.root;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.artux.pda.map.di.scope.PerGameMap;

import javax.inject.Inject;

@PerGameMap
public class TopSelectorMenu extends Table {

    private final Label.LabelStyle labelStyle;

    @Inject
    public TopSelectorMenu(Label.LabelStyle labelStyle) {
        super();
        this.labelStyle = labelStyle;

        align(Align.left);
        defaults()
                .pad(10);

        setTouchable(Touchable.enabled);
        addListener(new ClickListener());
    }

    public Cell<Actor> add(Actor actor, boolean asButton) {
        if (asButton && getCells().size > 0) {
            add(new Label("//", labelStyle));
        }
        return add(actor);
    }

}
