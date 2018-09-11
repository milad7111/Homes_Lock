package com.projects.company.homes_lock.models.datamodels.mqtt;

import com.google.gson.annotations.SerializedName;
import com.projects.company.homes_lock.models.datamodels.response.BaseModel;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MessageModel extends BaseModel {

    @SerializedName("topic")
    private String mTopic;
    @SerializedName("MqttMessage")
    private MqttMessage mMqttMessage;

    public MessageModel(String mTopic, MqttMessage mMqttMessage) {
        this.mTopic = mTopic;
        this.mMqttMessage = mMqttMessage;
    }
}
