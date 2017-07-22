package com.zoulf.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zoulf.factory.Factory;
import com.zoulf.factory.model.api.RspModel;
import com.zoulf.factory.model.api.message.MsgCreateModel;
import com.zoulf.factory.model.card.MessageCard;
import com.zoulf.factory.model.db.Message;
import com.zoulf.factory.model.db.Message_Table;
import com.zoulf.factory.net.NetWork;
import com.zoulf.factory.net.RemoteService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 消息工具类
 *
 * @author Zoulf
 * @version 1.0.0
 */
public class MessageHelper {

  // 从本地找消息
  public static Message findFromLocal(String id) {
    return SQLite.select()
        .from(Message.class)
        .where(Message_Table.id.eq(id))
        .querySingle();
  }

  // 发送是异步进行的
  public static void push(final MsgCreateModel model) {
    Factory.runOnAsync(new Runnable() {
      @Override
      public void run() {
        // 如果是一个已经发送过的消息，则不能重新发送
        // 正在发送状态：如果一个消息正在发送，则不能重新发送
        Message message = findFromLocal(model.getId());
        if (message != null && message.getStatus() != Message.STATUS_FAILED) {
          return;
        }

        // 如果是文件类型的（语音，图片，文件），需要先上传后才发送

        // 我们在发送的时候，需要通知界面更新状态，Card
        final MessageCard card = model.buildCard();
        Factory.getMessageCenter().dispatch(card);

        // 正常情况下的消息（文字），直接发送，进行网络调度
        RemoteService service = NetWork.remote();
        service.msgPush(model).enqueue(new Callback<RspModel<MessageCard>>() {
          @Override
          public void onResponse(Call<RspModel<MessageCard>> call,
              Response<RspModel<MessageCard>> response) {
            RspModel<MessageCard> rspModel = response.body();
            if (rspModel != null && rspModel.success()) {
              MessageCard rspCard = rspModel.getResult();
              if (rspCard != null) {
                // 成功，调度
                Factory.getMessageCenter().dispatch(rspCard);
              }
            } else {
              // 解析是否是账户异常
              Factory.decodeRspCode(rspModel, null);
              onFailure(call, null);
            }
          }

          @Override
          public void onFailure(Call<RspModel<MessageCard>> call, Throwable t) {
            // 通知信息发送失败的状态并重新发送一次
            card.setStatus(Message.STATUS_FAILED);
            Factory.getMessageCenter().dispatch(card);
          }
        });
      }
    });
    return;
  }
}
