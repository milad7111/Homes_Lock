package com.projects.company.homes_lock.utils;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTHandler {
    private static MqttAndroidClient client;
    private static String TAG = "MQTTHandler Class";

    public static void setup(final MqttCallback mMqttCallbackListener, Context mContext) {
        MqttAndroidClient client = new MqttAndroidClient(
                mContext,
                "tcp://5.196.101.48:1883",
                MqttClient.generateClientId());

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                mMqttCallbackListener.connectionLost(cause);
                Log.w(TAG, "Connection to broker Lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                try {
                    mMqttCallbackListener.messageArrived(topic, message);
                    Log.i(TAG, "New Message Arrived.");
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                mMqttCallbackListener.deliveryComplete(token);
                Log.i(TAG, "Receiving Message Completed.");
            }
        });
    }

    public static void connect(IMqttActionListener mIMqttActionListener) {
        try {
            MqttConnectOptions mMqttOptions = new MqttConnectOptions();
            IMqttToken mToken = client.connect(mMqttOptions);
            mToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.i(TAG, "Connection to broker Success.");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.e(TAG, "Connection to broker Failed");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void subscribe(final IMqttActionListener mIMqttActionListener) {
        try {
            IMqttToken subToken = client.subscribe("response/toggle/123456789", 1);

            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mIMqttActionListener.onSuccess(asyncActionToken);
                    Log.i(TAG, "Subscribing Done.");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    mIMqttActionListener.onFailure(asyncActionToken, exception);
                    Log.e(TAG, "Subscribe Failed.");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void toggle(final IMqttActionListener mIMqttActionListener, String mLockSerialNumber) {
        String payload = "toggle";
        IMqttDeliveryToken publishToken = null;

        try {
            publishToken = client.publish("toggle/" + mLockSerialNumber, new MqttMessage(payload.getBytes("UTF-8")));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        if (publishToken != null)
            publishToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mIMqttActionListener.onSuccess(asyncActionToken);
                    Log.i(TAG, "Publishing Done.");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    mIMqttActionListener.onFailure(asyncActionToken, exception);
                    Log.e(TAG, "Publishing Failed.");
                }
            });
        else
            Log.e(TAG, "publishToken is null.");
    }
}
