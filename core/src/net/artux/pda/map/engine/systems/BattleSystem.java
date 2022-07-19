package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.data.PlayerData;
import net.artux.pda.map.engine.pathfinding.FlatTiledNode;
import net.artux.pda.map.models.items.Item;
import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pda.map.ui.bars.Slot;

import java.util.Random;

public class BattleSystem extends BaseSystem implements Disposable, Drawable {

    private final GameStateManager gsm;
    private SoundsSystem soundsSystem;
    private AssetManager assetManager;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

    private ShapeRenderer sr = new ShapeRenderer();
    private Random random = new Random();

    private MapOrientationSystem mapOrientationSystem;

    private boolean playerShoot = false;

    private Slot weaponSlot;

    public BattleSystem(AssetManager assetManager, GameStateManager gameStateManager) {
        super(Family.all(HealthComponent.class, PositionComponent.class, WeaponComponent.class).get());
        this.assetManager = assetManager;
        this.gsm = gameStateManager;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        soundsSystem = engine.getSystem(SoundsSystem.class);
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
            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
            style.up = new TextureRegionDrawable(assetManager.get("ui/slots/slot.png", Texture.class));
            style.imageUp = new TextureRegionDrawable(assetManager.get("ui/icons/ic_rifle.png", Texture.class));

            weaponSlot = new Slot(userInterface, style);
            weaponSlot.pad(20);
            weaponSlot.getCell(weaponSlot.getLabel()).align(Align.left);
            float height = playerSystem.getHud().getHeight() * 0.9f;
            userInterface.getHudTable().add(weaponSlot).height(height).padLeft(20);

            if (entityWeapon.resource != null) {
                PlayerData.bullet = entityWeapon.resource.getTitle();
                weaponSlot.setText(entityWeapon.resource.getQuantity() + "/" + entityWeapon.getMagazine());
            }else{
                StringBuilder stringBuilder = new StringBuilder();
                for (Item i:
                        getEngine().getSystem(PlayerSystem.class).getPlayerComponent().gdxData.getAllItems()) {
                    stringBuilder.append("{").append(i.getId()).append("}").append(" ");
                }

                gsm.getPlatformInterface().toast("Патроны для "+ entityWeapon.getSelected().getTitle()
                        + " отсутсутвуют, необходим id: " + entityWeapon.getSelected().getBulletId() + ", есть " + stringBuilder.toString());
            }

        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for (Entity entity : entities) {
            WeaponComponent entityWeapon = wm.get(entity);
            entityWeapon.update(deltaTime);
        }

        HealthComponent healthComponent = hm.get(player);
        PositionComponent positionComponent = pm.get(player);
        WeaponComponent entityWeapon = wm.get(player);


        if (entityWeapon.getSelected() != null) {
            PlayerData.selectedWeapon = entityWeapon.getSelected().getTitle();
            if (entityWeapon.resource != null) {
                PlayerData.bullet = entityWeapon.resource.getTitle();
                weaponSlot.setText(entityWeapon.resource.getQuantity() + "/" + entityWeapon.getMagazine());
            }
        }
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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);

            PositionComponent entityPosition = pm.get(entity);
            WeaponComponent entityWeapon = wm.get(entity);
            MoodComponent moodComponent = mm.get(entity);
            Entity enemy = moodComponent.enemy;

            if (player != entity) {
                if (enemy != null) {
                    PositionComponent enemyPosition = pm.get(enemy);
                    HealthComponent enemyHealth = hm.get(enemy);

                    if (mapOrientationSystem.isGraphActive()) {
                        FlatTiledNode entityNode = mapOrientationSystem.getWorldGraph().getNodeInPosition(entityPosition.getPosition());
                        FlatTiledNode enemyNode = mapOrientationSystem.getWorldGraph().getNodeInPosition(enemyPosition.getPosition());
                        if (!mapOrientationSystem.collisionDetector.collides(new Ray<>(new Vector2(entityNode.x, entityNode.y), new Vector2(enemyNode.x, enemyNode.y)))) { //TODO new in update
                            if (entityWeapon.getSelected() != null) {
                                if (entityWeapon.shoot()) {
                                    drawShoot(batch, enemyPosition.getPosition(), entityPosition.getPosition());
                                    shoot(enemyHealth, entityWeapon, entityPosition.getPosition());
                                }
                            }
                        }
                    } else if (entityWeapon.getSelected() != null) {
                        if (entityWeapon.shoot()) {
                            drawShoot(batch, enemyPosition.getPosition(), entityPosition.getPosition());
                            shoot(enemyHealth, entityWeapon, entityPosition.getPosition());
                        }
                    }
                    if (enemyHealth.isDead())
                        moodComponent.setEnemy(null);
                }
            } else if (playerShoot) {
                if (enemy != null) {
                    PositionComponent enemyPosition = pm.get(enemy);
                    HealthComponent enemyHealth = hm.get(enemy);

                    if (entityWeapon.getSelected() != null) {
                        if (entityWeapon.shoot()) {
                            drawShoot(batch, enemyPosition.getPosition(), entityPosition.getPosition());
                            shoot(enemyHealth, entityWeapon, entityPosition.getPosition());
                        }
                    }
                    if (enemyHealth.isDead())
                        moodComponent.setEnemy(null);
                }
            }
        }
        batch.begin();

    }

    private void drawShoot(Batch batch, Vector2 enemyPosition, Vector2 entityPosition) {
        sr.setColor(Color.ORANGE);
        sr.setProjectionMatrix(batch.getProjectionMatrix());

        sr.begin(ShapeRenderer.ShapeType.Filled);

        Vector2 diff = enemyPosition.cpy().sub(entityPosition);
        Vector2 delayed = entityPosition.cpy().add(diff.scl(0.3f));

        /*sr.rectLine(delayed,
                getPointNear(enemyPosition, entityWeapon.getSelected().precision), 1);*/ // разбос
        sr.rectLine(delayed, enemyPosition, 1);
        sr.end();

    }

    private void shoot(HealthComponent enemyHealth, WeaponComponent entityWeapon, Vector2 shootPosition) {
        enemyHealth.value -= entityWeapon.getSelected().getDamage();
        soundsSystem.playShoot(shootPosition);
    }
}
