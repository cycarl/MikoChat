package com.xana.mikochat.factory.presenter.account;

import android.text.TextUtils;

import com.xana.mikochat.common.Common;
import com.xana.mikochat.factory.R;
import com.xana.mikochat.factory.data.DataSource;
import com.xana.mikochat.factory.data.helper.AccountHelper;
import com.xana.mikochat.factory.model.api.account.RegisterModel;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.persistence.Account;
import com.xana.mikochat.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.regex.Pattern;

public class RegisterPresenter
    extends BasePresenter<RegisterContract.View>
        implements RegisterContract.Presenter, DataSource.Callback<User>{

    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    @Override
    public void register(String phone, String password, String name) {
        start();
        RegisterContract.View view = getView();
        if(!checkMobie(phone)){
            view.showError(R.string.data_account_register_invalid_parameter_mobile);
        }else if(name.length()<2){
            view.showError(R.string.data_account_register_invalid_parameter_name);
        }else if(password.length()<6){
            view.showError(R.string.data_account_register_invalid_parameter_password);
        }else {
            /* 网络请求 */
            RegisterModel model = new RegisterModel(phone, password, name, Account.getPushId());
            AccountHelper.register(model, this);
        }
    }

    @Override
    public boolean checkMobie(String phone) {
        return !TextUtils.isEmpty(phone)&& Pattern.matches(Common.Constance.REGEX_MOBILE, phone);
    }

    @Override
    public void onDataLoaded(User user) {
        /**
         * 注册成功, 告知界面
         */
        final RegisterContract.View view = getView();
        if(view==null)
            return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.registerSuccess();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int str) {
        final RegisterContract.View view = getView();
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
