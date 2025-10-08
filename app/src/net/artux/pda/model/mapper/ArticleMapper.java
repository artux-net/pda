package net.artux.pda.model.mapper;

import net.artux.pda.model.news.ArticleModel;
import net.artux.pdanetwork.model.ArticleDto;
import net.artux.pdanetwork.model.ArticleSimpleDto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = TimeMapper.class)
public interface ArticleMapper {

    ArticleMapper INSTANCE = Mappers.getMapper(ArticleMapper.class);

    ArticleModel model(ArticleDto noteModel);

    List<ArticleModel> model(List<ArticleDto> noteModel);

    ArticleModel models(ArticleSimpleDto noteModel);

    List<ArticleModel> models(List<ArticleSimpleDto> noteModel);



}
