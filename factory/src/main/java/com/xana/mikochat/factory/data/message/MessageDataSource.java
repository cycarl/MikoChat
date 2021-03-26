package com.xana.mikochat.factory.data.message;

import com.xana.mikochat.factory.data.DbDataSource;
import com.xana.mikochat.factory.model.db.Message;

/**
 * 消息的数据源定义，他的实现是：MessageRepository
 * 关注的对象是Message表
 */
public interface MessageDataSource extends DbDataSource<Message> {
}
