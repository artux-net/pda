package net.artux.pda.gdx;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import net.artux.pda.app.ForegroundService;
import net.artux.pda.app.PDAApplication;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.GdxAdapter;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.quest.StoryModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.quest.story.StoryStateModel;
import net.artux.pda.model.user.UserModel;
import net.artux.pda.ui.activities.MainActivity;
import net.artux.pda.ui.viewmodels.QuestViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class CoreFragment extends AndroidFragmentApplication implements PlatformInterface {

    public static final String RECEIVER_INTENT = "RECEIVER_INTENT";
    public static final String RECEIVE_STORY_DATA = "RECEIVER_DATA";
    public static final String RECEIVE_ERROR = "RECEIVER_ERROR";

    private StoryStateModel lastStoryState;
    private ForegroundService foregroundService;
    private boolean bound = false;
    private GdxAdapter gdxAdapter;
    private QuestViewModel questViewModel;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        UserModel user = (UserModel) args.getSerializable("user");
        StoryModel storyModel = (StoryModel) args.getSerializable("story");
        StoryDataModel dataModel = (StoryDataModel) args.getSerializable("data");
        GameMap map = (GameMap) args.getSerializable("map");

        lastStoryState = dataModel.getCurrentState();

        GdxAdapter.Builder builder = new GdxAdapter.Builder(this)
                .map(map)
                .user(user)
                .story(storyModel)
                .storyData(dataModel)
                .props(((PDAApplication) requireActivity().getApplication()).getProperties());
        gdxAdapter = (GdxAdapter) builder.build();
        Timber.i("Core view created");
        return initializeForView(gdxAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        questViewModel = provider.get(QuestViewModel.class);
    }

    @Override
    public void onResume() {
        Timber.i("Core resumed");
        Bundle args = getArguments();
        UserModel user = (UserModel) args.getSerializable("user");
        StoryModel storyModel = (StoryModel) args.getSerializable("story");
        StoryDataModel dataModel = (StoryDataModel) args.getSerializable("data");
        GameMap map = (GameMap) args.getSerializable("map");
        DataRepository dataRepository = gdxAdapter.getDataRepository();
        dataRepository.setStoryDataModel(dataModel);
        dataRepository.setGameMap(map);
        dataRepository.setStoryModel(storyModel);
        dataRepository.setUserModel(user);
        super.onResume();
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
                        questViewModel.beginWithStage(Integer.parseInt(chapterId), Integer.parseInt(stageId));
                       /* intent = new Intent(this, QuestActivity.class);
                        intent.putExtra("storyId", storyId);
                        intent.putExtra("chapterId", );
                        intent.putExtra("stageId", );
                        Timber.d("Start QuestActivity - %s - %s", data.get("chapter"), data.get("stage"));*/
                    }
                } else if (data.containsKey("seller")) {
                    String sellerId = data.get("seller");
                    String mapId = data.get("map");

                    if (sellerId != null && mapId != null) {
                        /*intent = new Intent(this, SellerActivity.class);
                        intent.putExtra("seller", Integer.parseInt(sellerId));
                        intent.putExtra("map", Integer.parseInt(mapId));
                        intent.putExtra("pos", data.get("pos"));
                        Timber.d("Start seller activity - %s", data.get("seller"));*/
                    }
                } else if (data.containsKey("openPda")) {
                    Timber.d("Start MainActivity");
                    intent = new Intent(getActivity(), MainActivity.class);
                }

                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void applyActions(Map<String, List<String>> actions) {
        //questViewModel..applyActions(actions);
    }

    @Override
    public void debug(String msg) {
        Timber.d(msg);
    }

    @Override
    public void toast(String msg) {
        if (Looper.myLooper() == null)
            Looper.prepare();
        //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void error(String msg, Throwable t) {
        Timber.e(t, msg);
        if (Looper.myLooper() == null)
            Looper.prepare();
        /*Toast.makeText(getApplicationContext(), "Critical error with map, try again later", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();*/
    }

    @Override
    public void restart() {
        gdxAdapter.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        /*Intent intent = new Intent(this, ForegroundService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver((dataChangeReceiver),
                new IntentFilter(RECEIVER_INTENT)
        );*/
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.i("Core pause");
    }

    @Override
    public void onStop() {
       // LocalBroadcastManager.getInstance(this).unregisterReceiver(dataChangeReceiver);
        super.onStop();
        Timber.i("Core stop");
       /* if (bound) {
            unbindService(connection);
            bound = false;
        }*/
    }

    @Override
    public void onDestroy() {
        Timber.d("Destroyed CoreStarter");
        super.onDestroy();
    }

}
