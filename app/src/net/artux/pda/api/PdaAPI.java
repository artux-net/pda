package net.artux.pda.api;


import net.artux.pda.map.model.input.Map;
import net.artux.pda.model.quest.ChapterModel;
import net.artux.pda.model.quest.StoriesContainer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PdaAPI {

    @GET("stories/story_{story}/chapter_{chapter}.cqe")
    Call<ChapterModel> getQuest(@Path("story") int story, @Path("chapter") int chapter);

    @GET("stories/story_{story}/maps/map_{map}.sm")
    Call<Map> getMap(@Path("story") int story, @Path("map") int map);

    @GET("stories")
    Call<StoriesContainer> getStories();

}
