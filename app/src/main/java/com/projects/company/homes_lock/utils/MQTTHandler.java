package com.projects.company.homes_lock.utils;

import android.content.Context;
import android.util.Log;

import com.projects.company.homes_lock.models.datamodels.mqtt.FailureModel;
import com.projects.company.homes_lock.models.datamodels.mqtt.MessageModel;

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

    public static void setup(final IMQTTListener mIMQTTListener, Context mContext) {
        client = new MqttAndroidClient(
                mContext,
                "tcp://broker.hivemq.com",
                MqttClient.generateClientId());

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                mIMQTTListener.onConnectionToBrokerLost(new FailureModel(cause.getMessage()));
                Log.w(TAG, "Connection to broker Lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                try {
                    mIMQTTListener.onMessageArrived(new MessageModel(topic, message));
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                mIMQTTListener.onDeliveryMessageComplete(token);
                Log.i(TAG, "Receiving Message Completed.");
            }
        });

        connect(mIMQTTListener);
    }

    public static void connect(final IMQTTListener mIMQTTListener) {
        try {
            MqttConnectOptions mMqttOptions = new MqttConnectOptions();
            mMqttOptions.setAutomaticReconnect(true);
            mMqttOptions.setCleanSession(false);
            mMqttOptions.setKeepAliveInterval(10);
            client.connect(mMqttOptions).setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mIMQTTListener.onConnectionSuccessful(asyncActionToken);
                    Log.i(TAG, "Connection to broker Success.");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    mIMQTTListener.onConnectionFailure(new FailureModel(exception.getMessage()));
                    Log.e(TAG, "Connection to broker Failed");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void subscribe(final IMQTTListener mIMQTTListener) {
        try {
            IMqttToken subToken = client.subscribe("#", 1);

            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mIMQTTListener.onSubscribeSuccessful(asyncActionToken);
                    Log.i(TAG, "Subscribing Done.");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    mIMQTTListener.onSubscribeFailure(new FailureModel(exception.getMessage()));
                    Log.e(TAG, "Subscribe Failed.");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void toggle(final IMQTTListener mIMQTTListener, String mLockSerialNumber) {
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
                    mIMQTTListener.onPublishSuccessful(asyncActionToken);
                    Log.i(TAG, "Publishing Done.");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    mIMQTTListener.onPublishFailure(new FailureModel(exception.getMessage()));
                    Log.e(TAG, "Publishing Failed.");
                }
            });
        else
            Log.e(TAG, "publishToken is null.");
    }
}
