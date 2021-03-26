package com.xana.mikochat.common.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.bumptech.glide.RequestManager;
import com.xana.mikochat.common.R;
import com.xana.mikochat.factory.model.IBaseModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class PortraitView extends CircleImageView {
    public PortraitView(Context context) {
        super(context);
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setup(RequestManager requestManager, IBaseModel user){
        if(user==null) return;
        setup(requestManager, user.getPortrait());
    }

    public void setup(RequestManager requestManager, String url) {
        setup(requestManager, R.drawable.default_portrait, url);
    }

    public void setup(RequestManager requestManager, int resId, String url) {
        if(TextUtils.isEmpty(url)) url = "https://mikochat.oss-ap-northeast-1.aliyuncs.com/portrait/202103/ekidora_0.jfif";
        requestManager
                .load(url)
               // .placeholder(resId)
                .centerCrop()
                .dontAnimate() // CircleImageView使用渐变动画会导致异常
                .into(this);
    }
}
