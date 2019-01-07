package com.projects.company.homes_lock.models.datamodels.response;

import org.json.JSONException;
import org.json.JSONObject;

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

            try {
                JSONObject body = new JSONObject(text);

                if (body.has("code"))
                    this.mCode = body.getInt("code");

                if (body.has("message"))
                    this.mMessage = body.getString("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
