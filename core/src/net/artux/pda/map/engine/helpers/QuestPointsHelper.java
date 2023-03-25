package net.artux.pda.map.engine.helpers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

import net.artux.pda.map.di.components.MapComponent;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.ClickComponent;
import net.artux.pda.map.engine.ecs.components.InteractiveComponent;
import net.artux.pda.map.engine.ecs.components.SpriteComponent;
import net.artux.pda.map.engine.ecs.components.map.ConditionComponent;
import net.artux.pda.map.engine.ecs.components.map.PointComponent;
import net.artux.pda.map.engine.ecs.components.map.QuestComponent;
import net.artux.pda.map.engine.ecs.components.map.TransferComponent;
import net.artux.pda.map.engine.ecs.systems.RenderSystem;
import net.artux.pda.map.utils.Mappers;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.Point;

import java.util.Collections;

public class QuestPointsHelper {

    private static final ComponentMapper<BodyComponent> bcm = ComponentMapper.getFor(BodyComponent.class);

    public static void createQuestPointsEntities(MapComponent coreComponent) {
        Engine engine = coreComponent.getEngine();
        GameMap map = coreComponent.getDataRepository().getGameMap();

        for (Point point : map.getPoints()) {
            engine.addEntity(pointEntity(coreComponent, point, coreComponent.getWorld()));
        }
    }

    private static Entity pointEntity(MapComponent coreComponent, final Point point, World world) {
        Engine engine = coreComponent.getEngine();
        AssetManager assetManager = coreComponent.getAssetsManager();
        PlatformInterface platformInterface = coreComponent.getDataRepository().getPlatformInterface();

        Entity entity = new Entity()
                .add(new BodyComponent(Mappers.vector2(point.getPos()), world))
                .add(new InteractiveComponent(point.getName(), point.getType(), () ->
                        platformInterface.send(point.getData())))
                .add(new ClickComponent(23, () -> engine.getSystem(RenderSystem.class)
                        .showText("Метка: " + point.getName(), Mappers.vector2(point.getPos()))));
        bcm.get(entity).body.setTransform(Mappers.vector2(point.getPos()), 0);

        if (point.getCondition() != null)
            entity.add(new ConditionComponent(point.getCondition()));
        else
            entity.add(new ConditionComponent(Collections.emptyMap()));

        PointComponent pointComponent = new PointComponent(point);
        if (pointComponent.getType() == PointComponent.Type.TRANSFER)
            entity.add(new TransferComponent());

        Texture texture = getPointTexture(assetManager, pointComponent.getType());
        int size = 23;
        if (texture != null) {
            entity.add(new SpriteComponent(texture, size, size));
            entity.add(pointComponent);
            if (point.getData().containsKey("chapter") && point.getData().containsKey("stage"))
                entity.add(new QuestComponent(point));
        }
        Gdx.app.debug("Points", "Point created at " + Mappers.vector2(point.getPos()) + " with name: " + point.getName());
        return entity;
    }

    public static Texture getPointTexture(AssetManager assetManager, PointComponent.Type type) {
        switch (type) {
            case QUEST:
                return assetManager.get("quest.png", Texture.class);
            case SELLER:
                return assetManager.get("seller.png", Texture.class);
            case CACHE:
                return assetManager.get("cache.png", Texture.class);
            case ADDITIONAL_QUEST:
                return assetManager.get("quest1.png", Texture.class);
            case TRANSFER:
                return assetManager.get("transfer.png", Texture.class);
            default:
                return null;
        }
    }

}
