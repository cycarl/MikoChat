package com.xana.mikochat.factory.model.db.view;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;
import com.xana.mikochat.common.app.Application;
import com.xana.mikochat.factory.model.IBaseModel;
import com.xana.mikochat.factory.model.db.AppDatabase;

/**
 * 用户基础信息的model
 */
@QueryModel(database = AppDatabase.class)
public class UserSimple implements IBaseModel{
    @Column
    public String id;
    @Column
    public String name;
    @Column
    public String portrait;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPortrait() {
        return portrait;
    }

    @Override
    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
