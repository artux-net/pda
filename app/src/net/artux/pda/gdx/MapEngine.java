package net.artux.pda.gdx;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.savedstate.SavedStateRegistryOwner;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.gson.Gson;

import net.artux.pda.map.GdxAdapter;
import net.artux.pda.map.model.input.Map;
import net.artux.pda.map.platform.PlatformInterface;
import net.artux.pda.model.mapper.UserMapper;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.quest.story.StoryStateModel;
import net.artux.pda.model.user.UserModel;
import net.artux.pda.ui.activities.MainActivity;
import net.artux.pda.ui.activities.QuestActivity;
import net.artux.pda.ui.fragments.quest.SellerActivity;
import net.artux.pda.ui.viewmodels.ProfileViewModel;
import net.artux.pda.ui.viewmodels.QuestViewModel;

import java.util.HashMap;

import timber.log.Timber;

public class MapEngine extends AndroidApplication implements PlatformInterface, LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {

    private final Gson gson = new Gson();
    private GdxMapper mapper = GdxMapper.INSTANCE;
    private ProfileViewModel viewModel;
    private LifecycleRegistry lifecycleRegistry;
    private final SavedStateRegistryController mSavedStateRegistryController = SavedStateRegistryController.create(this);

    private QuestViewModel questViewModel;

    ViewModelStore viewModelStore = new ViewModelStore();
    UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        lifecycleRegistry = new LifecycleRegistry(this);
        mSavedStateRegistryController.performRestore(savedInstanceState);

        if (viewModel == null)
            viewModel = getViewModelFactory(this).create(ProfileViewModel.class);
        if (questViewModel == null)
            questViewModel = getViewModelFactory(this).create(QuestViewModel.class);

        String pos = getIntent().getStringExtra("pos");

        UserModel member = userMapper.dto(viewModel.getUserRepository().getCachedMember().getOrThrow());
        StoryDataModel dataModel = questViewModel.getStoryData().getValue().getOrThrow();
        Map map = gson.fromJson(getIntent().getStringExtra("map"), Map.class);
        map.setPlayerPos(pos);
        GdxAdapter.Builder builder = new GdxAdapter.Builder(this)
                .map(map)
                .user(mapper.user(member))
                .storyData(mapper.data(dataModel));

        initialize(builder.build(), config);
    }


    @Override
    public void onStart() {
        super.onStart();
        lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
    }

    @NonNull
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mSavedStateRegistryController.performSave(outState);
    }

    @NonNull
    @Override
    public final SavedStateRegistry getSavedStateRegistry() {
        return mSavedStateRegistryController.getSavedStateRegistry();
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
                    String chapterId = data.get("chapter");
                    String stageId = data.get("stage");
                    if (chapterId != null && stageId != null) {
                        intent = new Intent(this, QuestActivity.class);
                        intent.putExtra("chapter", Integer.parseInt(chapterId));
                        intent.putExtra("stage", Integer.parseInt(stageId));
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
                } else if (data.containsKey("map")) {
                    String mapIdObject = data.get("map");
                    if (mapIdObject != null) {
                        int mapId = Integer.parseInt(mapIdObject);
                        String pos = data.get("pos");
                        StoryStateModel memberResult = questViewModel.getStoryData().getValue().getOrThrow().getCurrent();
                        MapHelper.prepareAndLoadMap(questViewModel, this, memberResult.getStoryId(), mapId, pos);
                    }
                    Timber.d("Start map - %s, position: %s", data.get("map"), data.get("pos"));
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

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }
}
