package net.artux.pda;


import net.artux.pda.map.model.Map;
import net.artux.pda.models.Dialog;
import net.artux.pda.views.quest.models.Chapter;
import net.artux.pda.views.quest.models.Stories;
import net.artux.pda.views.rating.UserInfo;
import net.artux.pdalib.LoginStatus;
import net.artux.pdalib.LoginUser;
import net.artux.pdalib.Member;
import net.artux.pdalib.Profile;
import net.artux.pdalib.RegisterUser;
import net.artux.pdalib.Status;
import net.artux.pdalib.profile.FriendModel;
import net.artux.pdalib.profile.Seller;
import net.artux.pdalib.profile.UpdateData;
import net.artux.pdalib.profile.items.Armor;
import net.artux.pdalib.profile.items.Item;
import net.artux.pdalib.profile.items.Weapon;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

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
    Call<Status> resetPassword(@Header("q") String loginOrEmail);

    @GET("/profile")
    Call<Profile> getProfile(@Query("pdaId") int pdaId);

    @GET("/profile")
    Call<Profile> getMyProfile();

    @POST("/profile")
    Call<Member> synchronize(@Body HashMap<String, List<String>> actions);

    @GET("/stories")
    Call<Chapter> getQuest(@Query("story") int story, @Query("chapter") int chapter);

    @GET("/stories")
    Call<Map> getMap(@Query("story") int story, @Query("map") int map);

    @GET("/stories")
    Call<Stories> getStories();

    @GET("/enc")
    Call<LinkedHashMap<String, String>> getCategories(@Query("loc") String locale);

    /*
    0 - friends
    1 - request
     */
    @GET("/friends")
    Call<List<FriendModel>> getFriends(@Query("pdaId") int pdaId, @Query("type") int type);

    @POST("/friends")
    Call<Status> reqFriend(@Query("req") int id);

    @POST("/friends")
    Call<Status> addFriend(@Query("add") int id);

    @POST("/friends")
    Call<Status> removeFriend(@Query("remove") int id);

    @GET("/ratings")
    Call<List<UserInfo>> getRating(@Query("from") int from);

    @GET("/items")
    Call<Seller> getSeller(@Query("id") int sellerId);

    @POST("/items?action=buy")
    Call<Status> buyItem(@Query("id") int sellerId, @Header("type") int type, @Body Item item);

    @POST("/items?action=sell")
    Call<Status> sellItem(@Header("type") int type, @Body Item item);

    @POST("/items?action=set")
    Call<Status> setArmor(@Header("type") int type, @Body Armor item);

    @POST("/items?action=set")
    Call<Status> setWeapon(@Header("type") int type, @Body Weapon item);

    @GET("/resetData")
    Call<Status> resetData();

}
