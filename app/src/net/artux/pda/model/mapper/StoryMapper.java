package net.artux.pda.model.mapper;

import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.quest.story.ParameterModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pdanetwork.model.ArmorDto;
import net.artux.pdanetwork.model.ArtifactDto;
import net.artux.pdanetwork.model.ItemDto;
import net.artux.pdanetwork.model.ParameterDto;
import net.artux.pdanetwork.model.StoryData;
import net.artux.pdanetwork.model.WeaponDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StoryMapper {

    StoryMapper INSTANCE = Mappers.getMapper(StoryMapper.class);

    @Mapping(target = "parametersMap", ignore = true)
    @Mapping(target = "allItems", ignore = true)
    StoryDataModel dataModel(StoryData storyData);

    ParameterModel paramModel(ParameterDto dto);

    default ItemType type(ArmorDto.TypeEnum type) {
        return Enum.valueOf(ItemType.class, type.getValue());
    }

    default ItemType type(WeaponDto.TypeEnum type) {
        return Enum.valueOf(ItemType.class, type.getValue());
    }

    default ItemType type(ArtifactDto.TypeEnum type) {
        return Enum.valueOf(ItemType.class, type.getValue());
    }

    default ItemType type(ItemDto.TypeEnum type) {
        return Enum.valueOf(ItemType.class, type.getValue());
    }
}
