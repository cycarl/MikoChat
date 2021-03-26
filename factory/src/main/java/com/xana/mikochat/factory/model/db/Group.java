package com.xana.mikochat.factory.model.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.xana.mikochat.factory.data.helper.GroupHelper;
import com.xana.mikochat.factory.model.IBaseModel;
import com.xana.mikochat.factory.model.db.view.GroupMemberSimple;
import com.xana.mikochat.factory.utils.DiffUiDataCallback;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * 群信息Model
 */
@Table(database = AppDatabase.class)
public class Group extends BaseDbModel<Group> implements Serializable, IBaseModel {
    @PrimaryKey
    private String id; // 群Id
    @Column
    private String name;// 群名称
    @Column
    private String desc;// 群描述
    @Column
    private String picture;// 群图片
    @Column
    private int notifyLevel;// 我在群中的消息通知级别-对象是我当前登录的账户
    @Column
    private Date joinAt;// 我的加入时间
    @Column
    private Date modifyAt;// 信息修改时间

    @ForeignKey(tableClass = User.class, stubbedRelationship = true)
    private User owner;// 创建者外键


    public Object holder; // 预留字段，用于界面显示


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPortrait() {
        return picture;
    }

    @Override
    public void setPortrait(String portrait) {
        this.picture = portrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getNotifyLevel() {
        return notifyLevel;
    }

    public void setNotifyLevel(int notifyLevel) {
        this.notifyLevel = notifyLevel;
    }

    public Date getJoinAt() {
        return joinAt;
    }

    public void setJoinAt(Date joinAt) {
        this.joinAt = joinAt;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        return notifyLevel == group.notifyLevel
                && Objects.equals(id, group.id)
                && Objects.equals(name, group.name)
                && Objects.equals(desc, group.desc)
                && Objects.equals(picture, group.picture)
                && Objects.equals(joinAt, group.joinAt)
                && Objects.equals(modifyAt, group.modifyAt)
                && Objects.equals(owner, group.owner)
                && Objects.equals(holder, group.holder);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean isSame(Group oldT) {
        // 进行对比判断时，判断是否为一个群的信息，判断id即可
        return Objects.equals(id, oldT.id);
    }

    @Override
    public boolean isUiContentSame(Group oldT) {
        // 如果界面显示信息有更改，只有可能是更改了：
        // 群名称，描述，图片，以及界面显示对应的Holder
        return Objects.equals(this.name, oldT.name)
                && Objects.equals(this.desc, oldT.desc)
                && Objects.equals(this.picture, oldT.picture)
                && Objects.equals(this.holder, oldT.holder);
    }

    private long memberCount = 0;
    public long getGroupMemberCount(){
        if(memberCount==0){
            memberCount = GroupHelper.getMemberCount(id);
        }
        return memberCount;
    }

    private List<GroupMemberSimple> members;
    public List<GroupMemberSimple> getSimpleMembers() {
        if(members==null||members.size()==0){
            members = GroupHelper.getSimpleMembers(id, 4);
        }
        return members;
    }
}
