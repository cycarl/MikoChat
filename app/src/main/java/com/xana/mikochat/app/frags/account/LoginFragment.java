package com.xana.mikochat.app.frags.account;

import android.content.Context;
import android.widget.EditText;

import com.xana.mikochat.app.R;
import com.xana.mikochat.app.activities.MainActivity;
import com.xana.mikochat.common.app.Fragment;
import com.xana.mikochat.common.app.PresenterFragment;
import com.xana.mikochat.factory.presenter.account.LoginContract;
import com.xana.mikochat.factory.presenter.account.LoginPresenter;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginFragment extends PresenterFragment<LoginContract.Presenter>
    implements LoginContract.View{
    private AccountTrigger mTrigger;

    @BindView(R.id.edit_phone)
    EditText mPhone;
    @BindView(R.id.edit_password)
    EditText mPassWord;
    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;

    public LoginFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mTrigger = (AccountTrigger)context;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick(){
        String phone = mPhone.getText().toString();
        String password = mPassWord.getText().toString();
        /* 调用Presenter逻辑处理 */
        mPresenter.login(phone, password);
    }

    @OnClick(R.id.txt_go_register)
    void onGoRegisterClick(){
        mTrigger.triggerView();
    }

    @Override
    protected LoginContract.Presenter initPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        /* 显示错误时 */
        mLoading.stop();
        mPhone.setEnabled(true);
        mPassWord.setEnabled(true);
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        mLoading.start();
        mPhone.setEnabled(false);
        mPassWord.setEnabled(false);
        mSubmit.setEnabled(false);
    }

    @Override
    public void loginSuccess() {
        MainActivity.show(getContext());
        getActivity().finish();
    }
}