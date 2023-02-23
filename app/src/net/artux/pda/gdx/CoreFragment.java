package net.artux.pda.gdx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import net.artux.pda.app.PDAApplication;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.GdxAdapter;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.items.ItemsContainerModel;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.quest.StoryModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.ui.activities.MainActivity;
import net.artux.pda.ui.viewmodels.QuestViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class CoreFragment extends AndroidFragmentApplication implements PlatformInterface {

    private GdxAdapter gdxAdapter;
    private QuestViewModel questViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            StoryModel storyModel = (StoryModel) args.getSerializable("story");
            StoryDataModel dataModel = (StoryDataModel) args.getSerializable("data");
            GameMap map = (GameMap) args.getSerializable("map");
            ItemsContainerModel items = (ItemsContainerModel) args.getSerializable("items");

            GdxAdapter.Builder builder = new GdxAdapter.Builder(this)
                    .map(map)
                    .story(storyModel)
                    .storyData(dataModel)
                    .items(items)
                    .props(((PDAApplication) requireActivity().getApplication()).getProperties());
            gdxAdapter = (GdxAdapter) builder.build();
            Timber.i("Core view created");
            return initializeForView(gdxAdapter);
        } else throw new RuntimeException("");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        questViewModel = provider.get(QuestViewModel.class);
        questViewModel.getStoryData().observe(getViewLifecycleOwner(), storyDataModel ->
                gdxAdapter.getDataRepository().setUserData(storyDataModel));
    }

    @Override
    public void onResume() {
        Timber.i("Core resumed");
        Bundle args = getArguments();
        if (args != null) {
            StoryDataModel dataModel = (StoryDataModel) args.getSerializable("data");
            GameMap map = (GameMap) args.getSerializable("map");

            DataRepository dataRepository = gdxAdapter.getDataRepository();
            dataRepository.setUserData(dataModel);
            dataRepository.setGameMap(map);
        }
        super.onResume();
    }

    @Override
    public void send(final Map<String, String> data) {
        runOnUiThread(() -> {
            if (data != null) {
                Timber.d("Got command: %s", data.toString());
                Intent intent = null;
                questViewModel.processData(data);
                if (data.containsKey("openPda")) {
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
    public void openPDA() {
        Map<String, String> data = new HashMap<>();
        data.put("openPda", "");
        send(data);
    }

    @Override
    public void applyActions(Map<String, List<String>> actions) {
        questViewModel.sync(actions);
    }

    @Override
    public void debug(String msg) {
        Timber.d(msg);
    }

    @Override
    public void toast(String msg) {
        if (Looper.myLooper() == null)
            Looper.prepare();
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void error(String msg, Throwable t) {
        Timber.e(t, msg);
        if (Looper.myLooper() == null)
            Looper.prepare();
        Toast.makeText(getActivity(), "Error with map, try again later: " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void restart() {
        gdxAdapter.create();
    }

    @Override
    public void rewardedAd() {
        Appodeal.show(requireActivity(), Appodeal.REWARDED_VIDEO, "default");
        Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
            @Override
            public void onRewardedVideoLoaded(boolean b) {

            }

            @Override
            public void onRewardedVideoFailedToLoad() {

            }

            @Override
            public void onRewardedVideoShown() {

            }

            @Override
            public void onRewardedVideoShowFailed() {

            }

            @Override
            public void onRewardedVideoFinished(double v, String s) {
                questViewModel.sync(Map.of("money", List.of(String.valueOf((int) v))));
            }

            @Override
            public void onRewardedVideoClosed(boolean b) {

            }

            @Override
            public void onRewardedVideoExpired() {

            }

            @Override
            public void onRewardedVideoClicked() {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.i("Core pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.i("Core stop");
    }

    @Override
    public void onDestroy() {
        Timber.d("Destroyed CoreStarter");
        super.onDestroy();
    }

}
