package net.artux.pda.models.quest.story;

import net.artux.pda.generated.models.ArmorDto;
import net.artux.pda.generated.models.ArtifactDto;
import net.artux.pda.generated.models.ItemDto;
import net.artux.pda.generated.models.ParameterDto;
import net.artux.pda.generated.models.StoryData;
import net.artux.pda.generated.models.WeaponDto;
import net.artux.pda.models.items.ItemType;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StoryMapper {

    StoryMapper INSTANCE = Mappers.getMapper(StoryMapper.class);

    @Mapping(target = "parametersMap", ignore = true)
    @Mapping(target = "allItems", ignore = true)
    StoryDataModel  dataModel(StoryData storyData);

    ParameterModel paramModel(ParameterDto dto);

    default ItemType type(ArmorDto.Type type) {
        return Enum.valueOf(ItemType.class, type.getValue());
    }

    default ItemType type(WeaponDto.Type type) {
        return Enum.valueOf(ItemType.class, type.getValue());
    }

    default ItemType type(ArtifactDto.Type type) {
        return Enum.valueOf(ItemType.class, type.getValue());
    }

    default ItemType type(ItemDto.Type type) {
        return Enum.valueOf(ItemType.class, type.getValue());
    }
}
