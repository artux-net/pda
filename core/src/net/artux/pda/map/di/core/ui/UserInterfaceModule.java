package net.artux.pda.map.di.core.ui;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.VisionComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.data.PlayerData;
import net.artux.pda.map.engine.systems.player.InteractionSystem;
import net.artux.pda.map.engine.systems.player.PlayerBattleSystem;
import net.artux.pda.map.engine.systems.player.PlayerSystem;
import net.artux.pda.map.ui.BackpackMenu;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pda.map.ui.bars.HUD;
import net.artux.pda.map.ui.bars.Slot;
import net.artux.pda.map.ui.blocks.MessagesBlock;
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
    public Actor initJoyTable(@Named("joyTable") Table joyTable, @Named("gameZone") Group gameZone, AssetsFinder assetsFinder, PlayerSystem playerSystem) {
        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();
        AssetManager assetManager = assetsFinder.getManager();
        style.knob = new TextureRegionDrawable(assetManager.get("ui/touchpad/knob.png", Texture.class));
        style.background = new TextureRegionDrawable(assetManager.get("ui/touchpad/back.png", Texture.class));
        style.knob.setMinHeight(170);
        style.knob.setMinWidth(170);

        Touchpad touchpad = new Touchpad(10, style);
        touchpad.setPosition(0, 0);
        touchpad.setBounds(50, 50, gameZone.getHeight() / 2.5f, gameZone.getHeight() / 2.5f);
        touchpad.addListener(new ChangeListener() {
            private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Color color = touchpad.getColor();
                float joyDeltaX = ((Touchpad) actor).getKnobPercentX();
                float joyDeltaY = ((Touchpad) actor).getKnobPercentY();
                vm.get(playerSystem.getPlayer()).set(joyDeltaX, joyDeltaY);
                if (joyDeltaY == 0 && joyDeltaX == 0)
                    touchpad.setColor(color.r, color.g, color.b, 0.2f);
                else
                    touchpad.setColor(color.r, color.g, color.b, 0.9f);
            }
        });
        MessagesBlock messagesBlock = new MessagesBlock(assetsFinder);
        joyTable.add(messagesBlock);
        joyTable.row();
        joyTable.add(touchpad)
                .width(gameZone.getHeight() / 2.5f)
                .height(gameZone.getHeight() / 2.5f)
                .align(Align.left)
                .space(50f)
                .pad(50f);

        return touchpad;
    }

    @IntoSet
    @Provides
    public Actor initAssistant(@Named("assistantTable") Table assistantBlock, BackpackMenu backpackMenu,
                               UserInterface userInterface, InteractionSystem interactionSystem, AssetManager assetManager) {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = userInterface.getLabelStyle().font;
        textButtonStyle.fontColor = userInterface.getLabelStyle().fontColor;
        textButtonStyle.up = new TextureRegionDrawable(assetManager.get("ui/slots/slot_wide.png", Texture.class));
        TextButton backpackSlot = new TextButton("Рюкзак", textButtonStyle);

        backpackSlot.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (userInterface.getStack().getChildren().contains(backpackMenu, false))
                    userInterface.getStack().removeActor(backpackMenu);
                else
                    userInterface.getStack().add(backpackMenu);
            }
        });

        Table actionsTable = new Table();
        assistantBlock.add(actionsTable);
        actionsTable.add();

        assistantBlock
                .add(backpackSlot)
                .width(200);

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(assetManager.get("ui/slots/slot.png", Texture.class));
        assistantBlock.addAction(new Action() {

            @Override
            public boolean act(float delta) {
                Collection<InteractiveComponent> components = interactionSystem.getInteractiveComponents();
                for (InteractiveComponent component : components) {
                    if (assistantBlock.findActor(component.title) == null) {
                        String icon;
                        switch (component.type) {
                            case FINDING:
                                icon = "ui/icons/icon_search.png";
                                break;
                            case TRANSFER:
                                icon = "ui/icons/ic_transfer.png";
                                break;
                            default:
                                icon = "ui/icons/icon_dialog.png";
                                break;
                        }

                        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
                        style.up = new TextureRegionDrawable(assetManager.get("ui/buttonBack.png", Texture.class));
                        TextureRegion textureRegion = new TextureRegion(assetManager.get(icon, Texture.class));
                        float pad = textureRegion.getRegionHeight() * 0.15f;
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
                        assistantBlock.add(button);
                    }
                }
                removeActors(assistantBlock, components.stream().map(InteractiveComponent::getTitle).collect(Collectors.toList()));
                return false;
            }

            private void removeActors(Table container, Collection<String> activeActions) {
                for (Actor actor : container.getChildren()) {
                    if (actor.getName() != null && !activeActions.contains(actor.getName())) {
                        Cell<Actor> cell = container.getCell(actor);
                        actor.remove();
                        // remove cell from table
                        container.getCells().removeValue(cell, true);
                        container.invalidate();
                    }
                }
            }

        });

        return assistantBlock;
    }


    @IntoSet
    @Provides
    public Actor initHud(HUD hud, @Named("hudTable") Table hudTable, UserInterface userInterface, PlayerSystem playerSystem, AssetManager assetManager) {
        hudTable.add(hud);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = userInterface.getLabelStyle().font;
        style.fontColor = userInterface.getLabelStyle().fontColor;
        style.up = new TextureRegionDrawable(assetManager.get("ui/slots/slot.png", Texture.class));

        Slot weaponSlot = new Slot(userInterface, style);
        weaponSlot.pad(20);
        weaponSlot.addAction(new Action() {
            private ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

            @Override
            public boolean act(float delta) {
                WeaponComponent entityWeapon = wm.get(playerSystem.getPlayer());
                ItemModel resource = entityWeapon.getBulletModel();
                if (entityWeapon.getSelected() != null) {
                    PlayerData.selectedWeapon = entityWeapon.getSelected().getTitle();
                    if (resource != null) {
                        PlayerData.bullet = resource.getTitle();
                        weaponSlot.setLabelText(entityWeapon.getMagazine() + "/" + resource.getQuantity());
                        weaponSlot.setText(entityWeapon.getSelected().getTitle());
                    }
                }
                return false;
            }
        });
        weaponSlot.getCell(weaponSlot.getSecondLabel()).align(Align.left);
        weaponSlot.addListener(new ClickListener() {
            private ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Entity entity = playerSystem.getPlayer();
                WeaponComponent entityWeapon = wm.get(entity);
                entityWeapon.switchWeapons();
            }
        });
        hudTable.add(weaponSlot).padLeft(20);

        return hudTable;
    }

    @IntoSet
    @Provides
    public Actor initControlTable(@Named("controlTable") Table controlTable, PlayerSystem playerSystem, PlayerBattleSystem battleSystem,
                                  InteractionSystem interactionSystem, AssetManager assetManager) {
        controlTable.add(addInteractButton(assetManager, "", "ui/icons/icon_shoot.png", new ClickListener() {
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
        }));


        controlTable.row();
        controlTable.addAction(new Action() {
            InteractiveComponent activeComponent;
            Actor lastActor;

            @Override
            public boolean act(float delta) {
                InteractiveComponent interactiveComponent = interactionSystem.getActiveInteraction();
                if (interactiveComponent == null) {
                    activeComponent = null;
                    controlTable.removeActor(lastActor);
                } else if (lastActor == null) {
                    controlTable.row();

                    String icon;
                    switch (interactiveComponent.type) {

                        case FINDING:
                            icon = "ui/icons/icon_search.png";
                            break;
                        case TRANSFER:
                            icon = "ui/icons/ic_transfer.png";
                            break;
                        default:
                            icon = "ui/icons/icon_dialog.png";
                            break;
                    }

                    lastActor = addInteractButton(assetManager, "", icon, new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            interactiveComponent.listener.interact();
                            Cell<Actor> cell = controlTable.getCell(actor);
                            actor.remove();
                            controlTable.getCells().removeValue(cell, true);
                            controlTable.invalidate();
                        }
                    });
                }
                return true;
            }
        });


        controlTable.row();
        controlTable.add(addInteractButton(assetManager, "", "ui/icons/icon_run.png", new ClickListener() {
            private ComponentMapper<VelocityComponent> vcm = ComponentMapper.getFor(VelocityComponent.class);
            private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                vcm.get(playerSystem.getPlayer()).running = false;
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                HealthComponent healthComponent = hm.get(playerSystem.getPlayer());
                if (healthComponent.stamina > 10f)
                    vcm.get(playerSystem.getPlayer()).running = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        }));

        controlTable.row();
        controlTable.add(addInteractButton(assetManager, "", "ui/icons/icon_target.png", new ChangeListener() {
            private ComponentMapper<VisionComponent> vcm = ComponentMapper.getFor(VisionComponent.class);
            private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                LinkedList<Entity> playerEnemies = vcm.get(playerSystem.getPlayer()).getVisibleEntities();
                MoodComponent moodComponent = mm.get(playerSystem.getPlayer());
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
        }));
        controlTable.add().colspan(3);

        return controlTable;
    }

    private ImageButton addInteractButton(AssetManager assetManager, String id, String iconPath, EventListener listener) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(assetManager.get("ui/buttonBack.png", Texture.class));
        TextureRegion textureRegion = new TextureRegion(assetManager.get(iconPath, Texture.class));
        float pad = textureRegion.getRegionHeight() * 0.15f;
        style.imageUp = new TextureRegionDrawable(textureRegion);

        ImageButton button = new ImageButton(style);
        button.pad(pad);
        button.setName(id);
        button.addListener(listener);
        return button;
    }

}
