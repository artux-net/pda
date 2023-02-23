package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.components.InteractiveComponent;
import net.artux.pda.map.engine.ecs.components.SpriteComponent;
import net.artux.pda.map.engine.ecs.components.StalkerComponent;
import net.artux.pda.map.engine.ecs.components.TimeComponent;
import net.artux.pda.map.view.LootMenu;
import net.artux.pda.map.view.UserInterface;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import javax.inject.Inject;

@PerGameMap
public class DeadCheckerSystem extends BaseSystem {

    private Group gameZone;
    private LootMenu lootMenu;
    private Label.LabelStyle labelStyle;
    private boolean deadMessage;
    private DataRepository dataRepository;
    private UserInterface userInterface;

    private ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

    private AssetManager assetManager;
    private final World world;

    @Inject
    public DeadCheckerSystem(UserInterface userInterface, LootMenu lootMenu, DataRepository dataRepository, AssetManager assetManager, World world) {
        super(Family.all(HealthComponent.class, BodyComponent.class).get());
        this.gameZone = userInterface;
        this.assetManager = assetManager;
        this.userInterface = userInterface;
        this.dataRepository = dataRepository;
        labelStyle = userInterface.getLabelStyle();
        this.world = world;
        labelStyle.fontColor = Color.RED;
        this.lootMenu = lootMenu;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for (int i = 0; i < getEntities().size(); i++) {
            Entity entity = getEntities().get(i);

            HealthComponent healthComponent = hm.get(entity);
            BodyComponent bodyComponent = pm.get(entity);

            if (healthComponent.isDead()) {
                final Entity deadEntity = new Entity();
                deadEntity.add(new BodyComponent(bodyComponent.getPosition(), world))
                        .add(new SpriteComponent(assetManager.get("gray.png", Texture.class), 4, 4));

                if (entity != getPlayer()) {
                    StalkerComponent stalkerComponent = entity.getComponent(StalkerComponent.class);
                    deadEntity.add(new InteractiveComponent("Обыскать: " + stalkerComponent.getName(), 5, () -> {
                        lootMenu.updateBot(stalkerComponent.getName(), stalkerComponent.getAvatar(), stalkerComponent.getInventory());
                        userInterface.getStack().add(lootMenu);

                        getEngine().removeEntity(deadEntity);
                    }))
                            .add(new TimeComponent(Instant.now().plus(1, ChronoUnit.MINUTES),
                                    () -> getEngine().removeEntity(entity)))
                            .add(stalkerComponent);
                } else {
                    getEngine().removeEntity(getPlayer());
                }

                getEngine().addEntity(deadEntity);
                getEngine().removeEntity(entity);
            }
        }
        if (!isPlayerActive()) {
            if (!deadMessage) {
                System.out.println("Dead message from :" + this);
                dataRepository.applyActions(Collections.singletonMap("xp", Collections.singletonList("-5")));

                TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
                style.font = labelStyle.font;
                style.fontColor = Color.RED;

                TextButton textButton = new TextButton("Игра провалена! \n Для продолжения нажмите в любом месте.", style);
                textButton.setFillParent(true);
                textButton.align(Align.center);
                textButton.getLabel().setAlignment(Align.center);
                userInterface.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        dataRepository.getPlatformInterface().restart();
                        super.clicked(event, x, y);
                    }
                });
                gameZone.clearChildren();
                gameZone.addActor(textButton);

                deadMessage = true;
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

}
