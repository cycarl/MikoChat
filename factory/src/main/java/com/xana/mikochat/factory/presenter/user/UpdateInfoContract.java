package com.xana.mikochat.factory.presenter.user;

import com.xana.mikochat.factory.presenter.BaseContract;

/**
 * 更新用户信息的契约
 */
public interface UpdateInfoContract {
    interface Presenter extends BaseContract.Presenter{
        void update(String photoFilePath, String desc, boolean isMan);
    }
    interface View extends BaseContract.View<Presenter>{
        void updateSucceed();
    }
}
