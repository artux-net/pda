package net.artux.pda.gdx;

import net.artux.pda.map.models.GangRelation;
import net.artux.pda.map.models.UserGdx;
import net.artux.pda.map.models.user.GdxData;
import net.artux.pda.map.models.user.ParameterGdx;
import net.artux.pda.models.quest.story.ParameterModel;
import net.artux.pda.models.quest.story.StoryDataModel;
import net.artux.pda.models.user.UserModel;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GdxMapper {

    GdxMapper INSTANCE = Mappers.getMapper(GdxMapper.class);

    @Mapping(target = "group", source = "gang.id")
    @Mapping(target = "relations", expression = "java(relation(dto.getRelations()))")
    UserGdx user(UserModel dto);

    GdxData data(StoryDataModel dto);

    ParameterGdx parameter(ParameterModel model);

    GangRelation relation(net.artux.pda.models.user.GangRelation relation);
}
