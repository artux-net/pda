package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.StalkerComponent;
import net.artux.pda.map.engine.components.TimeComponent;
import net.artux.pda.map.engine.systems.player.PlayerSystem;
import net.artux.pda.map.ui.UserInterface;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Random;

import javax.inject.Inject;

@PerGameMap
public class DeadCheckerSystem extends BaseSystem {

    private Group gameZone;
    private Label.LabelStyle labelStyle;
    private boolean deadMessage;
    private DataRepository dataRepository;
    private UserInterface userInterface;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

    private AssetManager assetManager;
    private Random random = new Random();

    @Inject
    public DeadCheckerSystem(UserInterface userInterface, DataRepository dataRepository, AssetManager assetManager) {
        super(Family.all(HealthComponent.class, PositionComponent.class).get());
        this.gameZone = userInterface;
        this.assetManager = assetManager;
        this.userInterface = userInterface;
        this.dataRepository = dataRepository;
        labelStyle = userInterface.getLabelStyle();
        labelStyle.fontColor = Color.RED;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for (int i = 0; i < getEntities().size(); i++) {
            Entity entity = getEntities().get(i);

            HealthComponent healthComponent = hm.get(entity);
            PositionComponent positionComponent = pm.get(entity);

            if (healthComponent.isDead()) {
                if (random.nextInt(10) < 5) {
                    final Entity deadEntity = new Entity();
                    deadEntity.add(new PositionComponent(positionComponent.getPosition()))
                            .add(new SpriteComponent(assetManager.get("gray.png", Texture.class), 4, 4));

                    if (entity != getPlayer()) {
                        StalkerComponent stalkerComponent = entity.getComponent(StalkerComponent.class);
                        deadEntity.add(new InteractiveComponent("Обыскать: " + stalkerComponent.getName(), 5, new InteractiveComponent.InteractListener() {
                                    @Override
                                    public void interact() {
                                        switch (random.nextInt(4)) {
                                            case 0:
                                                dataRepository.applyActions(Collections.singletonMap("add", Collections.singletonList("85:1")));
                                                break;
                                            case 1:
                                                dataRepository.applyActions(Collections.singletonMap("add", Collections.singletonList("83:2")));
                                                break;
                                            case 2:
                                                dataRepository.applyActions(Collections.singletonMap("add", Collections.singletonList("84:1")));
                                                break;
                                            case 3:
                                                dataRepository.applyActions(Collections.singletonMap("add", Collections.singletonList("85:2")));
                                                break;
                                            case 4:
                                                dataRepository.applyActions(Collections.singletonMap("add", Collections.singletonList("83:1")));
                                                break;
                                        }

                                        getEngine().removeEntity(deadEntity);
                                    }
                                }))
                                .add(new TimeComponent(Instant.now().plus(1, ChronoUnit.MINUTES),
                                        () -> getEngine().removeEntity(entity)))
                                .add(stalkerComponent);
                    } else {
                        getEngine().removeEntity(getPlayer());
                        getEngine().removeSystem(getEngine().getSystem(PlayerSystem.class));
                    }

                    getEngine().addEntity(deadEntity);
                }
                getEngine().removeEntity(entity);
            }
        }
        if (!isPlayerActive()) {
            if (!deadMessage) {
                System.out.println("Dead message from :" + this);
                dataRepository.applyActions(Collections.singletonMap("xp", Collections.singletonList("-5")));
                Group deadMessageGroup = new Group();

                TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
                style.font = labelStyle.font;
                style.fontColor = Color.RED;

                TextButton textButton = new TextButton("Игра провалена! \n Для продолжения нажмите в любом месте.", style);
                textButton.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                textButton.align(Align.center);
                textButton.getLabel().setAlignment(Align.center);
                deadMessageGroup.addActor(textButton);
                userInterface.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        dataRepository.getPlatformInterface().restart();
                        super.clicked(event, x, y);
                    }
                });
                gameZone.addActor(deadMessageGroup);

                deadMessage = true;
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

}
