package com.projects.company.homes_lock.database.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import static com.projects.company.homes_lock.utils.helper.DataHelper.dateDifference;

@Entity(tableName = "notification")
public class Notification {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "messageId")
    private String messageId;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "ticker")
    @Nullable
    private String ticker = "";

    @ColumnInfo(name = "deliveredPriority")
    private String deliveredPriority;

    @ColumnInfo(name = "originalPriority")
    private String originalPriority;

    @ColumnInfo(name = "sentTime")
    private Long sentTime;

    @ColumnInfo(name = "contentTitle")
    private String contentTitle;

    @ColumnInfo(name = "summarySubText")
    @Nullable
    private String summarySubText = "";

    @Ignore
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

    public Notification(
            @NonNull String messageId,
            @NonNull String ticker,
            String message,
            String deliveredPriority,
            String originalPriority,
            String contentTitle,
            Long sentTime) {
        this.messageId = messageId;
        this.ticker = ticker;
        this.message = message;
        this.deliveredPriority = deliveredPriority;
        this.originalPriority = originalPriority;
        this.sentTime = sentTime;
        this.contentTitle = contentTitle;
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

    public String getCustomSentTime() {
        return dateDifference(new Date(sentTime));
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

    @Nullable
    public String getTicker() {
        return ticker;
    }

    public void setTicker(@Nullable String ticker) {
        this.ticker = ticker;
    }
}
