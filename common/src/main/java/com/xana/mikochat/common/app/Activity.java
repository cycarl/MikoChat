package com.xana.mikochat.common.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.xana.mikochat.common.widget.convention.PlaceHolderView;

import java.util.List;

import butterknife.ButterKnife;

public abstract class Activity extends AppCompatActivity {

    protected PlaceHolderView mPlaceHolderView;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindows();
        if(initArgs(getIntent().getExtras())) {
            setContentView(getLayoutId());
            init();
            initWidget();
            initData();
        }else {
            finish();
        }
    }

    /**
     * 初始化控件调用之前
     */
    protected void init(){

    }

    /**
     * 初始化窗口
     */
    protected void initWindows(){

    }

    /**
     * 初始化相关参数
     * @param bundle
     * @return true参数正确
     */
    protected boolean initArgs(Bundle bundle){
        return true;
    }

    /**
     * 得到当前界面资源文件id
     * @return 资源文件id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget(){
        ButterKnife.bind(this);
    }

    protected void initData(){

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        /* 获取当前Activity下的所有Fragment*/
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if(fragments!=null && fragments.size()>0){
            for (Fragment fragment : fragments) {
                if(fragment instanceof com.xana.mikochat.common.app.Fragment && ((com.xana.mikochat.common.app.Fragment) fragment).onBackPressed())
                    return;
            }
        }
        super.onBackPressed();
        finish();
    }

    /**
     * 设置占位布局
     * @param placeHolderView
     */
    public void setPlaceHolderView(PlaceHolderView placeHolderView){
        mPlaceHolderView = placeHolderView;
    }
}
