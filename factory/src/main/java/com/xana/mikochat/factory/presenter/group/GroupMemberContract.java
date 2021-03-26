package com.xana.mikochat.factory.presenter.group;

import com.xana.mikochat.factory.model.db.view.GroupMemberSimple;
import com.xana.mikochat.factory.presenter.BaseContract;

public interface GroupMemberContract {
    interface Presenter extends BaseContract.Presenter{
        void refresh();
    }

    interface View extends BaseContract.RecyclerView<Presenter, GroupMemberSimple>{
        String getGroupId();
    }
}

