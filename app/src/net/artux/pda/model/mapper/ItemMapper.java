package net.artux.pda.model.mapper;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

import net.artux.pda.model.items.DetectorModel;
import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.items.ItemsContainerModel;
import net.artux.pdanetwork.model.ArmorDto;
import net.artux.pdanetwork.model.ArtifactDto;
import net.artux.pdanetwork.model.DetectorDto;
import net.artux.pdanetwork.model.ItemDto;
import net.artux.pdanetwork.model.ItemsContainer;
import net.artux.pdanetwork.model.MedicineDto;
import net.artux.pdanetwork.model.WeaponDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = RETURN_DEFAULT)
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "copy", ignore = true)
    DetectorModel model(DetectorDto dto);

    @Mapping(target = "all", ignore = true)
    ItemsContainerModel model(ItemsContainer itemsContainer);

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

    default ItemType type(DetectorDto.TypeEnum type) {
        return Enum.valueOf(ItemType.class, type.getValue());
    }

    default ItemType type(MedicineDto.TypeEnum type) {
        return Enum.valueOf(ItemType.class, type.getValue());
    }

}
