package com.punuo.sys.net;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.punuo.sys.net.clusterutil.clustering.ClusterManager;
import com.punuo.sys.net.datepicker.CustomDatePicker;
import com.punuo.sys.net.datepicker.DateFormatUtils;
import com.punuo.sys.net.push.model.GetHistoryTrackModel;
import com.punuo.sys.net.push.model.GetStationsModel;
import com.punuo.sys.net.push.request.GetHistoryTrackRequest;
import com.punuo.sys.net.push.request.GetStationsRequest;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import top.androidman.SuperButton;

import static org.greenrobot.eventbus.EventBus.TAG;

public class HistoryActivity extends Activity implements View.OnClickListener {
    public LocationClient mLocationClient;

    private TextView positionText;
    private CustomDatePicker mTimerPicker;
    private CustomDatePicker customDatePicker1;
    private MapView mapView;
    private SuperButton superButton;
    private SuperButton historyButton;
    private BaiduMap baiduMap;
    private ClusterManager<MyItem> mClusterManager;
    private boolean isFirstLocate = true;
    private List<MyItem> items = new ArrayList<MyItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        initTimerPicker();
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_history);
        mapView = (MapView) findViewById(R.id.bmapView);
        superButton = findViewById(R.id.r_button);
        historyButton = findViewById(R.id.set_button);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimerPicker.show("2018-10-17 18:00");
            }
        });
        superButton.setOnClickListener(this);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        BaiduMap.OnMapLongClickListener listener = new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                showAboutDialog(latLng);
            }
        };
        baiduMap.setOnMapLongClickListener(listener);
        Log.i(TAG, "成功1");mClusterManager = new ClusterManager<MyItem>(this, baiduMap);
        BaiduMap.OnMapStatusChangeListener onMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {

            }
        };
        baiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
        /*positionText = (TextView) findViewById(R.id.position_text_view);*/
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(HistoryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(HistoryActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(HistoryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(HistoryActivity.this, permissions, 1);
        } else {
            requestLocation();
        }
    }

    private void initTimerPicker() {
        String beginTime = "2021-1-1 18:00";
        String endTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);

        //mTvSelectedTime.setText(endTime);

        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp, String string) {
                if (string == null) string = "RSRP";
                //mTvSelectedTime.setText(DateFormatUtils.long2Str(timestamp, true));
                Log.i("nono", DateFormatUtils.long2Str(timestamp, true) + "和" + string);
                getHistoryTrackRequest(DateFormatUtils.long2Str(timestamp, true), string);
            }
        }, beginTime, endTime);
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true);
        // 显示时和分
        mTimerPicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        mTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true);
    }



    private void addMarkers() {

    }
    public void showAboutDialog(LatLng latLng){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText("当前经纬度");
        title.setPadding(10,10,10,10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);
        title.setTextColor(getResources().getColor(R.color.common_title_bg));
        dialog.setCustomTitle(title);
        dialog.setMessage("当前所在地经度是" + latLng.latitude+"\n"+"纬度是"+latLng.longitude);
        dialog.setPositiveButton("显示附近基站", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "onClick: 点击");
                getStationsLocation(latLng.latitude,latLng.longitude);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private GetHistoryTrackRequest getHistoryTrackRequest;
    public void getHistoryTrackRequest(String time, String theme){
        if (getHistoryTrackRequest != null && !getHistoryTrackRequest.isFinished) return;

        getHistoryTrackRequest = new GetHistoryTrackRequest();


        if (theme.equals("RSRP")){
            getHistoryTrackRequest.addUrlParam("Theme", "RSRP_UL");
        } else if (theme.equals("SINR")){
            getHistoryTrackRequest.addUrlParam("Theme", "SINR_UL");
        }else if (theme.equals("UL")){
            getHistoryTrackRequest.addUrlParam("Theme", "THROUGHPUT_UL");
        }
        else if (theme.equals("DL")){
            getHistoryTrackRequest.addUrlParam("Theme", "THROUGHPUT_DL");
        }else {
            getHistoryTrackRequest.addUrlParam("Theme", "PCI");
        }
        getHistoryTrackRequest.addUrlParam("Time", time);

        getHistoryTrackRequest.setRequestListener(new RequestListener<GetHistoryTrackModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(GetHistoryTrackModel result) {
                if (result == null) return;
                Log.i("Result", "????????");
                //构建折线点坐标
                List<LatLng> points = new ArrayList<LatLng>();
                /*points.add(new LatLng(39.965,116.404));
                points.add(new LatLng(39.925,116.454));
                points.add(new LatLng(39.955,116.494));
                points.add(new LatLng(39.905,116.554));
                points.add(new LatLng(39.965,116.604));

                List<Integer> colors = new ArrayList<>();
                colors.add(Integer.valueOf(Color.BLUE));
                colors.add(Integer.valueOf(Color.RED));
                colors.add(Integer.valueOf(Color.YELLOW));
                colors.add(Integer.valueOf(Color.GREEN));

//设置折线的属性
                OverlayOptions mOverlayOptions = new PolylineOptions()
                        .width(10)
                        .color(0xAAFF0000)
                        .points(points)
                        .colorsValues(colors);//设置每段折线的颜色

//在地图上绘制折线
//mPloyline 折线对象
                Overlay mPolyline = baiduMap.addOverlay(mOverlayOptions);*/
                for (int i = 0; i < result.locationsList.size(); i++) {
                    LatLng point = new LatLng(result.locationsList.get(i).latitude, result.locationsList.get(i).longitude);
                    points.add(point);
                    Log.i(TAG, "onSuccess: 坐标"+ point);
                }
                List<Integer> colors = new ArrayList<>();
                for (int i = 1; i < result.locationsList.size(); i++) {
                    //colors.add(Integer.valueOf(Color.RED));
                    switch (theme){
                        case "RSRP":
                            if (result.locationsList.get(i).paraData >-95){
                                colors.add(Integer.valueOf(Color.GREEN));
                            } else if (result.locationsList.get(i).paraData > -105 && result.locationsList.get(i).paraData <-95){
                                colors.add(Integer.valueOf(Color.YELLOW));
                            } else {
                                colors.add(Integer.valueOf(Color.RED));
                            }
                            break;
                        case "SNIR":
                            if (result.locationsList.get(i).paraData > 16){
                                colors.add(Integer.valueOf(Color.GREEN));
                            } else if (result.locationsList.get(i).paraData > 3 && result.locationsList.get(i).paraData < 16){
                                colors.add(Integer.valueOf(Color.YELLOW));
                            } else {
                                colors.add(Integer.valueOf(Color.RED));
                            }
                            break;
                        case "UL":
                            if (result.locationsList.get(i).paraData > 40){
                                colors.add(Integer.valueOf(Color.GREEN));
                            } else if (result.locationsList.get(i).paraData > 20 && result.locationsList.get(i).paraData < 40){
                                colors.add(Integer.valueOf(Color.YELLOW));
                            } else {
                                colors.add(Integer.valueOf(Color.RED));
                            }
                            break;
                        case "DL":
                            if (result.locationsList.get(i).paraData > 600){
                                colors.add(Integer.valueOf(Color.GREEN));
                            } else if (result.locationsList.get(i).paraData > 400 && result.locationsList.get(i).paraData < 600){
                                colors.add(Integer.valueOf(Color.YELLOW));
                            } else {
                                colors.add(Integer.valueOf(Color.RED));
                            }
                            break;
                        default:
                            colors.add(Integer.valueOf(Color.RED));
                    }
                }
                OverlayOptions mOverlayOptions = new PolylineOptions()
                        .width(10)
                        .color(0xAAFF0000)
                        .points(points)
                        .colorsValues(colors);
                Overlay mPolyline = baiduMap.addOverlay(mOverlayOptions);
                Log.i("result", Integer.toString(result.locationsList.size()));
                ToastUtils.showToast("获取成功");
                LatLng startPoint = new LatLng(result.locationsList.get(0).latitude, result.locationsList.get(0).longitude);
                LatLng endPoint = new LatLng(result.locationsList.get(result.locationsList.size() - 1).latitude, result.locationsList.get(result.locationsList.size() - 1).longitude);
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.start);
                OverlayOptions option = new MarkerOptions()
                        .position(startPoint) //必传参数
                        .icon(bitmap).perspective(true); //必传参数
//在地图上添加Marker，并显示
                baiduMap.addOverlay(option);
                BitmapDescriptor bitmap2 = BitmapDescriptorFactory
                        .fromResource(R.drawable.end);
                OverlayOptions option2 = new MarkerOptions()
                        .position(endPoint) //必传参数
                        .icon(bitmap2).perspective(true);
//在地图上添加Marker，并显示
                baiduMap.addOverlay(option2);
                /*for (int i = 0; i <  result.locationsList.size(); i++) {
                    LatLng point = new LatLng(result.locationsList.get(i).latitude, result.locationsList.get(i).longitude);
                    items.add(new MyItem(point));
                    Log.i("ww", "onSuccess: "+i);
                    //构建Marker图标
                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_basestation);
                    //构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions()
                            .position(point)
                            .icon(bitmap);
                    //在地图上添加Marker，并显示
                    baiduMap.addOverlay(option);
                }*/
            }

            @Override
            public void onError(Exception e) {
                Log.i("错误是：", e.getMessage());
                ToastUtils.showToast("获取失败");
            }
        });
        HttpManager.addRequest(getHistoryTrackRequest);
    }


    private GetStationsRequest getStationsRequest;
    public void getStationsLocation (double latitude, double
            longitude){
        if (getStationsRequest != null && !getStationsRequest.isFinished){
            return;
        }
        getStationsRequest = new GetStationsRequest();
        getStationsRequest.addUrlParam("longitude",longitude);
        getStationsRequest.addUrlParam("latitude",latitude);

        getStationsRequest.setRequestListener(new RequestListener<GetStationsModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(GetStationsModel result) {
                if (result == null) {
                    return;
                }
                for (int i = 0; i <  result.stationsList.size(); i++) {
                    LatLng point = new LatLng(result.stationsList.get(i).latitude, result.stationsList.get(i).longitude);
                    items.add(new MyItem(point));
                    Log.i("ww", "onSuccess: "+i);
                    //构建Marker图标
                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_basestation);
                    //构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions()
                            .position(point)
                            .icon(bitmap);
                    //在地图上添加Marker，并显示
                    baiduMap.addOverlay(option);
                }
                ToastUtils.showToast("获取成功");
//                mClusterManager.addItems(items);
//                LatLng point = new LatLng(result.stations.latitude, result.stations.longitude);
//                //构建Marker图标
//                BitmapDescriptor bitmap = BitmapDescriptorFactory
//                        .fromResource(R.drawable.ic_basestation);
//                //构建MarkerOption，用于在地图上添加Marker
//                OverlayOptions option = new MarkerOptions()
//                        .position(point)
//                        .icon(bitmap);
//                //在地图上添加Marker，并显示
//                baiduMap.addOverlay(option);
            }

            @Override
            public void onError(Exception e) {
                Log.i(TAG, "onError:");
                ToastUtils.showToast("获取失败");
            }
        });
        HttpManager.addRequest(getStationsRequest);
    }

    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            ToastUtils.showToast("当前位置" + location.getAddrStr());
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.
                Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        //option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//如果不设置则默认gcj02 从而导致偏差
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(HistoryActivity.this,ChooseActivity.class);
        startActivity(intent);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(location);
            }
        }

    }
}