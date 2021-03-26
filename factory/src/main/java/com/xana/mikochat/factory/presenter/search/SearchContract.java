package com.xana.mikochat.factory.presenter.search;


import com.xana.mikochat.factory.model.card.GroupCard;
import com.xana.mikochat.factory.model.card.UserCard;
import com.xana.mikochat.factory.presenter.BaseContract;

import java.util.List;


public interface SearchContract {
    interface Presenter extends BaseContract.Presenter {
        // 搜索内容
        void search(String content);
    }

    // 搜索人的界面
    interface UserView extends BaseContract.View<Presenter> {
        void searchSuccess(List<UserCard> userCards);
    }

    // 搜索群的界面
    interface GroupView extends BaseContract.View<Presenter> {
        void searchSuccess(List<GroupCard> groupCards);
    }

}
