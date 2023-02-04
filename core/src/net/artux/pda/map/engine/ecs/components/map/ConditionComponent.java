package net.artux.pda.map.engine.ecs.components.map;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionComponent extends HashMap<String, List<String>> implements Component {

    public ConditionComponent(Map<String, List<String>> condition) {
        super(condition);
    }
}