package net.artux.pda.map.di.modules.ui;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.ecs.ai.EntityComponent;
import net.artux.pda.map.ecs.battle.MoodComponent;
import net.artux.pda.map.ecs.battle.PlayerBattleSystem;
import net.artux.pda.map.ecs.battle.WeaponComponent;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.map.ecs.interactive.InteractionSystem;
import net.artux.pda.map.ecs.interactive.InteractiveComponent;
import net.artux.pda.map.ecs.physics.PlayerMovingSystem;
import net.artux.pda.map.ecs.player.PlayerSystem;
import net.artux.pda.map.ecs.vision.VisionComponent;
import net.artux.pda.map.view.collection.list.MessagesList;
import net.artux.pda.map.view.label.PDALabel;
import net.artux.pda.map.view.root.UserInterface;
import net.artux.pda.map.view.view.DetailedHUD;
import net.artux.pda.map.view.view.bars.Slot;
import net.artux.pda.map.view.window.BackpackWindow;
import net.artux.pda.model.items.ItemModel;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;


@Module(includes = HeaderInterfaceModule.class)
public class UserInterfaceModule {

    @IntoSet
    @Provides
    public Actor initJoyTable(MessagesList messagesList, @Named("joyTable") Table joyTable,
                              @Named("gameZone") Group gameZone, AssetsFinder assetsFinder,
                              PlayerMovingSystem playerMovingSystem) {
        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();
        AssetManager assetManager = assetsFinder.getManager();
        style.knob = new TextureRegionDrawable(assetManager.get("textures/ui/touchpad/knob.png", Texture.class));
        style.background = new TextureRegionDrawable(assetManager.get("textures/ui/touchpad/back.png", Texture.class));
        style.knob.setMinHeight(170);
        style.knob.setMinWidth(170);

        Touchpad touchpad = new Touchpad(10, style);
        touchpad.setBounds(50, 50, 200, 200);
        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Color color = touchpad.getColor();
                float joyDeltaX = ((Touchpad) actor).getKnobPercentX();
                float joyDeltaY = ((Touchpad) actor).getKnobPercentY();
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                    playerMovingSystem.setRunning(true);
                playerMovingSystem.setVelocity(joyDeltaX, joyDeltaY);

                if (joyDeltaY == 0 && joyDeltaX == 0)
                    touchpad.setColor(color.r, color.g, color.b, 0.2f);
                else
                    touchpad.setColor(color.r, color.g, color.b, 0.9f);
            }
        });
        joyTable.add(messagesList);
        joyTable.row();
        float size = gameZone.getHeight() / 2.5f;
        joyTable.add(touchpad)
                .width(size)
                .height(size)
                .pad(50)
                .left()
                .bottom();
        return touchpad;
    }

    @IntoSet
    @Provides
    public Actor initAssistant(@Named("assistantTable") Table assistantBlock,
                               InteractionSystem interactionSystem, AssetManager assetManager) {
        HorizontalGroup horizontalGroup = new HorizontalGroup();
        horizontalGroup.pad(20f);
        horizontalGroup.space(10f);
        assistantBlock.add(horizontalGroup);
        assistantBlock.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                Collection<InteractiveComponent> components = interactionSystem.getInteractiveComponents();
                removeActors(horizontalGroup, components.stream().map(InteractiveComponent::getTitle).collect(Collectors.toList()));
                for (InteractiveComponent component : components) {
                    if (horizontalGroup.findActor(component.title) != null)
                        continue;
                    String icon;
                    switch (component.type) {
                        case FINDING:
                            icon = "textures/ui/icons/icon_search.png";
                            break;
                        case TRANSFER:
                            icon = "textures/ui/icons/ic_transfer.png";
                            break;
                        default:
                            icon = "textures/ui/icons/icon_dialog.png";
                            break;
                    }

                    TextureRegion textureRegion = new TextureRegion(assetManager.get(icon, Texture.class));
                    float pad = textureRegion.getRegionHeight() * 0.15f;

                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
                    style.up = new TextureRegionDrawable(assetManager.get("textures/ui/buttonBack.png", Texture.class));
                    style.imageUp = new TextureRegionDrawable(textureRegion);

                    ImageButton button = new ImageButton(style);
                    button.pad(pad);
                    button.setName(component.title);
                    button.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);
                            component.listener.interact();
                        }
                    });

                    horizontalGroup
                            .addActor(button);

                }
                return false;
            }

            private void removeActors(HorizontalGroup container, Collection<String> activeActions) {
                if (activeActions.size() != container.getChildren().size) {
                    for (int i = 0; i < container.getChildren().size; i++) {
                        Actor actor1 = container.getChild(i);
                        if (!activeActions.contains(actor1.getName()))
                            actor1.remove();
                    }
                }

            }

        });

        return assistantBlock;
    }

    private final ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private final ComponentMapper<EntityComponent> scm = ComponentMapper.getFor(EntityComponent.class);

    @IntoSet
    @Provides
    public Actor initHud(BackpackWindow backpackWindow, DetailedHUD hud, Slot weaponSlot,
                         @Named("hudTable") Table hudTable,
                         @Named("targetLabel") PDALabel targetLabel,
                         UserInterface userInterface, PlayerSystem playerSystem,
                         PlayerBattleSystem playerBattleSystem) {
        hudTable.add(hud);
        hud.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                if (userInterface.getStack().getChildren().contains(backpackWindow, false))
                    userInterface.getStack().removeActor(backpackWindow);
                else {
                    userInterface.getStack().add(backpackWindow);
                    backpackWindow.update();
                }
            }
        });

        targetLabel.setWrap(true);
        targetLabel.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                Entity player = playerSystem.getPlayer();
                Entity enemy = mm.get(player).getEnemy();
                if (enemy == null) {
                    targetLabel.setText("");
                    return false;
                }
                if (!scm.has(enemy))
                    return false;
                targetLabel.setText(scm.get(enemy).getDescription());
                return false;
            }
        });

        weaponSlot.pad(20);
        weaponSlot.addAction(new Action() {
            private final ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

            @Override
            public boolean act(float delta) {
                WeaponComponent entityWeapon = wm.get(playerSystem.getPlayer());
                ItemModel resource = entityWeapon.getBulletModel();
                if (entityWeapon.getSelected() != null) {
                    weaponSlot.setText(entityWeapon.getSelected().getTitle());
                    if (resource != null) {
                        weaponSlot.setLabelText(entityWeapon.getMagazine() + "/" + resource.getQuantity());
                    } else {
                        weaponSlot.setLabelText("0/0");
                    }
                } else {
                    weaponSlot.setText("Оружие отсутствует");//todo locale
                    weaponSlot.setLabelText("");
                }
                return false;
            }
        });
        weaponSlot.addListener(new ActorGestureListener() {
            private final ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                Entity entity = playerSystem.getPlayer();
                WeaponComponent entityWeapon = wm.get(entity);
                entityWeapon.switchWeapons();
            }

            @Override
            public boolean longPress(Actor actor, float x, float y) {
                playerBattleSystem.reload();
                return true;
            }
        });
        hudTable.add(weaponSlot).padLeft(20);
        hudTable.row();
        hudTable.add(targetLabel).pad(10).fillX();
        return hudTable;
    }

    @IntoSet
    @Provides
    public Actor initControlTable(@Named("controlTable") Table controlTable, @Named("gameZone") Group gameZone, PlayerSystem playerSystem,
                                  PlayerBattleSystem battleSystem, AssetManager assetManager, PlayerMovingSystem playerMovingSystem) {
        float offset = 50;
        float step = 60;
        controlTable.add(addInteractButton(assetManager, "textures/ui/icons/icon_shoot.png", new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                battleSystem.setPlayerShoot(true);
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                battleSystem.setPlayerShoot(false);
                super.touchUp(event, x, y, pointer, button);
            }
        })).padRight(offset += step);

        controlTable.row();

        controlTable.add(addInteractButton(assetManager, "textures/ui/icons/icon_target.png", new ChangeListener() {
            private final ComponentMapper<VisionComponent> vcm = ComponentMapper.getFor(VisionComponent.class);
            private final ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MoodComponent moodComponent = mm.get(playerSystem.getPlayer());
                LinkedList<Entity> playerEnemies = vcm.get(playerSystem.getPlayer())
                        .getVisibleEntities();

                Entity playerEnemyTarget = moodComponent.getEnemy();
                if (playerEnemies.size() > 1) {
                    Iterator<Entity> iterator = playerEnemies.iterator();
                    if (playerEnemyTarget == playerEnemies.getLast() ||
                            playerEnemyTarget == null || !playerEnemies.contains(playerEnemyTarget))
                        moodComponent.setEnemy(playerEnemies.getFirst());
                    else while (iterator.hasNext()) {
                        if (iterator.next() == playerEnemyTarget) {
                            moodComponent.setEnemy(iterator.next());
                            break;
                        }
                    }
                } else if (playerEnemies.size() == 1)
                    moodComponent.setEnemy(playerEnemies.getFirst());
            }
        })).padRight(offset += step);
        controlTable.row();

        ClickListener clickListener = new ClickListener();
        ImageButton runButton = addInteractButton(assetManager, "textures/ui/icons/icon_run.png", clickListener);
        runButton.addAction(new Action() {
            final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

            @Override
            public boolean act(float delta) {
                if (playerSystem.getPlayer() != null) {
                    HealthComponent healthComponent = hm.get(playerSystem.getPlayer());

                    playerMovingSystem.setRunning(runButton.isPressed() && healthComponent.getStamina() > 10f);
                }

                return false;
            }
        });

        controlTable.add(runButton)
                .padRight(offset + step)
                .padBottom(step);
        float size = gameZone.getHeight() / 3f;
        controlTable.setSize(size, size);

        return controlTable;
    }

    private ImageButton addInteractButton(AssetManager assetManager, String iconPath, EventListener listener) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(assetManager.get("textures/ui/buttonBack.png", Texture.class));
        TextureRegion textureRegion = new TextureRegion(assetManager.get(iconPath, Texture.class));
        float pad = textureRegion.getRegionHeight() * 0.15f;
        style.imageUp = new TextureRegionDrawable(textureRegion);

        ImageButton button = new ImageButton(style);
        button.pad(pad);
        button.addListener(listener);
        return button;
    }

}
