package net.artux.pda.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

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

    public void put(String id, MutableLiveData<T> object){
        object.observeForever(new Observer<T>() {
            @Override
            public void onChanged(T t) {
                mSharedPreferences.edit().putString(id, gson.toJson(t)).apply();
            }
        });
    }

    public MutableLiveData<T> get(String id){
        if(!mSharedPreferences.contains(id))
            return null;
        MutableLiveData<T> data = new MutableLiveData<T>();
        data.setValue(gson.fromJson(mSharedPreferences.getString(id, ""), typeParameterClass));
        return data;
    }

}
