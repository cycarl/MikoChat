package com.xana.mikochat.app.frags.main;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xana.mikochat.app.R;
import com.xana.mikochat.app.activities.MessageActivity;
import com.xana.mikochat.app.activities.PersonalActivity;
import com.xana.mikochat.common.app.Fragment;
import com.xana.mikochat.common.app.PresenterFragment;
import com.xana.mikochat.common.widget.EmptyView;
import com.xana.mikochat.common.widget.GalleryView;
import com.xana.mikochat.common.widget.PortraitView;
import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;
import com.xana.mikochat.face.Face;
import com.xana.mikochat.factory.model.db.Session;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.presenter.message.SessionContract;
import com.xana.mikochat.factory.presenter.message.SessionPresenter;
import com.xana.mikochat.utils.TimeUtil;

import net.qiujuer.genius.ui.Ui;

import butterknife.BindView;
import butterknife.OnClick;

public class ActiveFragment extends PresenterFragment<SessionContract.Presenter>
        implements SessionContract.View {

    @BindView(R.id.empty)
    EmptyView mEmptyView;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    private RecyclerAdapter<Session> mAdapter;

    public ActiveFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initWidget(android.view.View root) {
        super.initWidget(root);
        // 初始化recycler
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter = new RecyclerAdapter<Session>() {
            @Override
            protected int getItemViewType(int position, Session session) {
                return R.layout.cell_session_list;
            }

            @Override
            protected ViewHolder<Session> onCreateViewHolder(android.view.View root, int viewType) {
                return new ActiveFragment.ViewHolder(root);
            }
        });

        mAdapter.setListener(new RecyclerAdapter.AdapterListener<Session>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Session session) {
                MessageActivity.show(getContext(), session);
            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder holder, Session session) {

            }
        });

        mEmptyView.bind(mRecyclerView);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        // 进行一次数据加载
        mPresenter.start();
    }


    @Override
    protected SessionContract.Presenter initPresenter() {
        return new SessionPresenter(this);
    }

    @Override
    public RecyclerAdapter<Session> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }



    class ViewHolder extends RecyclerAdapter.ViewHolder<Session> {

        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.txt_content)
        TextView mContent;
        @BindView(R.id.txt_time)
        TextView mTime;

        public ViewHolder(android.view.View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Session session) {
            mPortrait.setup(Glide.with(ActiveFragment.this), session.getPicture());
            mName.setText(session.getTitle());

            String str = session.getContent()==null? "": session.getContent();
            Spannable spannable = new SpannableString(str);
            // 解析表情
            Face.decode(mContent, spannable, (int)mContent.getTextSize());
            // 把内容设置到布局上
            mContent.setText(spannable);

            mTime.setText(TimeUtil.getString(session.getModifyAt()));
        }

/*        @OnClick(R.id.im_portrait)
        void onPortraitClick() {
            PersonalActivity.show(getActivity(), mData.getId());
        }*/
    }
}