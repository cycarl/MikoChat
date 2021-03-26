package com.xana.mikochat.app.frags.panel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xana.mikochat.app.R;
import com.xana.mikochat.common.app.Fragment;
import com.xana.mikochat.common.tools.UiTool;
import com.xana.mikochat.common.widget.GalleryView;
import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;
import com.xana.mikochat.face.Face;

import net.qiujuer.genius.ui.Ui;

import java.io.File;
import java.util.List;

/**
 * 底部面板实现
 */
public class PanelFragment extends Fragment {
    private View mFacePanel, mGelleryPanel, mRecordPanel;
    private PanelCallback mCallback;


    public void setCallback(PanelCallback callback){
        mCallback = callback;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_panel;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initFace(root);
        initRecord(root);
        initGallery(root);
    }

    private void initFace(View root) {
        View facePanel = mFacePanel = root.findViewById(R.id.lay_panel_face);
        ImageView backspace = facePanel.findViewById(R.id.im_backspace);

        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除emoji
                if(mCallback==null) return;
                EditText editText = mCallback.getEditText();

                // 模拟软键盘点击
                KeyEvent event = new KeyEvent(0,
                        0, 0, 0, KeyEvent.KEYCODE_DEL,
                        0, 0,0,KeyEvent.KEYCODE_ENDCALL);
                mCallback.getEditText().dispatchKeyEvent(event);

            }
        });

        TabLayout tabLayout = facePanel.findViewById(R.id.tab);
        ViewPager viewPager = facePanel.findViewById(R.id.pager);
        tabLayout.setupWithViewPager(viewPager);

        // 每一个emoji 48dp
        final int minFaceSize = (int) Ui.dipToPx(getResources(), 48);
        final int totalScreen = UiTool.getScreenWidth(getActivity());
        final int spanCount = totalScreen / minFaceSize;
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Face.all(getContext()).size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view==object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                // 添加项
                LayoutInflater inflater = LayoutInflater.from(getContext());
                RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.lay_face_content, container, false);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
                // 设置Adapter
                List<Face.Bean> beans = Face.all(getContext()).get(position).faces;
                FaceAdapter faceAdapter = new FaceAdapter(beans, new RecyclerAdapter.AdapterListener<Face.Bean>() {
                    @Override
                    public void onItemClick(RecyclerAdapter.ViewHolder holder, Face.Bean bean) {
                        // 将emoji添加到EditText
                        if(mCallback==null) return;
                        EditText editText = mCallback.getEditText();
                        Face.inputFace(getContext(), editText.getText(), bean, (int) (editText.getTextSize()+Ui.dipToPx(getResources(), 2)));

                    }

                    @Override
                    public void onItemLongClick(RecyclerAdapter.ViewHolder holder, Face.Bean bean) {
                        // TODO 长按
                    }
                });
                recyclerView.setAdapter(faceAdapter);

                container.addView(recyclerView);
                return recyclerView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                // 移除项
                container.removeView((View) object);
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                // emoji的描述
                return Face.all(getContext()).get(position).name;
            }
        });

    }

    private void initRecord(View root) {
    }

    private void initGallery(View root) {
        View galleryPanel = mGelleryPanel = root.findViewById(R.id.lay_panel_gallery);

        GalleryView galleryView = galleryPanel.findViewById(R.id.view_gallery);
        TextView selectedSize = galleryPanel.findViewById(R.id.txt_gallery_select_count);

        galleryView.setup(getLoaderManager(), new GalleryView.SelectedChangeListener() {
            @Override
            public void onSelectedCountChanged(int count) {
                selectedSize.setText(String.format(getString(R.string.label_gallery_selected_size), count));
            }
        });
        mGelleryPanel.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGallerySendClick(galleryView, galleryView.getSelectedPath());
                selectedSize.setText(String.format(getString(R.string.label_gallery_selected_size), 0));
            }
        });
    }

    private void onGallerySendClick(GalleryView galleryView, String[] paths) {
        galleryView.clear();
        if(mCallback==null) return;
        mCallback.sendGallery(paths);

    }


    public void showFace(){
        mFacePanel.setVisibility(View.VISIBLE);
        mGelleryPanel.setVisibility(View.GONE);
      //  mRecordPanel.setVisibility(View.GONE);
    }
    public void showRecord(){
     //   mRecordPanel.setVisibility(View.VISIBLE);
        mGelleryPanel.setVisibility(View.GONE);
        mFacePanel.setVisibility(View.GONE);
    }
    public void showGallery(){
        mGelleryPanel.setVisibility(View.VISIBLE);
        mFacePanel.setVisibility(View.GONE);
    //    mRecordPanel.setVisibility(View.GONE);
    }

    public interface PanelCallback{
        EditText getEditText();
        void sendGallery(String[] paths);
        void sendRecord(File file, long duration);
    }
}