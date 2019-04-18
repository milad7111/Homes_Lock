package com.projects.company.homes_lock.models.datamodels.response;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class ResponseBodyModel extends ResponseBody {

    private int mCode = -1;
    private String mMessage = null;
    private String mRegistrationId = null;

    public ResponseBodyModel(String data, boolean isResisteration) {
        if (isResisteration)
            this.mRegistrationId = data;
        else {
            String text = data.substring(data.indexOf("text=") + 5, data.indexOf("]") - 3) + "}";

            this.mCode = Integer.valueOf(text.substring(text.indexOf("code:") + 9, text.indexOf(",\"message\":")));
            this.mMessage = (text.substring(text.indexOf("\"message\":") + 11)).substring(0, (text.substring(text.indexOf("\"message\":") + 11)).indexOf("\",\""));
        }
    }

    public String getMessage() {
        return mMessage;
    }

    public String getRegistrationId() {
        return mRegistrationId;
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
}
