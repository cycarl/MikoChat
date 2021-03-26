package com.xana.mikochat.app.frags.search;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xana.mikochat.app.R;
import com.xana.mikochat.app.activities.PersonalActivity;
import com.xana.mikochat.app.activities.SearchActivity;
import com.xana.mikochat.common.app.PresenterFragment;
import com.xana.mikochat.common.widget.EmptyView;
import com.xana.mikochat.common.widget.PortraitView;
import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;
import com.xana.mikochat.factory.model.card.GroupCard;
import com.xana.mikochat.factory.presenter.search.SearchContract;
import com.xana.mikochat.factory.presenter.search.SearchGroupPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchGroupFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment, SearchContract.GroupView {

    @BindView(R.id.empty)
    EmptyView mEmptyView;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;


    private RecyclerAdapter<GroupCard> mAdapter;

    public SearchGroupFragment() {
        // Required empty public constructor
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_group;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        // 初始化recycler
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter = new RecyclerAdapter<GroupCard>() {

            @Override
            protected int getItemViewType(int position, GroupCard groupCard) {
                return R.layout.cell_search_group_list;
            }

            @Override
            protected ViewHolder<GroupCard> onCreateViewHolder(View root, int viewType) {
                return new SearchGroupFragment.ViewHolder(root);
            }
        });


        mEmptyView.bind(mRecyclerView);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void initData() {
        super.initData();
        search("");
    }


    @Override
    public void search(String query) {
        mPresenter.search(query);
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchGroupPresenter(this);
    }

    @Override
    public void searchSuccess(List<GroupCard> groupCards) {
        mAdapter.replace(groupCards);
        /* 如果有数据则显示recycler 否者显示Empty */
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }


    class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCard> {

        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.im_join)
        ImageView mJoin;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(GroupCard groupCard) {
            mPortrait.setup(Glide.with(SearchGroupFragment.this), groupCard.getPicture());
            mName.setText(groupCard.getName());
            mJoin.setEnabled(groupCard.getJoinAt() == null);
        }

        @OnClick(R.id.im_join)
        void onJoinClick(){
            // 进入创建这界面进行申请加群
            PersonalActivity.show(getContext(), mData.getOwnerId());
        }

    }
}