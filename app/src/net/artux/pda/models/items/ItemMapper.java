package net.artux.pda.models.items;

import net.artux.pda.generated.models.ArmorDto;
import net.artux.pda.generated.models.ArtifactDto;
import net.artux.pda.generated.models.WeaponDto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    default ItemType type(ArmorDto.Type type) {
        return Enum.valueOf(ItemType.class, type.getValue());
    }

    default ItemType type(WeaponDto.Type type) {
        return Enum.valueOf(ItemType.class, type.getValue());
    }

    default net.artux.pda.map.models.items.ItemType type(ArtifactDto.Type type) {
        return Enum.valueOf(net.artux.pda.map.models.items.ItemType.class, type.getValue());
    }

}
