package net.artux.pda.map.view.debug.widgets;

import static net.artux.pda.model.user.Gang.LONERS;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.artux.engine.scenes.SceneManager;
import net.artux.pda.commands.Commands;
import net.artux.pda.map.content.ContentGenerator;
import net.artux.pda.map.controller.ConditionEntityManager;
import net.artux.pda.map.di.components.CoreComponent;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.ecs.ai.EntityComponent;
import net.artux.pda.map.ecs.battle.MoodComponent;
import net.artux.pda.map.ecs.battle.WeaponComponent;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.map.ecs.characteristics.PlayerComponent;
import net.artux.pda.map.ecs.creation.EntityProcessorSystem;
import net.artux.pda.map.ecs.effects.EffectsComponent;
import net.artux.pda.map.ecs.effects.ejection.EjectionSystem;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.player.PlayerSystem;
import net.artux.pda.map.ecs.vision.FogOfWarComponent;
import net.artux.pda.map.ecs.vision.VisionComponent;
import net.artux.pda.map.engine.entities.Bodies;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.repository.EngineSaver;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.map.view.Utils;
import net.artux.pda.map.view.root.FontManager;
import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.quest.Text;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.Gang;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

@PerGameMap
public class WeaponWidget extends Table {

    @Inject
    public WeaponWidget(Skin skin, ConditionEntityManager conditionEntityManager, Engine engine,
                        EjectionSystem ejectionSystem,
                        EngineSaver saver,
                        EntityProcessorSystem entityProcessorSystem,
                        ContentGenerator contentGenerator,
                        World world,
                        PlayerSystem playerSystem,
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

        TextField weaponless = new TextField("", skin);
        add(weaponless);
        TextField weaponStats = new TextField("", skin);
        add(weaponStats);
        row();

        addLabel("Точность", labelStyle, new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Entity player = engine.getEntitiesFor(
                        Family.one(PlayerComponent.class).get()
                ).first();

                WeaponComponent weapon = player.getComponent(WeaponComponent.class);
                String valueofper = weaponless.getText();
                weapon.getSelected().setPrecision(Float.parseFloat(valueofper));

            }
        });

        addLabel("Скорострельность", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Entity player = engine.getEntitiesFor(
                        Family.one(PlayerComponent.class).get()
                ).first();

                WeaponComponent weapon = player.getComponent(WeaponComponent.class);
                String valueofspeed = weaponless.getText();
                weapon.getSelected().setSpeed(Float.parseFloat(valueofspeed));

            }
        });

        addLabel("Урон", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Entity player = engine.getEntitiesFor(
                        Family.one(PlayerComponent.class).get()
                ).first();
                WeaponComponent weapon = player.getComponent(WeaponComponent.class);
                String valueofdam = weaponless.getText();
                weapon.getSelected().setDamage(Float.parseFloat(valueofdam));

            }
        });

        addLabel("Дальность", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Entity player = engine.getEntitiesFor(
                        Family.one(PlayerComponent.class).get()
                ).first();
                WeaponComponent weapon = player.getComponent(WeaponComponent.class);
                String valueofdis = weaponless.getText();
                weapon.getSelected().setDistance(Float.parseFloat(valueofdis));

            }
        });

        addLabel("Текщая Точность", labelStyle, new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Entity player = engine.getEntitiesFor(
                        Family.one(PlayerComponent.class).get()
                ).first();

                WeaponComponent weapon = player.getComponent(WeaponComponent.class);
                weaponStats.setText(String.valueOf(weapon.getSelected().getPrecision()));

            }
        });

        addLabel("Текщая Скорострельность", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Entity player = engine.getEntitiesFor(
                        Family.one(PlayerComponent.class).get()
                ).first();

                WeaponComponent weapon = player.getComponent(WeaponComponent.class);
                weaponStats.setText(String.valueOf(weapon.getSelected().getSpeed()));

            }
        });

        addLabel("Текщий Урон", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Entity player = engine.getEntitiesFor(
                        Family.one(PlayerComponent.class).get()
                ).first();

                WeaponComponent weapon = player.getComponent(WeaponComponent.class);
                weaponStats.setText(String.valueOf(weapon.getSelected().getDamage()));
            }
        });

        addLabel("Текщая Дальность", labelStyle, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Entity player = engine.getEntitiesFor(
                        Family.one(PlayerComponent.class).get()
                ).first();

                WeaponComponent weapon = player.getComponent(WeaponComponent.class);
                weaponStats.setText(String.valueOf(weapon.getSelected().getDistance()));
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
