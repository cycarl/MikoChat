package com.xana.mikochat.app.activities;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.xana.mikochat.app.R;
import com.xana.mikochat.app.frags.account.AccountTrigger;
import com.xana.mikochat.app.frags.account.LoginFragment;
import com.xana.mikochat.app.frags.account.RegisterFragment;
import com.xana.mikochat.common.app.Activity;
import com.xana.mikochat.common.app.Fragment;

import net.qiujuer.genius.ui.compat.UiCompat;

import butterknife.BindView;

/**
 * 账户Activity 包含登录和注册Fragment
 */
public class AccountActivity extends Activity implements AccountTrigger{

    private Fragment curFragment;
    private Fragment loginFragment;
    private Fragment registerFragment;

    @BindView(R.id.im_bg)
    ImageView mBg;

    public static void show(Context context){
        context.startActivity(new Intent(context, AccountActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        curFragment = loginFragment = new LoginFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, loginFragment)
                .commit();
/*        Glide.with(this)
                .load(R.drawable.bg_launch_gamma)
                .centerCrop()
                .into(new ViewTarget<ImageView, GlideDrawable>(mBg) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        Drawable drawable = resource.getCurrent();
                        *//* 使用适配类进行包装 *//*
                        drawable = DrawableCompat.wrap(drawable);
                        drawable.setColorFilter(UiCompat.getColor(getResources(), R.color.colorAccent),
                                PorterDuff.Mode.SCREEN); *//* 着色效果和颜色 和蒙版模式 *//*
                        this.view.setImageDrawable(drawable);
                    }
                });*/

    }

    @Override
    public void triggerView() {
        Fragment fragment = loginFragment;
        if(curFragment==loginFragment){
            if(registerFragment==null)
                registerFragment = new RegisterFragment();
            fragment = registerFragment;
        }
        curFragment = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lay_container, curFragment)
                .commit();
    }
}