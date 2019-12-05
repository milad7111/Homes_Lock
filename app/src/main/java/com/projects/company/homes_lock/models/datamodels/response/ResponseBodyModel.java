package com.projects.company.homes_lock.models.datamodels.response;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import timber.log.Timber;

public class ResponseBodyModel extends ResponseBody {

    private int mCode = -1;
    private String mMessage = null;
    private String mRegistrationId = null;
    private String mSerialNumber = null;

    public ResponseBodyModel(String registrationId, boolean isRegistration, String serialNumber) {
        if (isRegistration) {
            this.mRegistrationId = registrationId;
            this.mSerialNumber = serialNumber;
        } else {
            String text = registrationId.substring(registrationId.indexOf("text=") + 5, registrationId.indexOf("]") - 3) + "}";

            try {
                this.mCode = Integer.valueOf(text.substring(text.indexOf("code:") + 9, text.indexOf(",\"message\":")));
                this.mMessage = (text.substring(text.indexOf("\"message\":") + 11)).substring(0, (text.substring(text.indexOf("\"message\":") + 11)).indexOf("\",\""));
            } catch (Exception e) {
                Timber.e(e.getCause().toString());
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

    public String getSerialNumber() {
        return mSerialNumber;
    }
}
