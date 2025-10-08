package net.artux.pda.model.mapper;

import net.artux.pda.model.items.SellerModel;
import net.artux.pdanetwork.model.SellerDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = ItemMapper.class)
public interface SellerMapper {

    SellerMapper INSTANCE = Mappers.getMapper(SellerMapper.class);

    @Mapping(target = "allItems", ignore = true)
    SellerModel model(SellerDto dto);

}
