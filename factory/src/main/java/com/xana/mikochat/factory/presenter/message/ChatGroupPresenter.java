package com.xana.mikochat.factory.presenter.message;

import com.xana.mikochat.factory.data.helper.GroupHelper;
import com.xana.mikochat.factory.data.message.MessageDataSource;
import com.xana.mikochat.factory.data.message.MessageGroupRepository;
import com.xana.mikochat.factory.data.message.MessageRepository;
import com.xana.mikochat.factory.model.db.Group;
import com.xana.mikochat.factory.model.db.Message;
import com.xana.mikochat.factory.model.db.view.GroupMemberSimple;
import com.xana.mikochat.factory.persistence.Account;

import java.util.List;

/**
 */
public class ChatGroupPresenter extends ChatPresenter<ChatContract.GroupView>
        implements ChatContract.Presenter {
    public ChatGroupPresenter(ChatContract.GroupView view, String receiverId) {
        super(new MessageGroupRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_GROUP);
    }


    @Override
    public void start() {
        super.start();
        Group group = GroupHelper.findFromLocal(mReceiverId);
        if(group!=null){
            ChatContract.GroupView view = getView();
            boolean isAdmin = Account.getUserId().equalsIgnoreCase(group.getOwner().getId());
            view.showAdmin(isAdmin);

            view.onInit(group);

            List<GroupMemberSimple> members = group.getSimpleMembers();
            long count = group.getGroupMemberCount();

            view.initMembers(members, count-members.size());
        }
    }
}
