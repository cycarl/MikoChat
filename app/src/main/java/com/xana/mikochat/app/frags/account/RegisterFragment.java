package com.xana.mikochat.app.frags.account;

import android.content.Context;
import android.widget.EditText;

import com.xana.mikochat.app.R;
import com.xana.mikochat.app.activities.MainActivity;
import com.xana.mikochat.common.app.PresenterFragment;
import com.xana.mikochat.factory.presenter.account.RegisterContract;
import com.xana.mikochat.factory.presenter.account.RegisterPresenter;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterFragment extends PresenterFragment<RegisterContract.Presenter>
        implements RegisterContract.View{

    @BindView(R.id.edit_phone)
    EditText mPhone;
    @BindView(R.id.edit_password)
    EditText mPassWord;
    @BindView(R.id.edit_name)
    EditText mName;
    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;

    private AccountTrigger mTrigger;

    public RegisterFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mTrigger = (AccountTrigger)context;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register;
    }

    @Override
    protected RegisterContract.Presenter initPresenter() {
        return new RegisterPresenter(this);
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick(){
        String phone = mPhone.getText().toString();
        String name = mName.getText().toString();
        String password = mPassWord.getText().toString();
        /* 调用Presenter逻辑处理 */
        mPresenter.register(phone, password, name);
    }

    @OnClick(R.id.txt_go_login)
    void onGoLoginClick(){
        mTrigger.triggerView();
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        /* 显示错误时 */
        mLoading.stop();
        mPhone.setEnabled(true);
        mName.setEnabled(true);
        mPassWord.setEnabled(true);
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        mLoading.start();
        mPhone.setEnabled(false);
        mName.setEnabled(false);
        mPassWord.setEnabled(false);
        mSubmit.setEnabled(false);
    }

    @Override
    public void registerSuccess() {
        /**
         * 注册成功 跳转到MainActivity
         */
        MainActivity.show(getContext());
        getActivity().finish();
    }
}