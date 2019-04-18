package com.projects.company.homes_lock.utils.mqtt;

import android.content.Context;
import android.util.Log;

import com.projects.company.homes_lock.models.datamodels.mqtt.FailureModel;
import com.projects.company.homes_lock.models.datamodels.mqtt.MessageModel;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTHandler {
    private static MqttAndroidClient client;
    private static String TAG = "MQTTHandler Class";
    private IMQTTListener mIMQTTListener;

    public void setup(IMQTTListener mIMQTTListener, Context mContext, String clientId) {
        this.mIMQTTListener = mIMQTTListener;
        client = new MqttAndroidClient(
                mContext,
                "tcp://185.208.175.56",
                clientId);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                MQTTHandler.this.mIMQTTListener.onConnectionToBrokerLost(new FailureModel(cause.getMessage()));
                Log.w(TAG, "Connection to broker Lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                try {
                    MQTTHandler.this.mIMQTTListener.onMessageArrived(new MessageModel(topic, message));
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                MQTTHandler.this.mIMQTTListener.onDeliveryMessageComplete(token);
                Log.i(TAG, "Receiving Message Completed.");
            }
        });

        connect();
    }

    private void connect() {
        try {
            MqttConnectOptions mMqttOptions = new MqttConnectOptions();
            mMqttOptions.setAutomaticReconnect(true);
            mMqttOptions.setCleanSession(false);
            mMqttOptions.setKeepAliveInterval(10);
            if (client != null)
                client.connect(mMqttOptions).setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        if (MQTTHandler.this.mIMQTTListener != null) {
                            subscribe();
                            MQTTHandler.this.mIMQTTListener.onConnectionSuccessful(asyncActionToken);
                            Log.i(TAG, "Connection to broker Success.");
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        if (MQTTHandler.this.mIMQTTListener != null) {
                            MQTTHandler.this.mIMQTTListener.onConnectionFailure(new FailureModel(exception.getMessage()));
                            Log.e(TAG, "Connection to broker Failed");
                        }
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void subscribe() {
        try {
            if (client != null) {
                IMqttToken subscribeToken = client.subscribe(String.format("rsp/%s/#", client.getClientId()), 0);

                subscribeToken.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        if (MQTTHandler.this.mIMQTTListener != null)
                            MQTTHandler.this.mIMQTTListener.onSubscribeSuccessful(asyncActionToken);
                        Log.i(TAG, "Subscribing Done.");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (MQTTHandler.this.mIMQTTListener != null)
                            MQTTHandler.this.mIMQTTListener.onSubscribeFailure(new FailureModel(exception.getMessage()));
                        Log.e(TAG, "Subscribe Failed.");
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void sendLockCommand(IMQTTListener mIMQTTListener, String mLockSerialNumber, byte[] command) {
        MQTTHandler.this.mIMQTTListener = mIMQTTListener;
        IMqttDeliveryToken publishToken = null;

        try {
            if (client != null)
                publishToken = client.publish(String.format("cmd/%s/%s", client.getClientId(), mLockSerialNumber), new MqttMessage(command));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        if (publishToken != null)
            publishToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    if (MQTTHandler.this.mIMQTTListener != null)
                        MQTTHandler.this.mIMQTTListener.onPublishSuccessful(asyncActionToken);
                    Log.i(TAG, "Publishing Done.");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (MQTTHandler.this.mIMQTTListener != null)
                        MQTTHandler.this.mIMQTTListener.onPublishFailure(new FailureModel(exception.getMessage()));
                    Log.e(TAG, "Publishing Failed.");
                }
            });
        else
            Log.e(TAG, "publishToken is null.");
    }

    public static void disconnect() {
        if (client != null) {
            try {
                client.disconnectForcibly();
                client.close();
                client = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
