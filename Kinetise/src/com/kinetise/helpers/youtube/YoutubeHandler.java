package com.kinetise.helpers.youtube;

import android.net.Uri;

public class YoutubeHandler {
    private static final String HTTP_REGEX = "^(http(s)?:\\/\\/).+";
    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    private static final String YOUTUBE_REGEX = "^(?:http(?:s)?:\\/\\/)?(?:www\\.)?(?:youtu\\.be\\/|youtube\\.com\\/).*";

    private String url;

    public static boolean isYoutubeVideoId(String videoUrl) {
        return !videoUrl.matches(HTTP_REGEX);
    }

    public YoutubeHandler(String url){
        if(isYoutubeVideoId(url))
            this.url = getUrlToVideoWithId(url);
        else
            this.url = url;
    }

    private String getUrlToVideoWithId(String id){
        return YOUTUBE_URL+id;
    }

    public boolean isYoutubeVideo() {
        return url.matches(YOUTUBE_REGEX);
    }

    public Uri getURI() {
        return Uri.parse(url);
    }

}
