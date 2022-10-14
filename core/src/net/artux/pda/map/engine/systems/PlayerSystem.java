package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.QuestComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pda.map.ui.bars.HUD;
import net.artux.pda.map.ui.bars.Slot;
import net.artux.pda.map.ui.blocks.AssistantBlock;
import net.artux.pda.model.quest.story.StoryStateModel;

public class PlayerSystem extends BaseSystem implements Disposable {

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<PlayerComponent> pmm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<VelocityComponent> vcm = ComponentMapper.getFor(VelocityComponent.class);

    private final float pixelsPerMeter = 3f;
    private final AssetManager assetManager;

    private Slot medicineSlot;
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
        InteractionSystem interactionSystem = engine.getSystem(InteractionSystem.class);
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

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(assetManager.get("ui/slots/slot.png", Texture.class));
        style.imageUp = new TextureRegionDrawable(assetManager.get("ui/bar/ic_health.png", Texture.class));

        AssistantBlock assistantBlock = userInterface.getAssistantBlock();
        medicineSlot = new Slot(userInterface, style);
        style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(assetManager.get("ui/slots/slot.png", Texture.class));
        style.imageUp = new TextureRegionDrawable(assetManager.get("ui/bar/ic_radiation.png", Texture.class));
        assistantBlock.getButtonsTable().add(medicineSlot);
        medicineSlot.addListener(new ClickListener() {
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

    private void savePreferences() {
        if (player != null) {
            Preferences preferences = Gdx.app.getPreferences("player");
            PlayerComponent playerComponent = getPlayerComponent();
            HealthComponent healthComponent = getHealthComponent();
            PositionComponent positionComponent = pm.get(player);

            StoryStateModel storyStateModel = playerComponent.gdxData.getCurrentState();

            preferences.putBoolean(storyStateModel.toString(), true);
            preferences.putFloat("x", positionComponent.getX());
            preferences.putFloat("y", positionComponent.getY());
            preferences.putFloat("health", healthComponent.value);
            preferences.putFloat("stamina", healthComponent.stamina);
            preferences.putFloat("radiation", healthComponent.radiation);

            preferences.flush();
        }
    }

    private void loadPreferences() {
        if (player != null) {
            Preferences preferences = Gdx.app.getPreferences("player");
            PlayerComponent playerComponent = getPlayerComponent();
            HealthComponent healthComponent = getHealthComponent();
            PositionComponent positionComponent = pm.get(player);

            StoryStateModel storyStateModel = playerComponent.gdxData.getCurrentState();
            if (preferences.contains(storyStateModel.toString())) {
                preferences.putBoolean(storyStateModel.toString(), true);
                positionComponent.setPosition(new Vector2(preferences.getFloat("x"), preferences.getFloat("y")));
                healthComponent.value = preferences.getFloat("health");
                healthComponent.stamina = preferences.getFloat("stamina");
                healthComponent.radiation = preferences.getFloat("radiation");
            } else preferences.clear();
            preferences.flush();
        }
    }
}
