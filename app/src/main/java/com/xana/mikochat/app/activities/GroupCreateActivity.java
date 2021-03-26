package com.xana.mikochat.app.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.xana.mikochat.app.R;
import com.xana.mikochat.app.frags.media.GalleryFragment;
import com.xana.mikochat.common.app.Application;
import com.xana.mikochat.common.app.PresenterActivity;
import com.xana.mikochat.common.app.ToolbarActivity;
import com.xana.mikochat.common.widget.PortraitView;
import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;
import com.xana.mikochat.factory.model.IBaseModel;
import com.xana.mikochat.factory.presenter.group.GroupCreateContract;
import com.xana.mikochat.factory.presenter.group.GroupCreatePresenter;
import com.yalantis.ucrop.UCrop;

import net.qiujuer.genius.ui.widget.EditText;

import java.io.File;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class GroupCreateActivity extends PresenterActivity<GroupCreateContract.Presenter>
    implements GroupCreateContract.View{

    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.edit_name)
    EditText mName;
    @BindView(R.id.edit_desc)
    EditText mDesc;
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;
    String mPicUrl;

    private Adapter mAdapter;


    public static void show(Context context){
        context.startActivity(new Intent(context, GroupCreateActivity.class));
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_create;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter = new Adapter());
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        hideSoftKeyboard();
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
                        .start(GroupCreateActivity.this);
            }
            /**
             * 在Fragment中使用getChildFragmentManager，防止与Activity的冲突
             */
        }).show(getSupportFragmentManager(), GalleryFragment.class.getName());
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
    private void loadPortrait(Uri uri) {
        Glide.with(this)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);

        mPicUrl = uri.getPath();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_create, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_create){
            onCreateClick();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onCreateClick() {
        hideSoftKeyboard();
        String name = mName.getText().toString().trim();
        String desc = mDesc.getText().toString().trim();
        mPresenter.create(name, desc, mPicUrl);
    }

    /**
     * 隐藏软键盘
      */
    private void hideSoftKeyboard(){
        View view = getCurrentFocus();
        if(view==null) return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected GroupCreateContract.Presenter initPresenter() {
        return new GroupCreatePresenter(this);
    }

    @Override
    public void onCreateSuccess() {
        hideLoading();
        Application.showToast(R.string.label_group_create_succeed);
        finish();
    }

    @Override
    public RecyclerAdapter<GroupCreateContract.ViewModel> getRecyclerAdapter() {
        return mAdapter;
    }


    @Override
    public void onAdapterDataChanged() {
        hideLoading();
    }



    private class Adapter extends RecyclerAdapter<GroupCreateContract.ViewModel>{

        @Override
        protected int getItemViewType(int position, GroupCreateContract.ViewModel viewModel) {
            return R.layout.cell_group_create_contact;
        }

        @Override
        protected ViewHolder<GroupCreateContract.ViewModel> onCreateViewHolder(View root, int viewType) {
            return new GroupCreateActivity.ViewHolder(root);
        }
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCreateContract.ViewModel>{
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.cb_select)
        CheckBox mSelect;

        ViewHolder(View itemView) {
            super(itemView);
        }

        @OnCheckedChanged(R.id.cb_select)
        void onCheckedChanged(boolean flag){
            mPresenter.changeSelect(mData, flag);
        }


        @Override
        protected void onBind(GroupCreateContract.ViewModel viewModel) {
            IBaseModel user = viewModel.user;
            mPortrait.setup(Glide.with(GroupCreateActivity.this),user);
            mName.setText(user.getName());
            mSelect.setChecked(viewModel.isSelected);
        }
    }
}