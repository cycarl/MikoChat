package com.xana.mikochat.app.frags.main;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xana.mikochat.app.R;
import com.xana.mikochat.app.activities.MessageActivity;
import com.xana.mikochat.app.activities.PersonalActivity;
import com.xana.mikochat.common.app.PresenterFragment;
import com.xana.mikochat.common.widget.EmptyView;
import com.xana.mikochat.common.widget.PortraitView;
import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.presenter.contact.ContactContract;
import com.xana.mikochat.factory.presenter.contact.ContactPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class ContactFragment extends PresenterFragment<ContactContract.Presenter>
        implements ContactContract.View {

    @BindView(R.id.empty)
    EmptyView mEmptyView;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    private RecyclerAdapter<User> mAdapter;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initWidget(android.view.View root) {
        super.initWidget(root);
        // 初始化recycler
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter = new RecyclerAdapter<User>() {
            @Override
            protected int getItemViewType(int position, User user) {
                return R.layout.cell_contact_list;
            }

            @Override
            protected ViewHolder<User> onCreateViewHolder(android.view.View root, int viewType) {
                return new ContactFragment.ViewHolder(root);
            }
        });

        mAdapter.setListener(new RecyclerAdapter.AdapterListener<User>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, User user) {
                MessageActivity.show(getContext(), user);
            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder holder, User user) {

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
    protected ContactContract.Presenter initPresenter() {
        return new ContactPresenter(this);
    }

    @Override
    public RecyclerAdapter<User> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }





    class ViewHolder extends RecyclerAdapter.ViewHolder<User> {

        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.txt_desc)
        TextView mDesc;

        public ViewHolder(android.view.View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(User user) {
            mPortrait.setup(Glide.with(ContactFragment.this), user);
            mName.setText(user.getName());
            mDesc.setText(user.getDesc());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick(){
            PersonalActivity.show(getActivity(), mData.getId());
        }
    }
}