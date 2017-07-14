package com.zoulf.factory.presenter;

import com.zoulf.factory.data.DataSource;
import com.zoulf.factory.data.DbDataSource;
import java.util.List;

/**
 * 基础的仓库源的Presenter定义
 *
 * @author Zoulf.
 */

public abstract class BaseSourcePresenter<Data, ViewModel, Source extends DbDataSource<Data>, View extends BaseContract.RecyclerView>
    extends BaseRecyclerPresenter<ViewModel, View>
    implements DataSource.SucceedCallback<List<Data>> {

  protected Source mSource;

  public BaseSourcePresenter(Source source, View view) {
    super(view);
    mSource = source;
  }

  @Override
  public void start() {
    super.start();
    if (mSource != null) {
      mSource.load(this);
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    mSource.dispose();
    mSource = null;
  }
}
