package com.zoulf.factory.presenter.message;

import com.zoulf.factory.data.helper.UserHelper;
import com.zoulf.factory.data.message.MessageRepository;
import com.zoulf.factory.model.db.Message;
import com.zoulf.factory.model.db.User;
import com.zoulf.factory.presenter.message.ChatContract.UserView;

/**
 * @author Zoulf.
 */

public class ChatUserPresenter extends ChatPresenter<ChatContract.UserView> implements
    ChatContract.Presenter {

  public ChatUserPresenter(UserView view, String receiverId) {
    // 数据源，View，接收者，接收者的类型
    super(new MessageRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_NONE);
  }

  @Override
  public void start() {
    super.start();

    // 从本地拿这个人的信息
    User receiver = UserHelper.findFromLocal(mReceiverId);
    getView().onInit(receiver);
  }
}
