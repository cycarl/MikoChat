package com.xana.mikochat.factory.data.helper;

import android.text.TextUtils;

import com.xana.mikochat.factory.Factory;
import com.xana.mikochat.factory.R;
import com.xana.mikochat.factory.data.DataSource;
import com.xana.mikochat.factory.model.api.RspModel;
import com.xana.mikochat.factory.model.api.account.AccountRspModel;
import com.xana.mikochat.factory.model.api.account.LoginModel;
import com.xana.mikochat.factory.model.api.account.RegisterModel;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.net.Network;
import com.xana.mikochat.factory.net.RemoteService;
import com.xana.mikochat.factory.persistence.Account;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountHelper {

    public static void register(RegisterModel model, final DataSource.Callback<User> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<AccountRspModel>> call = service.accountRegister(model);
        /* 异步请求 */
        call.enqueue(new AccountRspCallback(callback));

    }

    public static void login(LoginModel model, final DataSource.Callback<User> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<AccountRspModel>> call = service.accountLogin(model);
        /* 异步请求 */
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 对设备Id进行绑定
     */
    public static void bindPush(final DataSource.Callback<User> callback) {
        String pushId = Account.getPushId();
        if (TextUtils.isEmpty(pushId)) return;

        RemoteService service = Network.remote();
        Call<RspModel<AccountRspModel>> call = service.accountBind(pushId);
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 请求回调接口封装
     */
    private static class AccountRspCallback implements Callback<RspModel<AccountRspModel>> {
        final DataSource.Callback<User> callback;

        private AccountRspCallback(DataSource.Callback<User> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(Call<RspModel<AccountRspModel>> call, Response<RspModel<AccountRspModel>> response) {
            /* 响应体 */
            RspModel<AccountRspModel> rspModel = response.body();
            if (rspModel.success()) {
                AccountRspModel accountRspModel = rspModel.getResult();

                /* 获取用户信息进行数据库写入 */
                final User user = accountRspModel.getUser();

                DbHelper.save(User.class, user);

                        // user.save();

/*                        FlowManager.getModelAdapter(User.class)
                                .save(user);
                        // 声明事务
                        DatabaseDefinition databaseDefinition = FlowManager.getDatabase(AppDatabase.class);
                        databaseDefinition.beginTransactionAsync(new ITransaction() {
                            @Override
                            public void execute(DatabaseWrapper databaseWrapper) {
                                FlowManager.getModelAdapter(User.class)
                                        .save(user);
                            }
                        }).build().execute();
                        */
                /* XML持久化 */
                Account.login(accountRspModel);

                /* 是否绑定设备 */
                if (accountRspModel.isBind()) {
                    Account.setBind(true);
                    if (callback != null)
                        callback.onDataLoaded(user);
                } else {
                    bindPush(callback);
                }
            } else {
                /* 对失败CODE进行分析, 显示对应提示 */
                Factory.decodeRspCode(rspModel, callback);
            }
        }

        @Override
        public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
            if (callback != null)
                callback.onDataNotAvailable(R.string.data_network_error);
        }
    }
}
