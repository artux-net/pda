package net.artux.pda.map.engine.world.helpers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import net.artux.pda.map.di.core.MapComponent;
import net.artux.pda.map.engine.components.ClickComponent;
import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.QuestComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.StatusComponent;
import net.artux.pda.map.engine.components.TransferComponent;
import net.artux.pda.map.engine.systems.PlayerSystem;
import net.artux.pda.map.engine.systems.RenderSystem;
import net.artux.pda.map.utils.Mappers;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.QuestUtil;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.Point;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.quest.story.StoryStateModel;

public class QuestPointsHelper {

    public static void createQuestPointsEntities(MapComponent coreComponent) {
        Engine engine = coreComponent.getEngine();
        AssetManager assetManager = coreComponent.getAssetsManager();
        PlatformInterface platformInterface = coreComponent.getDataRepository().getPlatformInterface();
        GameMap map = coreComponent.getDataRepository().getGameMap();
        StoryDataModel dataModel = engine.getSystem(PlayerSystem.class).getPlayerComponent().gdxData;
        StoryStateModel storyStateModel = dataModel.getCurrentState();


        for (Point point : map.getPoints()) {
            if (point.getData().containsKey("static"))
                engine.addEntity(addPoint(engine, assetManager, point, platformInterface, dataModel));
            else if (point.getData().containsKey("chapter")) {
                if ((Integer.parseInt(point.getData().get("chapter")) == storyStateModel.getChapterId()
                        || Integer.parseInt(point.getData().get("chapter")) == 0))
                    engine.addEntity(addPoint(engine, assetManager, point, platformInterface, dataModel));
            } else engine.addEntity(addPoint(engine, assetManager, point, platformInterface, dataModel));
        }
    }

    private static Entity addPoint(final Engine engine, AssetManager assetManager, final Point point, PlatformInterface platformInterface, StoryDataModel dataModel) {
        Entity entity = new Entity();
        entity.add(new StatusComponent(QuestUtil.check(point.getCondition(), dataModel)))
                .add(new PositionComponent(Mappers.vector2(point.getPos())))
                .add(new InteractiveComponent(point.getName(), point.getType(), () -> platformInterface.send(point.getData())))
                .add(new ClickComponent(23, () -> engine.getSystem(RenderSystem.class)
                        .showText("Метка: " + point.getName(), Mappers.vector2(point.getPos()))));

        Texture texture = null;
        switch (point.getType()) {
            case 0:
            case 1:
                entity.add(new QuestComponent());
                texture = assetManager.get("quest.png", Texture.class);
                break;
            case 4:
                texture = assetManager.get("seller.png", Texture.class);
                break;
            case 5:
                texture = assetManager.get("cache.png", Texture.class);
                break;
            case 6:
                entity.add(new QuestComponent());
                texture = assetManager.get("quest1.png", Texture.class);
                break;
            case 7: {
                texture = assetManager.get("transfer.png", Texture.class);
                entity.add(new TransferComponent());
            }
            break;
        }
        int size = 23;
        if (texture != null)
            entity.add(new SpriteComponent(texture, size, size));

        return entity;
    }

}
