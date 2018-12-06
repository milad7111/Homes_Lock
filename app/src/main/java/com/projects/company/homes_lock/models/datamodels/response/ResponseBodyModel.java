package com.projects.company.homes_lock.models.datamodels.response;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseBodyModel {

    private int mCode = -1;
    private String mMessage = null;

    public ResponseBodyModel(String source) {
        String text = source.substring(source.indexOf("text=") + 5, source.indexOf("]") - 3) + "}";

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

    public int getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }
}
