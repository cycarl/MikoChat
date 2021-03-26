package com.xana.mikochat.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xana.mikochat.factory.model.db.Session;
import com.xana.mikochat.factory.model.db.Session_Table;


/**
 * 会话辅助工具类
 */
public class SessionHelper {
    // 从本地查询Session
    public static Session findFromLocal(String id) {
        return SQLite.select()
                .from(Session.class)
                .where(Session_Table.id.eq(id))
                .querySingle();
    }
}
