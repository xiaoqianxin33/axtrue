package com.chinalooke.yuwan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by xiao on 2016/9/21.
 */

public class GrapeGridview extends GridView {
    public GrapeGridview(Context context) {
        super(context);
    }

    public GrapeGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GrapeGridview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //通过重新dispatchTouchEvent方法来禁止滑动
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;//禁止Gridview进行滑动
        }
        return super.dispatchTouchEvent(ev);
    }
}
