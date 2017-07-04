package com.zoulf.factory.presenter.search;

import android.support.annotation.StringRes;
import com.zoulf.factory.data.DataSource;
import com.zoulf.factory.data.helper.UserHelper;
import com.zoulf.factory.model.card.UserCard;
import com.zoulf.factory.presenter.BasePresenter;
import com.zoulf.factory.presenter.search.SearchContract.UserView;
import java.util.List;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import retrofit2.Call;

/**
 * 搜索群的逻辑实现
 *
 * @author Zoulf.
 */
public class SearchUserPresenter extends BasePresenter<SearchContract.UserView>
    implements SearchContract.Presenter, DataSource.Callback<List<UserCard>> {

  private Call searchCall;

  public SearchUserPresenter(UserView view) {
    super(view);
  }

  @Override
  public void search(String content) {
    start();

    Call call = searchCall;
    if (call != null && !call.isCanceled()) {
      // 如果有上一次的请求，并且没有取消
      // 则调用取消请求操作
      call.cancel();
    }
    UserHelper.search(content, this);
  }

  @Override
  public void onDataLoaded(final List<UserCard> userCards) {
    // 搜索成功
    final SearchContract.UserView view = getView();
    if (view != null) {
      Run.onUiAsync(new Action() {
        @Override
        public void call() {
          view.onSearchDone(userCards);
        }
      });
    }
  }

  @Override
  public void onDataNotAvailableLoaded(@StringRes final int strRes) {
    // 搜索失败
    final SearchContract.UserView view = getView();
    if (view != null) {
      Run.onUiAsync(new Action() {
        @Override
        public void call() {
          view.showError(strRes);
        }
      });
    }
  }
}
