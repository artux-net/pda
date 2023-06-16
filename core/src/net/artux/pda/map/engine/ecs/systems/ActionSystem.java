package net.artux.pda.map.engine.ecs.systems;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ApplicationLogger;

import net.artux.pda.map.engine.ecs.components.TimeComponent;
import net.artux.pda.map.managers.notification.NotificationController;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.model.chat.ChatEvent;
import net.artux.pda.model.chat.UserMessage;

import javax.inject.Inject;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@PerGameMap
public class ActionSystem extends BaseSystem {

    private final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private final ScriptEngine engine = scriptEngineManager.getEngineByName("nashorn");
    private final Bindings bindings;
    private final ApplicationLogger logger;
    private final NotificationController notificationController;

    @Inject
    public ActionSystem(ApplicationLogger logger, NotificationController notificationController) {
        super(Family.all(TimeComponent.class).get());
        bindings = engine.createBindings();
        bindings.put("engine", engine);
        bindings.put("notificationController", notificationController);

        this.logger = logger;
        this.notificationController = notificationController;
    }

    public void eval(String script) {
        try {
            engine.eval(script, bindings);
        } catch (ScriptException e) {
            logger.error("ActionSystem","Script error: " + e.getMessage());
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
