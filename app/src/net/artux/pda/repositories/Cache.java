package net.artux.pda.repositories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class Cache<T> {

    private final Class<T> typeParameterClass;
    private final SharedPreferences mSharedPreferences;
    private final Gson gson;

    public Cache(Class<T> typeParameterClass, Context context, Gson gson) {
        this.typeParameterClass = typeParameterClass;
        this.gson = gson;
        mSharedPreferences = context.getSharedPreferences(typeParameterClass.getName(), Context.MODE_PRIVATE);
    }

    @SuppressLint("ApplySharedPref")
    public void put(String id, T object){
        mSharedPreferences.edit().putString(id, gson.toJson(object)).commit();
    }

    public T get(String id){
        if(!mSharedPreferences.contains(id))
            return null;
        return gson.fromJson(mSharedPreferences.getString(id, ""), typeParameterClass);
    }

    public boolean remove(String id){
        if(!mSharedPreferences.contains(id))
            return false;

        return mSharedPreferences.edit().remove(id).commit();
    }

    public String[] getIds(){
        return mSharedPreferences.getAll().keySet().toArray(new String[0]);
    }

    @SuppressLint("ApplySharedPref")
    public void clear(){
        mSharedPreferences.edit().clear().commit();
    }
}
