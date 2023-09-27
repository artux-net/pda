package net.artux.pda.map.ecs.effects.ejection;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Timer;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.content.AnomalyHelper;
import net.artux.pda.map.ecs.anomaly.AnomalyComponent;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.map.ecs.interactive.PassivityComponent;
import net.artux.pda.map.ecs.interactive.map.SpawnComponent;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.player.PlayerSystem;
import net.artux.pda.map.ecs.render.RenderSystem;
import net.artux.pda.map.controller.notification.NotificationController;
import net.artux.pda.map.controller.notification.NotificationType;
import net.artux.pda.map.di.components.MapComponent;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.ecs.sound.AudioSystem;
import net.artux.pda.model.chat.UserMessage;
import net.artux.pda.model.map.SpawnModel;

import javax.inject.Inject;

@PerGameMap
public class EjectionSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> controls;

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);
    private final ComponentMapper<SpawnComponent> scm = ComponentMapper.getFor(SpawnComponent.class);

    private final RenderSystem renderSystem;
    private final NotificationController notificationController;
    private final LocaleBundle localeBundle;
    private final MapComponent mapComponent;
    private final PlayerSystem playerSystem;
    private final AudioSystem audioSystem;
    private final Music ejectionMusic;

    private static final boolean test = false;

    @Inject
    public EjectionSystem(MapComponent mapComponent, RenderSystem renderSystem,
                          AudioSystem audioSystem,
                          AssetManager assetManager,
                          NotificationController notificationController,
                          LocaleBundle localeBundle, PlayerSystem playerSystem) {
        this.playerSystem = playerSystem;
        this.mapComponent = mapComponent;
        this.renderSystem = renderSystem;
        this.localeBundle = localeBundle;
        this.audioSystem = audioSystem;
        this.notificationController = notificationController;

        ejectionMusic = assetManager.get("audio/music/background/ejection.ogg", Music.class);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family.all(HealthComponent.class, BodyComponent.class).get());
        controls = engine.getEntitiesFor(Family.all(SpawnComponent.class, BodyComponent.class)
                .exclude(PassivityComponent.class).get());

        int secs = random(120, 540);
        if (test)
            secs = 10;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                startEjection();
            }
        }, secs);
    }

    boolean ejectionActive = false;

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (ejectionActive)
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);
                BodyComponent entityBody = pm.get(entity);
                HealthComponent healthComponent = hcm.get(entity);

                boolean hidden = false;
                for (int j = 0; j < controls.size(); j++) {
                    Entity controlPoint = controls.get(j);
                    BodyComponent controlBody = pm.get(controlPoint);
                    SpawnModel spawnModel = scm.get(controlPoint).getSpawnModel();

                    float dst = entityBody.getPosition().dst(controlBody.getPosition());
                    if (dst < spawnModel.getR()) {
                        hidden = true;
                    }
                }

                if (entity == playerSystem.getPlayer()){
                    if (hidden)
                        notificationController.setTitle("В укрытии");
                    else
                        notificationController.setTitle("Вернитесь в укрытие");
                }

                if (hidden)
                    continue;

                healthComponent.health(-0.02f);
                healthComponent.radiationValue(0.007f);
            }

    }

    private void startEjection() {
        notificationController.notify(NotificationType.ATTENTION,
                localeBundle.get("main.ejection.notif"), localeBundle.get("main.ejection.notif.desc"));

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                processEjection();
            }
        }, random(15, 30));
    }

    public void processEjection() {
        ejectionActive = true;
        audioSystem.stopBackgroundMusic();
        audioSystem.playMusic(ejectionMusic);
        UserMessage message = notificationController.generateMessage();
        notificationController.setTitle(localeBundle.get("main.ejection.begin"));
        message.setContent(localeBundle.get("main.ejection.notif.message"));
        notificationController.addMessage(message);

        int secs = random(40, 90);
        renderSystem.setRedEffect(secs);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                endEjection();
            }
        }, secs);
    }

    private void endEjection() {
        ejectionActive = false;

        audioSystem.fadeStopMusic(ejectionMusic);
        audioSystem.startBackgroundMusic();
        getEngine().removeAllEntities(Family.all(AnomalyComponent.class).get());
        notificationController.notify(NotificationType.ATTENTION,
                localeBundle.get("main.ejection.end"), localeBundle.get("main.ejection.end.desc"));
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                AnomalyHelper.createAnomalies(mapComponent);
            }
        }, random(10, 15));

    }
}
