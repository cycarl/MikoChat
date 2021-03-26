package com.xana.mikochat.factory.presenter.account;

import android.text.TextUtils;

import com.xana.mikochat.factory.R;
import com.xana.mikochat.factory.data.DataSource;
import com.xana.mikochat.factory.data.helper.AccountHelper;
import com.xana.mikochat.factory.model.api.account.LoginModel;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.persistence.Account;
import com.xana.mikochat.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

public class LoginPresenter extends BasePresenter<LoginContract.View>
    implements LoginContract.Presenter, DataSource.Callback<User>{

    public LoginPresenter(LoginContract.View view) {
        super(view);
    }

    @Override
    public void login(String phone, String password) {
        start();
        final LoginContract.View  view = getView();


        if(TextUtils.isEmpty(phone)||TextUtils.isEmpty(password)){
            view.showError(R.string.data_account_login_invalid_parameter);
        }else {
            LoginModel model = new LoginModel(phone, password, Account.getPushId());
            AccountHelper.login(model, this);
        }
    }

    @Override
    public void onDataLoaded(User user) {
        /**
         * 登录成功, 告知界面
         */
        final LoginContract.View view = getView();
        if(view==null)
            return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.loginSuccess();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int str) {
        final LoginContract.View view = getView();
        if(view==null)
            return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(str);
            }
        });
    }
}
