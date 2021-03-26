package com.xana.mikochat.factory.presenter.contact;

import com.xana.mikochat.factory.Factory;
import com.xana.mikochat.factory.data.helper.UserHelper;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.persistence.Account;
import com.xana.mikochat.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

public class PersonalPresenter extends BasePresenter<PersonalContract.View>
    implements PersonalContract.Presenter{

    private User user;


    public PersonalPresenter(PersonalContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        // 个人界面用户数据
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                PersonalContract.View view = getView();
                if(view!=null) {
                    String id = view.getUserId();
                    User user = UserHelper.searchFirstOfNet(id);
                    onLoaded(user);
                }
            }
        });
    }

    private void onLoaded(User user) {
        this.user = user;
        boolean isSelf = user.getId().equalsIgnoreCase(Account.getUserId());
        boolean isFollow =  isSelf || user.isFollow();
        boolean allowSayHello = !isSelf && isFollow;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        PersonalContract.View view = getView();
                        if(view==null) return;
                        view.loadSuccess(user);
                        view.setFollowStatus(isFollow);
                        view.allowSayHello(allowSayHello);
                    }
                });
            }
        });
    }

    @Override
    public User getUserPersonal() {
        return user;
    }
}
