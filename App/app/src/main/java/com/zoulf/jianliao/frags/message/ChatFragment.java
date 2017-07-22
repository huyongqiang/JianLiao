package com.zoulf.jianliao.frags.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.zoulf.common.app.PresenterFragment;
import com.zoulf.common.widget.PortraitView;
import com.zoulf.common.widget.adapter.TextWatcherAdapter;
import com.zoulf.common.widget.recycler.RecyclerAdapter;
import com.zoulf.factory.model.db.Message;
import com.zoulf.factory.model.db.User;
import com.zoulf.factory.persistence.Account;
import com.zoulf.factory.presenter.message.ChatContract;
import com.zoulf.jianliao.R;
import com.zoulf.jianliao.activities.MessageActivity;
import java.util.Objects;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;

/**
 * @author Zoulf.
 */

public abstract class ChatFragment<InitMode>
    extends PresenterFragment<ChatContract.Presenter>
    implements OnOffsetChangedListener,
    ChatContract.View<InitMode> {

  protected String mReceiverId;
  protected Adapter mAdapter;

  @BindView(R.id.toolbar)
  Toolbar mToolbar;

  @BindView(R.id.recycler)
  RecyclerView mRecyclerView;

  @BindView(R.id.appbar)
  AppBarLayout mAppBarLayout;

  @BindView(R.id.collapsingToolbarLayout)
  CollapsingToolbarLayout mCollapsingLayout;

  @BindView(R.id.edit_content)
  EditText mContent;

  @BindView(R.id.btn_submit)
  View mSubmit;

  @Override
  protected void initArgs(Bundle bundle) {
    super.initArgs(bundle);
    mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
  }

  @Override
  protected void initWidget(View root) {
    super.initWidget(root);
    initToolbar();
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mAdapter = new Adapter();
    mRecyclerView.setAdapter(mAdapter);
  }

  @Override
  protected void initData() {
    super.initData();
    // 开始进行初始化操作
    mPresenter.start();
  }

  // 初始化Toolbar
  protected void initToolbar() {
    Toolbar toolbar = mToolbar;
    toolbar.setNavigationIcon(R.drawable.ic_back);
    toolbar.setNavigationOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        getActivity().finish();
      }
    });
  }

  // 给界面的Appbar设置一个监听，得到关闭与打开的时候的进度
  private void initAppbar() {
    mAppBarLayout.addOnOffsetChangedListener(this);
  }

  // 初始化输入框监听
  private void initEditContent() {
    mContent.addTextChangedListener(new TextWatcherAdapter() {
      @Override
      public void afterTextChanged(Editable s) {
        String content = s.toString().trim();
        boolean needSendMsg = !TextUtils.isEmpty(content);
        // 设置状态，改变对应的Icon
        mSubmit.setActivated(needSendMsg);
      }
    });
  }

  @OnClick(R.id.btn_face)
  void onFaceClick() {
// Todo
  }

  @OnClick(R.id.btn_record)
  void onRecordClick() {
// Todo
  }

  @OnClick(R.id.btn_submit)
  void onSubmitClick() {
    if (mSubmit.isActivated()) {
      // 发送
      String content = mContent.getText().toString();
      mContent.setText("");
      mPresenter.pushText(content);
    } else {

    }
  }

  private void onMoreClick() {
    // Todo
  }

  @Override
  public RecyclerAdapter<Message> getRecyclerAdapter() {
    return mAdapter;
  }

  @Override
  public void onAdapterDataChanged() {
    // 界面没有占位布局，RecyclerView是一直显示的，所以不需要做任何操作
  }

  // 内容的适配器
  private class Adapter extends RecyclerAdapter<Message> {

    @Override
    protected int getItemViewType(int position, Message message) {

      // 这里不需要由于懒加载而load的原因是，懒加载是能够创建主键的，但是其他内容为空
      // 判断发送者的id（是否是我自己）来决定是使用左边的布局还是右边的布局
      boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());

      switch (message.getType()) {
        // 文字
        case Message.TYPE_STR:
          return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;

        // 语音
        case Message.TYPE_AUDIO:
          return isRight ? R.layout.cell_chat_audio_right : R.layout.cell_chat_audio_left;

        // 图片
        case Message.TYPE_PIC:
          return isRight ? R.layout.cell_chat_pic_right : R.layout.cell_chat_pic_left;

        // 文档
        default:
          return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
      }
    }

    @Override
    protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
      switch (viewType) {
        // 因为无论左边还是右边，里面的控件布局都是一样的
        case R.layout.cell_chat_text_right:
        case R.layout.cell_chat_text_left:
          return new TextHolder(root);

        case R.layout.cell_chat_audio_right:
        case R.layout.cell_chat_audio_left:
          return new AudioHolder(root);

        case R.layout.cell_chat_pic_right:
        case R.layout.cell_chat_pic_left:
          return new PicHolder(root);

        // 默认情况下，返回Text类型的holder
        default:
          return new TextHolder(root);
      }
    }
  }

  // Holder的基类
  // BaseHolder不设为private的原因是里面需要进行一个ButterKnife的View的注入，设置为private会导致无法注入的问题
  class BaseHolder extends RecyclerAdapter.ViewHolder<Message> {

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    // 允许为空，左边（联系人发来的）没有，右边（你发出去的）有
    @Nullable
    @BindView(R.id.loading)
    Loading mLoading;

    public BaseHolder(View itemView) {
      super(itemView);
    }

    @Override
    protected void onBind(Message message) {
      User sender = message.getSender();
      // 进行User数据加载，因为加载Message时是懒加载，所以getSender中并没有User
      sender.load();
      // 头像加载
      mPortrait.setup(Glide.with(ChatFragment.this), sender);

      if (mLoading != null) {
        // 当前布局在右边
        int status = message.getStatus();
        if (status == Message.STATUS_DONE) {
          // 正常状态，即发送成功，则隐藏Loading
          mLoading.stop();
          mLoading.setVisibility(View.GONE);
        } else if (status == Message.STATUS_CREATED) {
          // 正在发送中的状态
          mLoading.setVisibility(View.VISIBLE);
          mLoading.setProgress(0);
          mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
          mLoading.start();
        } else if (status == Message.STATUS_FAILED) {
          // 发送失败，允许重新发送
          mLoading.setVisibility(View.VISIBLE);
          mLoading.stop();
          mLoading.setProgress(1);
          mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.alertImportant));
        }

        // 当状态时错误时才允许点击头像
        mPortrait.setEnabled(status == Message.STATUS_FAILED);
      }
    }

    @OnClick(R.id.im_portrait)
    void onRePushClick() {
      // 重新发送，只有你发出去的能重新发送，通过Loading来判断是谁发的
      if (mLoading != null && mPresenter.rePush(mData)) {
        // 必须是右边才可能需要重新发送
        // 状态改变需要重新刷新界面当前的信息
        updateData(mData);
      }
    }
  }

  // 文字的Holder
  class TextHolder extends BaseHolder {

    @BindView(R.id.txt_content)
    TextView mContent;

    public TextHolder(View itemView) {
      super(itemView);
    }

    @Override
    protected void onBind(Message message) {
      super.onBind(message);

      // 把内容设置到布局上
      mContent.setText(message.getContent());
    }
  }

  // 语音的Holder
  class AudioHolder extends BaseHolder {

    @BindView(R.id.txt_content)
    TextView mContent;

    public AudioHolder(View itemView) {
      super(itemView);
    }

    @Override
    protected void onBind(Message message) {
      super.onBind(message);

      // 把内容设置到布局上
      mContent.setText(message.getContent());
    }
  }

  // 图片的Holder
  class PicHolder extends BaseHolder {

    @BindView(R.id.txt_content)
    TextView mContent;

    public PicHolder(View itemView) {
      super(itemView);
    }

    @Override
    protected void onBind(Message message) {
      super.onBind(message);

      // 把内容设置到布局上
      mContent.setText(message.getContent());
    }
  }

}
