package com.xana.mikochat.factory.presenter.group;

import com.xana.mikochat.factory.model.db.Group;
import com.xana.mikochat.factory.presenter.BaseContract;

public interface GroupContract {
    interface Presenter extends BaseContract.Presenter{

    }
    interface View extends BaseContract.RecyclerView<Presenter, Group>{

    }
}
