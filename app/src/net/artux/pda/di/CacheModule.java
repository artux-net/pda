package net.artux.pda.di;

import android.content.Context;

import com.google.gson.Gson;

import net.artux.pda.app.DataManager;
import net.artux.pda.model.Summary;
import net.artux.pda.repositories.Cache;
import net.artux.pdanetwork.model.ArticleDto;
import net.artux.pdanetwork.model.ItemsContainer;
import net.artux.pdanetwork.model.NoteDto;
import net.artux.pdanetwork.model.Profile;
import net.artux.pdanetwork.model.SellerDto;
import net.artux.pdanetwork.model.Story;
import net.artux.pdanetwork.model.StoryData;
import net.artux.pdanetwork.model.StoryDto;
import net.artux.pdanetwork.model.UserDto;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class CacheModule {

    @Provides
    public DataManager dataManager(@ApplicationContext Context context) {
        return new DataManager(context);
    }

    @Provides
    public Cache<Profile> getProfileCache(@ApplicationContext Context context, Gson gson) {
        return new Cache<>(Profile.class, context, gson);
    }

    @Provides
    public Cache<ItemsContainer> getItemsCache(@ApplicationContext Context context, Gson gson) {
        return new Cache<>(ItemsContainer.class, context, gson);
    }

    @Provides
    public Cache<UserDto> getUserCache(@ApplicationContext Context context, Gson gson) {
        return new Cache<>(UserDto.class, context, gson);
    }

    @Provides
    public Cache<Story> getStoryCache(@ApplicationContext Context context, Gson gson) {
        return new Cache<>(Story.class, context, gson);
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

    @Provides
    public Cache<ArticleDto> getArticleCache(@ApplicationContext Context context, Gson gson) {
        return new Cache<>(ArticleDto.class, context, gson);
    }

    @Provides
    public Cache<SellerDto> getSellerCache(@ApplicationContext Context context, Gson gson) {
        return new Cache<>(SellerDto.class, context, gson);
    }

    @Provides
    public Cache<StoryDto> getStoriesCache(@ApplicationContext Context context, Gson gson) {
        return new Cache<>(StoryDto.class, context, gson);
    }
}
