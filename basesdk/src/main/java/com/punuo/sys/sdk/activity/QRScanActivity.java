package com.punuo.sys.sdk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.router.SDKRouter;
import com.punuo.sys.sdk.R;

import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

/**
 * Created by han.chen.
 * Date on 2019-08-15.
 **/
@Route(path = SDKRouter.ROUTER_QR_SCAN_ACTIVITY)
public class QRScanActivity extends BaseSwipeBackActivity implements QRCodeView.Delegate {
    private ZBarView mZBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scan_activity);
        mZBarView = (ZBarView) findViewById(R.id.zbarview);
        mZBarView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
        mZBarView.setType(BarcodeType.ALL, null); // 识别所有类型的码
        mZBarView.setDelegate(this);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Intent intent = new Intent();
        intent.putExtra("result", result);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        String tipText = mZBarView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZBarView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZBarView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e("QRScanActivity", "打开相机出错");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZBarView.startCamera();
        mZBarView.startSpotAndShowRect();
    }

    @Override
    protected void onStop() {
        mZBarView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZBarView.onDestroy();
        super.onDestroy();
    }
}
