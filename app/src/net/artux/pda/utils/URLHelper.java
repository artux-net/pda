package net.artux.pda.utils;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import net.artux.pda.common.PropertyFields;

public class URLHelper {

    private static final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
    private static String resourceUrl;
    private static String apiUrl;
    {
        resourceUrl = remoteConfig.getString(PropertyFields.RESOURCE_URL);
        apiUrl = remoteConfig.getString(PropertyFields.API_URL);
    }

    public static String getResourceURL(String part){
        if (part == null)
            return null;
        String background_url;
        if (!part.contains("http")) {
            if (part.startsWith("/"))
                background_url = resourceUrl + part;
            else
                background_url = resourceUrl + "/" + part;
        } else
            background_url = part;
        return background_url;
    }

    public static String getApiUrl(String part){
        if (part == null)
            return null;
        String background_url;
        if (!part.contains("http")) {
            if (part.startsWith("/"))
                background_url = apiUrl + part;
            else
                background_url = apiUrl + "/" + part;
        } else
            background_url = part;
        return background_url;
    }

}
