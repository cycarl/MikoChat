package com.xana.mikochat.app.helper;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

import android.support.v4.app.Fragment;

/**
 * 完成对Fragment的调度和重用
 * 最优Fragment切换
 */
public class NavHelper<T>{
    private final SparseArray<Tab<T>> tabs = new SparseArray<>();
    private final FragmentManager fragmentManager;
    private final int containerId;
    private final Context context;
    private final OnTabChangedListener<T> listener;
    /* 当前选中的Tab */
    private Tab<T> currentTab;

    public NavHelper(Context context, int containerId, FragmentManager fragmentManager, OnTabChangedListener<T> listener) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        this.context = context;
        this.listener = listener;
    }

    /**
     * 添加Tab
     * @param menuId
     * @param tab
     * @return NavHelper<T>
     */
    public NavHelper<T> add(int menuId, Tab<T> tab){
        tabs.put(menuId, tab);
        return this;
    }

    /**
     * 获取当前点击的Tab
     * @return
     */
    public Tab<T> getCurrentTab(){
        return currentTab;
    }

    /**
     * 执行点击菜单操作
     * @param menuId 菜单项id
     * @return 是否能够处理这个点击
     */
    public boolean performClickMenu(int menuId){
        Tab<T> tab = tabs.get(menuId);
        if(tab!=null){
            doSelect(tab);
            return true;
        }
        return false;
    }

    /**
     * 进行真实的Tab选择操作
     * @param tab
     */
    private void doSelect(Tab<T> tab){
        Tab<T> oldTab = null;
        if(currentTab!=null){
            oldTab = currentTab;
            if(oldTab==tab){
                notifyTabReselect(tab);
                return;
            }
        }
        currentTab = tab;
        doTabChanged(currentTab, oldTab);
    }

    private void doTabChanged(Tab<T> newTab, Tab<T> oldTab){
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if(oldTab!=null){
            if(oldTab.fragment!=null){
                /* 从界面移除 但还在Fragment的缓存空间中 */
                ft.detach(oldTab.fragment);
            }
        }
        if(newTab!=null){
            if(newTab.fragment==null){
                /* 首次新建fragment */
                Fragment fragment = Fragment.instantiate(context, newTab.clx.getName(), null);
                newTab.fragment = fragment;
                ft.add(containerId, fragment, newTab.clx.getName());
            }else {
                /* 重新加载Fragment */
                ft.attach(newTab.fragment);
            }
        }
        ft.commit();
        notifyTabSelect(newTab, oldTab);
    }

    /**
     * 自定义监听器
     * @param newtab
     * @param oldTab
     */
    private void notifyTabSelect(Tab<T> newtab, Tab<T> oldTab){
        if(listener!=null){
            listener.onTabChanged(newtab, oldTab);
        }
    }
    private void notifyTabReselect(Tab<T> tab){
        // TODO 二次点击
    }

    /**
     * 自定义Tab的基础属性
     * @param <T>
     */
    public static class Tab<T>{
        /* Fragment对应的Class信息 */
        public Class<? extends Fragment> clx;
        /* 额外的字段，用户自己设定需要使用 */
        public T extra;
        Fragment fragment;

        public Tab(Class<? extends Fragment> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }
    }

    /**
     * 定义事件处理完成后的回调
     * @param <T>
     */
    public interface OnTabChangedListener<T>{
        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
    }
}
