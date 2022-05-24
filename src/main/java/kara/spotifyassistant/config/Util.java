package kara.spotifyassistant.config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.util.UriBuilder;

import java.util.ArrayList;
import java.util.List;

public class Util {


    public static int generateRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min) + min));
    }

    public static Object[] randomPickFromArray(Object[] arr, int numberToBePicked) {
        List<Object> picked = new ArrayList<>();
        for (int count = 0; count < numberToBePicked; count++) {
            picked.add(arr[generateRandomNumber(0, arr.length - 1)]);
        }
        return picked.toArray();
    }

    public static JSONObject[] randomPickFromArray(JSONArray arr, int numberToBePicked) {
        List<JSONObject> picked = new ArrayList<>();
        for (int count = 0; count < numberToBePicked; count++) {
            picked.add(arr.getJSONObject(generateRandomNumber(0, arr.length() - 1)));
        }
        return (JSONObject[]) picked.toArray();
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
