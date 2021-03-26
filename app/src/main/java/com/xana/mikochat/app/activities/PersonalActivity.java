package com.xana.mikochat.app.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xana.mikochat.app.R;
import com.xana.mikochat.common.app.PresenterActivity;
import com.xana.mikochat.common.app.ToolbarActivity;
import com.xana.mikochat.common.widget.PortraitView;
import com.xana.mikochat.common.widget.convention.PlaceHolderView;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.presenter.BaseContract;
import com.xana.mikochat.factory.presenter.contact.PersonalContract;
import com.xana.mikochat.factory.presenter.contact.PersonalPresenter;


import net.qiujuer.genius.res.Resource;

import butterknife.BindView;
import butterknife.OnClick;

public class PersonalActivity extends PresenterActivity<PersonalContract.Presenter>
    implements PersonalContract.View {
    private static final String BOUND_KEY_ID = "BOUND_KEY_ID";
    private String userId;

    @BindView(R.id.im_header)
    ImageView mHeader;
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;
    @BindView(R.id.txt_name)
    TextView mName;
    @BindView(R.id.txt_desc)
    TextView mDesc;
    @BindView(R.id.txt_follows)
    TextView mFollows;
    @BindView(R.id.txt_following)
    TextView mFollowing;
    @BindView(R.id.btn_say_hello)
    Button mSayHello;

    private MenuItem mFollowItem;
    private boolean mIsFollow=false;

    public static void show(Context context, String userId) {
        if(context==null||TextUtils.isEmpty(userId)) return;

        Intent intent = new Intent(context, PersonalActivity.class);
        intent.putExtra(BOUND_KEY_ID, userId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        userId = bundle.getString(BOUND_KEY_ID);
        return !TextUtils.isEmpty(userId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
    }


    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.personal, menu);
        mFollowItem = menu.findItem(R.id.action_follow);
        changeFllowItemStatus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_follow) {
            // 进行关注操作
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_say_hello)
    void onSayHelloClick() {
        // 开始聊天
        User user = mPresenter.getUserPersonal();
        if(user==null) return;
        MessageActivity.show(this, user);

    }

    /**
     * 更改关注菜单状态
     */
    private void changeFllowItemStatus(){
        if(mFollowItem ==null)
            return;
        Drawable drawable = mIsFollow?
                getResources()
                .getDrawable(R.drawable.ic_favorite):
                getResources()
                .getDrawable(R.drawable.ic_favorite_border);

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Resource.Color.WHITE);
        mFollowItem.setIcon(drawable);
    }

    @Override
    protected PersonalContract.Presenter initPresenter() {
        return new PersonalPresenter(this);
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void loadSuccess(User user) {
        if(user==null) return;
        mPortrait.setup(Glide.with(this), user);
        mName.setText(user.getName());
        mDesc.setText(user.getDesc());
        mFollows.setText(String.format(getString(R.string.label_follows), user.getFollows()));
        mFollowing.setText(String.format(getString(R.string.label_following), user.getFollowing()));
        // 隐藏loading
        hideLoading();
    }

    @Override
    public void allowSayHello(boolean isAllow) {
        mSayHello.setVisibility(isAllow? View.VISIBLE: View.GONE);
    }

    @Override
    public void setFollowStatus(boolean isFollow) {
        mIsFollow = isFollow;
        changeFllowItemStatus();
    }
}
