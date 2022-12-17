package net.artux.pda.map.di.core;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import net.artux.pda.common.PropertyFields;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.VisionComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.data.PlayerData;
import net.artux.pda.map.engine.systems.BattleSystem;
import net.artux.pda.map.engine.systems.InteractionSystem;
import net.artux.pda.map.engine.systems.MapLoggerSystem;
import net.artux.pda.map.engine.systems.MovingSystem;
import net.artux.pda.map.engine.systems.PlayerSystem;
import net.artux.pda.map.engine.systems.RenderSystem;
import net.artux.pda.map.engine.systems.SoundsSystem;
import net.artux.pda.map.ui.DebugMenu;
import net.artux.pda.map.ui.Logger;
import net.artux.pda.map.ui.UIFrame;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pda.map.ui.bars.HUD;
import net.artux.pda.map.ui.bars.Slot;
import net.artux.pda.map.ui.blocks.ControlBlock;
import net.artux.pda.map.ui.blocks.MessagesBlock;
import net.artux.pda.model.items.ItemModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;


@Module
public class UserInterfaceModule {

    @Provides
    public UIFrame uiFrame(UserInterface userInterface) {
        return userInterface.getUIFrame();
    }

    @Provides
    public BitmapFont getFont(AssetsFinder assetsFinder) {
        return assetsFinder.getFontManager().getFont(24);
    }

    @Provides
    public Label.LabelStyle getLabelStyle(BitmapFont font) {
        return new Label.LabelStyle(font, Color.GRAY);
    }

    @Provides
    @Named("gameZone")
    public Group getGameZone(UserInterface userInterface) {
        return userInterface.getGameZone();
    }

    @Provides
    @Named("hudTable")
    public Table getHudTable(@Named("gameZone") Group gameZone) {
        float w = gameZone.getWidth();
        float h = gameZone.getHeight();

        Table hudTable = new Table();
        hudTable.setPosition(0, h);
        hudTable.align(Align.left | Align.top);
        hudTable.defaults().align(Align.left);
        hudTable.setWidth(w / 3);
        gameZone.addActor(hudTable);
        return hudTable;
    }

    @Provides
    @Named("controlTable")
    public Table getControlTable(@Named("gameZone") Group gameZone) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        ControlBlock controlBlock = new ControlBlock();
        controlBlock.defaults()
                .pad(10)
                .height(h / 8)
                .width(h / 8)
                .right();
        controlBlock.setPosition(w - w / 3 - h / 28f, h / 28f);
        Color color = gameZone.getColor();
        color.a = 0.7f;
        controlBlock.setColor(color);
        color.a = 1f;
        gameZone.setColor(color);
        gameZone.addActor(controlBlock);
        return controlBlock;
    }

    @Provides
    @Named("joyTable")
    public Table getJoyTable(@Named("gameZone") Group gameZone) {
        Table table = new Table();
        table.align(Align.left);
        table.setSize(gameZone.getWidth() / 2.5f, gameZone.getHeight() / 2);
        gameZone.addActor(table);
        return table;
    }

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
        joyTable.add(touchpad)
                .width(gameZone.getHeight() / 2.5f)
                .height(gameZone.getHeight() / 2.5f)
                .align(Align.left)
                .space(50f)
                .pad(50f);

        joyTable.row();
        MessagesBlock messagesBlock = new MessagesBlock(assetsFinder);
        joyTable.add(messagesBlock);

        return touchpad;
    }

    @IntoSet
    @Provides
    public Actor initHud(@Named("hudTable") Table hudTable, UserInterface userInterface, PlayerSystem playerSystem, AssetManager assetManager) {
        HUD hud = new HUD(assetManager, playerSystem, userInterface);
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
        float height = hudTable.getHeight() * 0.9f;
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
    public Actor initControlTable(@Named("controlTable") Table controlTable, PlayerSystem playerSystem, BattleSystem battleSystem,
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

    @IntoSet
    @Provides
    public Actor initHeader(UIFrame uiFrame, AssetManager assetManager, DataRepository dataRepository) {
        TextureRegionDrawable pauseDrawable = new TextureRegionDrawable(assetManager.get("ui/exit.png", Texture.class));
        Button.ButtonStyle pauseButtonStyle = new Button.ButtonStyle(pauseDrawable, pauseDrawable, pauseDrawable);
        Button pauseButton = new Button(pauseButtonStyle);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.clicked(event, x, y);
                Gdx.app.debug("UserInterface", "touched pause - user interface");
                dataRepository.applyActions(Collections.emptyMap());
                HashMap<String, String> data = new HashMap<>();
                data.put("openPda", "");
                dataRepository.getPlatformInterface().send(data);
            }
        });

        Button.ButtonStyle occupationsButtonStyle = new Button.ButtonStyle();
        occupationsButtonStyle.up = new TextureRegionDrawable(assetManager.get("ui/burger.png", Texture.class));
        Button menuButton = new Button(occupationsButtonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                //isMenuOpen = !isMenuOpen;
            }
        });


        uiFrame.getLeftHeaderTable().add(menuButton);
        uiFrame.getRightHeaderTable().add(pauseButton);

        return menuButton;
    }

    @IntoSet
    @Provides
    public Actor initDebugMode(@Named("gameZone") Group gameZone, UIFrame uiFrame, UserInterface userInterface, Skin skin, Label.LabelStyle labelStyle,
                               Engine engine, AssetManager assetManager, Properties properties) {
        if (properties.getProperty(PropertyFields.TESTER_MODE, "false").equals("true")) {
            Button.ButtonStyle bugStyle = new Button.ButtonStyle();
            bugStyle.up = new TextureRegionDrawable(assetManager.get("ui/bug.png", Texture.class));

            final Button debugButton = new Button(bugStyle);
            final DebugMenu debugMenu = new DebugMenu(skin, labelStyle);
            debugMenu.setSize(gameZone.getWidth(), gameZone.getHeight());

            debugButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    if (gameZone.getChildren().indexOf(debugMenu, false) == -1)
                        gameZone.addActor(debugMenu);
                    else
                        gameZone.removeActor(debugMenu);
                }
            });

            debugMenu.addCheckBox("Ускорение движения", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MovingSystem.speedup = ((CheckBox) actor).isChecked();
                }
            }, MovingSystem.speedup);

            debugMenu.addCheckBox("Вечный бег", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MovingSystem.alwaysRun = ((CheckBox) actor).isChecked();
                }
            }, MovingSystem.alwaysRun);

            debugMenu.addCheckBox("Учитывать столкновения", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MovingSystem.playerWalls = ((CheckBox) actor).isChecked();
                }
            }, MovingSystem.playerWalls);
            debugMenu.addCheckBox("Отобразить стены игрока", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MapLoggerSystem.showPlayerWalls = ((CheckBox) actor).isChecked();
                }
            }, MapLoggerSystem.showPlayerWalls);

            debugMenu.addCheckBox("Показать границы UI элементов", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    userInterface.setDebug(((CheckBox) actor).isChecked(), true);
                }
            }, false);

            debugMenu.addCheckBox("Отобразить карту ИИ", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MapLoggerSystem.showTiles = ((CheckBox) actor).isChecked();
                }
            }, MapLoggerSystem.showTiles);
            debugMenu.addCheckBox("Пути ИИ", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MapLoggerSystem.showPaths = ((CheckBox) actor).isChecked();
                }
            }, MapLoggerSystem.showPaths);

            debugMenu.addCheckBox("Показывать все сущности", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    RenderSystem.showAll = ((CheckBox) actor).isChecked();
                }
            }, RenderSystem.showAll);

            debugMenu.addCheckBox("Логгер", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Logger.visible = ((CheckBox) actor).isChecked();
                }
            }, Logger.visible);

            debugMenu.addCheckBox("Фоновая музыка", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (((CheckBox) actor).isChecked())
                        engine.getSystem(SoundsSystem.class).startBackgroundMusic();
                    else
                        engine.getSystem(SoundsSystem.class).stopMusic();
                }
            }, true);

            uiFrame.getLeftHeaderTable().add(debugButton);
            return debugButton;
        }
        return new Actor();
    }

}
