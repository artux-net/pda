package net.artux.pda.model.mapper;

import net.artux.pda.model.StatusModel;
import net.artux.pdanetwork.model.Status;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = StoryMapper.class)
public interface StatusMapper {

    StatusMapper INSTANCE = Mappers.getMapper(StatusMapper.class);

    @Mapping(target = "storyDataModel", source = "storyData")
    StatusModel model(Status status);
}
