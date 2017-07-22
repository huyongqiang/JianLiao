package com.zoulf.jianliao.frags.message;


import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.zoulf.common.widget.PortraitView;
import com.zoulf.factory.model.db.User;
import com.zoulf.factory.presenter.message.ChatContract;
import com.zoulf.factory.presenter.message.ChatContract.Presenter;
import com.zoulf.factory.presenter.message.ChatUserPresenter;
import com.zoulf.jianliao.R;
import com.zoulf.jianliao.activities.PersonalActivity;

/**
 * 用户聊天界面
 */
public class ChatUserFragment extends ChatFragment<User>
    implements ChatContract.UserView {

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
  protected void initWidget(View root) {
    super.initWidget(root);
    // 这个是聊天时上拉图片的加载，之所以不直接设置图片是因为直接设置图片会导致图片有一个压扁的状态，用Glide可以保持当前比例
    Glide.with(this)
        .load(R.drawable.default_banner_personal)
        .centerCrop()
        .into(new ViewTarget<CollapsingToolbarLayout, GlideDrawable>(mCollapsingLayout) {
          @Override
          public void onResourceReady(GlideDrawable resource,
              GlideAnimation<? super GlideDrawable> glideAnimation) {
            // 设置资源
            this.view.setContentScrim(resource.getCurrent());
          }
        });
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

  @Override
  protected Presenter initPresenter() {
    // 初始化Presenter
    return new ChatUserPresenter(this, mReceiverId);
  }

  @Override
  public void onInit(User user) {
    // 对和你聊天的朋友的信息进行初始化操作
    mPortraitView.setup(Glide.with(this), user.getPortrait());
    mCollapsingLayout.setTitle(user.getName());
  }
}
