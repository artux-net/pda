package net.artux.pda.model.mapper;

import net.artux.pda.model.items.ItemType;
import net.artux.pdanetwork.model.ArmorDto;
import net.artux.pdanetwork.model.ArtifactDto;
import net.artux.pdanetwork.model.ItemDto;
import net.artux.pdanetwork.model.WeaponDto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    default ItemType type(ArmorDto.TypeEnum type) {
        return Enum.valueOf(ItemType.class, type.getValue());
    }

    default ItemType type(WeaponDto.TypeEnum type) {
        return Enum.valueOf(ItemType.class, type.getValue());
    }

    default ItemDto.TypeEnum type(ArtifactDto.TypeEnum type) {
        return Enum.valueOf(ItemDto.TypeEnum.class, type.getValue());
    }

}
