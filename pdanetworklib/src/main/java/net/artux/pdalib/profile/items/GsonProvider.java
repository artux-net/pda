package net.artux.pdalib.profile.items;

import com.google.gson.Gson;

public class GsonProvider {

    static Gson gson = new Gson();

    public static Gson getInstance() {
        return gson;
    }
}
