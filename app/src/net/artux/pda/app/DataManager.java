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
        mSharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    public String getString(String name){
        return mSharedPreferences.getString(name, "");
    }

    public void setString(String name, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(name, value);
        editor.commit();
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
