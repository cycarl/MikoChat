package com.xana.mikochat.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.xana.mikochat.app.R;
import com.xana.mikochat.common.app.Activity;
import com.xana.mikochat.common.widget.AngleImageView;

public class ImageActivity extends Activity {

    public static final String KEY_IMAGE_URL = "KEY_IMAGE_URL";

    private String mPicUrl;
    private AngleImageView mImage;

    public static void show(Context context, String picUrl){
        if(context==null || TextUtils.isEmpty(picUrl)) return;
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra(KEY_IMAGE_URL, picUrl);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mPicUrl = bundle.getString(KEY_IMAGE_URL);
        return !TextUtils.isEmpty(mPicUrl);
    }

    @Override
    protected void initWidget() {
        mImage = findViewById(R.id.im_image);
        super.initWidget();
        Glide.with(this)
                .load(mPicUrl)
                .into(mImage);

    }
}