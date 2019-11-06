package com.projects.company.homes_lock.utils.mqtt;

import android.content.Context;

import com.projects.company.homes_lock.models.datamodels.mqtt.FailureModel;
import com.projects.company.homes_lock.models.datamodels.mqtt.MessageModel;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import timber.log.Timber;

import static com.projects.company.homes_lock.base.BaseApplication.activeUserObjectId;

public class MQTTHandler {
    private static MqttAndroidClient client;
    private static String mDeviceSerialNumber;
    private static IMQTTListener _mIMQTTListener;
    private static IMQTTListener _mPublisherIMQTTListener;

    public static void setup(IMQTTListener mIMQTTListener, Context mContext, String deviceSerialNumber) {
        if (_mIMQTTListener == null) {
            _mIMQTTListener = mIMQTTListener;
            mDeviceSerialNumber = deviceSerialNumber;
            client = new MqttAndroidClient(
                    mContext,
                    "tcp://185.208.175.56",
                    String.format("Android:%s:%s", deviceSerialNumber, activeUserObjectId));

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    if (_mPublisherIMQTTListener != null)
                        _mPublisherIMQTTListener.onConnectionToBrokerLost(new FailureModel(cause.getMessage()));

                    if (_mIMQTTListener != null)
                        _mIMQTTListener.onConnectionToBrokerLost(new FailureModel(cause.getMessage()));

                    Timber.w("Connection to broker Lost.");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    try {
                        if (_mPublisherIMQTTListener != null)
                            _mPublisherIMQTTListener.onMessageArrived(new MessageModel(topic, message));

                        Timber.i("Message Arrived.");
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    if (_mPublisherIMQTTListener != null)
                        _mPublisherIMQTTListener.onDeliveryMessageComplete(token);

                    Timber.i("Receiving Message Completed.");
                }
            });

            connect();
        }
    }

    private static void connect() {
        try {
            MqttConnectOptions mMqttOptions = new MqttConnectOptions();
            mMqttOptions.setAutomaticReconnect(true);
            mMqttOptions.setCleanSession(false);
            mMqttOptions.setKeepAliveInterval(10);
            if (client != null)
                client.connect(mMqttOptions).setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        if (_mIMQTTListener != null) {
                            subscribe();
                            _mIMQTTListener.onConnectionSuccessful(asyncActionToken);
                            Timber.i("Connection to broker Success.");
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        if (_mIMQTTListener != null) {
                            _mIMQTTListener.onConnectionFailure(new FailureModel(exception.getMessage()));
                            Timber.e("Connection to broker Failed");
                        }
                    }
                });
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void subscribe() {
        try {
            if (client != null) {
                IMqttToken subscribeToken = client.subscribe(String.format("rsp/%s/#", mDeviceSerialNumber), 0);

                subscribeToken.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        if (_mIMQTTListener != null)
                            _mIMQTTListener.onSubscribeSuccessful(asyncActionToken);
                        Timber.i("Subscribing Done.");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (_mIMQTTListener != null)
                            _mIMQTTListener.onSubscribeFailure(new FailureModel(exception.getMessage()));
                        Timber.e("Subscribe Failed.");
                    }
                });
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void sendLockCommand(IMQTTListener mIMQTTListener, String mLockSerialNumber, byte[] command) {
        _mPublisherIMQTTListener = mIMQTTListener;
        IMqttDeliveryToken publishToken = null;

        try {
            if (client != null)
                publishToken = client.publish(String.format("cmd/%s/%s", mDeviceSerialNumber, mLockSerialNumber), new MqttMessage(command));
        } catch (Exception e) {
            Timber.e(e);
        }

        if (publishToken != null)
            publishToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    if (_mPublisherIMQTTListener != null)
                        _mPublisherIMQTTListener.onPublishSuccessful(asyncActionToken);
                    Timber.i("Publishing Done.");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (_mPublisherIMQTTListener != null)
                        _mPublisherIMQTTListener.onPublishFailure(new FailureModel(exception.getMessage()));
                    Timber.e("Publishing Failed.");
                }
            });
        else
            Timber.e("publishToken is null.");
    }

    public static void mqttDisconnect() {
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
