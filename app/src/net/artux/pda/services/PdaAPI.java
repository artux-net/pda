package net.artux.pda.services;


import net.artux.pda.map.model.input.Map;
import net.artux.pda.ui.fragments.quest.models.Chapter;
import net.artux.pda.ui.fragments.quest.models.Stories;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PdaAPI {

    @GET("stories/story_{story}/chapter_{chapter}.cqe")
    Call<Chapter> getQuest(@Path("story") int story, @Path("chapter") int chapter);

    @GET("stories/story_{story}/maps/map_{map}.sm")
    Call<Map> getMap(@Path("story") int story, @Path("map") int map);

    @GET("stories")
    Call<Stories> getStories();

}
