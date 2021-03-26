package com.xana.mikochat.app.frags.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.xana.mikochat.app.R;
import com.xana.mikochat.app.frags.media.GalleryFragment;
import com.xana.mikochat.common.app.Application;
import com.xana.mikochat.common.app.Fragment;
import com.xana.mikochat.common.widget.PortraitView;
import com.xana.mikochat.factory.Factory;
import com.xana.mikochat.factory.net.UploadHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 用户更新信息的界面
 */
public class UpdateInfoFragment extends Fragment {
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

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
            final Throwable cropError = UCrop.getError(data);
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

        String localPath = uri.getPath();
        Log.e("TAG", "localPath:"+localPath);

        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
               String url =  UploadHelper.uploadPortrait(localPath);
                Log.e("TAG", "remotePath:"+url);
            }
        });
    }


}