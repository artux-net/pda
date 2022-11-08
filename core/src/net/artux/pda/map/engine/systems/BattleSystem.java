package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.components.BulletComponent;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.VisionComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.data.PlayerData;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pda.map.ui.bars.Slot;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.ItemType;

import java.util.Random;

public class BattleSystem extends BaseSystem implements Disposable {

    private ImmutableArray<Entity> bullets;

    private final PlatformInterface platformInterface;
    private final AssetManager assetManager;

    private MapOrientationSystem mapOrientationSystem;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VisionComponent> vm = ComponentMapper.getFor(VisionComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<BulletComponent> bcm = ComponentMapper.getFor(BulletComponent.class);

    private ShapeRenderer sr = new ShapeRenderer();
    private Random random = new Random();

    private boolean playerShoot = false;

    private Slot weaponSlot;

    public BattleSystem(AssetManager assetManager, PlatformInterface platformInterface) {
        super(Family.all(HealthComponent.class, VisionComponent.class, PositionComponent.class, WeaponComponent.class).get());
        this.assetManager = assetManager;
        this.platformInterface = platformInterface;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        bullets = engine.getEntitiesFor(Family.all(PositionComponent.class, VelocityComponent.class, BulletComponent.class).get());

        mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);

        InteractionSystem interactionSystem = engine.getSystem(InteractionSystem.class);
        interactionSystem.addButton("ui/icons/icon_shoot.png", new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                playerShoot = true;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                playerShoot = false;
                super.touchUp(event, x, y, pointer, button);
            }
        });

        UserInterface userInterface = interactionSystem.getUserInterface();
        PlayerSystem playerSystem = engine.getSystem(PlayerSystem.class);
        WeaponComponent entityWeapon = wm.get(player);
        if (entityWeapon.getSelected() != null) {
            TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
            style.font = userInterface.getLabelStyle().font;
            style.fontColor = userInterface.getLabelStyle().fontColor;
            style.up = new TextureRegionDrawable(assetManager.get("ui/slots/slot.png", Texture.class));

            weaponSlot = new Slot(userInterface, style);
            weaponSlot.pad(20);
            weaponSlot.getCell(weaponSlot.getSecondLabel()).align(Align.left);
            float height = playerSystem.getHud().getHeight() * 0.9f;
            userInterface.getHudTable().add(weaponSlot).height(height).padLeft(20);

            ItemModel resource = entityWeapon.getBulletModel();

            if (resource != null) {
                PlayerData.bullet = resource.getTitle();
                weaponSlot.setLabelText(entityWeapon.getMagazine() + "/" + resource.getQuantity());
                weaponSlot.setText(entityWeapon.getSelected().getTitle());
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (ItemModel i :
                        getEngine().getSystem(PlayerSystem.class).getPlayerComponent().gdxData.getAllItems()) {
                    if (i.getType() == ItemType.BULLET)
                        stringBuilder.append("{").append(i.getBaseId()).append("}").append(" ");
                }

                platformInterface.toast("Патроны для " + entityWeapon.getSelected().getTitle()
                        + " отсутсутвуют, необходим id: " + entityWeapon.getSelected().getBulletId() + ", есть " + stringBuilder.toString());
            }

            weaponSlot.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entityWeapon.switchWeapons();
                }
            });

        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for (Entity entity : getEntities()) {
            WeaponComponent entityWeapon = wm.get(entity);
            entityWeapon.update(deltaTime);
        }

        {
            WeaponComponent entityWeapon = wm.get(player);

            ItemModel resource = entityWeapon.getBulletModel();
            if (entityWeapon.getSelected() != null) {
                PlayerData.selectedWeapon = entityWeapon.getSelected().getTitle();
                if (resource != null) {
                    PlayerData.bullet = resource.getTitle();
                    weaponSlot.setLabelText(entityWeapon.getMagazine() + "/" + resource.getQuantity());
                    weaponSlot.setText(entityWeapon.getSelected().getTitle());
                }
            }
            MoodComponent moodComponent = mm.get(player);
            VisionComponent visionComponent = vm.get(player);

            if (moodComponent.hasEnemy()) {
                if (visionComponent.isSeeing(moodComponent.getEnemy()))
                    if (playerShoot && entityWeapon.shoot())
                        entityWeapon.sendBullet(player, moodComponent.getEnemy());
            }
        }

        for (Entity bullet : bullets) {
            BulletComponent bulletComponent = bcm.get(bullet);
            PositionComponent bulletPosition = pm.get(bullet);
            Vector2 targetPosition = bulletComponent.getTargetPosition();

            PositionComponent targetEntityPosition = pm.get(bulletComponent.getTarget());
            MoodComponent targetEntityMood = mm.get(bulletComponent.getTarget());
            HealthComponent targetEntityHealth = hm.get(bulletComponent.getTarget());

            float dstToTarget = bulletPosition.dst(targetPosition);
            if (targetEntityPosition.epsilonEquals(bulletPosition, 4f)) {
                targetEntityHealth.damage(bulletComponent.getDamage());

                if (!targetEntityMood.hasEnemy()) {
                    targetEntityMood.setEnemy(bulletComponent.getAuthor());
                    MoodComponent playerMood = mm.get(player);
                    if (bulletComponent.getAuthor() == player && !targetEntityMood.isEnemy(playerMood)) {
                        playerMood.setRelation(targetEntityMood, -10);
                    }
                }
            } else if (dstToTarget > bulletComponent.getLastDstToTarget())
                getEngine().removeEntity(bullet);
            else
                bulletComponent.setLastDstToTarget(dstToTarget);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    public Vector2 getPointNear(PositionComponent positionComponent, float precision) {
        double r = 5 / precision;

        double angle = random.nextInt(360);

        Vector2 basePosition = positionComponent.getPosition();
        float x = (float) (Math.cos(angle) * r);
        float y = (float) (Math.sin(angle) * r);
        return new Vector2(basePosition.x + x, basePosition.y + y);
    }

    @Override
    public void dispose() {
        sr.dispose();
    }
}
