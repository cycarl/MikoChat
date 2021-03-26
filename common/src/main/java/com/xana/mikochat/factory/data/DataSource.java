package com.xana.mikochat.factory.data;

import android.support.annotation.StringRes;

public interface DataSource {

    /**
     * 回调接口
     * @param <T>
     */
    interface Callback<T> extends SucceedCallback<T>, FailedCallback{

    }

    interface SucceedCallback<T>{
        void onDataLoaded(T data);
    }
    interface FailedCallback{
        void onDataNotAvailable(@StringRes int str);
    }

    /**
     * 销毁操作
     */
    void dispose();
}
