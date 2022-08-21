package net.artux.pda.model.mapper;

import net.artux.pda.model.StatusModel;
import net.artux.pdanetwork.model.Status;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StatusMapper {

    StatusMapper INSTANCE = Mappers.getMapper(StatusMapper.class);

    StatusModel model(Status status);
}
