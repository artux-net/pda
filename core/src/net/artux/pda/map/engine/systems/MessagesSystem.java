package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

import net.artux.pda.map.model.Map;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pdalib.Member;

public class MessagesSystem extends EntitySystem {

    private UserInterface userInterface;

    public MessagesSystem(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public void addMessage(String content, String title){
        userInterface.getMessagesBlock().addMessage("ui/icons/ic_attention.png", content, title);
    }
}
