package com.xana.mikochat.factory.model.db.view;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;
import com.xana.mikochat.factory.model.IBaseModel;
import com.xana.mikochat.factory.model.db.AppDatabase;

/**
 * 群成员基本信息model
 */
@QueryModel(database = AppDatabase.class)
public class GroupMemberSimple implements IBaseModel {
    @Column
    public String userId;  // User.id = GroupMember.userId
    @Column
    public String name; // User.name
    @Column
    public String alias; // GroupMember.alias
    @Column
    public String portrait; // User.portrait

    public String getId() {
        return userId;
    }

    public void setId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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
