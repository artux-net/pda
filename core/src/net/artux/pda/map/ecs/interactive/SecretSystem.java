package net.artux.pda.map.ecs.interactive;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.content.ContentGenerator;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.render.SpriteComponent;
import net.artux.pda.map.ecs.interactive.map.SecretComponent;
import net.artux.pda.map.ecs.render.RenderSystem;
import net.artux.pda.map.ecs.systems.BaseSystem;
import net.artux.pda.map.managers.notification.NotificationController;
import net.artux.pda.map.managers.notification.NotificationType;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.view.window.LootWindow;
import net.artux.pda.map.view.root.UserInterface;

import javax.inject.Inject;

@PerGameMap
public class SecretSystem extends BaseSystem {

    private final RenderSystem renderSystem;
    private final AssetManager assetManager;
    private final UserInterface userInterface;
    private final ContentGenerator contentGenerator;
    private final LootWindow lootWindow;

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<SecretComponent> sm = ComponentMapper.getFor(SecretComponent.class);
    private final ComponentMapper<SpriteComponent> scm = ComponentMapper.getFor(SpriteComponent.class);

    private final NotificationController notificationController;
    private final LocaleBundle localeBundle;

    @Inject
    public SecretSystem(AssetManager assetManager,
                        NotificationController notificationController,
                        RenderSystem renderSystem, UserInterface userInterface, ContentGenerator contentGenerator, LootWindow lootWindow, LocaleBundle localeBundle) {
        super(Family.all(SecretComponent.class, BodyComponent.class).get());
        this.assetManager = assetManager;
        this.userInterface = userInterface;
        this.contentGenerator = contentGenerator;
        this.lootWindow = lootWindow;
        this.localeBundle = localeBundle;
        this.renderSystem = renderSystem;
        this.notificationController = notificationController;

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int j = 0; j < getEntities().size(); j++) {
            Entity secretEntity = getEntities().get(j);
            BodyComponent body = pm.get(secretEntity);
            SecretComponent secret = sm.get(secretEntity);

            if (!isPlayerActive())
                continue;

            Entity player = getPlayer();
            BodyComponent playerBody = pm.get(player);
            float dst = playerBody.getPosition().dst(body.getPosition());
            if (dst > 10f)
                continue;

            if (!scm.has(secretEntity)) {
                int size = 23;
                String title = localeBundle.get("main.secret");
                secretEntity
                        .add(new InteractiveComponent(title, 5, () -> {
                            lootWindow.updateBot(title, "cache.png", contentGenerator.getRandomItems());
                            userInterface.getStack().add(lootWindow);

                            getEngine().removeEntity(secretEntity);
                        }))
                        .add(new SpriteComponent(assetManager.get("cache.png", Texture.class), size, size))
                        .add(new ClickComponent(size,
                                () -> renderSystem.showText(title, playerBody.getPosition())));
                notificationController.notify(NotificationType.ATTENTION,
                        localeBundle.get("main.secret.found"),
                        localeBundle.get("main.secret.found.desc", title));
            }

        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }


}
