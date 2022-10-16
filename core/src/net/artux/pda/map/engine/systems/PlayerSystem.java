package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.QuestComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.components.player.UserVelocityInput;
import net.artux.pda.map.ui.BackpackMenu;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pda.map.ui.bars.HUD;
import net.artux.pda.map.ui.bars.Slot;
import net.artux.pda.map.ui.blocks.AssistantBlock;
import net.artux.pda.model.items.MedicineModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.quest.story.StoryStateModel;

public class PlayerSystem extends BaseSystem implements Disposable {

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<UserVelocityInput> uvm = ComponentMapper.getFor(UserVelocityInput.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<PlayerComponent> pmm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<VelocityComponent> vcm = ComponentMapper.getFor(VelocityComponent.class);

    private final float pixelsPerMeter = 3f;
    private final AssetManager assetManager;
    private InteractionSystem interactionSystem;

    private Slot backpackSlot;
    private HUD hud;

    public PlayerSystem(AssetManager assetManager) {
        super(Family.all(PositionComponent.class, QuestComponent.class).get());
        this.assetManager = assetManager;
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        InteractionSystem interactionSystem = engine.getSystem(InteractionSystem.class);
        interactionSystem.getUserInterface().getAssistantBlock().setTouchable(Touchable.disabled);
        interactionSystem.getUserInterface().getControlBlock().setTouchable(Touchable.disabled);
        interactionSystem.getUserInterface().getTouchpad().setTouchable(Touchable.disabled);
        interactionSystem.getUserInterface().getAssistantBlock().setVisible(false);
        interactionSystem.getUserInterface().getControlBlock().setVisible(false);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        interactionSystem = engine.getSystem(InteractionSystem.class);
        interactionSystem.addButton("ui/icons/icon_run.png", new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                vcm.get(player).running = false;
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                HealthComponent healthComponent = hm.get(player);
                if (healthComponent.stamina > 10f)
                    vcm.get(player).running = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        UserInterface userInterface = interactionSystem.getUserInterface();
        hud = new HUD(assetManager, engine, userInterface);
        userInterface.getHudTable().add(hud);

        AssistantBlock assistantBlock = userInterface.getAssistantBlock();

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = userInterface.getLabelStyle().font;
        textButtonStyle.fontColor = userInterface.getLabelStyle().fontColor;
        textButtonStyle.up = new TextureRegionDrawable(assetManager.get("ui/slots/slot.png", Texture.class));
        backpackSlot = new Slot(userInterface, textButtonStyle);
        backpackSlot.setText("Рюкзак");
        backpackSlot.layout();
        backpackSlot.setLabelText("");

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(assetManager.get("ui/slots/slot.png", Texture.class));
        style.imageUp = new TextureRegionDrawable(assetManager.get("ui/bar/ic_radiation.png", Texture.class));
        assistantBlock.getButtonsTable().add(backpackSlot);
        backpackSlot.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                userInterface.switchBackpack();
                super.clicked(event, x, y);
            }
        });

        loadPreferences();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (player != null) {
            MoodComponent moodComponent = mm.get(player);
            PositionComponent playerPosition = pm.get(player);
            SpriteComponent spriteComponent = sm.get(player);
            UserVelocityInput userVelocityInput = uvm.get(player);
            Entity enemy = moodComponent.enemy;
            Vector2 direction;
            if (enemy == null) {
                direction = userVelocityInput.getVelocity();
            } else {
                PositionComponent enemyPosition = pm.get(enemy);
                direction = enemyPosition.getPosition().cpy().sub(playerPosition.getPosition());
            }

            float degrees = (float) (Math.atan2(
                    -direction.x,
                    direction.y
            ) * 180.0d / Math.PI);
            if (direction.x != 0 && direction.y != 0)
                spriteComponent.setRotation(degrees);
        }
    }

    public HUD getHud() {
        return hud;
    }

    public PlayerComponent getPlayerComponent() {
        return pmm.get(player);
    }

    public HealthComponent getHealthComponent() {
        return hm.get(player);
    }

    public Vector2 getPosition() {
        PositionComponent playerPosition = pm.get(player);
        return playerPosition.getPosition();
    }

    public int getDistance() {
        if (entities.size > 0) {
            return (int) (getPosition().dst(pm.get(entities.get(0)).getPosition()) / pixelsPerMeter);//TODO select target
        } else return -1;
    }

    @Override
    public void dispose() {
        savePreferences();
    }

    public void savePreferences() {
        if (player != null) {
            Preferences preferences = Gdx.app.getPreferences("player");
            Preferences positionPrefs = Gdx.app.getPreferences("position");

            PlayerComponent playerComponent = getPlayerComponent();
            HealthComponent healthComponent = getHealthComponent();
            PositionComponent positionComponent = pm.get(player);

            StoryStateModel storyStateModel = playerComponent.gdxData.getCurrentState();

            positionPrefs.putBoolean(storyStateModel.toString(), true);
            positionPrefs.putFloat("x", positionComponent.getX());
            positionPrefs.putFloat("y", positionComponent.getY());

            preferences.putFloat("health", healthComponent.value);
            preferences.putFloat("radiation", healthComponent.radiation);

            preferences.flush();
            positionPrefs.flush();
        }
    }

    private void loadPreferences() {
        if (player != null) {
            Preferences preferences = Gdx.app.getPreferences("player");
            Preferences positionPrefs = Gdx.app.getPreferences("position");

            HealthComponent healthComponent = getHealthComponent();
            healthComponent.value = preferences.getFloat("health", 100);
            if (healthComponent.isDead())
                healthComponent.value = 50;
            healthComponent.radiation = preferences.getFloat("radiation", 0);

            PositionComponent positionComponent = pm.get(player);
            PlayerComponent playerComponent = getPlayerComponent();
            StoryStateModel storyStateModel = playerComponent.gdxData.getCurrentState();
            if (positionPrefs.contains(storyStateModel.toString())) {
                positionComponent.setPosition(new Vector2(positionPrefs.getFloat("x"), positionPrefs.getFloat("y")));
            } else positionPrefs.clear();

            positionPrefs.flush();
        }
    }

    public void updateData(StoryDataModel dataModel) {
        UserInterface userInterface = interactionSystem.getUserInterface();
        BackpackMenu backpackMenu = userInterface.getBackpackMenu();
        backpackMenu.update(dataModel, new MedicineListener(){
            @Override
            public void treat(MedicineModel model) {
                HealthComponent healthComponent = hm.get(player);
                healthComponent.treat(model);
            }
        });
    }

    public interface MedicineListener{
        void treat(MedicineModel model);
    }
}
