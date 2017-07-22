package com.zoulf.common.app;

import android.content.Context;
import android.support.annotation.StringRes;
import com.zoulf.factory.presenter.BaseContract;

/**
 * @author Zoulf.
 */

public abstract class PresenterFragment<Presenter extends BaseContract.Presenter> extends MyFragment
    implements BaseContract.View<Presenter> {

  protected Presenter mPresenter;

  protected abstract Presenter initPresenter();

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    // 在界面onAttach之后就触发
    initPresenter();
  }

  @Override
  public void showError(@StringRes int str) {
    // 显示错误，优先使用占位布局
    if (mPlaceHolderView != null) {
      mPlaceHolderView.triggerError(str);
      return;
    } else {
      MyApplication.showToast(str);
    }
  }

  @Override
  public void showLoading() {
    if (mPlaceHolderView != null) {
      mPlaceHolderView.triggerLoading();
    }
  }

  @Override
  public void setPresenter(Presenter presenter) {
    // View中赋值Presenter
    mPresenter = presenter;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mPresenter != null) {
      mPresenter.destroy();
    }
  }
}
