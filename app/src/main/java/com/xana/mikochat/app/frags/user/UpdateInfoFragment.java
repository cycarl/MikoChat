package com.xana.mikochat.app.frags.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xana.mikochat.app.R;
import com.xana.mikochat.app.activities.MainActivity;
import com.xana.mikochat.app.frags.media.GalleryFragment;
import com.xana.mikochat.common.app.Application;
import com.xana.mikochat.common.app.Fragment;
import com.xana.mikochat.common.app.PresenterFragment;
import com.xana.mikochat.common.widget.PortraitView;
import com.xana.mikochat.factory.Factory;
import com.xana.mikochat.factory.net.UploadHelper;
import com.xana.mikochat.factory.presenter.user.UpdateInfoContract;
import com.xana.mikochat.factory.presenter.user.UpdateInfoPresenter;
import com.yalantis.ucrop.UCrop;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 用户更新信息的界面
 */
public class UpdateInfoFragment extends PresenterFragment<UpdateInfoContract.Presenter>
        implements UpdateInfoContract.View{

    @BindView(R.id.im_sex)
    ImageView mSex;

    @BindView(R.id.edit_desc)
    EditText mDesc;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;

    private String mPortraitPath;
    private boolean isMan = true;

    public UpdateInfoFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_update_info;
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        new GalleryFragment().setmListener(new GalleryFragment.OnSelectedListener() {
            @Override
            public void onSelectedImages(String path) {
                /*　图片压缩选项　*/
                UCrop.Options options = new UCrop.Options();
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                options.setCompressionQuality(96);
                /* 头像缓存地址 */
                File dPath = Application.getPortraitTmpFile();
                UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                .withAspectRatio(1, 1) /* 1:1 图片比例 */
                .withMaxResultSize(520, 520)
                .start(getActivity());
            }
            /**
             * 在Fragment中使用getChildFragmentManager，防止与Activity的冲突
             */
        }).show(getChildFragmentManager(), GalleryFragment.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if(resultUri!=null)
                loadPortrait(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {

            Application.showToast(R.string.data_rsp_error_unknown);
//            final Throwable cropError = UCrop.getError(data);
        }
    }
    /**
     * 加载和上传新头像
     * @param uri
     */
    private void loadPortrait(Uri uri){
        Glide.with(this)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);

        mPortraitPath = uri.getPath();
        /* OOS图片上传 */
/*
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
               String url =  UploadHelper.uploadPortrait(mPortraitPath);
                Log.e("TAG", "remotePath:"+url);
            }
        });
        */
    }

    @OnClick(R.id.im_sex)
    void onSexClick(){
        isMan = !isMan;
        Drawable drawable = getResources().getDrawable(isMan? R.drawable.ic_sex_man: R.drawable.ic_sex_woman);
        mSex.setImageDrawable(drawable);
        // 设置背景的层级
        mSex.getBackground().setLevel(isMan? 0: 1);
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick(){
        String desc = mDesc.getText().toString();
        /* 调用Presenter逻辑处理 */
        mPresenter.update(mPortraitPath, desc, isMan);
    }

    @Override
    protected UpdateInfoContract.Presenter initPresenter() {
        return new UpdateInfoPresenter(this);
    }

    @Override
    public void updateSucceed() {
        MainActivity.show(getContext());
        getActivity().finish();
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        /* 显示错误时 */
        mLoading.stop();
        mPortrait.setEnabled(true);
        mSex.setEnabled(true);
        mDesc.setEnabled(true);
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        mLoading.start();
        mPortrait.setEnabled(false);
        mSex.setEnabled(false);
        mDesc.setEnabled(false);
        mSubmit.setEnabled(false);
    }

}