package com.zoulf.jianliao.frags.message;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.OnClick;
import com.zoulf.common.app.MyFragment;
import com.zoulf.common.widget.adapter.TextWatcherAdapter;
import com.zoulf.jianliao.R;
import com.zoulf.jianliao.activities.MessageActivity;

/**
 * @author Zoulf.
 */

public abstract class ChatFragment extends MyFragment implements OnOffsetChangedListener {

  protected String mReceiverId;

  @BindView(R.id.toolbar)
  Toolbar mToolbar;

  @BindView(R.id.recycler)
  RecyclerView mRecyclerView;

  @BindView(R.id.appbar)
  AppBarLayout mAppBarLayout;

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
    } else {

    }
  }

  private void onMoreClick() {
    // Todo
  }

}
