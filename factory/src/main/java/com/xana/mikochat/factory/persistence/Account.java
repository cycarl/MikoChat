package com.xana.mikochat.factory.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xana.mikochat.factory.Factory;
import com.xana.mikochat.factory.model.api.account.AccountRspModel;
import com.xana.mikochat.factory.model.db.User;
import com.xana.mikochat.factory.model.db.User_Table;

public class Account {
    public static final String KEY_PUSH_ID = "KEY_PUSH_ID";
    public static final String KEY_IS_BIND = "KEY_IS_BIND";
    public static final String KEY_TOEKN = "KEY_TOEKN";
    public static final String KEY_USER_ID = "KEY_USER_ID";
    public static final String KEY_ACCOUNT = "KEY_ACCOUNT";

    /* 设备推送Id */
    private static String pushId;
    /* 设备ID是否已经绑定到了服务器 */
    private static boolean isBind;
    // 用户token 用于接口请求
    private static String token;
    private static String userId;
    /* 当前登录的账号 */
    private static String account;



    /* 数据持久化 */
    private static void save(Context context){
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(), Context.MODE_PRIVATE);
        sp.edit()
                .putString(KEY_PUSH_ID, pushId)
                .putBoolean(KEY_IS_BIND, isBind)
                .putString(KEY_TOEKN, token)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_ACCOUNT, account)
                .apply();
    }

    /* 加载数据 */
    public static void load(Context context){
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(), Context.MODE_PRIVATE);
        pushId = sp.getString(KEY_PUSH_ID, "");
        isBind = sp.getBoolean(KEY_IS_BIND, false);
        token = sp.getString(KEY_TOEKN, "");
        userId = sp.getString(KEY_USER_ID, "");
        account = sp.getString(KEY_ACCOUNT, "");
    }

    /**
     * 设置并存储设备Id
     * @param pushId
     */
    public static void setPushId(String pushId){
        Account.pushId = pushId;
        Account.save(Factory.app());
    }
    public static String getPushId(){
        return pushId;
    }

    public static boolean isLogin() {
        return !TextUtils.isEmpty(userId)
                && !TextUtils.isEmpty(token);
    }

    /* 用户信息是否完善 */
    public static boolean isComplete(){
        // 描述，头像， 性别信息完善
        if(isLogin()){
            User user = getUser();
            return !TextUtils.isEmpty(user.getDesc())
                    &&!TextUtils.isEmpty(user.getPortrait())
                    &&user.getSex()!=0;
        }
        return false;
    }

    public static boolean isBind() {
        return isBind;
    }

    public static void setBind(boolean isBind){
        Account.isBind = isBind;
        Account.save(Factory.app());
    }

    /**
     * 保存用户信息到XML, token, 用户id
     * @param model
     */
    public static void login(AccountRspModel model) {
        Account.token = model.getToken();
        Account.userId = model.getUser().getId();
        Account.account = model.getAccount();
        save(Factory.app());
    }

    /**
     * 查询当前用户信息
     * @return user
     */
    public static User getUser(){
        return TextUtils.isEmpty(userId)? new User():
                SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(userId))
                .querySingle();
    }

    public static String getToken(){
        return Account.token;
    }

    public static String getUserId() {
        return userId;
    }
}
