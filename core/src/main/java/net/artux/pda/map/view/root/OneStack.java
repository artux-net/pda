package net.artux.pda.map.view.root;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

import java.util.Iterator;

public class OneStack extends Stack {

    public OneStack(){
        super();
    }

    @Override
    public void addActor(Actor actor) {
        if (getChildren().size > 1) {
            int lastIndex = getChildren().size - 1;
            removeActorAt(lastIndex, true);
        }
        super.addActor(actor);
    }
}
