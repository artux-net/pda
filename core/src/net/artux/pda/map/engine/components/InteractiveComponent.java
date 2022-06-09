package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;

import net.artux.pda.map.ui.UserInterface;

public class InteractiveComponent implements Component {

    public String title;
    public Type type;
    public InteractListener listener;

    public InteractiveComponent(String title, int type, InteractListener listener) {
        this.title = title;
        switch (type){
            /*case 0:
            case 1:
            case 4:
            case 6:*/

            case 5:
                this.type = Type.FINDING;
                break;
            case 1:
            case 3:
                this.type = Type.ACTION;
                break;
            case -1:
            case 7:
                this.type = Type.TRANSFER;
                break;
            default:
                this.type = Type.DIALOG;
                break;
        }
        this.listener = listener;
    }

    public interface InteractListener{
        void interact(UserInterface userInterface);
    }

    public enum Type{
        DIALOG,
        ACTION,
        FINDING,
        TRANSFER
    }
}
