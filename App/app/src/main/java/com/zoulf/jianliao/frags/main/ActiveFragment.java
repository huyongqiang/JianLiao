package com.zoulf.jianliao.frags.main;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import com.bumptech.glide.Glide;
import com.zoulf.common.app.PresenterFragment;
import com.zoulf.common.widget.EmptyView;
import com.zoulf.common.widget.PortraitView;
import com.zoulf.common.widget.recycler.RecyclerAdapter;
import com.zoulf.common.widget.recycler.RecyclerAdapter.AdapterListenerImpl;
import com.zoulf.common.widget.recycler.RecyclerAdapter.ViewHolder;
import com.zoulf.factory.model.db.Session;
import com.zoulf.factory.presenter.message.SessionContract;
import com.zoulf.factory.presenter.message.SessionContract.Presenter;
import com.zoulf.jianliao.R;
import com.zoulf.jianliao.activities.MessageActivity;
import com.zoulf.utils.DateTimeUtil;

/**
 * @author Zoulf.
 */
public class ActiveFragment extends PresenterFragment<SessionContract.Presenter>
    implements SessionContract.View {

  @BindView(R.id.empty)
  EmptyView mEmptyView;

  @BindView(R.id.recycler)
  RecyclerView mRecycler;

  // 适配器，User，可以直接从数据库查询数据
  private RecyclerAdapter<Session> mAdapter;

  public ActiveFragment() {
    // Required empty public constructor
  }

  @Override
  protected int getContentLayoutId() {
    return R.layout.fragment_active;
  }

  @Override
  protected void initWidget(View root) {
    super.initWidget(root);
    // 初始化Recycler
    mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    mRecycler.setAdapter(mAdapter = new RecyclerAdapter<Session>() {
      @Override
      protected int getItemViewType(int position, Session session) {
        // 返回cell的布局id
        return R.layout.cell_chat_list;
      }

      @Override
      protected ViewHolder<Session> onCreateViewHolder(View root, int viewType) {
        return new ActiveFragment.MyViewHolder(root);
      }
    });

    // 点击事件监听
    mAdapter.setListener(new AdapterListenerImpl<Session>() {
      @Override
      public void onItemClick(ViewHolder holder, Session session) {
        // 跳转到聊天界面
        MessageActivity.show(getContext(), session);
      }
    });

    // 初始化占位布局
    mEmptyView.bind(mRecycler);
    setPlaceHolderView(mEmptyView);
  }

  @Override
  protected void onFirstInit() {
    super.onFirstInit();
    // 进行一次数据加载
    mPresenter.start();
  }

  @Override
  protected Presenter initPresenter() {
    return null;
  }

  @Override
  public RecyclerAdapter<Session> getRecyclerAdapter() {
    return mAdapter;
  }

  @Override
  public void onAdapterDataChanged() {
    mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
  }

  // 界面数据渲染
  class MyViewHolder extends RecyclerAdapter.ViewHolder<Session> {

    @BindView(R.id.im_portrait)
    PortraitView mPortraitView;

    @BindView(R.id.txt_name)
    TextView mName;

    @BindView(R.id.txt_content)
    TextView mContent;

    @BindView(R.id.txt_time)
    TextView mTime;

    public MyViewHolder(View itemView) {
      super(itemView);
    }

    @Override
    protected void onBind(Session session) {
      mPortraitView.setup(Glide.with(ActiveFragment.this), session.getPicture());
      mName.setText(session.getTitle());
      mContent.setText(TextUtils.isEmpty(session.getContent()) ? "" : session.getContent());
      mTime.setText(DateTimeUtil.getSimpleDate(session.getModifyAt()));
    }
  }
}
