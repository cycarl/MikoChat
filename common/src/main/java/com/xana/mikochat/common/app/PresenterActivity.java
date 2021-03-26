package com.xana.mikochat.common.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.xana.mikochat.common.R;
import com.xana.mikochat.factory.presenter.BaseContract;

public abstract class PresenterActivity<T extends BaseContract.Presenter>
        extends ToolbarActivity
        implements BaseContract.View<T> {

    protected T mPresenter;

    protected abstract T initPresenter();

    @Override
    protected void init() {
        super.init();
        initPresenter();
    }

    @Override
    public void showError(int str) {
        hideDialogLoading();
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerError(str);
        } else {
            Application.showToast(str);
        }
    }

    private ProgressDialog mProgressDialog;

    @Override
    public void showLoading() {
        // TODO 加载框
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerLoading();
        } else {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dialog_Loading_Light);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setCancelable(true);
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                mProgressDialog.setMessage(getText(R.string.prompt_loading));
            }
            mProgressDialog.show();
        }
    }

    protected void hideDialogLoading() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    public void hideLoading() {

        hideDialogLoading();
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerOk();
        }
    }

    public void ok() {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerOk();
        }
    }

    @Override
    public void setPresenter(T presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.destroy();
    }
}

