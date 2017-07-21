package com.zoulf.factory.presenter.message;

import com.zoulf.factory.model.db.Group;
import com.zoulf.factory.model.db.Message;
import com.zoulf.factory.model.db.User;
import com.zoulf.factory.presenter.BaseContract;

/**
 * @author Zoulf.
 */

public interface ChatContract {

  interface Presenter extends BaseContract.Presenter {

    void pushText(String content);

    void pushAudio(String path);

    // 发送图片，支持多张图片，所以用数组
    void pushPic(String[] path);

    // 重新发送一个消息，返回是否调度成功
    boolean rePush(Message message);
  }

  // 聊天界面的基类
  interface View<InitModel> extends BaseContract.RecyclerView<Presenter, Message> {

    // 初始化的model
    void onInit(InitModel model);
  }

  // 人聊天的界面
  interface UserView extends View<User> {

  }

  // 群聊天的界面
  interface GroupView extends View<Group> {

  }

}
