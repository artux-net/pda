package net.artux.pda.map.engine.components;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.Component;

public class DeadComponent implements Component {

    public String name;
    public String group;
    public int avatar;

    public DeadComponent(String name, String group) {
        this.name = name;
        this.group = group;
        this.avatar = random(0, 30);
    }
}
