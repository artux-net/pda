package net.artux.pda.map.view.debug.widgets;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.artux.engine.scenes.SceneManager;
import net.artux.pda.commands.Commands;
import net.artux.pda.map.ecs.battle.MoodComponent;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.map.ecs.characteristics.PlayerComponent;
import net.artux.pda.map.ecs.effects.ejection.EjectionSystem;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.managers.ConditionEntityManager;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.repository.EngineSaver;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.map.di.components.CoreComponent;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.view.FontManager;
import net.artux.pda.map.view.view.bars.Utils;

import java.util.Collections;

import javax.inject.Inject;

@PerGameMap
public class ActionsWidget extends Table {

    @Inject
    public ActionsWidget(Skin skin, ConditionEntityManager conditionEntityManager, Engine engine,
                         EjectionSystem ejectionSystem,
                         EngineSaver saver,
                         DataRepository dataRepository,
                         CoreComponent coreComponent,
                         SceneManager sceneManager,
                         PlatformInterface platformInterface,
                         FontManager fontManager) {
        super(skin);
        left();
        defaults().left();
        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));

        Label.LabelStyle labelStyle = new Label.LabelStyle(fontManager.getFont(48), Color.GRAY);
        addLabel("Отключить условия точек (показать все)", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                conditionEntityManager.disableConditions();
            }
        });

        addLabel("Начать выброс", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ejectionSystem.processEjection();
            }
        });

        addLabel("Убить всех", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ImmutableArray<Entity> entities = engine
                        .getEntitiesFor(Family.all(BodyComponent.class, MoodComponent.class, HealthComponent.class).exclude(PlayerComponent.class).get());
                for (Entity entity : entities) {
                    HealthComponent healthComponent = entity.getComponent(HealthComponent.class);
                    healthComponent.damage(10000);
                }
            }
        });

        addLabel("Сохранить состояние карты", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saver.save(engine);
            }
        });

        addLabel("Перезапуск этой карты", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.set(coreComponent.getPreloadState());
            }
        });

        addLabel("Перезапуск движка", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                platformInterface.restart();
            }
        });

        addLabel("Выдать 1000 RU", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dataRepository.applyActions(Collections
                        .singletonMap(Commands.MONEY, Collections.singletonList("1000")));
            }
        });

        addLabel("Выдать 10000 RU", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dataRepository.applyActions(Collections
                        .singletonMap(Commands.MONEY, Collections.singletonList("10000")));
            }
        });

        addLabel("Выдать 50000 RU", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dataRepository.applyActions(Collections
                        .singletonMap(Commands.MONEY, Collections.singletonList("50000")));
            }
        });
    }

    private void addLabel(String title, Label.LabelStyle labelStyle, ClickListener clickListener) {
        Label label = new Label(title, labelStyle);
        add(label);
        row();
        label.addListener(clickListener);
    }


}
