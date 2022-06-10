package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import net.artux.pda.map.ui.UserInterface;

public class MessagesSystem extends EntitySystem {

    private UserInterface userInterface;

    public MessagesSystem(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public void addMessage(String content, String title){
        userInterface.getMessagesBlock().addMessage("ui/icons/ic_attention.png", content, title);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (userInterface.getMessagesBlock().getTable().getCells().size > 0)
            userInterface.getMessagesBlock().setTouchable(Touchable.enabled);
        else
            userInterface.getMessagesBlock().setTouchable(Touchable.disabled);
    }
}
