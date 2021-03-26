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
import com.xana.mikochat.common.app.Application;
import com.xana.mikochat.common.app.PresenterFragment;
import com.xana.mikochat.common.widget.EmptyView;
import com.xana.mikochat.common.widget.PortraitView;
import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;
import com.xana.mikochat.factory.model.card.UserCard;
import com.xana.mikochat.factory.presenter.contact.FollowContract;
import com.xana.mikochat.factory.presenter.contact.FollowPresenter;
import com.xana.mikochat.factory.presenter.search.SearchContract;
import com.xana.mikochat.factory.presenter.search.SearchUserPresenter;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;

import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

public class SearchUserFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment, SearchContract.UserView {

    @BindView(R.id.empty)
    EmptyView mEmptyView;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    private RecyclerAdapter<UserCard> mAdapter;

    public SearchUserFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        // 初始化recycler
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter = new RecyclerAdapter<UserCard>() {
            @Override
            protected int getItemViewType(int position, UserCard userCard) {
                return R.layout.cell_search_list;
            }

            @Override
            protected ViewHolder<UserCard> onCreateViewHolder(View root, int viewType) {
                return new SearchUserFragment.ViewHolder(root);
            }
        });
        mAdapter.setListener(new RecyclerAdapter.AdapterListener<UserCard>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, UserCard userCard) {
                PersonalActivity.show(getContext(), userCard.getId());
            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder holder, UserCard userCard) {
                Application.showToast("删除此项！");
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
        // 接口Activity向Fragment传递keyword
        mPresenter.search(query);
    }


    @Override
    protected SearchContract.Presenter initPresenter() {
        // 初始化Presenter
        return new SearchUserPresenter(this);
    }

    @Override
    public void searchSuccess(List<UserCard> cardList) {
        mAdapter.replace(cardList);
        /* 如果有数据则显示recycler 否者显示Empty */
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }


    class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard>
            implements FollowContract.View {

        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.im_follow)
        ImageView mFollow;

        private FollowContract.Presenter mPresenter;

        public ViewHolder(View itemView) {
            super(itemView);
            mPresenter = new FollowPresenter(this);
        }

        @Override
        protected void onBind(UserCard userCard) {
            mPortrait.setup(Glide.with(SearchUserFragment.this), userCard);
            mName.setText(userCard.getName());
            mFollow.setEnabled(!userCard.isFollow());
        }

        @OnClick(R.id.im_follow)
        void onFollowClick(View view){
            // 发起关注
            mPresenter.follow(mData.getId());
        }

        @Override
        public void followSuccess(UserCard userCard) {
            if(mFollow.getDrawable() instanceof LoadingDrawable){
                ((LoadingDrawable) mFollow.getDrawable()).stop();
                mFollow.setImageResource(R.drawable.sel_opt_done_add);
            }
            updateData(userCard);
        }

        @Override
        public void showError(int str) {
            if(mFollow.getDrawable() instanceof LoadingDrawable){
                ((LoadingDrawable) mFollow.getDrawable()).stop();
                mFollow.setImageResource(R.drawable.sel_opt_done_add);
            }
            Application.showToast(str);
        }

        @Override
        public void showLoading() {
            // 创建一个Loading
            int minSize = (int) Ui.dipToPx(getResources(), 22);
            int maxSize = (int) Ui.dipToPx(getResources(), 30);
            int[] colors = new int[]{UiCompat.getColor(getResources(), R.color.white_alpha_208)};
            LoadingDrawable drawable = new LoadingCircleDrawable(minSize, maxSize);
            drawable.setBackgroundColor(0);
            drawable.setForegroundColor(colors);

            mFollow.setImageDrawable(drawable);
            // 启动动画
            drawable.start();
        }

        @Override
        public void setPresenter(FollowContract.Presenter presenter) {
            mPresenter = presenter;
        }
    }
}