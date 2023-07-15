package net.artux.pda.map.view.collection.list;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

import javax.inject.Inject;

public abstract class ListView extends ScrollPane {

    private final VerticalGroup verticalGroup;

    public ListView() {
        super(new VerticalGroup().left().bottom());

        verticalGroup = (VerticalGroup) getActor();
        verticalGroup.pad(10);
        setScrollingDisabled(true, false);
    }

    public Actor addItemView(Actor view) {
        verticalGroup.addActor(view);
        return view;
    }

    public Actor removeItemView(Actor view) {
        verticalGroup.removeActor(view);
        return view;
    }

}
