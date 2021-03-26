package com.xana.mikochat.app.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xana.mikochat.app.R;
import com.xana.mikochat.common.app.PresenterActivity;
import com.xana.mikochat.common.app.ToolbarActivity;
import com.xana.mikochat.common.widget.PortraitView;
import com.xana.mikochat.common.widget.recycler.RecyclerAdapter;
import com.xana.mikochat.factory.model.db.GroupMember;
import com.xana.mikochat.factory.model.db.view.GroupMemberSimple;
import com.xana.mikochat.factory.presenter.group.GroupMemberContract;
import com.xana.mikochat.factory.presenter.group.GroupMemberPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class GroupMemberActivity extends PresenterActivity<GroupMemberContract.Presenter>
        implements  GroupMemberContract.View{
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    private RecyclerAdapter<GroupMemberSimple> mAdapter;

    public static String KEY_GROUP_ID = "KEY_GROUP_ID";
    public static String KEY_IS_ADMIN = "KEY_IS_ADMIN";
    private String mGroupId;
    private boolean mIsAdmin;

    public static void show(Context context, String groupId){
        show(context, groupId, false);
    }

    public static void showAdmin(Context context, String receiverId) {
        show(context, receiverId, true);
    }

    private static void show(Context context, String groupId, boolean isAdmin){
        if(TextUtils.isEmpty(groupId)) return;
        Intent intent = new Intent(context, GroupMemberActivity.class);
        intent.putExtra(KEY_GROUP_ID, groupId);
        intent.putExtra(KEY_IS_ADMIN, isAdmin);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_member;
    }
    @Override
    protected boolean initArgs(Bundle bundle) {
        mGroupId = bundle.getString(KEY_GROUP_ID);
        mIsAdmin = bundle.getBoolean(KEY_IS_ADMIN);
        return !TextUtils.isEmpty(mGroupId);
    }
    @Override
    protected void initWidget() {
        super.initWidget();
        mToolbar.setTitle(R.string.title_member_list);

        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<GroupMemberSimple>() {
            @Override
            protected int getItemViewType(int position, GroupMemberSimple groupMemberSimple) {
                return R.layout.cell_group_create_contact;
            }

            @Override
            protected ViewHolder<GroupMemberSimple> onCreateViewHolder(View root, int viewType) {
                return new GroupMemberActivity.ViewHolder(root);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.refresh();
    }

    @Override
    protected GroupMemberContract.Presenter initPresenter() {
        return new GroupMemberPresenter(this);
    }


    @Override
    public RecyclerAdapter<GroupMemberSimple> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        hideLoading();
    }

    @Override
    public String getGroupId() {
        return mGroupId;
    }


    class ViewHolder extends RecyclerAdapter.ViewHolder<GroupMemberSimple>{
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;

        @BindView(R.id.txt_name)
        TextView mName;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.cb_select).setVisibility(View.GONE);
        }

        @Override
        protected void onBind(GroupMemberSimple groupMemberSimple) {
            mPortrait.setup(Glide.with(GroupMemberActivity.this), groupMemberSimple);
            mName.setText(groupMemberSimple.getName());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick(){
            PersonalActivity.show(GroupMemberActivity.this, mData.getId());
        }
    }
}