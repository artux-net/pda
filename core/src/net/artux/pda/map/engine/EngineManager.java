package net.artux.pda.map.engine;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.components.player.UserVelocityInput;
import net.artux.pda.map.engine.systems.ArtifactSystem;
import net.artux.pda.map.engine.systems.BattleSystem;
import net.artux.pda.map.engine.systems.CameraSystem;
import net.artux.pda.map.engine.systems.ClicksSystem;
import net.artux.pda.map.engine.systems.DataSystem;
import net.artux.pda.map.engine.systems.DeadCheckerSystem;
import net.artux.pda.map.engine.systems.Drawable;
import net.artux.pda.map.engine.systems.InteractionSystem;
import net.artux.pda.map.engine.systems.MapLoggerSystem;
import net.artux.pda.map.engine.systems.MapOrientationSystem;
import net.artux.pda.map.engine.systems.MessagesSystem;
import net.artux.pda.map.engine.systems.MoodSystem;
import net.artux.pda.map.engine.systems.MovingSystem;
import net.artux.pda.map.engine.systems.PlayerSystem;
import net.artux.pda.map.engine.systems.RenderSystem;
import net.artux.pda.map.engine.systems.SoundsSystem;
import net.artux.pda.map.engine.systems.StatesSystem;
import net.artux.pda.map.engine.systems.TargetingSystem;
import net.artux.pda.map.engine.systems.WorldSystem;
import net.artux.pda.map.engine.world.helpers.AnomalyHelper;
import net.artux.pda.map.engine.world.helpers.ControlPointsHelper;
import net.artux.pda.map.engine.world.helpers.QuestPointsHelper;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pda.map.ui.bars.HUD;
import net.artux.pdalib.Member;

public class EngineManager extends InputListener implements Drawable, Disposable, GestureDetector.GestureListener {

    private Map map;
    private Member member;

    private Engine engine;

    private ClicksSystem clicksSystem;
    private CameraSystem cameraSystem;

    private boolean controlPoints = true;
    private boolean questPoints = true;
    private boolean anomalies = true;

    public EngineManager(AssetsFinder assetsFinder, Stage stage, UserInterface userInterface, GameStateManager gameStateManager) {
        this.engine = new Engine();
        this.map = (Map) gameStateManager.get("map");
        this.member = gameStateManager.getMember();

        //player
        Entity player = new Entity();
        UserVelocityInput velocityComponent = new UserVelocityInput();
        Camera camera = stage.getViewport().getCamera();
        camera.position.x = map.getPlayerPosition().x;
        camera.position.y = map.getPlayerPosition().y;
        player.add(new PositionComponent(map.getPlayerPosition()))
                .add(new VelocityComponent())
                .add(new SpriteComponent(velocityComponent, assetsFinder.get().get("gg.png", Texture.class), 32, 32))
                .add(new WeaponComponent(member))
                .add(new MoodComponent(member))
                .add(new HealthComponent())
                .add(velocityComponent)
                .add(new PlayerComponent(camera, member));
        engine.addEntity(player);

        engine.addSystem(new MapOrientationSystem(assetsFinder, map));
        engine.addSystem(new CameraSystem());
        engine.addSystem(new SoundsSystem(assetsFinder.get()));
        engine.addSystem(new WorldSystem(assetsFinder.get()));
        engine.addSystem(new DataSystem(map, member));
        engine.addSystem(new InteractionSystem(stage, userInterface, assetsFinder.get()));
        engine.addSystem(new PlayerSystem(assetsFinder.get()));
        userInterface.getHudTable().add(new HUD(assetsFinder.get(), engine, userInterface));

        if (controlPoints)
            ControlPointsHelper.createControlPointsEntities(engine, assetsFinder.get());
        if (questPoints)
            QuestPointsHelper.createQuestPointsEntities(engine, assetsFinder.get());
        if (anomalies)
            AnomalyHelper.createAnomalies(engine, assetsFinder.get());

        engine.addSystem(new MessagesSystem(userInterface));
        engine.addSystem(new ArtifactSystem());
        engine.addSystem(new ClicksSystem());
        engine.addSystem(new MapLoggerSystem());
        engine.addSystem(new RenderSystem(stage));
        engine.addSystem(new BattleSystem(assetsFinder.get()));
        engine.addSystem(new StatesSystem());
        engine.addSystem(new TargetingSystem());
        engine.addSystem(new MoodSystem(assetsFinder.get()));
        engine.addSystem(new MovingSystem());
        engine.addSystem(new DeadCheckerSystem(userInterface, gameStateManager, assetsFinder.get()));

        clicksSystem = engine.getSystem(ClicksSystem.class);
        cameraSystem = engine.getSystem(CameraSystem.class);

        stage.addListener(this);
    }

    public void update(float dt) {
        engine.update(dt);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (EntitySystem s : engine.getSystems()) {
            if (s instanceof Drawable) ((Drawable) s).draw(batch, parentAlpha);
        }
    }

    @Override
    public void dispose() {
        for (EntitySystem s : engine.getSystems()) {
            if (s instanceof Disposable) ((Disposable) s).dispose();
        }
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if(clicksSystem!=null)
            clicksSystem.clicked(x, y);
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        cameraSystem.setDetached(true);
        cameraSystem.moveBy(-deltaX, deltaY);
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        //Gdx.app.debug("", velocityX + ":" + velocityY);
        //TODO fling
        return false;
    }

    private Vector2 lastPointer;
    private float lastZoom;

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        cameraSystem.setZooming(true);
        cameraSystem.setDetached(true);

        float panAmount = pointer1.dst(pointer2);
        float initAmount = initialPointer1.dst(initialPointer2);
        float newZoom = (initAmount / panAmount) * cameraSystem.getCurrentZoom();

        Vector2 centerPoint = new Vector2((pointer1.x + pointer2.x) / 2, (pointer1.y + pointer2.y) / 2);

        if (lastPointer != null) {
            Vector2 cameraPosition = cameraSystem.getPosition();
            Vector2 cameraShift = new Vector2(centerPoint.x - cameraPosition.x, cameraPosition.y - centerPoint.y);
            Vector2 cameraShiftValue = cameraShift.cpy().scl(lastZoom - newZoom);

            if (cameraSystem.setZoom(newZoom))
                cameraSystem.moveBy(cameraShiftValue.x, cameraShiftValue.y);

            cameraSystem.moveBy(lastPointer.x - centerPoint.x, centerPoint.y - lastPointer.y);
        }

        lastPointer = centerPoint;
        lastZoom = newZoom;

        return false;
    }

    @Override
    public void pinchStop() {
        cameraSystem.setZooming(false);
        lastPointer = null;
    }

    public Engine getEngine() {
        return engine;
    }
}
