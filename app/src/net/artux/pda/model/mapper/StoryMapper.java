package net.artux.pda.model.mapper;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.Point;
import net.artux.pda.model.map.SpawnModel;
import net.artux.pda.model.quest.ChapterModel;
import net.artux.pda.model.quest.Stage;
import net.artux.pda.model.quest.StoryItem;
import net.artux.pda.model.quest.StoryModel;
import net.artux.pda.model.quest.story.ParameterModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.quest.story.StoryStateModel;
import net.artux.pdanetwork.model.ChapterDto;
import net.artux.pdanetwork.model.ParameterDto;
import net.artux.pdanetwork.model.Spawn;
import net.artux.pdanetwork.model.StoryData;
import net.artux.pdanetwork.model.StoryDto;
import net.artux.pdanetwork.model.StoryInfo;
import net.artux.pdanetwork.model.Text;
import net.artux.pdanetwork.model.Transfer;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Mapper(uses = {ItemMapper.class, UserMapper.class}, imports = LinkedList.class,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueMappingStrategy = RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
        nullValueIterableMappingStrategy = RETURN_DEFAULT,
        nullValueMapMappingStrategy = RETURN_DEFAULT,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StoryMapper {

    StoryMapper INSTANCE = Mappers.getMapper(StoryMapper.class);

    StoryModel story(StoryDto story);

    @Mapping(target = "parametersMap", ignore = true)
    @Mapping(target = "allItems", ignore = true)
    @Mapping(target = "currentWearable", ignore = true)
    StoryDataModel dataModel(StoryData storyData);

    ParameterModel paramModel(ParameterDto dto);

    @Mapping(target = "needs", defaultExpression = "java(new LinkedList<Integer>())")
    @Mapping(target = "complete", expression = "java(isStoryComplete(value.getId().intValue(), dataModel))")
    StoryItem storyItem(StoryInfo value, StoryDataModel dataModel);

    default List<StoryItem> storyItem(List<StoryInfo> value, StoryDataModel dataModel){
        LinkedList<StoryItem> result = new LinkedList<>();
        for (StoryInfo item : value)
            result.add(storyItem(item, dataModel));
        return result;
    }

    default boolean isStoryComplete(int id, StoryDataModel dataModel){
        StoryStateModel stateModel = dataModel.getStateByStoryId(id);
        if (stateModel == null)
            return false;
        return stateModel.getOver();
    }

    GameMap map(net.artux.pdanetwork.model.GameMap map);

    Point point(net.artux.pdanetwork.model.Point point);

    List<Point> points(List<net.artux.pdanetwork.model.Point> point);

    @Mapping(target = "params", ignore = true)
    SpawnModel spawn(Spawn spawn);

    List<SpawnModel> spawns(List<Spawn> spawn);

    @Mapping(target = "title", ignore = true)
    ChapterModel chapter(ChapterDto chapter);

    default Map<Long, Stage> stagesMap(Map<String, net.artux.pdanetwork.model.Stage> stageMap) {
        Map<Long, Stage> result = new HashMap<>();
        for (Map.Entry<String, net.artux.pdanetwork.model.Stage> entry : stageMap.entrySet()) {
            result.put(entry.getValue().getId(), stage(entry.getValue()));
        }
        return result;
    }

    Stage stage(net.artux.pdanetwork.model.Stage stage);

    net.artux.pda.model.quest.Text map(Text value);

    net.artux.pda.model.quest.Transfer map(Transfer value);

}
