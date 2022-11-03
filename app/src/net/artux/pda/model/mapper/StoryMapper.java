package net.artux.pda.model.mapper;

import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.Point;
import net.artux.pda.model.quest.ChapterModel;
import net.artux.pda.model.quest.Sound;
import net.artux.pda.model.quest.Stage;
import net.artux.pda.model.quest.StoryItem;
import net.artux.pda.model.quest.story.ParameterModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pdanetwork.model.Chapter;
import net.artux.pdanetwork.model.ParameterDto;
import net.artux.pdanetwork.model.StoryData;
import net.artux.pdanetwork.model.StoryDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {ItemMapper.class, UserMapper.class})
public interface StoryMapper {

    StoryMapper INSTANCE = Mappers.getMapper(StoryMapper.class);

    @Mapping(target = "parametersMap", ignore = true)
    @Mapping(target = "allItems", ignore = true)
    StoryDataModel dataModel(StoryData storyData);

    ParameterModel paramModel(ParameterDto dto);

    List<StoryItem> stories(List<StoryDto> dtos);

    GameMap map(net.artux.pdanetwork.model.GameMap map);

    Point point(net.artux.pdanetwork.model.Point point);

    ChapterModel chapter(Chapter chapter);

    Stage stage(net.artux.pdanetwork.model.Stage stage);

    Sound sound(net.artux.pdanetwork.model.Sound sound);

}
