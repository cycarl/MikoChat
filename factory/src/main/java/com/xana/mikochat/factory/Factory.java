package com.xana.mikochat.factory;

import android.support.annotation.StringRes;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.xana.mikochat.common.app.Application;
import com.xana.mikochat.factory.data.DataSource;
import com.xana.mikochat.factory.data.group.GroupCenter;
import com.xana.mikochat.factory.data.group.GroupDispatcher;
import com.xana.mikochat.factory.data.message.MessageCenter;
import com.xana.mikochat.factory.data.message.MessageDispatcher;
import com.xana.mikochat.factory.data.user.UserCenter;
import com.xana.mikochat.factory.data.user.UserDispatcher;
import com.xana.mikochat.factory.model.api.PushModel;
import com.xana.mikochat.factory.model.api.RspModel;
import com.xana.mikochat.factory.model.card.GroupCard;
import com.xana.mikochat.factory.model.card.GroupMemberCard;
import com.xana.mikochat.factory.model.card.MessageCard;
import com.xana.mikochat.factory.model.card.UserCard;
import com.xana.mikochat.factory.model.db.GroupMember;
import com.xana.mikochat.factory.persistence.Account;
import com.xana.mikochat.factory.utils.DBFlowExclusionStrategy;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Factory {
    private static final Factory instance;
    private final Executor executor;
    private final Gson gson;

    static {
        instance = new Factory();
    }

    private Factory() {
        // 新建一个线程池
        executor = Executors.newFixedThreadPool(4);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                // 设置一个过滤器, 数据库级别的ModeL不进行Json转换
                .setExclusionStrategies(new DBFlowExclusionStrategy())
                .create();
    }

    /**
     * 代理初始化
     */
    public static void setup(){
        /* 初始化数据库 */
        FlowManager.init(new FlowConfig.Builder(app())
            .openDatabasesOnInit(true)
            .build());
        /* 持久化数据 */
        Account.load(app());
    }

    /**
     * 返回全局Application
     * @return
     */
    public static Application app() {
        return Application.getInstance();

    }

    public static void runOnAsync(Runnable runnable) {
        // 异步执行
        instance.executor.execute(runnable);
    }

    public static Gson getGson() {
        return instance.gson;
    }

    /**
     * 进行错误Code的解析，
     * 把网络返回的Code值进行统一的规划并返回为一个String资源
     *
     * @param model    RspModel
     * @param callback DataSource.FailedCallback 用于返回一个错误的资源Id
     */
    public static void decodeRspCode(RspModel model, DataSource.FailedCallback callback) {
        if (model == null)
            return;

        // 进行Code区分
        switch (model.getCode()) {
            case RspModel.SUCCEED:
                return;
            case RspModel.ERROR_SERVICE:
                decodeRspCode(R.string.data_rsp_error_service, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_USER:
                decodeRspCode(R.string.data_rsp_error_not_found_user, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP:
                decodeRspCode(R.string.data_rsp_error_not_found_group, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP_MEMBER:
                decodeRspCode(R.string.data_rsp_error_not_found_group_member, callback);
                break;
            case RspModel.ERROR_CREATE_USER:
                decodeRspCode(R.string.data_rsp_error_create_user, callback);
                break;
            case RspModel.ERROR_CREATE_GROUP:
                decodeRspCode(R.string.data_rsp_error_create_group, callback);
                break;
            case RspModel.ERROR_CREATE_MESSAGE:
                decodeRspCode(R.string.data_rsp_error_create_message, callback);
                break;
            case RspModel.ERROR_PARAMETERS:
                decodeRspCode(R.string.data_rsp_error_parameters, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_ACCOUNT:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_account, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_NAME:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_name, callback);
                break;
            case RspModel.ERROR_ACCOUNT_TOKEN:
                Application.showToast(R.string.data_rsp_error_account_token);
                instance.logout();
                break;
            case RspModel.ERROR_ACCOUNT_LOGIN:
                decodeRspCode(R.string.data_rsp_error_account_login, callback);
                break;
            case RspModel.ERROR_ACCOUNT_REGISTER:
                decodeRspCode(R.string.data_rsp_error_account_register, callback);
                break;
            case RspModel.ERROR_ACCOUNT_NO_PERMISSION:
                decodeRspCode(R.string.data_rsp_error_account_no_permission, callback);
                break;
            case RspModel.ERROR_UNKNOWN:
            default:
                decodeRspCode(R.string.data_rsp_error_unknown, callback);
                break;
        }
    }

    private static void decodeRspCode(@StringRes final int resId,
                                      final DataSource.FailedCallback callback) {
        if (callback != null)
            callback.onDataNotAvailable(resId);
    }


    /**
     * 收到账户退出的消息需要进行账户退出重新登录
     */
    private void logout() {

    }

    /**
     * 处理推送来的消息
     *
     * @param message 消息
     */
    public static void dispatchPush(String message) {
        //
        if(!Account.isLogin()) return;
        PushModel model = PushModel.decode(message);
        if(model==null) return;
        Log.e("TAG", model.toString());

        List<PushModel.Entity> entities = model.getEntities();
        for (PushModel.Entity entity : entities) {
            switch (entity.type){
                case PushModel.ENTITY_TYPE_LOGOUT:
                    instance.logout();
                    return;
                case PushModel.ENTITY_TYPE_MESSAGE:
                    MessageCard messagecard = getGson().fromJson(entity.content, MessageCard.class);
                    getMessageCenter().dispatch(messagecard);
                    break;
                case PushModel.ENTITY_TYPE_ADD_FRIEND:
                    UserCard userCard = getGson().fromJson(entity.content, UserCard.class);
                    getUserCenter().dispatch(userCard);
                    break;
                case PushModel.ENTITY_TYPE_ADD_GROUP:
                    GroupCard groupCard = getGson().fromJson(entity.content, GroupCard.class);
                    getGroupCenter().dispatch(groupCard);
                    break;

                case PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS:
                    GroupMemberCard groupMemberCard = getGson().fromJson(entity.content, GroupMemberCard.class);
                    getGroupCenter().dispatch(groupMemberCard);
                    break;

                case PushModel.ENTITY_TYPE_MODIFY_GROUP_MEMBERS:
                    Type type = new TypeToken<List<GroupMemberCard>>(){}.getType();
                    List<GroupMemberCard> groupMemberCards = getGson().fromJson(entity.content, type);
                    getGroupCenter().dispatch(groupMemberCards.toArray(new GroupMemberCard[0]));
                    break;

                case PushModel.ENTITY_TYPE_EXIT_GROUP_MEMBERS:
                    // TODO 群员退出
            }
        }

    }

    /**
     * 获取一个用户中心实例
     * @return
     */
    public static UserCenter getUserCenter(){
        return UserDispatcher.instance();
    }

    /**
     * 获取一个消息中心实例
     * @return
     */
    public static MessageCenter getMessageCenter(){
        return MessageDispatcher.instance();
    }

    /**
     * 获取一个群中心的实例
     * @return
     */
    public static GroupCenter getGroupCenter(){
        return GroupDispatcher.instance();
    }
}
