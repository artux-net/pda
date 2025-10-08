package net.artux.pda.model.mapper;

import net.artux.pda.model.profile.NoteModel;
import net.artux.pdanetwork.model.NoteCreateDto;
import net.artux.pdanetwork.model.NoteDto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = TimeMapper.class)
public interface NoteMapper {

    NoteMapper INSTANCE = Mappers.getMapper(NoteMapper.class);

    NoteCreateDto createDto(NoteModel noteModel);
    NoteModel model(NoteDto dto);
    List<NoteModel> model(List<NoteDto> dto);

}
