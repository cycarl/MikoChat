package com.xana.mikochat.factory.presenter.contact;

import com.xana.mikochat.factory.R;
import com.xana.mikochat.factory.data.DataSource;
import com.xana.mikochat.factory.data.helper.UserHelper;
import com.xana.mikochat.factory.model.card.UserCard;
import com.xana.mikochat.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

public class FollowPresenter extends BasePresenter<FollowContract.View>
        implements FollowContract.Presenter, DataSource.Callback<UserCard> {
    public FollowPresenter(FollowContract.View view) {
        super(view);
    }

    @Override
    public void follow(String id) {
        start();
        UserHelper.follow(id, this);
    }

    @Override
    public void onDataLoaded(final UserCard data) {
        final FollowContract.View view = getView();
        if(view!=null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.followSuccess(data);
                }
            });
        }

    }

    @Override
    public void onDataNotAvailable(final int str) {
        final FollowContract.View view = getView();
        if(view!=null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.showError(str);
                }
            });
        }
    }
}
