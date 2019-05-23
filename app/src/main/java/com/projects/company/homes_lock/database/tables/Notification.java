package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "notification")
public class Notification {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "messageId")
    private String messageId;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "deliveredPriority")
    private String deliveredPriority;

    @ColumnInfo(name = "originalPriority")
    private String originalPriority;

    @ColumnInfo(name = "sentTime")
    private Long sentTime;

    @ColumnInfo(name = "contentTitle")
    private String contentTitle;

    @ColumnInfo(name = "summarySubText")
    private String summarySubText;

    public Notification(
            @NonNull String messageId,
            String message,
            String deliveredPriority,
            String originalPriority,
            Long sentTime,
            String contentTitle,
            String summarySubText) {
        this.messageId = messageId;
        this.message = message;
        this.deliveredPriority = deliveredPriority;
        this.originalPriority = originalPriority;
        this.sentTime = sentTime;
        this.contentTitle = contentTitle;
        this.summarySubText = summarySubText;
    }

    @NonNull
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(@NonNull String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeliveredPriority() {
        return deliveredPriority;
    }

    public void setDeliveredPriority(String deliveredPriority) {
        this.deliveredPriority = deliveredPriority;
    }

    public String getOriginalPriority() {
        return originalPriority;
    }

    public void setOriginalPriority(String originalPriority) {
        this.originalPriority = originalPriority;
    }

    public Long getSentTime() {
        return sentTime;
    }

    public void setSentTime(Long sentTime) {
        this.sentTime = sentTime;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getSummarySubText() {
        return summarySubText;
    }

    public void setSummarySubText(String summarySubText) {
        this.summarySubText = summarySubText;
    }
}
