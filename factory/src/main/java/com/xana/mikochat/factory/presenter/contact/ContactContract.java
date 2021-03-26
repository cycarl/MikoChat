package com.xana.mikochat.factory.presenter.contact;

import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.presenter.BaseContract;

import java.util.List;

public interface ContactContract {
    interface Presenter extends BaseContract.Presenter{

    }
    interface View extends BaseContract.RecyclerView<Presenter, User>{

    }
}
