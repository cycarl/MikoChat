package com.xana.mikochat.app.frags.main;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xana.mikochat.app.R;
import com.xana.mikochat.app.activities.MessageActivity;
import com.xana.mikochat.app.activities.PersonalActivity;
import com.xana.mikochat.common.app.Fragment;
import com.xana.mikochat.common.app.PresenterFragment;
import com.xana.mikochat.common.widget.EmptyView;
import com.xana.mikochat.common.widget.PortraitView;
import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;
import com.xana.mikochat.factory.model.db.Group;
import com.xana.mikochat.factory.model.db.Message;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.presenter.group.GroupContract;
import com.xana.mikochat.factory.presenter.group.GroupPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class GroupFragment extends PresenterFragment<GroupContract.Presenter>
        implements GroupContract.View{


    @BindView(R.id.empty)
    EmptyView mEmptyView;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    private RecyclerAdapter<Group> mAdapter;


    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_group;
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        mPresenter.start();
    }

    @Override
    protected void initWidget(android.view.View root) {
        super.initWidget(root);
        // 初始化recycler
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(mAdapter = new RecyclerAdapter<Group>() {
            @Override
            protected int getItemViewType(int position, Group group) {
                return R.layout.cell_group_list;
            }

            @Override
            protected ViewHolder<Group> onCreateViewHolder(View root, int viewType) {
                return new GroupFragment.ViewHolder(root);
            }
        });

        mAdapter.setListener(new RecyclerAdapter.AdapterListener<Group>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Group group) {
                // 群聊天
                MessageActivity.show(getContext(), group);
            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder holder, Group group) {
                // TODO 删除
            }
        });

        mEmptyView.bind(mRecyclerView);
        setPlaceHolderView(mEmptyView);
    }


    @Override
    protected GroupContract.Presenter initPresenter() {
        return new GroupPresenter(this);
    }

    @Override
    public RecyclerAdapter<Group> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mEmptyView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }



    class ViewHolder extends RecyclerAdapter.ViewHolder<Group> {

        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.txt_desc)
        TextView mDesc;
        @BindView(R.id.txt_member)
        TextView mMember;

        public ViewHolder(android.view.View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Group group) {
            mPortrait.setup(Glide.with(GroupFragment.this), group);
            mName.setText(group.getName());
            mDesc.setText(group.getDesc());
            mMember.setText((String)group.holder);
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick(){
            PersonalActivity.show(getActivity(), mData.getOwner().getId());
        }
    }
}