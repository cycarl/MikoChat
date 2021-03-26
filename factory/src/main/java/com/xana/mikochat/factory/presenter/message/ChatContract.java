package com.xana.mikochat.factory.presenter.message;

import com.xana.mikochat.factory.model.db.Group;
import com.xana.mikochat.factory.model.db.Message;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.model.db.view.GroupMemberSimple;
import com.xana.mikochat.factory.presenter.BaseContract;

import java.util.List;


/**
 * 聊天契约
 */
public interface ChatContract {
    interface Presenter extends BaseContract.Presenter {
        // 发送文字
        void pushText(String content);

        // 发送语音
        void pushAudio(String path);

        // 发送图片
        void pushImages(String[] paths);

        // 重新发送一个消息，返回是否调度成功
        boolean rePush(Message message);
    }

    // 界面的基类
    interface View<InitModel> extends BaseContract.RecyclerView<Presenter, Message> {
        // 初始化的Model
        void onInit(InitModel model);

    }

    // 人聊天的界面
    interface UserView extends View<User> {

    }

    // 群聊天的界面
    interface GroupView extends View<Group> {
        // 显示管理员菜单
        void showAdmin(boolean isAdmin);
        // 显示群成员
        void initMembers(List<GroupMemberSimple> members, long more);
    }
}
