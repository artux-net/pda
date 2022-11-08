package net.artux.pda.utils;

import android.content.Context;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

import net.artux.pda.model.quest.Sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MultiExoPlayer {

    private final Context context;
    private final List<Sound> sounds;
    private final HashMap<Sound, SimpleExoPlayer> player = new HashMap<>();
    boolean muted;

    public MultiExoPlayer(Context context, List<Sound> soundList) {
        this.context = context;
        sounds = soundList;
        /*myDownloadService = new MyDownloadService(1);
        for (Sound sound:soundList){
            DownloadRequest downloadRequest =
                    new DownloadRequest.Builder(sound.getUrl(), Uri.parse(sound.getUrl())).build();

            DownloadService.sendAddDownload(
                    context,
                    MyDownloadService.class,
                    downloadRequest,
                    *//* foreground= *//* true);
            //myDownloadService.send//addDownload(downloadRequest);
        }*/
    }

    private Sound getSound(int id) {
        if (sounds != null && sounds.size() != 0) {
            for (Sound sound : sounds) {
                if (id == sound.getId())
                    return sound;
            }
        }
        return null;
    }

    public void setSound(int[] ids) {
        reset(ids);
        if (ids != null && ids.length != 0)
            for (int id : ids) {
                Sound sound = getSound(id);

                if (sound != null) {
            /*DataSource.Factory cacheDataSourceFactory =
                    new CacheDataSource.Factory()
                            .setCache(myDownloadService.getDownloadCache())
                            .setUpstreamDataSourceFactory(myDownloadService.getDataSourceFactory())
                            .setCacheWriteDataSinkFactory(null); // Disable writing.


            ProgressiveMediaSource mediaSource =
                    new ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(sound.getUrl()));*/
                    if (!player.containsKey(sound)) {
                        System.out.println("1");
                        addSound(sound);
                    } else {
                        System.out.println("2");
                        SimpleExoPlayer play = player.get(sound);
                        if (play != null)
                            if (!(play.getPlaybackState() == Player.STATE_READY && play.getPlayWhenReady()))
                                play.play();
                    }
                }
            }
    }

    void addSound(Sound sound) {
        SimpleExoPlayer play = new SimpleExoPlayer.Builder(context)
                .build();
        System.out.println("play: " + sound.getUrl() + " id: " + sound.getId());
        for (String param : sound.getParams()) {
            if (param.equals("loop")) {
                play.setRepeatMode(play.REPEAT_MODE_ONE);
            } else if (param.contains(":")) {
                String[] keyValue = param.split(":");
                if (keyValue[0].equals("volume")) {
                    play.setVolume(Float.parseFloat(keyValue[1]));
                }
            }
        }
        String mediaUrl = URLHelper.getResourceURL(sound.getUrl());

        play.setMediaItem(MediaItem.fromUri(mediaUrl));
        play.prepare();
        play.play();
        player.put(sound, play);
    }

    void reset(int[] ids) {
        Iterator<Map.Entry<Sound, SimpleExoPlayer>> iterator = player.entrySet().iterator();
        List<Sound> remove = new ArrayList<>();
        while (iterator.hasNext()) {
            boolean f = true;
            Map.Entry<Sound, SimpleExoPlayer> e = iterator.next();
            if (ids != null && ids.length != 0)
                for (int id : ids) {
                    Sound s = getSound(id);
                    if (s != null) {
                        if (s.equals(e.getKey())) {
                            f = false;
                            break;
                        }
                    }
                }
            if (f)
                remove.add(e.getKey());
        }

        for (Sound i : remove) {
            SimpleExoPlayer p = player.get(i);
            if (p != null) {
                System.out.println("released:" + i.getId());
                p.release();
            }
            System.out.println("remove: " + i.getUrl() + " id " + i.getId());
            player.remove(i);
        }
    }

    public void release() {
        for (SimpleExoPlayer exoPlayer : player.values()) {
            if (exoPlayer != null)
                exoPlayer.release();
        }
    }

    public void unmute() {
        muted = false;
        for (Map.Entry<Sound, SimpleExoPlayer> s : player.entrySet())
            if (s.getValue() != null)
                for (String param : s.getKey().getParams())
                    if (param.equals("loop")) {
                        s.getValue().setRepeatMode(s.getValue().REPEAT_MODE_ONE);
                    } else if (param.contains(":")) {
                        String[] keyValue = param.split(":");
                        if (keyValue[0].equals("volume")) {
                            s.getValue().setVolume(Float.parseFloat(keyValue[1]));
                        }
                    }
    }

    public void mute() {
        muted = true;
        for (SimpleExoPlayer exoPlayer : player.values()) {
            if (exoPlayer != null)
                exoPlayer.setVolume(0);
        }
    }

    public boolean isMuted() {
        return muted;
    }
}
