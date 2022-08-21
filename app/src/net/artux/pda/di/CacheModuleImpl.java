package net.artux.pda.di;

import android.content.Context;

import net.artux.pda.app.DataManager;
import net.artux.pda.map.model.input.Map;
import net.artux.pda.model.Summary;
import net.artux.pda.repositories.Cache;
import net.artux.pda.ui.fragments.quest.models.Chapter;
import net.artux.pdanetwork.model.Profile;
import net.artux.pdanetwork.model.StoryData;
import net.artux.pdanetwork.model.UserDto;

import dagger.Module;
import dagger.Provides;

@Module(includes = ContextModule.class)
public class CacheModuleImpl{

    @Provides
    public DataManager dataManager(Context context){
        return new DataManager(context);
    }

    @Provides
    public Cache<Profile> getProfileCache() {
        /*profileCache =
        memberCache = new Cache<>(UserDto.class, getApplicationContext(), gson);
        chapterCache = new Cache<>(Chapter.class, getApplicationContext(), gson);
        mapCache = new Cache<>(Map.class, getApplicationContext(), gson);
        summaryCache = new Cache<>(Summary.class, getApplicationContext(), gson);*/
        //return new Cache<>(Profile.class, getApplicationContext(), gson);
        return null;
    }

    @Provides
    public Cache<UserDto> getUserCache() {
        return null;
    }

    @Provides
    public Cache<Chapter> getChapterCache() {
        return null;
    }

    @Provides
    public Cache<Map> getMapCache() {
        return null;
    }

    @Provides
    public Cache<Summary> getSummaryCache() {
        return null;
    }

    @Provides
    public Cache<StoryData> getStoryDataCache() {
        return null;
    }
}
