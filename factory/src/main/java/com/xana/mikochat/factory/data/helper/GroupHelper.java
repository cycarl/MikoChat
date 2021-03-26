package com.xana.mikochat.factory.data.helper;


import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xana.mikochat.factory.Factory;
import com.xana.mikochat.factory.R;
import com.xana.mikochat.factory.data.DataSource;
import com.xana.mikochat.factory.model.api.RspModel;
import com.xana.mikochat.factory.model.api.group.GroupCreateModel;
import com.xana.mikochat.factory.model.card.GroupCard;
import com.xana.mikochat.factory.model.card.GroupMemberCard;
import com.xana.mikochat.factory.model.card.UserCard;
import com.xana.mikochat.factory.model.db.Group;
import com.xana.mikochat.factory.model.db.GroupMember;
import com.xana.mikochat.factory.model.db.GroupMember_Table;
import com.xana.mikochat.factory.model.db.Group_Table;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.model.db.User_Table;
import com.xana.mikochat.factory.model.db.view.GroupMemberSimple;
import com.xana.mikochat.factory.net.Network;
import com.xana.mikochat.factory.net.RemoteService;
import com.xana.mikochat.factory.presenter.search.SearchGroupPresenter;
import com.xana.mikochat.utils.CollectionUtil;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 对群的一个简单的辅助工具类
 */
public class GroupHelper {
    public static Group find(String groupId) {
        // 查询群的信息，先本地，后网络
        Group group = findFromLocal(groupId);
        if (group == null)
            return findFromNet(groupId);
        return group;
    }

    public static Group findFromLocal(String groupId) {
        // 本地找群信息
        return SQLite.select()
                .from(Group.class)
                .where(Group_Table.id.eq(groupId))
                .querySingle();
    }

    public static Group findFromNet(String groupId) {
        RemoteService service = Network.remote();
        try {
            RspModel<GroupCard> rspModel = service.groupShow(groupId).execute().body();
            if (rspModel.success()) {
                GroupCard groupCard = rspModel.getResult();
                Factory.getGroupCenter().dispatch(groupCard);
                User owner = UserHelper.search(groupCard.getOwnerId());
                return groupCard.build(owner);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 创建群
    public static void create(GroupCreateModel model, DataSource.Callback<GroupCard> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<GroupCard>> call = service.groupCreate(model);
        call.enqueue(new Callback<RspModel<GroupCard>>() {
            @Override
            public void onResponse(Call<RspModel<GroupCard>> call, Response<RspModel<GroupCard>> response) {
                RspModel<GroupCard> rspModel = response.body();
                if (rspModel.success()) {
                    GroupCard groupCard = rspModel.getResult();
                    Factory.getGroupCenter().dispatch(groupCard);
                    callback.onDataLoaded(groupCard);
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<GroupCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    public static Call search(String name, DataSource.Callback<List<GroupCard>> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<List<GroupCard>>> call = service.groupSearch(name);
        call.enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                RspModel<List<GroupCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<GroupCard> groupCards = rspModel.getResult();
                    callback.onDataLoaded(groupCards);
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        return call;
    }

    public static void refreshGroups() {
        RemoteService service = Network.remote();
        Call<RspModel<List<GroupCard>>> call = service.userGroups("");
        call.enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                RspModel<List<GroupCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<GroupCard> groupCards = rspModel.getResult();
                    if (groupCards != null && groupCards.size() > 0) {
                        Factory.getGroupCenter().dispatch(groupCards.toArray(new GroupCard[0]));

                    }
                } else {
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
            }
        });
    }

    public static long getMemberCount(String id) {
        return SQLite.selectCountOf()
                .from(GroupMember.class)
                .where(GroupMember_Table.group_id.eq(id))
                .count();
    }

    /**
     * 网络请求获取群成员
     *
     * @param group
     */
    public static void refreshGroupMember(Group group) {
        RemoteService service = Network.remote();
        Call<RspModel<List<GroupMemberCard>>> call = service.groupMembers(group.getId());
        call.enqueue(new Callback<RspModel<List<GroupMemberCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupMemberCard>>> call, Response<RspModel<List<GroupMemberCard>>> response) {
                RspModel<List<GroupMemberCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<GroupMemberCard> memberCards = rspModel.getResult();
                    if (memberCards != null && memberCards.size() > 0) {
                        Factory.getGroupCenter().dispatch(memberCards.toArray(new GroupMemberCard[0]));
                    }
                } else {
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupMemberCard>>> call, Throwable t) {
            }
        });
    }

    public static List<GroupMemberSimple> getSimpleMembers(String id, int size) {

        return SQLite.select(User_Table.id.withTable().as("userId"),
                User_Table.name.withTable().as("name"),
                GroupMember_Table.alias.withTable().as("alias"),
                User_Table.portrait.withTable().as("portrait"))
                .from(GroupMember.class)
                .join(User.class, Join.JoinType.INNER)
                .on(GroupMember_Table.user_id.withTable().eq(User_Table.id.withTable()))
                .where(GroupMember_Table.group_id.withTable().eq(id))
                .orderBy(GroupMember_Table.user_id, true)
                .limit(size)
                .queryCustomList(GroupMemberSimple.class);
    }
}
