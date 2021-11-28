package net.artux.pda.gdx;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.savedstate.SavedStateRegistryOwner;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.gson.Gson;

import net.artux.pda.map.GdxAdapter;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.platform.PlatformInterface;
import net.artux.pda.repositories.Result;
import net.artux.pda.ui.activities.MainActivity;
import net.artux.pda.ui.activities.QuestActivity;
import net.artux.pda.viewmodels.ProfileViewModel;
import net.artux.pda.ui.fragments.quest.SellerActivity;
import net.artux.pdalib.Member;

import java.util.HashMap;

import timber.log.Timber;

public class CoreStarter extends AndroidApplication implements PlatformInterface, LifecycleOwner, SavedStateRegistryOwner {

    private Gson gson = new Gson();
    private GdxAdapter gdxAdapter;
    private ProfileViewModel viewModel;
    private LifecycleRegistry lifecycleRegistry;
    private final SavedStateRegistryController mSavedStateRegistryController = SavedStateRegistryController.create(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();


        lifecycleRegistry = new LifecycleRegistry(this);
        mSavedStateRegistryController.performRestore(savedInstanceState);

        if(viewModel == null)
            viewModel = getViewModelFactory(this).create(ProfileViewModel.class);

        Result<Member> member = viewModel.getUserRepository().getCachedMember();
        if (member instanceof Result.Success){
            Map map = gson.fromJson(getIntent().getStringExtra("map"), Map.class);
            gdxAdapter = new GdxAdapter(CoreStarter.this);
            gdxAdapter.put("map", map);
            gdxAdapter.put("member", ((Result.Success<Member>) member).getData());
            initialize(gdxAdapter, config);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleRegistry.markState(Lifecycle.State.STARTED);
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
            if (data!=null) {
                Timber.d("Got command: %s", data.toString());
                Intent intent = null;
                if(data.containsKey("chapter")){
                    intent = new Intent(this, QuestActivity.class);
                    intent.putExtra("chapter", Integer.parseInt(data.get("chapter")));
                    intent.putExtra("stage", Integer.parseInt(data.get("stage")));
                    Timber.d("Start QuestActivity - %s - %s", data.get("chapter"), data.get("stage"));
                }else if (data.containsKey("seller")){
                    intent = new Intent(this, SellerActivity.class);
                    intent.putExtra("seller", Integer.parseInt(data.get("seller")));
                    intent.putExtra("map", Integer.parseInt(data.get("map")));
                    intent.putExtra("pos", data.get("pos"));
                    Timber.d("Start seller activity - %s", data.get("seller"));
                }else if (data.containsKey("map")){
                    intent = new Intent(this, QuestActivity.class);
                    intent.putExtra("map", Integer.parseInt(data.get("map")));
                    intent.putExtra("pos", data.get("pos"));
                    Timber.d("Start map - %s, position: %s", data.get("map"), data.get("pos"));
                }else if (data.containsKey("openPda")){
                    Timber.d("Start MainActivity");
                    intent = new Intent(this, MainActivity.class);
                }

                if (intent != null){
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        Timber.d("Destroyed CoreStarter");
        gdxAdapter = null;
        super.onDestroy();
    }

}
