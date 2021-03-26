package com.xana.mikochat.common.app;

import android.content.Context;

import com.xana.mikochat.factory.presenter.BaseContract;

public abstract class PresenterFragment<T extends BaseContract.Presenter>
        extends Fragment implements BaseContract.View<T> {

    protected T mPresenter;

    protected abstract T initPresenter();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initPresenter();
    }

    @Override
    public void showError(int str) {
        if(mPlaceHolderView!=null) {
            mPlaceHolderView.triggerError(str);
        } else {
            Application.showToast(str);
        }
    }

    @Override
    public void showLoading() {
        // TODO 加载框
        if(mPlaceHolderView!=null){
            mPlaceHolderView.triggerLoading();
        }
    }

    @Override
    public void setPresenter(T presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null)
            mPresenter.destroy();
    }

}
