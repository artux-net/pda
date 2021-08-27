package net.artux.pda;


import net.artux.pda.map.model.Map;
import net.artux.pda.ui.fragments.chat.Dialog;
import net.artux.pda.ui.fragments.quest.models.Chapter;
import net.artux.pda.ui.fragments.quest.models.Stories;
import net.artux.pda.ui.fragments.rating.UserInfo;
import net.artux.pdalib.LoginStatus;
import net.artux.pdalib.LoginUser;
import net.artux.pdalib.Member;
import net.artux.pdalib.Profile;
import net.artux.pdalib.QueryPage;
import net.artux.pdalib.RegisterUser;
import net.artux.pdalib.ResponsePage;
import net.artux.pdalib.Status;
import net.artux.pdalib.news.Article;
import net.artux.pdalib.profile.FriendModel;
import net.artux.pdalib.profile.Note;
import net.artux.pdalib.profile.Seller;
import net.artux.pdalib.profile.UpdateData;
import net.artux.pdalib.profile.items.Armor;
import net.artux.pdalib.profile.items.Weapon;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface PdaAPI{

    @POST("login")
    Call<LoginStatus> loginUser(@Body LoginUser user);

    @GET("login")
    Call<Member> loginUser();

    @PUT("login")
    Call<Status> updateFields(@Body UpdateData updateData);

    @POST("register")
    Call<Status> registerUser(@Body RegisterUser user);

    @GET("dialogs")
    Call<List<Dialog>> getDialogs();

    @GET("dialogs")
    Call<List<Dialog>> getFirstDialogs(@Header("f") String something);

    @GET("reset")
    Call<Status> resetPassword(@Query("email") String loginOrEmail);

    @GET("profile")
    Call<Profile> getProfile(@Query("pdaId") int pdaId);

    @GET("profile")
    Call<Profile> getMyProfile();

    @PUT("actions")
    Call<Member> synchronize(@Body HashMap<String, List<String>> actions);

    @GET(BuildConfig.PROTOCOL + "://" + BuildConfig.URL + "stories/story_{story}/chapter_{chapter}.cqe")
    Call<Chapter> getQuest(@Path("story") int story, @Path("chapter") int chapter);

    @GET(BuildConfig.PROTOCOL + "://" + BuildConfig.URL + "stories/story_{story}/maps/map_{map}.sm")
    Call<Map> getMap(@Path("story") int story, @Path("map") int map);

    @GET(BuildConfig.PROTOCOL + "://" + BuildConfig.URL + "stories")
    Call<Stories> getStories();

    @GET("enc/list")
    Call<LinkedHashMap<String, String>> getCategories(@Query("loc") String locale);

    /*
    0 - friends
    1 - request
     */
    @GET("friends")
    Call<List<FriendModel>> getFriends(@Query("pdaId") int pdaId, @Query("type") int type);

    @POST("friends")
    Call<Status> reqFriend(@Query("req") int id);

    @POST("friends")
    Call<Status> addFriend(@Query("add") int id);

    @POST("friends")
    Call<Status> removeFriend(@Query("remove") int id);

    @GET("ratings")
    Call<ResponsePage<UserInfo>> getRating(@Query("number") Integer number);

    @GET("items/{id}")
    Call<Seller> getSeller(@Path("id") int sellerId);

    @POST("items/buy/{type}")
    Call<Status> buyItem(@Path("type") int type, @Query("seller") int sellerId, @Body String jsonItem);

    @POST("items/sell/{type}")
    Call<Status> sellItem(@Path("type") int type, @Body String jsonItem);

    @POST("items/set/4")
    Call<Status> setArmor(@Body Armor item);

    @POST("items/set/{type}")
    Call<Status> setWeapon(@Path("type") int type, @Body Weapon item);

    @GET("reset/data")
    Call<Status> resetData();

    @PUT("notes")
    Call<Note> updateNote(@Body Note note);

    @POST("notes")
    Call<Note> createNote(@Body String title);

    @DELETE("notes")
    Call<Status> deleteNote(@Query("id") int cid);

    @GET("feed")
    Call<ResponsePage<Article>> getFeed();
}
