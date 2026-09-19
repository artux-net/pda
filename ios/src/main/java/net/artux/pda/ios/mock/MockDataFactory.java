package net.artux.pda.ios.mock;

import com.badlogic.gdx.ApplicationAdapter;

import net.artux.pda.common.PropertyFields;
import net.artux.pda.map.GdxAdapter;
import net.artux.pda.model.items.ItemsContainerModel;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.quest.StoryModel;
import net.artux.pda.model.quest.story.StoryDataModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Builds the same GdxAdapter the Android app builds in CoreFragment (core is fully
 * platform-agnostic - no Android types anywhere in this chain), except every piece of
 * data it needs from a real account/backend is hand-rolled here instead of coming from
 * QuestActivity's intent args. There's no login/registration/network layer on this build.
 */
public final class MockDataFactory {

    private MockDataFactory() {
    }

    public static ApplicationAdapter createApplication() {
        // StoryDataModel/StoryModel/GameMap have Kotlin default parameter values, but
        // aren't @JvmOverloads, so every constructor param still has to be passed
        // explicitly from Java.
        StoryDataModel storyData = new StoryDataModel(
                "iOS", "Tester", null, 0, 0, 1, null, null
        );
        // StoryDataModel.avatar's getter does field!!.contains("http") - never setting it
        // (staying null) throws the moment anything reads it.
        storyData.setAvatar("0");

        GameMap gameMap = new GameMap(
                0L, "Кордон", 0, "kordon.tmx", "500:500",
                new ArrayList<>(), new ArrayList<>()
        );

        StoryModel storyModel = new StoryModel(
                1L, "Mock story",
                new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedList<>()
        );

        ItemsContainerModel items = new ItemsContainerModel();

        Properties properties = new Properties();
        // RandomSpawnerHelper.init() reads these two unconditionally as soon as the map
        // loads - Float.parseFloat((String) properties.get(...)) - and TESTER_MODE is
        // read both as a String (AppModule) and compared to Boolean.TRUE directly
        // (HeaderInterfaceModule); leaving any of them out crashes on map load.
        properties.put(PropertyFields.TESTER_MODE, "false");
        properties.put(PropertyFields.GROUP_BOT_FREQ, "60");
        properties.put(PropertyFields.SINGLE_BOT_FREQ, "30");

        return new GdxAdapter.Builder(new MockPlatformInterface())
                .storyData(storyData)
                .story(storyModel)
                .map(gameMap)
                .items(items)
                .props(properties)
                .logger(new ConsoleApplicationLogger())
                .build();
    }
}
