package com.xana.mikochat.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xana.mikochat.factory.Factory;
import com.xana.mikochat.factory.R;
import com.xana.mikochat.factory.data.DataSource;
import com.xana.mikochat.factory.data.user.UserCenter;
import com.xana.mikochat.factory.model.api.RspModel;
import com.xana.mikochat.factory.model.api.user.UserUpdateModel;
import com.xana.mikochat.factory.model.card.UserCard;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.model.db.User_Table;
import com.xana.mikochat.factory.model.db.view.UserSimple;
import com.xana.mikochat.factory.net.Network;
import com.xana.mikochat.factory.net.RemoteService;
import com.xana.mikochat.factory.persistence.Account;
import com.xana.mikochat.factory.presenter.contact.FollowPresenter;
import com.xana.mikochat.utils.CollectionUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHelper {
    // 更新用户信息
    public static void update(UserUpdateModel model, final DataSource.Callback<UserCard> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<UserCard>> call = service.userUpdate(model);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    Factory.getUserCenter().dispatch(userCard);

                    callback.onDataLoaded(userCard);
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }
    // 搜索
    public static Call search(String name, final DataSource.Callback<List<UserCard>> callback){
        RemoteService service = Network.remote();
        Call<RspModel<List<UserCard>>> call = service.userSearch(name);
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if(rspModel.success()){
                    List<UserCard> userCardList = rspModel.getResult();
                    callback.onDataLoaded(userCardList);
                }else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }
            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        return call;
    }
    // 用户关注
    public static void follow(String id, final DataSource.Callback<UserCard> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<UserCard>> call = service.userFollow(id);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if(rspModel.success()){
                    UserCard userCard = rspModel.getResult();

                    // 保存并通知联系人列表刷新
                    Factory.getUserCenter().dispatch(userCard);

                    //返回数据
                    callback.onDataLoaded(userCard);
                }else{
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }


    public static void refreshContacts(){
        RemoteService service = Network.remote();
        Call<RspModel<List<UserCard>>> call = service.userContacts();
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if(rspModel.success()){
                    List<UserCard> userCardList = rspModel.getResult();
                    if(userCardList==null||userCardList.size()==0)
                        return;

                    UserCard[] userCards = CollectionUtil.toArray(userCardList, UserCard.class);
                    Factory.getUserCenter().dispatch(userCards);
                }else {
                    Factory.decodeRspCode(rspModel, null);
                }
            }
            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                //  callback.onDataNotAvailable(R.string.data_network_error);
                //  nothing
            }
        });
    }

    /**
     * 搜索一个用户，优先查询本地数据库
     * 如果没有则网络请求获取
     * @param id
     * @return
     */
    public static User search(String id){
        User user = searchFromLocal(id);
        if(user==null)
            return searchFromNet(id);
        return user;
    }

    public static User searchFirstOfNet(String id){
        User user = searchFromNet(id);
        if(user==null)
            return searchFromLocal(id);
        return user;
    }

    public static User searchFromLocal(String id){
        return SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }

    public static User searchFromNet(String id){
        RemoteService service = Network.remote();
        Call<RspModel<UserCard>> call = service.userShow(id);
        try {
            Response<RspModel<UserCard>> response = call.execute();
            UserCard userCard = response.body().getResult();
            if(userCard!=null) {
                Factory.getUserCenter().dispatch(userCard);
                return userCard.build();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<User> getContacts(){
        return SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .queryList();
    }

    public static List<UserSimple> getSimpleContacts(){
        return SQLite.select(User_Table.id.withTable().as("id"),
                User_Table.name.withTable().as("name"),
                User_Table.portrait.withTable().as("portrait"))
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .queryCustomList(UserSimple.class);
    }

}
