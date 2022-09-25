package net.artux.pda.model.mapper;

import net.artux.pda.model.quest.story.ParameterModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pdanetwork.model.ParameterDto;
import net.artux.pdanetwork.model.StoryData;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ItemMapper.class, UserMapper.class})
public interface StoryMapper {

    StoryMapper INSTANCE = Mappers.getMapper(StoryMapper.class);

    @Mapping(target = "parametersMap", ignore = true)
    @Mapping(target = "allItems", ignore = true)
    StoryDataModel dataModel(StoryData storyData);

    ParameterModel paramModel(ParameterDto dto);
    
}
