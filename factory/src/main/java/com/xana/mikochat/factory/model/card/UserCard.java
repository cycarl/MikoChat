package com.xana.mikochat.factory.model.card;

import com.xana.mikochat.factory.model.IBaseModel;
import com.xana.mikochat.factory.model.db.User;

import java.util.Date;

@SuppressWarnings("unused")
public class UserCard implements IBaseModel {
    private String id;
    private String name;
    private String phone;
    private String portrait;
    private String desc;
    private int sex = 0;

    // 用户关注人的数量
    private int follows;
    // 用户粉丝的数量
    private int following;
    // 我与当前User的关系状态，是否已经关注了这个人
    private boolean isFollow;
    // 用户信息最后的更新时间
    private Date modifyAt;



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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    private transient User user;
    public User build(){
        if(user==null){
            user = new User();
            user.setId(id);
            user.setName(name);
            user.setPortrait(portrait);
            user.setPhone(phone);
            user.setDesc(desc);
            user.setSex(sex);
            user.setFollow(isFollow);
            user.setFollows(follows);
            user.setFollowing(following);
            user.setModifyAt(modifyAt);
        }
        return user;
    }
}
