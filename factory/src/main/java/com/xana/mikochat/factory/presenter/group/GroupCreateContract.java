package com.xana.mikochat.factory.presenter.group;

import com.xana.mikochat.factory.model.IBaseModel;
import com.xana.mikochat.factory.presenter.BaseContract;

public interface GroupCreateContract{
    interface Presenter extends BaseContract.Presenter{
        void create(String name, String dec, String picUrl);
        void changeSelect(ViewModel model, boolean isSelected);

    }
    interface View extends BaseContract.RecyclerView<Presenter, ViewModel>{
        void onCreateSuccess();
    }

    class ViewModel{
        public IBaseModel user;
        public boolean isSelected;
    }
}
