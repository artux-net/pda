package net.artux.pda.gdx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

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

import timber.log.Timber;

public class MapEngine extends AndroidApplication implements PlatformInterface {

    private StoryStateModel lastStoryState;

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

        initialize(builder.build(), new AndroidApplicationConfiguration());
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
    public void debug(String msg) {
        Timber.d(msg);
    }

    @Override
    public void toast(String msg) {
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
        finish();
    }

    @Override
    protected void onDestroy() {
        Timber.d("Destroyed CoreStarter");
        super.onDestroy();
    }

}
