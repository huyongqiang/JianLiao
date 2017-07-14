package com.zoulf.factory.data.user;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zoulf.factory.data.BaseDbRepository;
import com.zoulf.factory.model.db.User;
import com.zoulf.factory.model.db.User_Table;
import com.zoulf.factory.persistence.Account;
import java.util.List;

/**
 * 联系人仓库
 *
 * @author Zoulf
 * @version 1.0.0
 */
public class ContactRepository extends BaseDbRepository<User> implements ContactDataSource{

  @Override
  public void load(SucceedCallback<List<User>> callback) {
    super.load(callback);

    // 加载本地数据库
    SQLite.select()
        .from(User.class)
        .where(User_Table.isFollow.eq(true))
        .and(User_Table.id.notEq(Account.getUserId()))
        .orderBy(User_Table.name, true)
        .limit(100)
        .async()
        .queryListResultCallback(this)
        .execute();

  }

  /**
   * 检查一个User是否是我需要关注的数据，即过滤操作
   *
   * @param user User
   * @return True 是我需要关注的数据
   */
  @Override
  protected boolean isRequired(User user) {
    return user.isFollow() && !user.getId().equals(Account.getUserId());
  }
}

