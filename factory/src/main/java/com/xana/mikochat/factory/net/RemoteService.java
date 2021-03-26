package com.xana.mikochat.factory.net;

import com.xana.mikochat.factory.model.api.RspModel;
import com.xana.mikochat.factory.model.api.account.AccountRspModel;
import com.xana.mikochat.factory.model.api.account.LoginModel;
import com.xana.mikochat.factory.model.api.account.RegisterModel;
import com.xana.mikochat.factory.model.api.group.GroupCreateModel;
import com.xana.mikochat.factory.model.api.group.GroupMemberAddModel;
import com.xana.mikochat.factory.model.api.message.MsgCreateModel;
import com.xana.mikochat.factory.model.api.user.UserUpdateModel;
import com.xana.mikochat.factory.model.card.GroupCard;
import com.xana.mikochat.factory.model.card.GroupMemberCard;
import com.xana.mikochat.factory.model.card.MessageCard;
import com.xana.mikochat.factory.model.card.UserCard;
import com.xana.mikochat.factory.model.db.Group;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * 网络请求的所有接口
 */
public interface RemoteService {
    /**
     * 登录注册接口
     * @param model
     * @return
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 绑定设备Id
     * @param pushId
     * @return
     */
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind(@Path(encoded = true, value = "pushId") String pushId);

    // 用户更新
    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);

    // 用户搜索
    @GET("user/search/{name}")
    Call<RspModel<List<UserCard>>> userSearch(@Path("name") String name);

    // 关注
    @PUT("user/follow/{userId}")
    Call<RspModel<UserCard>> userFollow(@Path("userId") String userId);

    // 获取联系人列表
    @GET("user/contact")
    Call<RspModel<List<UserCard>>> userContacts();

    // 获取联系人详情
    @GET("user/{id}")
    Call<RspModel<UserCard>> userShow(@Path("id") String id);

    // 发送消息的接口
    @POST("msg")
    Call<RspModel<MessageCard>> msgPush(@Body MsgCreateModel model);

    // 创建群
    @POST("group")
    Call<RspModel<GroupCard>> groupCreate(@Body GroupCreateModel model);

    // 查找群
    @POST("group/{groupId}")
    Call<RspModel<GroupCard>> groupShow(@Path("groupId") String groupId);

    // 搜索群
    @GET("group/search/{name}")
    Call<RspModel<List<GroupCard>>> groupSearch(@Path(value = "name", encoded = true) String name);

    // 我的群列表
    @GET("group/list/{date}")
    Call<RspModel<List<GroupCard>>> userGroups(@Path(value = "date", encoded = true) String date);

    // 群成员列表
    @GET("group/{groupId}/member")
    Call<RspModel<List<GroupMemberCard>>> groupMembers(@Path("groupId") String groupId);

    // 添加新成员
    @POST("group/{groupId}/member")
    Call<RspModel<List<GroupMemberCard>>> groupMemberAdd(@Path("groupId") String groupId, @Body GroupMemberAddModel model);
}
