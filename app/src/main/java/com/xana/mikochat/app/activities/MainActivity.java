package com.xana.mikochat.app.activities;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.xana.mikochat.app.R;
import com.xana.mikochat.app.frags.main.ActiveFragment;
import com.xana.mikochat.app.frags.main.ContactFragment;
import com.xana.mikochat.app.frags.main.GroupFragment;
import com.xana.mikochat.app.helper.NavHelper;
import com.xana.mikochat.common.app.Activity;
import com.xana.mikochat.common.widget.PortraitView;
import com.xana.mikochat.factory.persistence.Account;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;


public class MainActivity extends Activity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        NavHelper.OnTabChangedListener<Integer> {
    @BindView(R.id.appbar)
    View mLayAppbar;

    @BindView(R.id.im_portrait)
    PortraitView mPortraitView;

//    @BindView(R.id.txt_title)
//    TextView mTitle;

    @BindView(R.id.lay_container)
    FrameLayout mContainer;

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @BindView(R.id.btn_action)
    FloatActionButton mAction;

    private NavHelper<Integer> mNavHelper;

    public static void show(Context context){
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if(Account.isComplete()){
            return super.initArgs(bundle);
        }
        UserActivity.show(this);
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mNavHelper = new NavHelper<>(this, R.id.lay_container,
                getSupportFragmentManager(),this);
        mNavHelper.add(R.id.action_home, new NavHelper.Tab<>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.title_contact))
                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group));

        mNavigation.setOnNavigationItemSelectedListener(this);

        Glide.with(this)
                .load(R.drawable.img_default)
                .centerCrop()
                .into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });
    }

    @Override
    protected void initData() {
        super.initData();
        Menu menu = mNavigation.getMenu();
        menu.performIdentifierAction(R.id.action_home, 0);

        mPortraitView.setup(Glide.with(this), Account.getUser());
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        PersonalActivity.show(this, Account.getUserId());
    }

    @OnClick(R.id.im_search)
    void onSearchMenuClick(){
        // 搜索按钮点击时，判断当前界面是联系人还是群 ？
        // 打开群搜索界面
        boolean f = Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group);
        int type = f? SearchActivity.TYPE_GROUP: SearchActivity.TYPE_USER;

        SearchActivity.show(this, type);
    }

    @OnClick(R.id.btn_action)
    void onActionClick(){
        // 浮动按钮点击时，判断当前界面是联系人还是群 ？
        if(Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group)){
            // 打开群创建界面
            GroupCreateActivity.show(this);
            return;
        }

        // 其他则进入联系人搜索界面
        SearchActivity.show(this, SearchActivity.TYPE_USER);
    }

    /**
     * 底部导航点击触发
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /* 转接事件流到工具类中 */
        return mNavHelper.performClickMenu(item.getItemId());
    }

    /**
     * 处理后回调的方法
     * @param newTab
     * @param oldTab
     */
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
      //  mTitle.setText(newTab.extra);

        float transY = 0;
        float rotation = 0;
        if(Objects.equals(newTab.extra,R.string.title_home)){
            transY = Ui.dipToPx(getResources(), 76);
        }else if(Objects.equals(newTab.extra, R.string.title_group)){
            mAction.setImageResource(R.drawable.ic_group_add);
            rotation = -360;
        }else{
            mAction.setImageResource(R.drawable.ic_contact_add);
            rotation = 360;
        }

        /* 设置浮动按钮动画 旋转，位移，弹性插值器，时间*/
        mAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AccelerateInterpolator(1))
                .setDuration(250)
                .start();
    }
}