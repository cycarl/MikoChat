package com.xana.mikochat.app.frags.message;

import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xana.mikochat.app.R;
import com.xana.mikochat.app.activities.PersonalActivity;
import com.xana.mikochat.common.widget.PortraitView;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.presenter.message.ChatContract;
import com.xana.mikochat.factory.presenter.message.ChatUserPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class ChatUserFragment extends ChatFragment<User>
        implements ChatContract.UserView {


    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    private MenuItem menuItem;

    public ChatUserFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.lay_chat_header_user;
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        verticalOffset = Math.abs(verticalOffset);
        float totalScrollRange = appBarLayout.getTotalScrollRange();
        if(totalScrollRange==0)
            return;
        float rate = 1 - verticalOffset / totalScrollRange;
        if(rate==0){
            menuItem.setVisible(true);
            mPortrait.setVisibility(View.INVISIBLE);
        }else if(rate==1){
            menuItem.setVisible(false);
            mPortrait.setVisibility(View.VISIBLE);
        }else {
            menuItem.setVisible(true);
            mPortrait.setVisibility(View.VISIBLE);
        }
        mPortrait.setScaleX(rate);
        mPortrait.setScaleY(rate);
        mPortrait.setAlpha(rate);
        menuItem.getIcon().setAlpha((int) (255-255*rate));
    }

    @Override
    protected int getHeaderImage() {
        return R.drawable.default_banner_chat;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        mToolbar.inflateMenu(R.menu.chat_user);
        menuItem = mToolbar.getMenu().findItem(R.id.action_person);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.action_person){
                    onPortraitClick();
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        PersonalActivity.show(getContext(), receiverId);
    }


    @Override
    public void onInit(User user) {
        // 对聊天对象初始化
        super.onInit(user);
        mPortrait.setup(Glide.with(this), user);

    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatUserPresenter(this, receiverId);
    }
}