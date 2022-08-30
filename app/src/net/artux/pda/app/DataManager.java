package net.artux.pda.app;

import android.content.Context;
import android.content.SharedPreferences;

import net.artux.pda.model.user.LoginUser;

import okhttp3.Credentials;

public class DataManager {

    private final SharedPreferences mSharedPreferences;

    public DataManager(Context context) {
        mSharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    public String getString(String name) {
        return mSharedPreferences.getString(name, "");
    }

    public void setString(String name, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public void setLoginUser(LoginUser user) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("login", user.getEmailOrLogin());
        editor.putString("pass", user.getPassword());
        editor.commit();
    }

    public boolean isAuthenticated() {
        return mSharedPreferences.contains("login") && mSharedPreferences.contains("pass");
    }

    public String getLogin() {
        return mSharedPreferences.getString("login", "");
    }

    public String getPass() {
        return mSharedPreferences.getString("pass", "");
    }

    public String getAuthToken() {
        if (isAuthenticated())
            return Credentials.basic(mSharedPreferences.getString("login", ""), mSharedPreferences.getString("pass", ""));
        else return "";
    }

    public void removeAllData() {
        mSharedPreferences.edit().clear().commit();
    }

}
