package com.zoulf.factory.presenter.message;

import com.zoulf.factory.model.db.Session;
import com.zoulf.factory.presenter.BaseContract;

/**
 * @author Zoulf.
 */

public interface SessionContract {

  interface Presenter extends BaseContract.Presenter{

  }

  interface View extends BaseContract.RecyclerView<Presenter, Session> {

  }
}
