package com.xana.mikochat.factory.presenter.account;
import com.xana.mikochat.factory.presenter.BaseContract;

public interface RegisterContract {
    interface View extends BaseContract.View<Presenter> {
        void registerSuccess();
    }

    interface Presenter extends BaseContract.Presenter{
        void register(String phone, String password, String name);
        boolean checkMobie(String phone);
    }
}
