package com.zoulf.factory.presenter.message;

import com.zoulf.factory.data.message.MessageDataSource;
import com.zoulf.factory.model.db.Message;
import com.zoulf.factory.presenter.BaseSourcePresenter;
import java.util.List;

/**
 * 聊天Presenter的基础类
 *
 * @author Zoulf.
 */

public class ChatPresenter<View extends ChatContract.View>
    extends BaseSourcePresenter<Message, Message, MessageDataSource, View>
    implements ChatContract.Presenter {

  private String mReceiverId;
  private int mReceiverType;


  public ChatPresenter(MessageDataSource source, View view, String receiverId, int receiverType) {
    super(source, view);
    mReceiverId = receiverId;
    mReceiverType = receiverType;
  }

  @Override
  public void pushText(String content) {

  }

  @Override
  public void pushAudio(String path) {

  }

  @Override
  public void pushPic(String[] path) {

  }

  @Override
  public boolean rePush(Message message) {
    return false;
  }

  @Override
  public void onDataLoaded(List<Message> messages) {

  }
}
