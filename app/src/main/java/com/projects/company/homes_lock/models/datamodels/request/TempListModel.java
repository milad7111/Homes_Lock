package com.projects.company.homes_lock.models.datamodels.request;

import com.projects.company.homes_lock.base.BaseModel;

public class TempListModel extends BaseModel {

    private String id;

    public TempListModel(String data) {
        this.id = data;
    }
}
