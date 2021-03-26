package com.xana.mikochat.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import com.xana.mikochat.factory.data.helper.MessageHelper;
import com.xana.mikochat.factory.data.message.MessageDataSource;
import com.xana.mikochat.factory.model.api.message.MsgCreateModel;
import com.xana.mikochat.factory.model.db.Message;
import com.xana.mikochat.factory.persistence.Account;
import com.xana.mikochat.factory.presenter.BaseSourcePresenter;
import com.xana.mikochat.factory.utils.DiffUiDataCallback;

import java.util.List;


/**
 * 聊天Presenter的基础类
 */
@SuppressWarnings("WeakerAccess")
public class ChatPresenter<View extends ChatContract.View>
        extends BaseSourcePresenter<Message, Message, MessageDataSource, View>
        implements ChatContract.Presenter {

    // 接收者Id，可能是群，或者人的ID
    protected String mReceiverId;
    // 区分是人还是群Id
    protected int mReceiverType;


    public ChatPresenter(MessageDataSource source, View view,
                         String receiverId, int receiverType) {
        super(source, view);
        this.mReceiverId = receiverId;
        this.mReceiverType = receiverType;
    }

    @Override
    public void pushText(String content) {
        // 构建一个新的消息
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, mReceiverType)
                .content(content, Message.TYPE_STR)
                .build();

        // 进行网络发送
        MessageHelper.push(model);
    }

    @Override
    public void pushAudio(String path) {
        // TODO 发送语音
    }

    @Override
    public void pushImages(String[] paths) {
        // TODO 发送图片
        if(paths==null||paths.length==0) return;
        for (String path : paths) {
            MsgCreateModel model = new MsgCreateModel.Builder()
                    .receiver(mReceiverId, mReceiverType)
                    .content(path, Message.TYPE_PIC)
                    .build();
            MessageHelper.push(model);
        }
    }

    @Override
    public boolean rePush(Message message) {
        // 确定消息是可重复发送的
        if (Account.getUserId().equalsIgnoreCase(message.getSender().getId())
                && message.getStatus() == Message.STATUS_FAILED) {

            // 更改状态
            message.setStatus(Message.STATUS_CREATED);
            // 构建发送Model
            MsgCreateModel model = MsgCreateModel.buildWithMessage(message);
            MessageHelper.push(model);
            return true;
        }

        return false;
    }

    @Override
    public void onDataLoaded(List<Message> messages) {
        ChatContract.View view = getView();
        if (view == null)
            return;

        // 拿到老数据
        @SuppressWarnings("unchecked")
        List<Message> old = view.getRecyclerAdapter().getItemList();

        // 差异计算
        DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(old, messages);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 进行界面刷新
        refreshData(result, messages);
    }
}
