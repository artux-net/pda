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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import net.artux.pda.map.GdxAdapter;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.platform.PlatformInterface;
import net.artux.pda.repositories.Result;
import net.artux.pda.ui.activities.MainActivity;
import net.artux.pda.ui.activities.QuestActivity;
import net.artux.pda.ui.fragments.quest.SellerActivity;
import net.artux.pda.viewmodels.ProfileViewModel;
import net.artux.pda.viewmodels.QuestViewModel;
import net.artux.pdalib.Member;

import java.util.HashMap;

import timber.log.Timber;

public class MapEngine extends AndroidApplication implements PlatformInterface, LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {

    private final Gson gson = new Gson();
    private GdxAdapter gdxAdapter;
    private ProfileViewModel viewModel;
    private LifecycleRegistry lifecycleRegistry;
    private final SavedStateRegistryController mSavedStateRegistryController = SavedStateRegistryController.create(this);

    private QuestViewModel questViewModel;

    ViewModelStore viewModelStore = new ViewModelStore();

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

        Result<Member> member = viewModel.getUserRepository().getCachedMember();
        if (member instanceof Result.Success ) {
            Map map = gson.fromJson(getIntent().getStringExtra("map"),Map.class);

            gdxAdapter = new GdxAdapter(MapEngine.this);

            map.setPlayerPos(pos);
            gdxAdapter.put("map", gson.fromJson(getIntent().getStringExtra("map"), Map.class));
            gdxAdapter.put("member", ((Result.Success<Member>) member).getData());
            initialize(gdxAdapter, config);
        }
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
                        if (chapterId!=null && stageId!=null) {
                            intent = new Intent(this, QuestActivity.class);
                            intent.putExtra("chapter", Integer.parseInt(chapterId));
                            intent.putExtra("stage", Integer.parseInt(stageId));
                            Timber.d("Start QuestActivity - %s - %s", data.get("chapter"), data.get("stage"));
                        }
                } else if (data.containsKey("seller")) {
                    String sellerId = data.get("seller");
                    String mapId = data.get("map");

                    if(sellerId != null && mapId!=null) {
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
                        Result<Member> memberResult = viewModel.getUserRepository().getCachedMember();
                        if (memberResult instanceof Result.Success) {
                            Member member = ((Result.Success<Member>) memberResult).getData();
                            String currentStory = member.getData().getTemp().get("currentStory");
                            if (currentStory != null) {
                                int storyId = Integer.parseInt(currentStory);
                                MapHelper.prepareAndLoadMap(questViewModel, this, storyId, mapId, pos);
                            }
                        }
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
        if (Looper.myLooper()==null)
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
        gdxAdapter = null;
        super.onDestroy();
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }
}
