package kara.spotifyassistant.config;

import kara.spotifyassistant.apiwrappers.SpotifyApiWrapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static int generateRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min) + min));
    }

    public static List<JSONObject> convertJsonArrayToList(JSONArray jsonArray) {
        List<JSONObject> result = new ArrayList<>();
        for (int count = 0; count < jsonArray.length(); count++) {
            result.add(jsonArray.getJSONObject(count));
        }
        return result;
    }

    public static class UrlBuilder {
        private String url;

        public UrlBuilder(String baseUrl) {
            this.url = baseUrl;
        }

        public UrlBuilder withParams(String param, String value) {
            if (!this.url.contains("?")) {
                this.url = url + "?";
            }
            this.url = url + param + "=" + value + "&";
            return this;
        }

        public String build() {
            return url.substring(0, url.length() - 1);
        }
    }
}
