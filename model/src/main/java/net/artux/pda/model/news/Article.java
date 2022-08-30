package net.artux.pda.model.news;

import java.util.List;

import lombok.Data;

@Data
public class Article {

    public String id;
    public String title;
    public String image;
    public List<String> tags;
    public String description;
    public long published;

}
