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
import com.xana.mikochat.app.frags.user.UpdateInfoFragment;
import com.xana.mikochat.common.app.Activity;
import com.xana.mikochat.factory.model.db.User;

import net.qiujuer.genius.ui.compat.UiCompat;

import butterknife.BindView;

public class UserActivity extends Activity {

    @BindView(R.id.im_bg)
    ImageView mBg;

    private UpdateInfoFragment curFragment;

    public static void show(Context context){
        context.startActivity(new Intent(context, UserActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        curFragment = new UpdateInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, curFragment)
                .commit();
    }

    /**
     * Activity接收图片剪切后的回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        curFragment.onActivityResult(requestCode, resultCode, data);
    }
}