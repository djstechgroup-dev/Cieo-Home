package com.kinetise.data.sourcemanager;

import java.io.InputStream;

public class DataFeedResponse {
    public InputStream dataStream;
    public long timestamp;

    public DataFeedResponse(InputStream stream, long responseTime) {
        dataStream = stream;
        timestamp = responseTime;
    }
}
