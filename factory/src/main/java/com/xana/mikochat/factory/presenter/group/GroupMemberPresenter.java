package com.xana.mikochat.factory.presenter.group;

import com.xana.mikochat.factory.Factory;
import com.xana.mikochat.factory.data.helper.GroupHelper;
import com.xana.mikochat.factory.data.helper.UserHelper;
import com.xana.mikochat.factory.model.db.Group;
import com.xana.mikochat.factory.model.db.view.GroupMemberSimple;
import com.xana.mikochat.factory.model.db.view.UserSimple;
import com.xana.mikochat.factory.presenter.BasePresenter;
import com.xana.mikochat.factory.presenter.BaseRecyclerPresenter;

import java.util.LinkedList;
import java.util.List;

public class GroupMemberPresenter extends BaseRecyclerPresenter<GroupMemberSimple, GroupMemberContract.View>
        implements GroupMemberContract.Presenter{

    public GroupMemberPresenter(GroupMemberContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        // loading...
        super.start();

    }

    @Override
    public void refresh() {
        Factory.runOnAsync(loader);
    }

    private Runnable loader = new Runnable() {

        @Override
        public void run() {

            GroupMemberContract.View view = getView();
            if(view==null) return;
            String groupId = view.getGroupId();

            // 查询所有
            List<GroupMemberSimple> members = GroupHelper.getSimpleMembers(groupId, -1);
            refreshData(members);
        }
    };
}
