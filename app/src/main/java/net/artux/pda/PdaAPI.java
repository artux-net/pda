package net.artux.pda;


import retrofit2.http.PUT;
import net.artux.pda.Models.Dialog;
import net.artux.pda.Models.LoginStatus;
import net.artux.pda.Models.LoginUser;
import net.artux.pda.Models.Member;
import net.artux.pda.Models.Profile;
import net.artux.pda.Models.RegisterUser;
import net.artux.pda.Models.Status;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import net.artux.pda.Models.profile.UpdateData;
import net.artux.pda.Views.Quest.Models.Chapter;

public interface PdaAPI{

    @POST("/login")
    Call<LoginStatus> loginUser(@Body LoginUser user);

    @GET("/login")
    Call<Member> loginUser();

    @PUT("/login")
    Call<Status> updateFields(@Body UpdateData updateData);

    @POST("/register")
    Call<Status> registerUser(@Body RegisterUser user);

    @GET("/dialogs")
    Call<List<Dialog>> getDialogs();

    @GET("/reset")
    Call<Status> resetPassword(@Query("q") String loginOrEmail);

    @GET("/user")
    Call<Profile> getProfile(@Query("pdaId") int pdaId);

    @GET("/user")
    Call<Profile> getMyProfile();

    @POST("/user")
    Call<Boolean> doActions(@Body HashMap<String, List<String>> actions);

    @GET("/stages")
    Call<Chapter> getQuest(@Query("story") int story, @Query("chapter") int chapter);

    @GET("/enc")
    Call<LinkedHashMap<String, String>> getCategories(@Query("loc") String locale);
}
