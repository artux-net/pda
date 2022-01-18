package net.artux.pda.map.model.components;

import com.badlogic.ashley.core.Component;

public class ClickComponent implements Component {

    public ClickListener clickListener;

    public ClickComponent(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener{

        void clicked();

    }

}
