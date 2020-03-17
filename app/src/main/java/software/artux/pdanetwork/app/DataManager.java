package software.artux.pdanetwork.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import at.favre.lib.armadillo.Armadillo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import software.artux.pdanetwork.Models.Member;
import software.artux.pdanetwork.Models.Status;
import software.artux.pdanetwork.Models.profile.UpdateData;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;

public class DataManager {

    private SharedPreferences mSharedPreferences;

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    private Member member;

    public DataManager() {
        mSharedPreferences = Armadillo.create(App.getContext(), "prefs")
                .password("f8a9a5t".toCharArray()) //use user provided password
                .secureRandom(new SecureRandom()) //provide your own secure random for salt/iv generation
                .encryptionFingerprint(App.getContext()) //add the user id to fingerprint
                .supportVerifyPassword(true) //enables optional password validation support `.isValidPassword()`
                .enableKitKatSupport(true) //enable optional kitkat support
                .enableDerivedPasswordCache(true) //enable caching for derived password making consecutive getters faster
                .build();
    }

    public void setDialogsJson(String dialogsJson) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("dialogsJson", dialogsJson);
        editor.apply();
    }

    public String getDialogsJson(){
        return mSharedPreferences.getString("dialogsJson", "");
    }

    public void setAuthToken(String authToken) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("token", authToken);
        editor.apply();
    }

    public String getAuthToken() {
        String token = mSharedPreferences.getString("token", "");
        return token;
    }

    public void setMember(Member member){
        this.member = member;
    }

    public Member getMember(){
        return member;
    }

    public void removeAllData(){
        member = null;

        mSharedPreferences.edit().clear().apply();
        mSharedPreferences = Armadillo.create(App.getContext(), "prefs")
                .password("f8a9a5t".toCharArray()) //use user provided password
                .secureRandom(new SecureRandom()) //provide your own secure random for salt/iv generation
                .encryptionFingerprint(App.getContext()) //add the user id to fingerprint
                .supportVerifyPassword(true) //enables optional password validation support `.isValidPassword()`
                .enableKitKatSupport(true) //enable optional kitkat support
                .enableDerivedPasswordCache(true) //enable caching for derived password making consecutive getters faster
                .build();
    }

    public int getStoryId(){
        return 0;
    }

    public int getChapter(int story){
        return 0;
    }
}
