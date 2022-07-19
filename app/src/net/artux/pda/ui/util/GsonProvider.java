package net.artux.pda.ui.util;

import com.google.gson.Gson;

public class GsonProvider {

    static Gson gson = new Gson();

    public static Gson getInstance() {
        return gson;
    }
}
