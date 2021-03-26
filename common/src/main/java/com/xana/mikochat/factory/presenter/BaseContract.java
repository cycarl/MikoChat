package com.xana.mikochat.factory.presenter;

import android.support.annotation.StringRes;

import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;

/**
 * MVP模式中公共基本契约
 */
public interface BaseContract {
    interface View<T extends Presenter>{
        void showError(@StringRes int str);
        void showLoading();
        void setPresenter(T presenter);
    }

    interface Presenter{
        void start();
        void destroy();
    }

    interface RecyclerView<T extends Presenter, D> extends View<T>{
        // void success(List<User> userList); 界面只能刷新某个数据集
        // 拿到适配器，自主进行刷新
        RecyclerAdapter<D> getRecyclerAdapter();
        // 数据改变时触发
        void onAdapterDataChanged();
    }
}
