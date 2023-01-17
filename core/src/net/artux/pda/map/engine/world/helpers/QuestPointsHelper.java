package net.artux.pda.map.engine.world.helpers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import net.artux.pda.map.di.core.MapComponent;
import net.artux.pda.map.engine.components.ClickComponent;
import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.Position;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.map.ConditionComponent;
import net.artux.pda.map.engine.components.map.PointComponent;
import net.artux.pda.map.engine.components.map.QuestComponent;
import net.artux.pda.map.engine.components.map.TransferComponent;
import net.artux.pda.map.engine.systems.RenderSystem;
import net.artux.pda.map.engine.systems.player.PlayerSystem;
import net.artux.pda.map.utils.Mappers;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.Point;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.quest.story.StoryStateModel;

import java.util.Collections;

public class QuestPointsHelper {

    public static void createQuestPointsEntities(MapComponent coreComponent) {
        Engine engine = coreComponent.getEngine();
        GameMap map = coreComponent.getDataRepository().getGameMap();
        StoryDataModel dataModel = engine.getSystem(PlayerSystem.class).getPlayerComponent().gdxData;
        StoryStateModel storyStateModel = dataModel.getCurrentState();

        for (Point point : map.getPoints()) {
            if (point.getData().containsKey("static"))
                engine.addEntity(pointEntity(coreComponent, point));
            else if (point.getData().containsKey("chapter")) {
                if ((Integer.parseInt(point.getData().get("chapter")) == storyStateModel.getChapterId()
                        || Integer.parseInt(point.getData().get("chapter")) == 0))
                    engine.addEntity(pointEntity(coreComponent, point));
            } else engine.addEntity(pointEntity(coreComponent, point));
        }
    }

    private static Entity pointEntity(MapComponent coreComponent, final Point point) {
        Engine engine = coreComponent.getEngine();
        AssetManager assetManager = coreComponent.getAssetsManager();
        PlatformInterface platformInterface = coreComponent.getDataRepository().getPlatformInterface();

        Entity entity = new Entity()
                .add(new Position(Mappers.vector2(point.getPos())))

                .add(new InteractiveComponent(point.getName(), point.getType(), () -> platformInterface.send(point.getData())))
                .add(new ClickComponent(23, () -> engine.getSystem(RenderSystem.class)
                        .showText("Метка: " + point.getName(), Mappers.vector2(point.getPos()))));


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
