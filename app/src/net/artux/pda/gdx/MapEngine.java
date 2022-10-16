package net.artux.pda.gdx;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import net.artux.pda.app.ForegroundService;
import net.artux.pda.map.GdxAdapter;
import net.artux.pda.map.model.input.GameMap;
import net.artux.pda.map.platform.PlatformInterface;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.quest.story.StoryStateModel;
import net.artux.pda.model.user.UserModel;
import net.artux.pda.ui.activities.MainActivity;
import net.artux.pda.ui.activities.QuestActivity;
import net.artux.pda.ui.fragments.quest.SellerActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MapEngine extends AndroidApplication implements PlatformInterface {

    public static final String RECEIVER_INTENT = "RECEIVER_INTENT";
    public static final String RECEIVE_STORY_DATA = "RECEIVER_DATA";
    public static final String RECEIVE_ERROR = "RECEIVER_ERROR";

    private StoryStateModel lastStoryState;
    private ForegroundService foregroundService;
    private boolean bound = false;
    private GdxAdapter gdxAdapter;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ForegroundService.ServiceBinder binder = (ForegroundService.ServiceBinder) service;
            foregroundService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    private BroadcastReceiver dataChangeReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        UserModel member = (UserModel) intent.getSerializableExtra("user");
        StoryDataModel dataModel = (StoryDataModel) intent.getSerializableExtra("data");
        GameMap map = (GameMap) intent.getSerializableExtra("map");
        lastStoryState = dataModel.getCurrentState();

        GdxAdapter.Builder builder = new GdxAdapter.Builder(this)
                .map(map)
                .user(member)
                .storyData(dataModel);

        gdxAdapter = (GdxAdapter) builder.build();
        initialize(gdxAdapter, new AndroidApplicationConfiguration());

        dataChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(RECEIVE_STORY_DATA)) {
                    StoryDataModel storyDataModel = (StoryDataModel) intent.getSerializableExtra(RECEIVE_STORY_DATA);
                    gdxAdapter.getDataRepository().setStoryDataModel(storyDataModel);
                    getIntent().putExtra("data", storyDataModel);
                } else if (intent.hasExtra(RECEIVE_ERROR)) {
                    Throwable throwable = (Throwable) intent.getSerializableExtra(RECEIVE_ERROR);
                    Timber.e(throwable, "Sync map error");
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void send(final HashMap<String, String> data) {
        runOnUiThread(() -> {
            if (data != null) {
                Timber.d("Got command: %s", data.toString());
                Intent intent = null;
                if (data.containsKey("chapter")) {
                    Integer storyId = lastStoryState.getStoryId();
                    String chapterId = data.get("chapter");
                    String stageId = data.get("stage");
                    if (chapterId != null && stageId != null) {
                        intent = new Intent(this, QuestActivity.class);
                        intent.putExtra("storyId", storyId);
                        intent.putExtra("chapterId", Integer.parseInt(chapterId));
                        intent.putExtra("stageId", Integer.parseInt(stageId));
                        Timber.d("Start QuestActivity - %s - %s", data.get("chapter"), data.get("stage"));
                    }
                } else if (data.containsKey("seller")) {
                    String sellerId = data.get("seller");
                    String mapId = data.get("map");

                    if (sellerId != null && mapId != null) {
                        intent = new Intent(this, SellerActivity.class);
                        intent.putExtra("seller", Integer.parseInt(sellerId));
                        intent.putExtra("map", Integer.parseInt(mapId));
                        intent.putExtra("pos", data.get("pos"));
                        Timber.d("Start seller activity - %s", data.get("seller"));
                    }
                } else if (data.containsKey("openPda")) {
                    Timber.d("Start MainActivity");
                    intent = new Intent(this, MainActivity.class);
                }

                if (intent != null) {
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void applyActions(Map<String, List<String>> actions) {
        foregroundService.applyActions(actions);
    }

    @Override
    public void debug(String msg) {
        Timber.d(msg);
    }

    @Override
    public void toast(String msg) {
        if (Looper.myLooper() == null)
            Looper.prepare();
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void error(String msg, Throwable t) {
        Timber.e(t, msg);
        if (Looper.myLooper() == null)
            Looper.prepare();
        Toast.makeText(getApplicationContext(), "Critical error with map, try again later", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void restart() {
        startActivity(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ForegroundService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver((dataChangeReceiver),
                new IntentFilter(RECEIVER_INTENT)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataChangeReceiver);
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    @Override
    protected void onDestroy() {
        Timber.d("Destroyed CoreStarter");
        super.onDestroy();
    }

}
