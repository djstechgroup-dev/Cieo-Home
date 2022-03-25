package com.kinetise.data.application.alterapimanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is container for values that are returned by alter Api Response, which are accessible by setters and getters<br>
 * User: Mateusz
 * Date: 13.03.13
 * Time: 12:57
 */
public class AAResponse {
    public String sessionId;
    public String title;
    public AAMessage message;
    public List<String> expiredUrls;
    public Map<String, String> applicationVariables;

    public AAResponse(){
        expiredUrls = new ArrayList<>();
        applicationVariables = new HashMap<>();
    }

}
