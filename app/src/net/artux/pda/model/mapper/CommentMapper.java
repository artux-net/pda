package net.artux.pda.model.mapper;

import net.artux.pda.model.news.CommentModel;
import net.artux.pdanetwork.model.CommentDto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = UserMapper.class)
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    CommentModel model(CommentDto dto);

    List<CommentModel> model(List<CommentDto> dto);
}
