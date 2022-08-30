package net.artux.pda.di;

import android.content.Context;

import com.google.gson.Gson;

import net.artux.pda.app.DataManager;
import net.artux.pda.map.model.input.Map;
import net.artux.pda.model.Summary;
import net.artux.pda.repositories.Cache;
import net.artux.pda.ui.fragments.quest.models.Chapter;
import net.artux.pdanetwork.model.NoteDto;
import net.artux.pdanetwork.model.Profile;
import net.artux.pdanetwork.model.StoryData;
import net.artux.pdanetwork.model.UserDto;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class CacheModuleImpl{

    @Provides
    public DataManager dataManager(@ApplicationContext Context context){
        return new DataManager(context);
    }

    @Provides
    public Cache<Profile> getProfileCache(@ApplicationContext Context context, Gson gson) {
        return new Cache<>(Profile.class, context, gson);
    }

    @Provides
    public Cache<UserDto> getUserCache(@ApplicationContext Context context, Gson gson) {
        return new Cache<>(UserDto.class, context, gson);
    }

    @Provides
    public Cache<Chapter> getChapterCache(@ApplicationContext Context context, Gson gson) {
        return new Cache<>(Chapter.class, context, gson);
    }

    @Provides
    public Cache<Map> getMapCache(@ApplicationContext Context context, Gson gson) {
        return new Cache<>(Map.class, context, gson);
    }

    @Provides
    public Cache<Summary> getSummaryCache(@ApplicationContext Context context, Gson gson) {
        return new Cache<>(Summary.class, context, gson);
    }

    @Provides
    public Cache<StoryData> getStoryDataCache(@ApplicationContext Context context, Gson gson) {
        return new Cache<>(StoryData.class, context, gson);
    }

    @Provides
    public Cache<NoteDto> getNoteCache(@ApplicationContext Context context, Gson gson) {
        return new Cache<>(NoteDto.class, context, gson);
    }
}
