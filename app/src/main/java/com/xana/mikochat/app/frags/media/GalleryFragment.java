package com.xana.mikochat.app.frags.media;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.xana.mikochat.app.R;
import com.xana.mikochat.app.helper.PermissionCallback;
import com.xana.mikochat.common.tools.UiTool;
import com.xana.mikochat.common.widget.GalleryView;


public class GalleryFragment
        extends BottomSheetDialogFragment
        implements GalleryView.SelectedChangeListener{

    private GalleryView mGallery;
    private OnSelectedListener mListener;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_gallery, container, false);
        mGallery = root.findViewById(R.id.galleryView);
        return root;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TransStatusBarBottomSheetDialog(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        mGallery.setup(getLoaderManager(), this);
    }

    @Override
    public void onSelectedCountChanged(int count) {
        /* 如果选择了一张图片，立即隐藏 */
        if(count>0){
            dismiss();
            if(mListener!=null){
                String[] paths = mGallery.getSelectedPath();
                mListener.onSelectedImages(paths[0]);
                mListener = null;
            }
        }
    }

    /**
     * 设置事件监听
     * @param listener
     * @return
     */
    public GalleryFragment setmListener(OnSelectedListener listener) {
        this.mListener = listener;
        return this;
    }

    /**
     * 选中图片的监听器
     */
    public interface OnSelectedListener{
        void onSelectedImages(String path);
    }
    public static class TransStatusBarBottomSheetDialog extends BottomSheetDialog{

        public TransStatusBarBottomSheetDialog(@NonNull Context context) {
            super(context);
        }
        public TransStatusBarBottomSheetDialog(@NonNull Context context, int theme) {
            super(context, theme);
        }
        protected TransStatusBarBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Window window = getWindow();
            if(window==null) return;
            /* 得到屏幕高度 */
            int screenHeight = UiTool.getScreenHeight(getOwnerActivity());
            /* 得到状态栏高度 */
            int statusHeight = UiTool.getStatusBarHeight(getOwnerActivity());

            /* 计算Dialog的高度 */
            int dialogHeight = screenHeight-statusHeight;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    dialogHeight<=0? ViewGroup.LayoutParams.MATCH_PARENT: dialogHeight);
        }
    }
}