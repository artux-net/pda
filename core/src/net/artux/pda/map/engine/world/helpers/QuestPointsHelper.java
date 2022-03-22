package net.artux.pda.map.engine.world.helpers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import net.artux.pda.map.engine.components.ClickComponent;
import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.systems.DataSystem;
import net.artux.pda.map.engine.systems.RenderSystem;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.model.Point;
import net.artux.pda.map.model.Transfer;
import net.artux.pda.map.states.State;
import net.artux.pdalib.Checker;
import net.artux.pdalib.Member;
import net.artux.pdalib.profile.Story;

import java.util.HashMap;

public class QuestPointsHelper {


    public static void createQuestPointsEntities(Engine engine, AssetManager assetManager) {
        Map map = engine.getSystem(DataSystem.class).getMap();
        Member member = engine.getSystem(DataSystem.class).getMember();

        for (Point point : map.getPoints()) {
            if (member != null && Checker.check(point.getCondition(), member))
                if (point.getData().containsKey("chapter")) {
                    int storyId = Integer.parseInt(member.getData().getTemp().get("currentStory"));
                    for (Story story : member.getData().getStories()) {
                        if (story.getStoryId() == storyId
                                && (Integer.parseInt(point.getData().get("chapter")) == story.getLastChapter()
                                || Integer.parseInt(point.getData().get("chapter")) == 0))
                            addPoint(engine, assetManager, point);
                    }
                } else addPoint(engine, assetManager, point);
        }

        for (Transfer transfer : map.getTransfers()) {
            if (member != null && Checker.check(transfer.condition, member))
                addTransferPoint(engine, assetManager, transfer);
        }
    }

    private static void addPoint(final Engine engine, AssetManager assetManager, final Point point) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(point.getPosition()))
                .add(new InteractiveComponent(point.getTitle(), point.type, new InteractiveComponent.InteractListener() {
                    @Override
                    public void interact() {
                        State.gsm.getPlatformInterface().send(point.getData());
                    }
                }))
                .add(new ClickComponent(new ClickComponent.ClickListener() {
                    @Override
                    public void clicked() {
                        engine.getSystem(RenderSystem.class)
                                .showText("Метка: " + point.getTitle(),point.getPosition());
                    }
                }));

        Texture texture = null;
        switch (point.type) {
            case 0:
            case 1:
                texture = assetManager.get("quest.png", Texture.class);
                break;
            case 4:
                texture = assetManager.get("seller.png", Texture.class);
                break;
            case 5:
                texture = assetManager.get("cache.png", Texture.class);
                break;
            case 6:
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
                    public void interact() {
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
                                .showText("Локация " + point.getMessage(),point.getPosition());
                    }
                }));

        entity.add(new SpriteComponent(assetManager.get("transfer.png", Texture.class), 32, 32));

        engine.addEntity(entity);
    }

}
