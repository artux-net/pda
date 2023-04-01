package net.artux.pda.map.utils.di.modules.ui;

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
import net.artux.pda.map.engine.data.PlayerData;
import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.components.InteractiveComponent;
import net.artux.pda.map.engine.ecs.components.MoodComponent;
import net.artux.pda.map.engine.ecs.components.VelocityComponent;
import net.artux.pda.map.engine.ecs.components.VisionComponent;
import net.artux.pda.map.engine.ecs.components.WeaponComponent;
import net.artux.pda.map.engine.ecs.systems.player.InteractionSystem;
import net.artux.pda.map.engine.ecs.systems.player.PlayerBattleSystem;
import net.artux.pda.map.engine.ecs.systems.player.PlayerMovingSystem;
import net.artux.pda.map.engine.ecs.systems.player.PlayerSystem;
import net.artux.pda.map.view.BackpackMenu;
import net.artux.pda.map.view.UserInterface;
import net.artux.pda.map.view.blocks.MessagesPlane;
import net.artux.pda.map.view.view.DetailedHUD;
import net.artux.pda.map.view.view.bars.Slot;
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
    public Actor initJoyTable(MessagesPlane messagesPlane, @Named("joyTable") Table joyTable,
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
            private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

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
        joyTable.add(messagesPlane);
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
        assistantBlock.add(horizontalGroup);
        assistantBlock.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                Collection<InteractiveComponent> components = interactionSystem.getInteractiveComponents();
                removeActors(horizontalGroup, components.stream().map(InteractiveComponent::getTitle).collect(Collectors.toList()));
                for (InteractiveComponent component : components) {
                    if (horizontalGroup.findActor(component.title) == null) {
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


    @IntoSet
    @Provides
    public Actor initHud(BackpackMenu backpackMenu, DetailedHUD hud, Slot weaponSlot, @Named("hudTable") Table hudTable,
                         UserInterface userInterface, PlayerSystem playerSystem, PlayerBattleSystem playerBattleSystem) {
        hudTable.add(hud);
        hud.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                if (userInterface.getStack().getChildren().contains(backpackMenu, false))
                    userInterface.getStack().removeActor(backpackMenu);
                else {
                    userInterface.getStack().add(backpackMenu);
                }
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
                    PlayerData.selectedWeapon = entityWeapon.getSelected().getTitle();
                    weaponSlot.setText(entityWeapon.getSelected().getTitle());
                    if (resource != null) {
                        PlayerData.bullet = resource.getTitle();
                        weaponSlot.setLabelText(entityWeapon.getMagazine() + "/" + resource.getQuantity());
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

        controlTable.add(addInteractButton(assetManager, "textures/ui/icons/icon_run.png", new ClickListener() {
                    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        playerMovingSystem.setRunning(false);
                        super.touchUp(event, x, y, pointer, button);
                    }

                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        HealthComponent healthComponent = hm.get(playerSystem.getPlayer());
                        if (healthComponent.getStamina() > 10f)
                            playerMovingSystem.setRunning(true);
                        return super.touchDown(event, x, y, pointer, button);
                    }
                }))
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
