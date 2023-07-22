package net.artux.pda.gdx;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import net.artux.pda.app.PDAApplication;
import net.artux.pda.map.GdxAdapter;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.items.ItemsContainerModel;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.quest.StoryModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.repositories.CommandController;
import net.artux.pda.ui.activities.MainActivity;
import net.artux.pda.ui.viewmodels.CommandViewModel;
import net.artux.pda.ui.viewmodels.QuestViewModel;
import net.artux.pda.utils.GDXTimberLogger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import barsoosayque.libgdxoboe.OboeAudio;
import timber.log.Timber;

public class CoreFragment extends AndroidFragmentApplication implements PlatformInterface {

    private GdxAdapter gdxAdapter;
    private QuestViewModel questViewModel;
    private GDXTimberLogger gdxTimberLogger;

    private CommandViewModel commandViewModel;

    @Override
    public AndroidAudio createAudio(Context context, AndroidApplicationConfiguration config) {
        return new OboeAudio(context.getAssets());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null) return null;

        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        questViewModel = provider.get(QuestViewModel.class);
        commandViewModel = provider.get(CommandViewModel.class);
        CommandController commandController = commandViewModel.getCommandController();

        gdxTimberLogger = new GDXTimberLogger();
        StoryModel storyModel = (StoryModel) args.getSerializable("story");
        StoryDataModel dataModel = (StoryDataModel) args.getSerializable("data");
        GameMap map = (GameMap) args.getSerializable("map");
        ItemsContainerModel items = (ItemsContainerModel) args.getSerializable("items");

        gdxAdapter = (GdxAdapter) new GdxAdapter.Builder(this)
                .map(map)
                .luaTable(commandController.getLuaTable())
                .story(storyModel)
                .storyData(dataModel)
                .items(items)
                .logger(gdxTimberLogger)
                .props(((PDAApplication) requireActivity().getApplication()).getProperties())
                .build();

        Timber.i("Core view created");
        return initializeForView(gdxAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        questViewModel.getStoryData().observe(getViewLifecycleOwner(), storyDataModel -> {
            if (gdxAdapter != null && !gdxAdapter.isDisposed())
                gdxAdapter.getDataRepository().setUserData(storyDataModel);
        });
    }

    @Override
    public void onResume() {
        Timber.i("Core resumed");
        Bundle args = getArguments();

        if (args != null) {
            GameMap map = (GameMap) args.getSerializable("map");

            DataRepository dataRepository = gdxAdapter.getDataRepository();
            dataRepository.setGameMap(map);

            boolean updated = args.getBoolean("updated", false);
            if (updated) args.putBoolean("updated", false);
            dataRepository.setUpdated(updated);
            setArguments(args);
            commandViewModel
                    .getCommandController()
                    .putObjectToScriptContext("adapter", gdxAdapter)
                    .putObjectToScriptContext("dataRepository", dataRepository)
                    .putObjectToScriptContext("sceneManager", gdxAdapter.getSceneManager())
                    .putObjectToScriptContext("core", gdxAdapter.getCoreComponent())
                    .putObjectToScriptContext("assetManager",
                            gdxAdapter.getCoreComponent().getAssetsManager());
        }
        setApplicationLogger(gdxTimberLogger);
        super.onResume();
    }

    @Override
    public void send(final Map<String, String> data) {
        runOnUiThread(() -> {
            if (data != null) {
                Timber.tag("Core Fragment").d("Got data - command: %s", data.toString());
                Intent intent = null;
                DataRepository dataRepository = gdxAdapter.getDataRepository();
                dataRepository.applyActions(Collections.emptyMap(), true); // apply all changes
                questViewModel.processData(data);
                if (data.containsKey("openPda")) {
                    Timber.d("Start MainActivity");
                    intent = new Intent(getActivity(), MainActivity.class);
                }
                if (intent == null)
                    return;

                requireActivity().finish();
                startActivity(intent);
            }
        });
    }

    @Override
    public void exit() {
        Map<String, String> data = new HashMap<>();
        data.put("openPda", "");
        send(data);
    }

    @Override
    public void applyActions(Map<String, List<String>> actions) {
        commandViewModel.processWithServer(actions);
    }

    @Override
    public void restart() {
        gdxAdapter.create();
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
        if (gdxAdapter != null) {
            gdxAdapter.dispose();
            gdxAdapter = null;
        }
        super.onDestroy();
    }

}
