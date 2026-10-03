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
        editor.remove("jwt");
        editor.putString("login", user.getEmailOrLogin());
        editor.putString("pass", user.getPassword());
        editor.commit();
    }

    /**
     * Stores the token issued after signing in with Google Play Games. That
     * account has no login/password the player ever typed, so this replaces
     * the Basic Auth credentials rather than living alongside them.
     */
    public void setJwtToken(String token) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove("login");
        editor.remove("pass");
        editor.putString("jwt", token);
        editor.commit();
    }

    public boolean isAuthenticated() {
        if (!mSharedPreferences.getString("jwt", "").isEmpty()) {
            return true;
        }
        if (mSharedPreferences.contains("login") && mSharedPreferences.contains("pass")){
            String login = mSharedPreferences.getString("login", "");
            String pass = mSharedPreferences.getString("pass", "");
            return !login.isEmpty() && !pass.isEmpty();
        }
        return false;
    }

    public String getLogin() {
        return mSharedPreferences.getString("login", "");
    }

    public String getPass() {
        return mSharedPreferences.getString("pass", "");
    }

    public String getAuthToken() {
        String jwt = mSharedPreferences.getString("jwt", "");
        if (!jwt.isEmpty())
            return "Bearer " + jwt;
        if (isAuthenticated())
            return Credentials.basic(mSharedPreferences.getString("login", ""), mSharedPreferences.getString("pass", ""));
        else return "";
    }

    public void removeAllData() {
        mSharedPreferences.edit().clear().commit();
    }

}
