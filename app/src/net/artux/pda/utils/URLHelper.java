package net.artux.pda.utils;

import net.artux.pda.common.PropertyFields;

import java.util.Properties;

public class URLHelper {

    private static String resourceUrl;
    private static String apiUrl;

    public static void init(Properties properties) {
        resourceUrl = properties.getProperty(PropertyFields.RESOURCE_URL);
        apiUrl = properties.getProperty(PropertyFields.API_URL);
    }

    public static String getResourceURL(String part) {
        if (part == null)
            return null;
        String background_url;
        if (!part.contains("http")) {
            background_url = resourceUrl + part;
        } else
            background_url = part;
        return background_url;
    }

    public static String getApiUrl(String part) {
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
