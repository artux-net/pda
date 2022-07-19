package net.artux.pda.services;


import net.artux.pda.BuildConfig;
import net.artux.pda.map.model.input.Map;
import net.artux.pda.models.user.ProfileModel;
import net.artux.pda.models.user.UserModel;
import net.artux.pda.models.user.RegisterUser;
import net.artux.pda.models.ResponsePage;
import net.artux.pda.models.Status;
import net.artux.pda.models.news.Article;
import net.artux.pda.models.profile.FriendModel;
import net.artux.pda.models.profile.Note;
import net.artux.pda.models.profile.Seller;
import net.artux.pda.models.profile.UpdateData;
import net.artux.pda.ui.fragments.quest.models.Chapter;
import net.artux.pda.ui.fragments.quest.models.Stories;
import net.artux.pda.ui.fragments.rating.UserInfo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PdaAPI{

    @GET("login")
    Call<UserModel> loginUser();

    @PUT("login")
    Call<Status> updateFields(@Body UpdateData updateData);

    @POST("register")
    Call<Status> registerUser(@Body RegisterUser user);

    @GET("reset")
    Call<Status> resetPassword(@Query("email") String loginOrEmail);

    @GET("profileModel/{pdaId}")
    Call<ProfileModel> getProfile(@Path("pdaId") int pdaId);

    @GET("profileModel")
    Call<ProfileModel> getMyProfile();

    @PUT("actions")
    Call<UserModel> synchronize(@Body HashMap<String, List<String>> actions);

    @GET(BuildConfig.PROTOCOL + "://" + BuildConfig.URL + "stories/story_{story}/chapter_{chapter}.cqe")
    Call<Chapter> getQuest(@Path("story") int story, @Path("chapter") int chapter);

    @GET(BuildConfig.PROTOCOL + "://" + BuildConfig.URL + "stories/story_{story}/maps/map_{map}.sm")
    Call<Map> getMap(@Path("story") int story, @Path("map") int map);

    @GET(BuildConfig.PROTOCOL + "://" + BuildConfig.URL + "stories")
    Call<Stories> getStories();

    @GET("enc/list")
    Call<LinkedHashMap<String, String>> getCategories(@Query("loc") String locale);
    
    @GET("friends/{id}")
    Call<List<FriendModel>> getFriends(@Path("id") int pdaId);

    @GET("friends/subs/{id}")
    Call<List<FriendModel>> getSubs(@Path("id") int pdaId);

    @POST("friends")
    Call<Status> requestFriend(@Query("pdaId") int id);

    @GET("ratings")
    Call<ResponsePage<UserInfo>> getRating(@Query("number") Integer number);

    @GET("items/{id}")
    Call<Seller> getSeller(@Path("id") int sellerId);

    @POST("items/buy")
    Call<Status> buyItem(@Query("seller") int sellerId, @Query("hash") int hash);

    @POST("items/sell")
    Call<Status> sellItem(@Query("hash") int hash);

    @POST("items/set")
    Call<Status> setArmor(@Query("hash") int hash);

    @POST("items/set")
    Call<Status> setWeapon(@Query("hash") int hash);

    @GET("reset/data")
    Call<UserModel> resetData();

    @PUT("notes")
    Call<Note> updateNote(@Body Note note);

    @POST("notes")
    Call<Note> createNote(@Body String title);

    @DELETE("notes")
    Call<Status> deleteNote(@Query("id") int cid);

    @GET("feed")
    Call<ResponsePage<Article>> getFeed();
}
