package com.projects.company.homes_lock.models.datamodels.notification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AndroidImmediatePush {

    @SerializedName("___jsonclass")
    @Expose
    private String jsonclass;
    @SerializedName("attachmentUrl")
    @Expose
    private Object attachmentUrl;
    @SerializedName("cancelOnTap")
    @Expose
    private Boolean cancelOnTap;
    @SerializedName("cancelAfter")
    @Expose
    private Integer cancelAfter;
    @SerializedName("sound")
    @Expose
    private Object sound;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("vibrate")
    @Expose
    private Object vibrate;
    @SerializedName("showBadge")
    @Expose
    private Object showBadge;
    @SerializedName("badgeNumber")
    @Expose
    private Integer badgeNumber;
    @SerializedName("contentTitle")
    @Expose
    private String contentTitle;
    @SerializedName("priority")
    @Expose
    private Object priority;
    @SerializedName("lightsOffMs")
    @Expose
    private Object lightsOffMs;
    @SerializedName("badge")
    @Expose
    private Integer badge;
    @SerializedName("lightsColor")
    @Expose
    private Object lightsColor;
    @SerializedName("largeIcon")
    @Expose
    private Object largeIcon;
    @SerializedName("lightsOnMs")
    @Expose
    private Object lightsOnMs;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("colorCode")
    @Expose
    private Integer colorCode;
    @SerializedName("summarySubText")
    @Expose
    private String summarySubText;
    @SerializedName("actions")
    @Expose
    private List<Action> actions = null;
    @SerializedName("contentAvailable")
    @Expose
    private Integer contentAvailable;
    @SerializedName("customHeaders")
    @Expose
    private Object customHeaders;

    public String getJsonClass() {
        return jsonclass;
    }

    public void setJsonClass(String jsonclass) {
        this.jsonclass = jsonclass;
    }

    public Object getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(Object attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public Boolean getCancelOnTap() {
        return cancelOnTap;
    }

    public void setCancelOnTap(Boolean cancelOnTap) {
        this.cancelOnTap = cancelOnTap;
    }

    public Integer getCancelAfter() {
        return cancelAfter;
    }

    public void setCancelAfter(Integer cancelAfter) {
        this.cancelAfter = cancelAfter;
    }

    public Object getSound() {
        return sound;
    }

    public void setSound(Object sound) {
        this.sound = sound;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Object getVibrate() {
        return vibrate;
    }

    public void setVibrate(Object vibrate) {
        this.vibrate = vibrate;
    }

    public Object getShowBadge() {
        return showBadge;
    }

    public void setShowBadge(Object showBadge) {
        this.showBadge = showBadge;
    }

    public Integer getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(Integer badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public Object getPriority() {
        return priority;
    }

    public void setPriority(Object priority) {
        this.priority = priority;
    }

    public Object getLightsOffMs() {
        return lightsOffMs;
    }

    public void setLightsOffMs(Object lightsOffMs) {
        this.lightsOffMs = lightsOffMs;
    }

    public Integer getBadge() {
        return badge;
    }

    public void setBadge(Integer badge) {
        this.badge = badge;
    }

    public Object getLightsColor() {
        return lightsColor;
    }

    public void setLightsColor(Object lightsColor) {
        this.lightsColor = lightsColor;
    }

    public Object getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(Object largeIcon) {
        this.largeIcon = largeIcon;
    }

    public Object getLightsOnMs() {
        return lightsOnMs;
    }

    public void setLightsOnMs(Object lightsOnMs) {
        this.lightsOnMs = lightsOnMs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getColorCode() {
        return colorCode;
    }

    public void setColorCode(Integer colorCode) {
        this.colorCode = colorCode;
    }

    public String getSummarySubText() {
        return summarySubText;
    }

    public void setSummarySubText(String summarySubText) {
        this.summarySubText = summarySubText;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Integer getContentAvailable() {
        return contentAvailable;
    }

    public void setContentAvailable(Integer contentAvailable) {
        this.contentAvailable = contentAvailable;
    }

    public Object getCustomHeaders() {
        return customHeaders;
    }

    public void setCustomHeaders(Object customHeaders) {
        this.customHeaders = customHeaders;
    }
}
