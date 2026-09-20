package net.artux.pda.ios.mock;

import com.badlogic.gdx.ApplicationAdapter;

import net.artux.pda.common.PropertyFields;
import net.artux.pda.map.GdxAdapter;
import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.items.ItemsContainerModel;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.quest.StoryModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.Gang;
import net.artux.pda.model.user.GangRelation;

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
        // MoodComponent's player constructor calls storyData.getGang().getId() and
        // storyData.getRelations().getFor(...) unconditionally on map load - leaving
        // either null NPEs there.
        StoryDataModel storyData = new StoryDataModel(
                "iOS", "Tester", null, 0, 0, 1, Gang.LONERS, new GangRelation()
        );
        // StoryDataModel.avatar's getter does field!!.contains("http") - never setting it
        // (staying null) throws the moment anything reads it.
        storyData.setAvatar("0");

        // WeaponModel/ArmorModel are Kotlin data classes with default param values but
        // aren't @JvmOverloads either, so every constructor param needs to be passed
        // explicitly here too.
        WeaponModel pistol = new WeaponModel(
                15f, 1f, 20f, 100f,
                8, 0, 30f, null
        );
        pistol.setType(ItemType.PISTOL);
        pistol.baseId = 0; // ItemModel.baseId is @JvmField - a plain field, not a setter.
        pistol.setTitle("Pistol");
        pistol.setWeight(1.5f);
        pistol.setQuantity(1);
        pistol.setEquipped(true);
        storyData.getWeapons().add(pistol);

        // WeaponComponent.equip() looks up ammo in the inventory by bulletId == baseId.
        ItemModel bullets = new ItemModel();
        bullets.setType(ItemType.BULLET);
        bullets.baseId = 0;
        bullets.setTitle("Pistol ammo");
        bullets.setQuantity(60);
        storyData.getBullets().add(bullets);

        ArmorModel armor = new ArmorModel(
                20f, 20f, 20f, 20f, 20f, 20f, 100f
        );
        armor.setType(ItemType.ARMOR);
        armor.baseId = 0;
        armor.setTitle("Armor");
        armor.setWeight(5f);
        armor.setQuantity(1);
        armor.setEquipped(true);
        storyData.getArmors().add(armor);

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
        // TimerSystem.addTimerAction() rolls "frequency >= random(0,1)" every ~60s tick,
        // so values already above 1.0 guarantee a spawn roughly every minute - mutants
        // showed up reliably within the first minute or two on a real device run.
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
