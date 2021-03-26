package com.xana.mikochat.app.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xana.mikochat.app.App;
import com.xana.mikochat.app.R;
import com.xana.mikochat.app.frags.message.ChatGroupFragment;
import com.xana.mikochat.app.frags.message.ChatUserFragment;
import com.xana.mikochat.common.Common;
import com.xana.mikochat.common.app.Activity;
import com.xana.mikochat.common.app.Fragment;
import com.xana.mikochat.factory.model.IBaseModel;
import com.xana.mikochat.factory.model.db.Group;
import com.xana.mikochat.factory.model.db.Message;
import com.xana.mikochat.factory.model.db.Session;

import butterknife.BindView;

public class MessageActivity extends Activity {
    // 接收者Id, 可以是群Id也可以是人的Id;
    public static final String KEY_RECEIVER_ID = "KEY_RECEIVER_ID";
    // 标识是否是群
    public static final String KEY_RECEIVER_IS_GROUP = "KEY_RECEIVER_IS_GROUP";


    @BindView(R.id.im_bg)
    ImageView mBg;

    private String receiverId;
    private boolean isGroup;

    /**
     * 联系人的聊天界面
     *
     * @param context
     * @param user
     */
    public static void show(Context context, IBaseModel user) {
        if (user == null || context == null || TextUtils.isEmpty(user.getId()))
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, user.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, false);
        context.startActivity(intent);
    }

    /**
     * 会话点击
     *
     * @param context
     * @param session
     */
    public static void show(Context context, Session session) {
        if (session == null || context == null || TextUtils.isEmpty(session.getId()))
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, session.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, session.getReceiverType() == Message.RECEIVER_TYPE_GROUP);
        context.startActivity(intent);
    }

    /**
     * 群聊天
     *
     * @return
     */
    public static void show(Context context, Group group) {
        if (group == null || TextUtils.isEmpty(group.getId()) || context == null)
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, group.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, true);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        receiverId = bundle.getString(KEY_RECEIVER_ID);
        isGroup = bundle.getBoolean(KEY_RECEIVER_IS_GROUP);
        return !TextUtils.isEmpty(receiverId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        int index = (int) (Math.random()*40);
        @SuppressLint("DefaultLocale")
        String imgUrl = String.format(Common.IMAGE_URL, index);
        Glide.with(this)
                .load(imgUrl)
                .fitCenter()
                .into(mBg);

        setTitle("");
        Fragment fragment = isGroup ? new ChatGroupFragment() : new ChatUserFragment();

        // 传递参数
        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECEIVER_ID, receiverId);
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, fragment)
                .commit();
    }
}