package com.projects.company.homes_lock.models.datamodels.response;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class ResponseBodyFailureModel extends ResponseBody {

    private String mMessage;

    public ResponseBodyFailureModel(String mMessage) {
        this.mMessage = mMessage;
    }

    @Override
    public MediaType contentType() {
        return null;
    }

    @Override
    public long contentLength() {
        return 0;
    }

    @Override
    public BufferedSource source() {
        return null;
    }

    public String getFailureMessage() {
        return mMessage;
    }
}
