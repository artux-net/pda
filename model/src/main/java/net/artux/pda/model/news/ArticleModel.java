package net.artux.pda.model.news;

import java.time.Instant;
import java.util.List;

import lombok.Data;

@Data
public class ArticleModel {

    private String id;
    private String title;
    private String image;
    private String url;
    private List<String> tags;
    private String description;
    private Instant published;

}
