package com.xana.mikochat.app.frags.message;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.xana.mikochat.app.R;
import com.xana.mikochat.app.activities.GroupMemberActivity;
import com.xana.mikochat.app.activities.PersonalActivity;
import com.xana.mikochat.factory.model.db.Group;
import com.xana.mikochat.factory.model.db.view.GroupMemberSimple;
import com.xana.mikochat.factory.presenter.group.GroupMemberContract;
import com.xana.mikochat.factory.presenter.message.ChatContract;
import com.xana.mikochat.factory.presenter.message.ChatGroupPresenter;

import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;

public class ChatGroupFragment extends ChatFragment<Group>
        implements ChatContract.GroupView {


    @BindView(R.id.lay_members)
    LinearLayout mMembers;

    @BindView(R.id.txt_more)
    TextView mMore;

    public ChatGroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.lay_chat_header_group;
    }

    @Override
    protected int getHeaderImage() {
        return R.drawable.default_banner_group;
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatGroupPresenter(this, receiverId);
    }

    @Override
    public void showAdmin(boolean isAdmin) {
        if(isAdmin){
            mToolbar.inflateMenu(R.menu.chat_group);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if(menuItem.getItemId()==R.id.action_add){
                        // 添加新成员
                        GroupMemberActivity.showAdmin(getContext(), receiverId);
                        return true;

                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void initMembers(List<GroupMemberSimple> members, long more) {
        if(members==null||members.size()==0) return;
        for (GroupMemberSimple member : members) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ImageView view = (ImageView) inflater.inflate(R.layout.cell_chat_member_portrait, mMembers, false);
            mMembers.addView(view, 0);
            Glide.with(this)
                    .load(member.getPortrait())
                    .centerCrop()
                    .placeholder(R.drawable.default_portrait)
                    .into(view);

            //
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersonalActivity.show(getContext(), member.getId());
                }
            });
        }

        if(more>0){
            mMore.setText("+"+more);
            mMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 显示成员列表
                    GroupMemberActivity.show(getContext(), receiverId);
                }
            });
        }else {
            mMore.setVisibility(View.GONE);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        verticalOffset = Math.abs(verticalOffset);
        float totalScrollRange = appBarLayout.getTotalScrollRange();
        if(totalScrollRange==0)
            return;
        float rate = 1 - verticalOffset / totalScrollRange;
        if(rate==0){
            mMembers.setVisibility(View.INVISIBLE);
        }else if(rate==1){
            mMembers.setVisibility(View.VISIBLE);
        }else {
            mMembers.setVisibility(View.VISIBLE);
        }
        mMembers.setScaleX(rate);
        mMembers.setScaleY(rate);
        mMembers.setAlpha(rate);
    }
}