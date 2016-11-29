package com.chinalooke.yuwan.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 控制拦截viewPage
 * Created by xiao on 2016/11/29.
 */

public class WodeCircleViewPage extends ViewPager {

    private float downX;
    private float downY;
    private float moveX;
    private float moveY;

    public WodeCircleViewPage(Context context) {
        super(context);
    }

    public WodeCircleViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = ev.getX();
                moveY = ev.getY();
                float dx = moveX - downX;
                float dy = moveY - downY;
                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx > 0) {
                        if (getCurrentItem() == 0) {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        } else {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    } else {
                        if (getCurrentItem() == 0) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
