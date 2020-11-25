package com.punuo.sys.sdk.view.loopholder;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.punuo.sys.sdk.R;
import com.punuo.sys.sdk.util.CommonUtil;


/**
 * Created by jliu on 2018/3/19.
 */
public class LoopIndicator extends LinearLayout implements LoopHolder.ICallback {

    private int mCurSize;

    public LoopIndicator(Context context) {
        this(context, null);
    }

    public LoopIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public void setData(int size) {
        this.removeAllViews();
        mCurSize = size;
        for (int i = 0; i < mCurSize; i++) {
            TextView view = (TextView) LayoutInflater.from(getContext()).inflate(
                    R.layout.sdk_loopmodule_indicator_layout, this, false);
            LayoutParams layoutParams = new LayoutParams(CommonUtil.dip2px(9), CommonUtil.dip2px(3));
            if (i != 0) {
                layoutParams.setMargins(CommonUtil.dip2px(5), 0, 0, 0);
            }
            view.setLayoutParams(layoutParams);
            addView(view);
        }
    }

    @Override
    public void select(int pos) {
        if (mCurSize <= 1) {
            return;
        }
        for (int i = 0; i < mCurSize; i++) {
            View view = getChildAt(i);
            if (i == pos) {
                view.setSelected(true);
            } else {
                view.setSelected(false);
            }
        }
    }

}