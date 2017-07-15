package com.zoulf.factory;

import android.support.annotation.StringRes;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.zoulf.common.app.MyApplication;
import com.zoulf.factory.data.DataSource;
import com.zoulf.factory.data.group.GroupCenter;
import com.zoulf.factory.data.group.GroupDispatcher;
import com.zoulf.factory.data.message.MessageCenter;
import com.zoulf.factory.data.message.MessageDispatcher;
import com.zoulf.factory.data.user.UserCenter;
import com.zoulf.factory.data.user.UserDispatcher;
import com.zoulf.factory.model.api.PushModel;
import com.zoulf.factory.model.api.PushModel.Entity;
import com.zoulf.factory.model.api.RspModel;
import com.zoulf.factory.model.card.GroupCard;
import com.zoulf.factory.model.card.GroupMemberCard;
import com.zoulf.factory.model.card.MessageCard;
import com.zoulf.factory.model.card.UserCard;
import com.zoulf.factory.persistence.Account;
import com.zoulf.factory.untils.DBFlowExclusionStrategy;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Zoulf.
 */

public class Factory {

  private static final String TAG = Factory.class.getSimpleName();
  // 单例模式
  private static final Factory instance;
  // 全局的线程池
  private final Executor executor;
  // 全局的Gson
  private final Gson gson;

  static {
    instance = new Factory();
  }

  private Factory() {
    // 新建一个4个线程的线程池
    executor = Executors.newFixedThreadPool(4);
    gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS") // 设置时间格式
        .setExclusionStrategies(new DBFlowExclusionStrategy()) // 设置一个过滤器，数据库级别的Model不进行Json转换
        .create();
  }

  /**
   * Factory 中的初始化
   */
  public static void setup() {
    // 初始化数据库
    FlowManager.init(new FlowConfig.Builder(app())
        .openDatabasesOnInit(true) // 数据库初始化的时候打开数据库
        .build());
    //持久化的数据进行初始化
    Account.load(app());
  }

  /**
   * 返回全局的Application
   *
   * @return Application
   */
  public static MyApplication app() {
    return MyApplication.getInstance();
  }

  /**
   * 异步运行的方法
   */
  public static void runOnAsync(Runnable runnable) {
    // 拿到单例，拿到线程池，然后异步执行
    instance.executor.execute(runnable);
  }

  /**
   * 返回一个全局的Gson，在这里可以进行Gson的一些全局的初始化
   *
   * @return Gson
   */
  public static Gson getGson() {
    return instance.gson;
  }

  /**
   * 进行错误Code的解析，
   * 把网络返回的Code值进行统一的规划并返回为一个String资源
   *
   * @param model RspModel
   * @param callback DataSource.FailedCallback,用于返回一个错误的资源ID
   */
  public static void decodeRspCode(RspModel model, DataSource.FailedCallback callback) {
    if (model == null) {
      return;
    }
    switch (model.getCode()) {
      case RspModel.SUCCEED:
        return;
      case RspModel.ERROR_SERVICE:
        decodeRspCode(R.string.data_rsp_error_service, callback);
        break;
      case RspModel.ERROR_NOT_FOUND_USER:
        decodeRspCode(R.string.data_rsp_error_not_found_user, callback);
        break;
      case RspModel.ERROR_NOT_FOUND_GROUP:
        decodeRspCode(R.string.data_rsp_error_not_found_group, callback);
        break;
      case RspModel.ERROR_NOT_FOUND_GROUP_MEMBER:
        decodeRspCode(R.string.data_rsp_error_not_found_group_member, callback);
        break;
      case RspModel.ERROR_CREATE_USER:
        decodeRspCode(R.string.data_rsp_error_create_user, callback);
        break;
      case RspModel.ERROR_CREATE_GROUP:
        decodeRspCode(R.string.data_rsp_error_create_group, callback);
        break;
      case RspModel.ERROR_CREATE_MESSAGE:
        decodeRspCode(R.string.data_rsp_error_create_message, callback);
        break;
      case RspModel.ERROR_PARAMETERS:
        decodeRspCode(R.string.data_rsp_error_parameters, callback);
        break;
      case RspModel.ERROR_PARAMETERS_EXIST_ACCOUNT:
        decodeRspCode(R.string.data_rsp_error_parameters_exist_account, callback);
        break;
      case RspModel.ERROR_PARAMETERS_EXIST_NAME:
        decodeRspCode(R.string.data_rsp_error_parameters_exist_name, callback);
        break;
      case RspModel.ERROR_ACCOUNT_TOKEN:
        MyApplication.showToast(R.string.data_rsp_error_account_token);
        instance.logout();
        break;
      case RspModel.ERROR_ACCOUNT_LOGIN:
        decodeRspCode(R.string.data_rsp_error_account_login, callback);
        break;
      case RspModel.ERROR_ACCOUNT_REGISTER:
        decodeRspCode(R.string.data_rsp_error_account_register, callback);
        break;
      case RspModel.ERROR_ACCOUNT_NO_PERMISSION:
        decodeRspCode(R.string.data_rsp_error_account_no_permission, callback);
        break;
      case RspModel.ERROR_UNKNOWN:
      default:
        decodeRspCode(R.string.data_rsp_error_unknown, callback);
        break;
    }
  }

  private static void decodeRspCode(@StringRes final int resId,
      final DataSource.FailedCallback callback) {
    if (callback != null) {
      callback.onDataNotAvailableLoaded(resId);
    }
  }

  /**
   * 收到账户退出的消息需要进行账户退出重新登录
   */
  private static void logout() {

  }

  /**
   * 处理推送来的消息
   *
   * @param str 消息
   */
  public static void dispatchPush(String str) {
    // 首先检查登录状态
    if (!Account.isLogin()) {
      return;
    }
    PushModel model = PushModel.decode(str);
    if (model == null) {
      return;
    }

    // 对推送集合进行遍历
    for (Entity entity : model.getEntities()) {
      Log.e(TAG, "dispatchPush-entity:" + entity.toString());
      switch (entity.type) {
        case PushModel.ENTITY_TYPE_LOGOUT:
          // 退出情况下直接返回，并且不可继续
          instance.logout();
          return;

        case PushModel.ENTITY_TYPE_MESSAGE: {
          // 普通消息
          MessageCard card = getGson().fromJson(entity.content, MessageCard.class);
          getMessageCenter().dispatch(card);
          break;
        }

        case PushModel.ENTITY_TYPE_ADD_FRIEND: {
          // 好友添加
          UserCard card = getGson().fromJson(entity.content, UserCard.class);
          getUserCenter().dispatch(card);
          break;
        }

        case PushModel.ENTITY_TYPE_ADD_GROUP: {
          // 添加群
          GroupCard card = getGson().fromJson(entity.content, GroupCard.class);
          getGroupCenter().dispatch(card);
          break;
        }

        case PushModel.ENTITY_TYPE_MODIFY_GROUP_MEMBERS:
        case PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS: {
          // 群成员变更，回来的是一个群成员的列表
          Type type = new TypeToken<List<GroupMemberCard>>() {
          }.getType();

          List<GroupMemberCard> cards = getGson().fromJson(entity.content, type);
          // 把数据集合丢到数据中心去处理
          getGroupCenter().dispatch(cards.toArray(new GroupMemberCard[0]));
          break;
        }

        case PushModel.ENTITY_TYPE_EXIT_GROUP_MEMBERS: {
          //ToDO
          // 成员退出的推送
        }


      }
    }
  }

  /**
   * 获取一个用户中心的实现类
   *
   * @return 用户中心的规范接口
   */
  public static UserCenter getUserCenter() {
    return UserDispatcher.instance();
  }

  /**
   * 获取一个消息中心的实现类
   *
   * @return 消息中心的规范接口
   */
  public static MessageCenter getMessageCenter() {
    return MessageDispatcher.instance();
  }

  /**
   * 获取一个群处理中心的实现类
   *
   * @return 群中心的规范接口
   */
  public static GroupCenter getGroupCenter() {
    return GroupDispatcher.instance();
  }
}
