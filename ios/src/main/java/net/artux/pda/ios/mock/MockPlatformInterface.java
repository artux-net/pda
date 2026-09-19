package net.artux.pda.ios.mock;

import com.badlogic.gdx.Gdx;

import net.artux.pda.map.utils.PlatformInterface;

import java.util.List;
import java.util.Map;

/**
 * Stands in for the real PlatformInterface the Android app module implements in
 * CoreFragment - which talks to QuestActivity/QuestViewModel and, through those, the
 * real backend. There's no equivalent on this iOS build (see MockDataFactory), so
 * every callback here just logs instead of acting on it.
 */
public class MockPlatformInterface implements PlatformInterface {

    private static final String TAG = "MockPlatformInterface";

    @Override
    public void putObjectToLuaContext(String key, Object value) {
        Gdx.app.log(TAG, "putObjectToLuaContext(" + key + ")");
    }

    @Override
    public void send(Map<String, String> data) {
        Gdx.app.log(TAG, "send() ignored (no backend on this build): " + data);
    }

    @Override
    public void applyActions(Map<String, List<String>> actions) {
        Gdx.app.log(TAG, "applyActions() ignored (no backend on this build): " + actions);
    }

    @Override
    public void restart() {
        Gdx.app.log(TAG, "restart() requested - exiting instead, there's no map reload without a real backend to refetch from.");
        Gdx.app.exit();
    }

    @Override
    public void exit() {
        Gdx.app.exit();
    }

    @Override
    public void openLogs() {
        Gdx.app.log(TAG, "openLogs() ignored (no log viewer on this build)");
    }
}
