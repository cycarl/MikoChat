package com.xana.mikochat.factory.presenter.contact;

import com.xana.mikochat.factory.model.IBaseModel;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.presenter.BaseContract;

public interface PersonalContract {
    interface Presenter extends BaseContract.Presenter{
        User getUserPersonal();
    }

    interface View extends BaseContract.View<Presenter>{

        String getUserId();

        void loadSuccess(User user);
        // 是否允许聊天
        void allowSayHello(boolean isAllow);
        void setFollowStatus(boolean isFollow);
    }
}
