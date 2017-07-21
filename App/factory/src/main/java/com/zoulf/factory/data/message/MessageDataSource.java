package com.zoulf.factory.data.message;

import com.zoulf.factory.data.DbDataSource;
import com.zoulf.factory.model.db.Message;

/**
 * 消息的数据源定义，他的实现类是:
 * 关注的对象是Message表
 *
 * @author Zoulf.
 */

public interface MessageDataSource extends DbDataSource<Message> {

}
