package com.zoulf.jianliao.frags.message;


import android.support.design.widget.AppBarLayout;
import com.zoulf.factory.model.db.Group;
import com.zoulf.factory.presenter.message.ChatContract;
import com.zoulf.factory.presenter.message.ChatContract.Presenter;
import com.zoulf.jianliao.R;

/**
 * 群聊天界面
 */
public class ChatGroupFragment extends ChatFragment<Group> implements ChatContract.GroupView {

  public ChatGroupFragment() {
    // Required empty public constructor
  }

  @Override
  protected int getContentLayoutId() {
    return R.layout.fragment_chat_group;
  }

  @Override
  protected Presenter initPresenter() {
    return null;
  }

  @Override
  public void onInit(Group group) {

  }

  @Override
  public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

  }
}
