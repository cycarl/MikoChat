package com.xana.mikochat.factory.data.helper;

import android.os.SystemClock;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xana.mikochat.common.Common;
import com.xana.mikochat.common.app.Application;
import com.xana.mikochat.factory.Factory;
import com.xana.mikochat.factory.model.api.RspModel;
import com.xana.mikochat.factory.model.api.message.MsgCreateModel;
import com.xana.mikochat.factory.model.card.MessageCard;
import com.xana.mikochat.factory.model.db.Message;
import com.xana.mikochat.factory.model.db.Message_Table;
import com.xana.mikochat.factory.net.Network;
import com.xana.mikochat.factory.net.RemoteService;
import com.xana.mikochat.factory.net.UploadHelper;
import com.xana.mikochat.utils.PicturesCompressor;
import com.xana.mikochat.utils.StreamUtil;

import org.w3c.dom.Text;

import java.io.File;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 消息工具类
 */
public class MessageHelper {
    // 从本地找消息
    public static Message findFromLocal(String id) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.id.eq(id))
                .querySingle();
    }

    // 发送是异步进行的
    public static void push(final MsgCreateModel model) {
        // 成功状态：如果是一个已经发送过的消息，则不能重新发送
        // 正在发送状态：如果是一个消息正在发送，则不能重新发送
        Message message = findFromLocal(model.getId());
        if (message != null && message.getStatus() != Message.STATUS_FAILED)
            return;


        // TODO 如果是文件类型的（语音，图片，文件），需要上传后才发送

        // 我们在发送的时候需要通知界面更新状态，Card;
        final MessageCard card = model.buildCard();
        Factory.getMessageCenter().dispatch(card);


        // 上传流媒体
        if(card.getType()!=Message.TYPE_STR&&!card.getContent().startsWith(UploadHelper.ENDPOINT)){
            String url = null;
            switch (card.getType()){
                case Message.TYPE_PIC:
                    url = uploadImage(card.getContent());
                    break;

                case Message.TYPE_AUDIO:
                    // TODO
                case Message.TYPE_FILE:
                    // TODO
                default:
                    // TODO
            }
            if(TextUtils.isEmpty(url)){
                Application.showToast("消息发送失败了呢! ");
                card.setStatus(Message.STATUS_FAILED);
                Factory.getMessageCenter().dispatch(card);
                return;
            }
            card.setContent(url);
            Factory.getMessageCenter().dispatch(card);
            // 界面card内容改变了 需要通知model改变
            model.refreshByCard(card);
        }

        // 直接发送, 进行网络调度
        RemoteService service = Network.remote();
        service.msgPush(model).enqueue(new Callback<RspModel<MessageCard>>() {
            @Override
            public void onResponse(Call<RspModel<MessageCard>> call, Response<RspModel<MessageCard>> response) {
                RspModel<MessageCard> rspModel = response.body();
                if (rspModel != null && rspModel.success()) {
                    MessageCard rspCard = rspModel.getResult();
                    if (rspCard != null) {
                        // 成功的调度
                        Factory.getMessageCenter().dispatch(rspCard);
                    }
                } else {
                    // 检查是否是账户异常
                    Factory.decodeRspCode(rspModel, null);
                    // 走失败流程
                    onFailure(call, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<MessageCard>> call, Throwable t) {
                // 通知失败
                card.setStatus(Message.STATUS_FAILED);
                Factory.getMessageCenter().dispatch(card);
            }
        });


//        Factory.runOnAsync(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
    }

    private static String uploadImage(String path) {

        return UploadHelper.uploadImage(path);

     /*
     // 图片压缩
        File file = null;
        String picUrl = null;
        try {
            file = Glide.with(Factory.app())
                    .load(path)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(file!=null){
                // 压缩
                String cacheDir = Application.getCacheDirFile().getAbsolutePath();
                String tempFile = String.format("%s/image/cache_%s.png", cacheDir, SystemClock.uptimeMillis());

                if(PicturesCompressor.compressImage(file.getAbsolutePath(), tempFile, Common.MAX_UPLOAD_IMAGE_SIZE)){
                    picUrl = UploadHelper.uploadImage(tempFile);
                    // 清理缓存
                    StreamUtil.delete(tempFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return TextUtils.isEmpty(picUrl)? Common.IMAGE: picUrl;
        */
    }

    /**
     * 查询一个消息，这个消息是一个群中的最后一条消息
     *
     * @param groupId 群Id
     * @return 群中聊天的最后一条消息
     */
    public static Message findLastWithGroup(String groupId) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(groupId))
                .orderBy(Message_Table.createAt, false) // 倒序查询
                .querySingle();
    }

    /**
     * 查询一个消息，这个消息是和一个人的最后一条聊天消息
     *
     * @param userId UserId
     * @return 聊天的最后一条消息
     */
    public static Message findLastWithUser(String userId) {
        return SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(userId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(userId))
                .orderBy(Message_Table.createAt, false) // 倒序查询
                .querySingle();
    }
}
