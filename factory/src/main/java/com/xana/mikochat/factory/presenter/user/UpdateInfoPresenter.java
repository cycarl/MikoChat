package com.xana.mikochat.factory.presenter.user;

import android.text.TextUtils;

import com.xana.mikochat.factory.Factory;
import com.xana.mikochat.factory.R;
import com.xana.mikochat.factory.data.DataSource;
import com.xana.mikochat.factory.data.helper.UserHelper;
import com.xana.mikochat.factory.model.api.user.UserUpdateModel;
import com.xana.mikochat.factory.model.card.UserCard;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.net.UploadHelper;
import com.xana.mikochat.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;


public class UpdateInfoPresenter extends BasePresenter<UpdateInfoContract.View>
        implements UpdateInfoContract.Presenter, DataSource.Callback<UserCard> {
    public UpdateInfoPresenter(UpdateInfoContract.View view) {
        super(view);
    }

    @Override
    public void update(final String photoFilePath, final String desc, final boolean isMan) {
        start();
        final UpdateInfoContract.View view = getView();
        if(TextUtils.isEmpty(photoFilePath)||TextUtils.isEmpty(desc)){
            view.showError(R.string.data_account_update_invalid_parameter);
        }else{
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                    String url = UploadHelper.uploadPortrait(photoFilePath);
                    if(TextUtils.isEmpty(url)){
                        view.showError(R.string.data_upload_error);
                    }else{
                        UserUpdateModel model = new UserUpdateModel("", url,
                                desc, isMan? User.SEX_MAN: User.SEX_WOMAN);

                        // 网络请求
                        UserHelper.update(model, UpdateInfoPresenter.this);
                    }
                }
            });
        }
    }

    @Override
    public void onDataLoaded(UserCard user) {
        final UpdateInfoContract.View view = getView();
        if (view==null) return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.updateSucceed();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int str) {
        final UpdateInfoContract.View view = getView();
        if (view==null) return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(str);
            }
        });
    }
}
