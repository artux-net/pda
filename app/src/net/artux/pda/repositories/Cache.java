package net.artux.pda.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;

public class Cache<T> {

    private final Class<T> typeParameterClass;
    private final SharedPreferences mSharedPreferences;
    private final Gson gson;

    public Cache(Class<T> typeParameterClass, Context context, Gson gson) {
        this.typeParameterClass = typeParameterClass;
        this.gson = gson;
        mSharedPreferences = context.getSharedPreferences(typeParameterClass.getName(), Context.MODE_PRIVATE);
    }

    public void put(String id, T object) {
        mSharedPreferences.edit().putString(id, gson.toJson(object)).apply();
    }

    private T getById(String id) {
        if (!mSharedPreferences.contains(id))
            return null;
        return gson.fromJson(mSharedPreferences.getString(id, ""), typeParameterClass);
    }

    public T get(String id) {
        try {
            return getById(id);
        } catch (Exception e) {
            clear();
            return null;
        }
    }

    public boolean remove(String id) {
        if (!mSharedPreferences.contains(id))
            return false;

        mSharedPreferences.edit().remove(id).apply();
        return true;
    }

    public String[] getIds() {
        return mSharedPreferences.getAll().keySet().toArray(new String[0]);
    }

    public List<T> getAll() {
        List<T> result = new LinkedList<>();
        try {
            for (String id : getIds()) {
                result.add(get(id));
            }
        }catch (Exception e){
            clear();
        }
        return result;
    }

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }
}
