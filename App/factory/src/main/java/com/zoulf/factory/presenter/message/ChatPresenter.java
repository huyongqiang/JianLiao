package com.zoulf.factory.presenter.message;

import android.support.v7.util.DiffUtil;
import com.zoulf.factory.data.helper.MessageHelper;
import com.zoulf.factory.data.message.MessageDataSource;
import com.zoulf.factory.model.api.message.MsgCreateModel;
import com.zoulf.factory.model.db.Message;
import com.zoulf.factory.presenter.BaseSourcePresenter;
import com.zoulf.factory.untils.DiffUiDataCallback;
import java.util.List;

/**
 * 聊天Presenter的基础类
 *
 * @author Zoulf.
 */

public class ChatPresenter<View extends ChatContract.View>
    extends BaseSourcePresenter<Message, Message, MessageDataSource, View>
    implements ChatContract.Presenter {

  // 接收者ID，可能是群或者人的ID
  protected String mReceiverId;
  // 区分是人还是群Id
  protected int mReceiverType;


  public ChatPresenter(MessageDataSource source, View view, String receiverId, int receiverType) {
    super(source, view);
    mReceiverId = receiverId;
    mReceiverType = receiverType;
  }

  @Override
  public void pushText(String content) {
    // 构建一个新的消息
    MsgCreateModel model = new MsgCreateModel.Builder()
        .receiver(mReceiverId, mReceiverType)
        .content(content, Message.TYPE_STR)
        .build();
    // 进行网络发送
    MessageHelper.push(model);
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
    ChatContract.View view = getView();
    if (view == null) {
      return;
    }
    // 得到老的数据
    @SuppressWarnings("unchecked")
    List<Message> old = view.getRecyclerAdapter().getItems();
    // 差异计算
    DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(old, messages);
    final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
    // 进行界面刷新
    refreshData(result, messages);
  }
}
