package com.xana.mikochat.factory.data.group;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xana.mikochat.factory.data.BaseDbRepository;
import com.xana.mikochat.factory.data.helper.GroupHelper;
import com.xana.mikochat.factory.model.db.Group;
import com.xana.mikochat.factory.model.db.Group_Table;
import com.xana.mikochat.factory.model.db.view.GroupMemberSimple;

import java.util.List;

public class GroupsRepository extends BaseDbRepository<Group> implements GroupsDataSource {


    @Override
    public void load(SucceedCallback<List<Group>> callback) {
        super.load(callback);
        SQLite.select()
                .from(Group.class)
                .orderBy(Group_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }
    @Override
    protected boolean isRequired(Group group) {
        long count = group.getGroupMemberCount();
        if (count>0){
            group.holder = buildGroupHolder(group);
        }else {
            group.holder = null;
            GroupHelper.refreshGroupMember(group);
        }
        return true;
    }

    private String buildGroupHolder(Group group) {
        List<GroupMemberSimple> groupMemberSimples = group.getSimpleMembers();
        if(groupMemberSimples==null||groupMemberSimples.size()==0)
            return "";
        StringBuilder builder = new StringBuilder();
        for (GroupMemberSimple simple : groupMemberSimples) {
            builder.append(!TextUtils.isEmpty(simple.alias)? simple.alias: simple.name);
            builder.append(", ");
        }
        builder.delete(builder.lastIndexOf(", "), builder.length());
        return builder.toString();
    }
}
