package kara.spotifyassistant.config;

import org.springframework.web.util.UriBuilder;

public class Util {

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
