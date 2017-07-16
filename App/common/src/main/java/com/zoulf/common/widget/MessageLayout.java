package com.zoulf.common.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @author Zoulf.
 */

public class MessageLayout extends LinearLayout {

  public MessageLayout(Context context) {
    super(context);
  }

  public MessageLayout(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public MessageLayout(Context context,
      @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(VERSION_CODES.LOLLIPOP)
  public MessageLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  protected boolean fitSystemWindows(Rect insets) {
    if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
      insets.left = 0;
      insets.top = 0;
      insets.right = 0;
    }
    return super.fitSystemWindows(insets);
  }
}
