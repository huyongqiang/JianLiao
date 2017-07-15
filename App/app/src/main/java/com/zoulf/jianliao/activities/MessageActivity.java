package com.zoulf.jianliao.activities;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.zoulf.common.app.MyActivity;
import com.zoulf.factory.model.Author;
import com.zoulf.factory.model.db.Group;
import com.zoulf.jianliao.R;

public class MessageActivity extends MyActivity {

  // 接收者Id，可以是人，也可以是群
  private static final String KEY_RECEIVER_ID = "KEY_RECEIVER_ID";
  // 是否是群
  private static final String KEY_RECEIVER_IS_GROUP = "KEY_RECEIVER_IS_GROUP";

  private String mReceiverId;
  private String mIsGroup;

  /**
   * 显示人的聊天界面
   *
   * @param context 上下文
   * @param author 人的信息
   */
  public static void show(Context context, Author author) {
    if (author == null || context == null || TextUtils.isEmpty(author.getId())) {
      return;
    }
    Intent intent = new Intent(context, MessageActivity.class);
    intent.putExtra(KEY_RECEIVER_ID, author.getId());
    intent.putExtra(KEY_RECEIVER_IS_GROUP, false);
    context.startActivity(intent);
  }

  /**
   * 群聊天
   *
   * @param context 上下文
   * @param group 群的Model
   */
  public static void show(Context context, Group group) {
    if (group == null || context == null || TextUtils.isEmpty(group.getId())) {
      return;
    }
    Intent intent = new Intent(context, MessageActivity.class);
    intent.putExtra(KEY_RECEIVER_ID, group.getId());
    intent.putExtra(KEY_RECEIVER_IS_GROUP, true);
    context.startActivity(intent);
  }

  @Override
  protected int getContentLayoutId() {
    return R.layout.activity_message;
  }

}
