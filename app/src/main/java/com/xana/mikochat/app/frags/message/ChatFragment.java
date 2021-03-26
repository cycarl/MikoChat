package com.xana.mikochat.app.frags.message;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.xana.mikochat.app.R;
import com.xana.mikochat.app.activities.ImageActivity;
import com.xana.mikochat.app.activities.MessageActivity;
import com.xana.mikochat.app.frags.panel.PanelFragment;
import com.xana.mikochat.common.app.Application;
import com.xana.mikochat.common.app.Fragment;
import com.xana.mikochat.common.app.PresenterFragment;
import com.xana.mikochat.common.widget.PortraitView;
import com.xana.mikochat.common.widget.adapter.TextWatcherAdapter;
import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;
import com.xana.mikochat.face.Face;
import com.xana.mikochat.factory.model.IBaseModel;
import com.xana.mikochat.factory.model.db.Message;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.persistence.Account;
import com.xana.mikochat.factory.presenter.message.ChatContract;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;
import net.qiujuer.widget.airpanel.AirPanel;
import net.qiujuer.widget.airpanel.Util;

import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class ChatFragment<InitModel extends IBaseModel> extends PresenterFragment<ChatContract.Presenter>
        implements AppBarLayout.OnOffsetChangedListener,
        ChatContract.View<InitModel>, PanelFragment.PanelCallback {
    protected String receiverId;

    protected Adapter mAdapter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.edit_content)
    EditText mContent;
    @BindView(R.id.btn_submit)
    ImageView mSubmit;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    private AirPanel.Boss mPanelBoss;
    private PanelFragment mPanelFragment;

    @Override
    protected final int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        receiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }

    @LayoutRes
    protected abstract int getHeaderLayoutId();

    private void onPanelOpened(){
        //
        if(mAppbar!=null)
            mAppbar.setExpanded(false, true);
    }

    @Override
    protected void initWidget(View root) {
        ViewStub viewStub = root.findViewById(R.id.view_header);
        viewStub.setLayoutResource(getHeaderLayoutId());
        viewStub.inflate();
        super.initWidget(root);

        mPanelBoss = root.findViewById(R.id.lay_content);
        mPanelBoss.setup(new AirPanel.PanelListener() {
            @Override
            public void requestHideSoftKeyboard() {
                // 请求隐藏软键盘
                Util.hideKeyboard(mContent);
            }
        });
        mPanelBoss.setOnStateChangedListener(new AirPanel.OnStateChangedListener() {
            @Override
            public void onPanelStateChanged(boolean isOpen) {
                if(isOpen)
                    onPanelOpened();
            }

            @Override
            public void onSoftKeyboardStateChanged(boolean isOpen) {
                if(isOpen)
                    onPanelOpened();
            }
        });

        mPanelFragment = (PanelFragment) getChildFragmentManager().findFragmentById(R.id.frag_panel);
        assert mPanelFragment != null;
        mPanelFragment.setCallback(this);

        initToolbar();
        initAppbar();
        initEditContent();
        // recycler基本设置
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new Adapter();
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    protected void initEditContent() {
        mContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String msg = s.toString().trim();
                boolean candSend = !TextUtils.isEmpty(msg);
                // 设置状态，改变对应的ICON
                mSubmit.setActivated(candSend);
            }
        });
    }

    protected void initToolbar() {
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void initAppbar() {
        mAppbar.addOnOffsetChangedListener(this);
    }

    @OnClick(R.id.btn_face)
    void onFaceClick() {
        mPanelBoss.openPanel();
        mPanelFragment.showFace();
    }

    @OnClick(R.id.btn_record)
    void onRecordClick() {
        mPanelBoss.openPanel();
        mPanelFragment.showRecord();
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        if (mSubmit.isActivated()) {
            // send message
            String text = mContent.getText().toString();
            mContent.setText("");
            mPresenter.pushText(text);
        } else {
            // more
            onMoreClick();
        }
    }

    protected void onMoreClick() {
        mPanelBoss.openPanel();
        mPanelFragment.showGallery();
    }



    @Override
    public RecyclerAdapter<Message> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        if(mAdapter.getItemCount()==0) return;
        mRecycler.smoothScrollToPosition(mAdapter.getItemCount()-1);
    }

    @DrawableRes
    protected abstract int getHeaderImage();

    @Override
    public void onInit(InitModel initModel) {
        mCollapsingToolbar.setTitle(initModel.getName());
//        Glide.with(this)
//                .load(initModel.getPortrait())
//                .centerCrop()
//                .placeholder(getHeaderImage())
//                .into(new ViewTarget<CollapsingToolbarLayout, GlideDrawable>(mCollapsingToolbar) {
//                    @Override
//                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
//                        this.view.setContentScrim(resource.getCurrent());
//                    }
//                });
    }


    @Override
    public EditText getEditText() {
        return mContent;
    }

    @Override
    public void sendGallery(String[] paths) {
        mPresenter.pushImages(paths);
    }

    @Override
    public void sendRecord(File file, long duration) {
        // TODO
     //   mPresenter.pushAudio();
    }

    @Override
    public boolean onBackPressed() {
        if(mPanelBoss.isOpen()){
            mPanelBoss.closePanel();
            return true;
        }
        return super.onBackPressed();
    }

    private class Adapter extends RecyclerAdapter<Message> {

        @Override
        protected int getItemViewType(int position, Message message) {

            boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());
            switch (message.getType()) {
                case Message.TYPE_STR:
                    return isRight ? R.layout.cell_chat_text_right
                            : R.layout.cell_chat_text_left;

                case Message.TYPE_AUDIO:
                    return isRight ? R.layout.cell_chat_audio_right
                            : R.layout.cell_chat_audio_left;

                case Message.TYPE_PIC:
                    return isRight ? R.layout.cell_chat_pic_right
                            : R.layout.cell_chat_pic_left;

                case Message.TYPE_FILE:
                    return isRight ? R.layout.cell_chat_text_right
                            : R.layout.cell_chat_text_left;

            }
            return 0;
        }

        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            switch (viewType) {
                case R.layout.cell_chat_text_right:
                case R.layout.cell_chat_text_left:
                    return new TextHolder(root);

                case R.layout.cell_chat_audio_right:
                case R.layout.cell_chat_audio_left:
                    return new AudioHolder(root);

                case R.layout.cell_chat_pic_right:
                case R.layout.cell_chat_pic_left:
                    return new PicHolder(root);
            }

            return new TextHolder(root);
        }
    }

    class BaseHolder extends RecyclerAdapter.ViewHolder<Message> {
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;

        @Nullable
        @BindView(R.id.loading)
        Loading mLoading;


        public BaseHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            User sender = message.getSender();
            sender.load();
            mPortrait.setup(Glide.with(ChatFragment.this), sender);

            if (mLoading != null) {
                // 当前布局在右边
                int status = message.getStatus();
                if (status == Message.STATUS_DONE) {
                    // 正常状态
                    mLoading.stop();
                    mLoading.setVisibility(View.GONE);
                } else if (status == Message.STATUS_CREATED) {
                    // 发送中
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(0);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
                    mLoading.start();
                } else if (status == Message.STATUS_FAILED) {
                    // 失败情况下，可以重新发送
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.stop();
                    mLoading.setProgress(1);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorError));
                }
                mLoading.setEnabled(status == Message.STATUS_FAILED);
                mLoading.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Application.showToast("重新发送消息");
                        onReSendClick();
                    }
                });

            }
        }

        void onReSendClick() {
            if(mPresenter.rePush(mData)){
                updateData(mData);
            }
        }
    }

    class TextHolder extends BaseHolder {
        @BindView(R.id.txt_content)
        TextView mContent;

        public TextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            Spannable spannable = new SpannableString(message.getContent());
            // 解析表情
            Face.decode(mContent, spannable, (int) Ui.dipToPx(getResources(), 20));

            // 把内容设置到布局上
            mContent.setText(spannable);
        }
    }
    class AudioHolder extends BaseHolder {

        public AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // TODO
        }
    }
    class PicHolder extends BaseHolder {

        @BindView(R.id.im_image)
        ImageView mImage;

        private int width;
        private int height;

        public PicHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);

            Glide.with(ChatFragment.this)
                    .load(message.getContent())
                    .fitCenter()
                    .into(mImage);
        }

        @OnClick(R.id.im_image)
        void onImageClick(){
            ImageActivity.show(getContext(), mData.getContent());
        }

    }
}
