package com.zoulf.factory.presenter.contract;

import com.zoulf.factory.Factory;
import com.zoulf.factory.data.helper.UserHelper;
import com.zoulf.factory.model.db.User;
import com.zoulf.factory.persistence.Account;
import com.zoulf.factory.presenter.BasePresenter;
import com.zoulf.factory.presenter.contract.PersonalContract.View;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * @author Zoulf.
 */
public class PersonalPresenter extends BasePresenter<View>
    implements PersonalContract.Presenter {

  private String id;
  private User user;

  public PersonalPresenter(PersonalContract.View view) {
    super(view);
  }

  @Override
  public void start() {
    super.start();

    // 个人界面用户数据优先从网络拉取
    Factory.runOnAsync(new Runnable() {
      @Override
      public void run() {
        PersonalContract.View view = getView();
        if (view != null) {
          String id = view.getUserId();
          User user = UserHelper.searchFirstOfNet(id);
          onLoaded(view, user);
        }
      }
    });

  }

  private void onLoaded(final PersonalContract.View view, final User user) {
    this.user = user;
    // 是否就是我自己
    final boolean isSelf = user.getId().equalsIgnoreCase(Account.getUserId());
    // 是否已经关注
    final boolean isFollow = isSelf || user.isFollow();
    // 已经关注同时不是自己才能聊天
    final boolean allowSayHello = isFollow && !isSelf;

    // 切换到Ui线程
    Run.onUiAsync(new Action() {
      @Override
      public void call() {
        view.onLoadDone(user);
        view.setFollowStatus(isFollow);
        view.allowSayHello(allowSayHello);
      }
    });
  }

  @Override
  public User getUserPersonal() {
    return user;
  }
}
