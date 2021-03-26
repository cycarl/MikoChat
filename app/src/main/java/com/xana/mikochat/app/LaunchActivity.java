package com.xana.mikochat.app;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.xana.mikochat.app.activities.AccountActivity;
import com.xana.mikochat.app.activities.MainActivity;
import com.xana.mikochat.app.frags.assist.PermissionsFragment;
import com.xana.mikochat.app.helper.PermissionCallback;
import com.xana.mikochat.common.Common;
import com.xana.mikochat.common.app.Activity;
import com.xana.mikochat.factory.persistence.Account;

@SuppressWarnings("unused")
public class LaunchActivity extends Activity implements PermissionCallback {

    // Drawable


    @Override
    protected int getLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        // 跟布局
        LinearLayout root = findViewById(R.id.activity_launch);

        int index = (int) (Math.random()*40);

        @SuppressLint("DefaultLocale")
        String imgUrl = String.format(Common.IMAGE_URL, index);

        Glide.with(this)
                .load(imgUrl)
                .placeholder(R.drawable.bg_launch_beta)
                .centerCrop()
                .into(new ViewTarget<LinearLayout, GlideDrawable>(root) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });

/*        // 获取颜色
        int color = UiCompat.getColor(getResources(), R.color.colorPrimary);
        // 创建一个Drawable
        ColorDrawable drawable = new ColorDrawable(color);
        // 设置给背景
        */
    }

    @Override
    protected void initData() {
        super.initData();

        // 动画进入到50%等待PushId获取到
     /*   startAnim(0.5f, new Runnable() {
            @Override
            public void run() {
                // 检查等待状态
                waitPushReceiverId();
            }
        });*/
        waitPushReceiverId();

    }

    /**
     * 等待个推框架对我们的PushId设置好值
     */
    private void waitPushReceiverId() {
        if (Account.isLogin()) {
            // 已经登录情况下，判断是否绑定
            // 如果没有绑定则等待广播接收器进行绑定
            if (Account.isBind()) {
                skip();
                return;
            }
        } else {
            // 没有登录
            // 如果拿到了PushId, 没有登录是不能绑定PushId的
            if (!TextUtils.isEmpty(Account.getPushId())) {
                // 跳转
                skip();
                return;
            }
        }

        // 循环等待
        getWindow().getDecorView()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitPushReceiverId();
                    }
                }, 500);
    }


    /**
     * 在跳转之前需要把剩下的50%进行完成
     */
    private void skip() {
/*        startAnim(1f, new Runnable() {
            @Override
            public void run() {
                reallySkip();
            }
        });*/

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                reallySkip();
            }
        }).start();
    }

    /**
     * 真实的跳转
     */
    private void reallySkip() {
        // 权限检测，跳转
        if (PermissionsFragment.haveAll(this, getSupportFragmentManager())) {
            reallySkips();
        }
    }

    public void reallySkips() {
        // 检查跳转到主页还是登录
        if (Account.isLogin()) {
            MainActivity.show(this);
        } else {
            AccountActivity.show(this);
        }
        finish();
    }

    @Override
    public void onSuccess() {
        reallySkips();
    }

    /*
    */
/**
     * 给背景设置一个动画
     * @param endProgress 动画的结束进度
     * @param endCallback 动画结束时触发
     *//*

    private void startAnim(float endProgress, final Runnable endCallback) {
        // 获取一个最终的颜色
        int finalColor = Resource.Color.WHITE; // UiCompat.getColor(getResources(), R.color.white);
        // 运算当前进度的颜色
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int) evaluator.evaluate(endProgress, mBgDrawable.getColor(), finalColor);
        // 构建一个属性动画
        ValueAnimator valueAnimator = ObjectAnimator.ofObject(this, property, evaluator, endColor);
        valueAnimator.setDuration(1500); // 时间
        valueAnimator.setIntValues(mBgDrawable.getColor(), endColor); // 开始结束值
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 结束时触发
                endCallback.run();
            }
        });
        valueAnimator.start();
    }

    private final Property<LaunchActivity, Object> property = new Property<LaunchActivity, Object>(Object.class, "color") {
        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }

        @Override
        public Object get(LaunchActivity object) {
            return object.mBgDrawable.getColor();
        }
    };
*/

}

