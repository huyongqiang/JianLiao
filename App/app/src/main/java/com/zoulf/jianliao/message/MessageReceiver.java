package com.zoulf.jianliao.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.igexin.sdk.PushConsts;
import com.zoulf.factory.Factory;
import com.zoulf.factory.data.helper.AccountHelper;
import com.zoulf.factory.persistence.Account;

/**
 * 个推消息接收器
 *
 * @author Zoulf.
 */

public class MessageReceiver extends BroadcastReceiver {

  private static final String TAG = MessageReceiver.class.getName();

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent == null) {
      return;
    }

    Bundle bundle = intent.getExtras();
    switch (bundle.getInt(PushConsts.CMD_ACTION)) {
      case PushConsts.GET_CLIENTID: {
        Log.i(TAG, "onReceive: GET_CLIENTID:" + bundle.toString());
        // 当Id初始化的时候
        // 获取设备Id
        onClientInit(bundle.getString("clientid"));
        break;
      }
      case PushConsts.GET_MSG_DATA: {
        // 常规消息送达
        byte[] payload = bundle.getByteArray("payload");
        if (payload != null) {
          String message = new String(payload);
          onMessageArrived(message);
          Log.i(TAG, "onReceive: GET_MES_DATA:" + message);
        }
        break;
      }
      default:
        Log.i(TAG, "onReceive: OTHER:" + bundle.toString());
        break;
    }
  }

  /**
   * 当Id初始化的时候
   *
   * @param cid 设备id
   */
  private void onClientInit(String cid) {
    // 设置设备Id
    Account.setPushId(cid);
    if (Account.isLogin()) {
      // 账户登录状态，进行一次PushId绑定
      // 没有登录时不能绑定PushId
      AccountHelper.bindPush(null);
    }
  }

  /**
   * 消息达到时
   *
   * @param message 新消息
   */
  private void onMessageArrived(String message) {
    // 交给Factory处理
    Factory.dispatchPush(message);
  }
}
