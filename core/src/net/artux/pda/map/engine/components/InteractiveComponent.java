package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;

public class InteractiveComponent implements Component {

    public String title;
    public Type type;
    public final InteractListener listener;

    public InteractiveComponent(String title, int type, InteractListener listener) {
        this.title = title;
        switch (type) {
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

    public void interact() {
        listener.interact();
    }

    public String getTitle() {
        return title;
    }

    public interface InteractListener {
        void interact();
    }

    public enum Type {
        DIALOG,
        ACTION,
        FINDING,
        TRANSFER
    }
}
