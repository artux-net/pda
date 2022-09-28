package net.artux.pda.map.engine.world.helpers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import net.artux.pda.map.engine.components.ClickComponent;
import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.QuestComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.systems.DataSystem;
import net.artux.pda.map.engine.systems.PlayerSystem;
import net.artux.pda.map.engine.systems.RenderSystem;
import net.artux.pda.map.model.Transfer;
import net.artux.pda.map.model.input.Map;
import net.artux.pda.map.model.input.Point;
import net.artux.pda.map.states.State;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pda.model.Checker;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.quest.story.StoryStateModel;
import net.artux.pda.model.user.UserModel;

import java.util.HashMap;

public class QuestPointsHelper {


    public static void createQuestPointsEntities(Engine engine, AssetManager assetManager) {
        Map map = engine.getSystem(DataSystem.class).getMap();
        UserModel userModel = engine.getSystem(PlayerSystem.class).getPlayerComponent().userModel;
        StoryDataModel dataModel = engine.getSystem(PlayerSystem.class).getPlayerComponent().gdxData;
        StoryStateModel storyStateModel = dataModel.getCurrentState();
        for (Point point : map.getPoints()) {
            //TODO sync with ui
            if (Checker.check(point.getCondition(), dataModel))
                if (point.getData().containsKey("chapter")) {
                    if ((Integer.parseInt(point.getData().get("chapter")) == storyStateModel.getChapterId()
                            || Integer.parseInt(point.getData().get("chapter")) == 0))
                        addPoint(engine, assetManager, point);
                } else addPoint(engine, assetManager, point);
        }

        /*for (Transfer transfer : map.getTransfers()) {
            if (userModel != null && Checker.check(transfer.condition, dataModel))
                addTransferPoint(engine, assetManager, transfer);
        }*/
    }

    private static void addPoint(final Engine engine, AssetManager assetManager, final Point point) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(point.getPosition()))
                .add(new InteractiveComponent(point.getTitle(), point.type, new InteractiveComponent.InteractListener() {
                    @Override
                    public void interact(UserInterface userInterface) {
                        State.gsm.getPlatformInterface().send(point.getData());
                    }
                }))
                .add(new ClickComponent(new ClickComponent.ClickListener() {
                    @Override
                    public void clicked() {
                        engine.getSystem(RenderSystem.class)
                                .showText("Метка: " + point.getTitle(), point.getPosition());
                    }
                }));

        Texture texture = null;
        switch (point.type) {
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
            case 7:
                texture = assetManager.get("transfer.png", Texture.class);
                break;
        }
        int size = 23;
        if (texture != null)
            entity.add(new SpriteComponent(texture, size, size));

        engine.addEntity(entity);
    }

    private static void addTransferPoint(final Engine engine, AssetManager assetManager, final Transfer point) {
        Entity entity = new Entity();

        entity.add(new PositionComponent(point.getPosition()))
                .add(new InteractiveComponent(point.getMessage(), -1, new InteractiveComponent.InteractListener() {
                    @Override
                    public void interact(UserInterface userInterface) {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("map", String.valueOf(point.getTo()));
                        data.put("pos", point.getToPosition());
                        State.gsm.getPlatformInterface().send(data);
                    }
                }))
                .add(new ClickComponent(new ClickComponent.ClickListener() {
                    @Override
                    public void clicked() {
                        engine.getSystem(RenderSystem.class)
                                .showText("Локация " + point.getMessage(), point.getPosition());
                    }
                }));

        entity.add(new SpriteComponent(assetManager.get("transfer.png", Texture.class), 32, 32));

        engine.addEntity(entity);
    }

}
