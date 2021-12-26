package net.artux.pda.gdx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;

import net.artux.pda.BuildConfig;
import net.artux.pda.map.model.Map;
import net.artux.pda.viewmodels.QuestViewModel;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import timber.log.Timber;

public class MapHelper {

    public static void prepareAndLoadMap(QuestViewModel questViewModel, AppCompatActivity context, int storyId, int mapId, String pos) {
        questViewModel.getMap(storyId, mapId).observe(context, new Observer<Map>() {
            @Override
            public void onChanged(Map map) {
                if (map != null) {
                    map.setPlayerPos(pos);
                    loadImages(context, map);
                    questViewModel.getMap().removeObserver(this);
                } else questViewModel.updateMap(storyId, mapId);
            }
        });
    }

    public static void prepareAndLoadMap(QuestViewModel questViewModel, MapEngine context, int storyId, int mapId, String pos) {
        questViewModel.getMap(storyId, mapId).observe(context, new Observer<Map>() {
            @Override
            public void onChanged(Map map) {
                if (map != null) {
                    map.setPlayerPos(pos);
                    loadImages(context, map);
                    questViewModel.getMap().removeObserver(this);
                } else questViewModel.updateMap(storyId, mapId);
            }
        });
    }

    static int num = 0;
    static int all = 3;

    private static void loadImages(Activity compatActivity, Map map) {
        loadImage(compatActivity, map.getTextureUri(), new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                saveBitmap(map.getTextureUri(), resource, compatActivity);
                checkAndStart(compatActivity, map);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Toast.makeText(compatActivity, "Unable to load the main texture.", Toast.LENGTH_LONG).show();
            }
        });
        loadImage(compatActivity, map.getBoundsTextureUri(), new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                saveBitmap(map.getBoundsTextureUri(), resource, compatActivity);
                checkAndStart(compatActivity, map);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Toast.makeText(compatActivity, "Unable to load bounds.", Toast.LENGTH_LONG).show();
                checkAndStart(compatActivity, map);
            }
        });
        loadImage(compatActivity, map.getBlurTextureUri(), new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                saveBitmap(map.getBlurTextureUri(), resource, compatActivity);
                checkAndStart(compatActivity, map);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Toast.makeText(compatActivity, "Unable to load blur.", Toast.LENGTH_LONG).show();
                checkAndStart(compatActivity, map);
            }
        });
    }

    public static void saveBitmap(String filename, Bitmap bitmap, Context context){
        if (!isFileExists(filename, context)){
            Timber.d("Caching for %s", formatFilename(filename));
            cacheBitmap(filename, bitmap, context);
        }else {
            Timber.d("%s exists, continue", formatFilename(filename));
        }
    }


    public static void cacheBitmap(String filename, Bitmap bitmap, Context context) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            FileOutputStream fo = context.openFileOutput(formatFilename(filename), Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isFileExists(String filename, Context context){
        filename = formatFilename(filename);
        String[] filenames = context.fileList();
        for (String s : filenames) {
            if (s.equals(filename))
                return true;
        }
        return false;
    }

    public static String formatFilename(String filename){
        return filename.replaceAll("/","");
    }

    static void checkAndStart(Activity context, Map map) {
        num++;
        if (num >= all)
            startMap(context, map);
    }

    static void startMap(Activity context, Map map) {
        Intent intent = new Intent(context, MapEngine.class);
        intent.putExtra("map", new Gson().toJson(map));
        context.startActivity(intent);
        context.finish();
    }

    private static void loadImage(Activity context, String uri, CustomTarget<Bitmap> target) {
        if (uri != null) {
            if (!uri.contains("http")) {
                String url = "https://" + BuildConfig.URL + "/" + uri;
                if (!context.isDestroyed())
                    Glide.with(context)
                            .asBitmap()
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .into(target);
            } else {
                if (!context.isDestroyed())
                    Glide.with(context)
                            .asBitmap()
                            .load(uri)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .into(target);
            }
            return;
        }
        num++;
    }


}
