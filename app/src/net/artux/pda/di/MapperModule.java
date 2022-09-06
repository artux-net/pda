package net.artux.pda.di;

import net.artux.pda.model.mapper.ItemMapper;
import net.artux.pda.model.mapper.NoteMapper;
import net.artux.pda.model.mapper.StageMapper;
import net.artux.pda.model.mapper.StatusMapper;
import net.artux.pda.model.mapper.StoryMapper;
import net.artux.pda.model.mapper.UserMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn({SingletonComponent.class})
public class MapperModule {

    @Provides
    @Singleton
    public StoryMapper storyMapper() {
        return StoryMapper.INSTANCE;
    }

    @Provides
    @Singleton
    public UserMapper userMapper(){
        return UserMapper.INSTANCE;
    }

    @Provides
    @Singleton
    public ItemMapper itemMapper(){
        return ItemMapper.INSTANCE;
    }

    @Provides
    @Singleton
    public NoteMapper noteMapper(){
        return NoteMapper.INSTANCE;
    }

    @Provides
    @Singleton
    public StageMapper stageMapper(){
        return StageMapper.INSTANCE;
    }

    @Provides
    @Singleton
    public StatusMapper statusMapper(){
        return StatusMapper.INSTANCE;
    }

}
