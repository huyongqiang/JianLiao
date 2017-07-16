package com.zoulf.jianliao.frags.message;


import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import com.zoulf.common.widget.PortraitView;
import com.zoulf.jianliao.R;
import com.zoulf.jianliao.activities.PersonalActivity;

/**
 * 用户聊天界面
 */
public class ChatUserFragment extends ChatFragment {

  @BindView(R.id.im_portrait)
  PortraitView mPortraitView;

  private MenuItem mUserInfoMenuItem;

  public ChatUserFragment() {
    // Required empty public constructor
  }

  @Override
  protected int getContentLayoutId() {
    return R.layout.fragment_chat_user;
  }

  @Override
  protected void initToolbar() {
    super.initToolbar();
    Toolbar toolbar = mToolbar;
    toolbar.inflateMenu(R.menu.chat_user);
    toolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_person) {
          onPortraitClick();
        }
        return false;
      }
    });

    // 拿到菜单Icon
    mUserInfoMenuItem = toolbar.getMenu().findItem(R.id.action_person);
  }

  // 进行高度的综合运算，透明我们的头像和Icon
  @Override
  public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
    View view = mPortraitView;
    MenuItem menuItem = mUserInfoMenuItem;
    if (view == null || menuItem == null) {
      return;
    }
    if (verticalOffset == 0) {
      // 完全展开
      view.setVisibility(View.VISIBLE);
      view.setScaleX(1);
      view.setScaleY(1);
      view.setAlpha(1);

      // 隐藏菜单
      menuItem.setVisible(false);
      menuItem.getIcon().setAlpha(0);
    } else {
      // 转换为正向的
      verticalOffset = Math.abs(verticalOffset);
      final int totalScrollRange = appBarLayout.getTotalScrollRange();
      if (verticalOffset >= totalScrollRange) {
        // 关闭状态
        view.setVisibility(View.INVISIBLE);
        view.setScaleX(0);
        view.setScaleY(0);
        view.setAlpha(0);

        // 显示菜单
        menuItem.setVisible(true);
        menuItem.getIcon().setAlpha(255);
      } else {
        float progress = 1 - verticalOffset / (float) totalScrollRange;
        view.setVisibility(View.VISIBLE);
        view.setScaleX(progress);
        view.setScaleY(progress);
        view.setAlpha(progress);

        // 和头像恰好相反
        menuItem.setVisible(true);
        menuItem.getIcon().setAlpha(255 - (int) (255 * progress));

      }
    }
  }

  @OnClick(R.id.im_portrait)
  void onPortraitClick() {
    PersonalActivity.show(getContext(), mReceiverId);
  }
}
