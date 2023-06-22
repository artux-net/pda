package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ApplicationLogger;

import net.artux.pda.common.ActionHandler;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.engine.ecs.components.TimeComponent;
import net.artux.pda.map.managers.notification.NotificationController;
import net.artux.pda.map.utils.di.scope.PerGameMap;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@PerGameMap
public class ActionSystem extends BaseSystem implements ActionHandler {

    private final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private final ScriptEngine engine = scriptEngineManager.getEngineByName("nashorn");
    private final Bindings bindings;
    private final ApplicationLogger logger;
    private final NotificationController notificationController;
    private final DataRepository dataRepository;

    @Inject
    public ActionSystem(ApplicationLogger logger, NotificationController notificationController, DataRepository dataRepository) {
        super(Family.all(TimeComponent.class).get());
        this.dataRepository = dataRepository;
        dataRepository.setActionHandler(this);
        bindings = engine.createBindings();
        bindings.put("engine", engine);
        bindings.put("player", getPlayer());
        bindings.put("notificationController", notificationController);

        this.logger = logger;
        this.notificationController = notificationController;
    }

    public void eval(String script) {
        try {
            engine.eval(script, bindings);
        } catch (ScriptException e) {
            logger.error("ActionSystem", "Script error: " + e.getMessage());
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    @Override
    public void applyActions(Map<String, List<String>> actions) {
        for (String command : actions.keySet()) {
            switch (command){
                case "script":
                    for (String script : actions.get(command)) {
                        eval(script);
                    }
                    break;
            }
        }
    }
}
