package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.QuestComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pda.map.ui.bars.HUD;
import net.artux.pda.map.ui.bars.Slot;
import net.artux.pda.map.ui.blocks.AssistantBlock;
import net.artux.pdalib.Member;

public class PlayerSystem extends BaseSystem {

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<PlayerComponent> pmm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<VelocityComponent> vcm = ComponentMapper.getFor(VelocityComponent.class);

    private final float pixelsPerMeter = 3f;
    private final AssetManager assetManager;

    private Slot medicineSlot;
    private Slot radiationSlot;
    private HUD hud;

    private int medicineCount = 0;
    private int radiationCount = 0;

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
                VelocityComponent velocityComponent = vcm.get(player);
                if (velocityComponent.stamina > 10f)
                    vcm.get(player).running = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        UserInterface userInterface = interactionSystem.getUserInterface();
        hud = new HUD(assetManager, engine, userInterface);
        userInterface.getHudTable().add(hud);

        ImageButton.ImageButtonStyle

        style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(assetManager.get("ui/slots/slot.png", Texture.class));
        style.imageUp = new TextureRegionDrawable(assetManager.get("ui/bar/ic_health.png", Texture.class));

        AssistantBlock assistantBlock = userInterface.getAssistantBlock();
        medicineSlot = new Slot(userInterface, style);
        style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(assetManager.get("ui/slots/slot.png", Texture.class));
        style.imageUp = new TextureRegionDrawable(assetManager.get("ui/bar/ic_radiation.png", Texture.class));
        radiationSlot = new Slot(userInterface, style);
        assistantBlock.getButtonsTable().add(medicineSlot);
        medicineSlot.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (medicineCount>0) {
                    medicineCount -= 1;
                    hm.get(player).treat(15);
                }
                super.clicked(event, x, y);
            }
        });
        radiationSlot.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (radiationCount>0) {
                    radiationCount -= 1;
                    hm.get(player).decreaseRadiation(5);
                }
                super.clicked(event, x, y);
            }
        });
        assistantBlock.getButtonsTable().add(radiationSlot);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        medicineSlot.setText(String.valueOf(medicineCount));
        radiationSlot.setText(String.valueOf(radiationCount));
    }

    public HUD getHud() {
        return hud;
    }

    public void addMedicine(int medicineCount){
        this.medicineCount += medicineCount;
    }

    public void addRadiation(int radiationCount){
        this.radiationCount += radiationCount;
    }

    public Member getPlayerMember() {
        PlayerComponent playerComponent = pmm.get(player);
        return playerComponent.member;
    }

    public float getHealth() {
        return hm.get(player).value;
    }

    public float getRadiation() {
        return hm.get(player).radiation;
    }

    public float getStamina() {
        return vcm.get(player).stamina;
    }

    public Vector2 getPosition() {
        PositionComponent playerPosition = pm.get(player);
        return playerPosition.getPosition();
    }

    public int getDistance() {
        if (entities.size > 0){
            return (int) (getPosition().dst(pm.get(entities.get(0)).getPosition()) / pixelsPerMeter);//TODO select target
        }else return -1;
    }
}
