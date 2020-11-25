package com.punuo.sys.sdk.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.punuo.sys.sdk.R;


public class PNLoadingDialog extends Dialog {
    private String mLoadingMsg = null;
    private TextView tvLoading;
    private View mCancel;
    private int btnVisibility = View.GONE;
    private View.OnClickListener mOnClickListener;

    public PNLoadingDialog(Context context) {
        this(context, R.style.LoadingViewDialog);
    }

    public PNLoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_view_dialog);
        tvLoading = findViewById(R.id.text_loading);
        mCancel = findViewById(R.id.btn_cancel);
        if (TextUtils.isEmpty(mLoadingMsg)) {
            tvLoading.setVisibility(View.GONE);
        } else {
            tvLoading.setVisibility(View.VISIBLE);
            tvLoading.setText(mLoadingMsg);
        }
        mCancel.setVisibility(btnVisibility);
        mCancel.setOnClickListener(mOnClickListener);
    }

    public void setLoadingMsg(String loadingMsg) {
        this.mLoadingMsg = loadingMsg;
        if (tvLoading != null && !TextUtils.isEmpty(loadingMsg)) {
            tvLoading.setText(mLoadingMsg);
        }
    }

    public void setBtnVisibility(int visibility) {
        btnVisibility = visibility;
        if (mCancel != null) {
            mCancel.setVisibility(visibility);
        }
    }

    public void setOnBtnClick(View.OnClickListener clickListener) {
        mOnClickListener = clickListener;
        if (mCancel != null) {
            mCancel.setOnClickListener(clickListener);
        }
    }
}
