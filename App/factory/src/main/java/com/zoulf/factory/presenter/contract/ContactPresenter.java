package com.zoulf.factory.presenter.contract;

import android.support.v7.util.DiffUtil;
import com.zoulf.common.widget.recycler.RecyclerAdapter;
import com.zoulf.factory.data.DataSource;
import com.zoulf.factory.data.helper.UserHelper;
import com.zoulf.factory.data.user.ContactDataSource;
import com.zoulf.factory.data.user.ContactRepository;
import com.zoulf.factory.model.db.User;
import com.zoulf.factory.presenter.BaseSourcePresenter;
import com.zoulf.factory.untils.DiffUiDataCallback;
import java.util.List;

/**
 * 联系人的Presenter实现
 *
 * @author Zoulf.
 */

public class ContactPresenter extends
    BaseSourcePresenter<User, User, ContactDataSource, ContactContract.View>
    implements ContactContract.Presenter,
    DataSource.SucceedCallback<List<User>> {


  public ContactPresenter(ContactContract.View view) {
    // 初始化数据仓库
    super(new ContactRepository(), view);
  }

  @Override
  public void start() {
    super.start();

    // 加载网络数据
    UserHelper.refreshContacts();
  }

  // 运行到这里时是子线程
  @Override
  public void onDataLoaded(List<User> users) {
    // 无论怎么操作，数据变更，最终都会通知到这里来
    final ContactContract.View view = getView();
    if (view == null) {
      return;
    }
    RecyclerAdapter<User> adapter = view.getRecyclerAdapter();
    List<User> old = adapter.getItems();

    // 进行数据对比
    DiffUtil.Callback callback = new DiffUiDataCallback<>(old, users);
    DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

    refreshData(result, users);
  }

  @Override
  public void destroy() {
    super.destroy();
    // 当界面销毁的时候，我们应该把数据监听进行销毁
    mSource.dispose();
  }
}
