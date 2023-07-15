package net.artux.pda.utils;

import net.artux.pda.common.PropertyFields;

import java.util.Properties;

import timber.log.Timber;

public class URLHelper {

    private static String resourceUrl;
    private static String apiUrl;

    public static void init(Properties properties) {
        resourceUrl = properties.getProperty(PropertyFields.RESOURCE_URL);
        apiUrl = properties.getProperty(PropertyFields.API_URL);
        Timber.i("PDANETWORK API: %s", apiUrl);
        Timber.i("Resource URL: %s", resourceUrl);
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
        String result;
        if (!part.startsWith("http")) {
            while (part.startsWith("/"))
                part = part.replaceFirst("/", "");
            result = apiUrl + part;
        } else
            result = part;
        return result;
    }

}
