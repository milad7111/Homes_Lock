package com.projects.company.homes_lock.models.datamodels.notification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Action {

    @SerializedName("___jsonclass")
    @Expose
    private String jsonclass;
    @SerializedName("inlineReply")
    @Expose
    private Object inlineReply;
    @SerializedName("textInputPlaceholder")
    @Expose
    private Object textInputPlaceholder;
    @SerializedName("options")
    @Expose
    private Integer options;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("inputButtonTitle")
    @Expose
    private Object inputButtonTitle;
    @SerializedName("title")
    @Expose
    private String title;

    public String getJsonclass() {
        return jsonclass;
    }

    public void setJsonclass(String jsonclass) {
        this.jsonclass = jsonclass;
    }

    public Object getInlineReply() {
        return inlineReply;
    }

    public void setInlineReply(Object inlineReply) {
        this.inlineReply = inlineReply;
    }

    public Object getTextInputPlaceholder() {
        return textInputPlaceholder;
    }

    public void setTextInputPlaceholder(Object textInputPlaceholder) {
        this.textInputPlaceholder = textInputPlaceholder;
    }

    public Integer getOptions() {
        return options;
    }

    public void setOptions(Integer options) {
        this.options = options;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getInputButtonTitle() {
        return inputButtonTitle;
    }

    public void setInputButtonTitle(Object inputButtonTitle) {
        this.inputButtonTitle = inputButtonTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

