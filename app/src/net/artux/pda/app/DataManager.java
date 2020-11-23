package net.artux.pda.app;

import android.content.Context;
import android.content.SharedPreferences;

import net.artux.pdalib.Member;

import java.security.SecureRandom;

import at.favre.lib.armadillo.Armadillo;

public class DataManager {

    private SharedPreferences mSharedPreferences;

    private static Member member;

    public DataManager(Context context) {
        mSharedPreferences = Armadillo.create(context, "prefs")
                .password("f8a9a5t".toCharArray()) //use user provided password
                .secureRandom(new SecureRandom()) //provide your own secure random for salt/iv generation
                .encryptionFingerprint(context) //add the user id to fingerprint
                .supportVerifyPassword(true) //enables optional password validation support `.isValidPassword()`
                .enableKitKatSupport(true) //enable optional kitkat support
                .enableDerivedPasswordCache(true) //enable caching for derived password making consecutive getters faster
                .build();
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
        return mSharedPreferences.getString("token", "");
    }

    public void setMember(Member member){
        DataManager.member = member;
    }

    public Member getMember(){
        return member;
    }

    public void removeAllData(){
        member = null;

        mSharedPreferences.edit().clear().apply();
    }

}
