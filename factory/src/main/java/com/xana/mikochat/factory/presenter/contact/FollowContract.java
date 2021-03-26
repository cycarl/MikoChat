package com.xana.mikochat.factory.presenter.contact;

import com.xana.mikochat.factory.model.card.UserCard;
import com.xana.mikochat.factory.presenter.BaseContract;

public interface FollowContract {
    interface Presenter extends BaseContract.Presenter{
        // 关注
        void follow(String id);
    }
    interface View extends BaseContract.View<Presenter>{
        // 关注成功
        void followSuccess(UserCard userCard);
    }
}
