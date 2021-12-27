package net.artux.pda.gdx;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.savedstate.SavedStateRegistryOwner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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

import java.io.FileNotFoundException;
import java.util.HashMap;

import timber.log.Timber;

public class MapEngine extends AndroidApplication implements PlatformInterface, LifecycleOwner, SavedStateRegistryOwner {

    private Gson gson = new Gson();
    private GdxAdapter gdxAdapter;
    private ProfileViewModel viewModel;
    private LifecycleRegistry lifecycleRegistry;
    private final SavedStateRegistryController mSavedStateRegistryController = SavedStateRegistryController.create(this);
    public Bitmap texture;
    public Bitmap bounds;
    public Bitmap blur;

    private QuestViewModel questViewModel;

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
            loadTextures(map);

            gdxAdapter = new GdxAdapter(MapEngine.this);

            map.setPlayerPos(pos);
            gdxAdapter.put("map", gson.fromJson(getIntent().getStringExtra("map"), Map.class));
            gdxAdapter.put("member", ((Result.Success<Member>) member).getData());
            initialize(gdxAdapter, config);
            Gdx.app.postRunnable(() -> {
                gdxAdapter.put("texture", fromBitmap(texture));
                gdxAdapter.put("bounds", fromBitmap(bounds));
                gdxAdapter.put("blur", fromBitmap(blur));
            });
        }
    }

    void loadTextures(Map map){
        try {
            texture = loadBitmap(map.getTextureUri(), this);
            bounds = loadBitmap(map.getBoundsTextureUri(), this);
            blur = loadBitmap(map.getBlurTextureUri(), this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap loadBitmap(String filename, Context context) throws FileNotFoundException {
        return BitmapFactory.decodeStream(context
                .openFileInput(filename.replaceAll("/","")));
    }

    Texture fromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            Texture tex = new Texture(bitmap.getWidth(), bitmap.getHeight(), Pixmap.Format.RGBA8888);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getTextureObjectHandle());
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            bitmap.recycle();
            return tex;
        }
        return null;
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
    protected void onDestroy() {
        Timber.d("Destroyed CoreStarter");
        gdxAdapter = null;
        super.onDestroy();
    }

}
