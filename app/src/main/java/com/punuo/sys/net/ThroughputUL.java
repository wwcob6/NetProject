package com.punuo.sys.net;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.CellInfoNr;
import android.telephony.CellSignalStrengthNr;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.punuo.sys.net.push.GetStationLocationRequest;
import com.punuo.sys.net.push.ProcessTasks;
import com.punuo.sys.net.push.getstationlocationmodel;
import com.punuo.sys.sdk.PnApplication;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;

import java.util.ArrayList;
import java.util.List;


public class ThroughputUL extends Activity implements SensorEventListener {

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    MapView mMapView;
    BaiduMap mBaiduMap;

    private TextView info;
    private RelativeLayout progressBarRl;
    private TextView position;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    float mCurrentZoom = 18f;//默认地图缩放比例值

    private SensorManager mSensorManager;


    List<LatLng> points = new ArrayList<LatLng>();//位置点集合
    List<LatLng> points1 = new ArrayList<LatLng>();
    List<LatLng> points2 = new ArrayList<LatLng>();
    List<LatLng> points3 = new ArrayList<LatLng>();
    List<LatLng> points4 = new ArrayList<LatLng>();
    List<LatLng> points5 = new ArrayList<LatLng>();
    List<LatLng> points6=new ArrayList<>();
    Polyline mPolyline;//运动轨迹图层
    LatLng last = new LatLng(0, 0);//上一个定位点
    MapStatus.Builder builder;


    private static long lastTotalRxBytes = 0;
    private static long lastTotalTxBytes = 0;
    private static long lastTime=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ul);
        ProcessTasks.commonLaunchTasks(PnApplication.getInstance());
        initView();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// 获取传感器管理服务

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.FOLLOWING, true, null));

        /**
         * 添加地图缩放状态变化监听，当手动放大或缩小地图时，拿到缩放后的比例，然后获取到下次定位，
         *  给地图重新设置缩放比例，否则地图会重新回到默认的mCurrentZoom缩放比例
         */
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {
                mCurrentZoom = arg0.zoom;
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {
                // TODO Auto-generated method stub

            }
        });

        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);//只用gps定位，需要在室外定位。
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    private void initView() {

        Button start = (Button) findViewById(R.id.buttonStart);
        Button finish = (Button) findViewById(R.id.buttonFinish);
        Button getstation = (Button) findViewById(R.id.buttongetstation);
        info = (TextView) findViewById(R.id.info);
        progressBarRl = (RelativeLayout) findViewById(R.id.progressBarRl);
        position = findViewById(R.id.LatandLong);

        getstation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(ThroughputUL.this);
                new AlertDialog.Builder(ThroughputUL.this).setTitle("请输入基站ID").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getStationLocation(editText.getText().toString());
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                }).show();

            }
        });

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mLocClient != null && !mLocClient.isStarted()) {
                    mLocClient.start();
                    progressBarRl.setVisibility(View.VISIBLE);
                    info.setText("GPS信号搜索中，请稍后...");
                    mBaiduMap.clear();

                }
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mLocClient != null && mLocClient.isStarted()) {
                    mLocClient.stop();

                    progressBarRl.setVisibility(View.GONE);

                    if (isFirstLoc) {
                        points.clear();
                        last = new LatLng(0, 0);
                        return;
                    }

                    //复位
                    points.clear();
                    last = new LatLng(0, 0);
                    isFirstLoc = true;

                }
            }
        });

    }

    double lastX;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];

        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;

            if (isFirstLoc) {
                lastX = x;
                return;
            }

            locData = new MyLocationData.Builder().accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {

            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder().accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            LatLng llfirst = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(llfirst).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            //注意这里只接受gps点，需要在室外定位。
            if (location.getLocType() == BDLocation.TypeGpsLocation) {

                info.setText("GPS信号弱，请稍后...");

                if (isFirstLoc) {//首次定位
                    //第一个点很重要，决定了轨迹的效果，gps刚开始返回的一些点精度不高，尽量选一个精度相对较高的起始点

                    LatLng ll = null;

                    ll = getMostAccuracyLocation(location);
                    if (ll == null) {
                        return;
                    }
                    isFirstLoc = false;
                    points.add(ll);//加入集合
                    last = ll;

                    //显示当前定位点，缩放地图
                    locateAndZoom(location, ll);
                    progressBarRl.setVisibility(View.GONE);

                    return;//画轨迹最少得2个点，首地定位到这里就可以返回了
                }

                //从第二个点开始
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                //sdk回调gps位置的频率是1秒1个，位置点太近动态画在图上不是很明显，可以设置点之间距离大于为5米才添加到集合中
                if (DistanceUtil.getDistance(last, ll) < 3) {
                    return;
                }
                points.add(ll);//如果要运动完成后画整个轨迹，位置点都在这个集合中
                last = ll;

                //显示当前定位点，缩放地图
                locateAndZoom(location, ll);

                long nowTotalRxBytes = getTotalRxBytes();
                long nowTotalTxBytes = getTotalTxBytes();
                long nowTime = System.currentTimeMillis();
                long speedTx = ((nowTotalTxBytes-lastTotalTxBytes)*1000/(nowTime-lastTime));
                lastTime = nowTime;
                lastTotalRxBytes = nowTotalRxBytes;
                lastTotalTxBytes = nowTotalTxBytes;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder currentposition=new StringBuilder();
                                currentposition.append("Throughpur_UL：").append(speedTx).append("\n");
                                position.setText(currentposition);
                            }
                        });
                        if(speedTx>=0&&speedTx<5){
                            points1.add(ll);
                            OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(points1);
                            mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                        }
                        else if(speedTx>=5&&speedTx<10){
                            points2.add(ll);
                            OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFFFFE0).points(points2);
                            mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                        }
                        else if(speedTx>=10&&speedTx<20){
                            points3.add(ll);
                            OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFFFF00).points(points3);
                            mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                        }
                        else if(speedTx>=20&&speedTx<40){
                            points4.add(ll);
                            OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAA808080).points(points4);
                            mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                        }
                        else if(speedTx>=40&&speedTx<60){
                            points5.add(ll);
                            OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAA008000).points(points4);
                            mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                        }
                        else if(speedTx>=60&&speedTx<1000){
                            points6.add(ll);
                            OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAA2E8B57).points(points5);
                            mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                        }
            }
        }

    }

    /**
     * 获取下行
     */
    public static long getTotalRxBytes(){
        //转换成kB
        return TrafficStats.getTotalRxBytes()==TrafficStats.UNSUPPORTED ? 0:(TrafficStats.getTotalRxBytes()/1024);
    }
    /**
     * 获取上行
     */
    public static long getTotalTxBytes(){
        return TrafficStats.getTotalTxBytes()==TrafficStats.UNSUPPORTED ? 0:(TrafficStats.getTotalTxBytes()/1024);
    }

    private void locateAndZoom(final BDLocation location, LatLng ll) {
        mCurrentLat = location.getLatitude();
        mCurrentLon = location.getLongitude();
        locData = new MyLocationData.Builder().accuracy(0)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentDirection).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);

        builder = new MapStatus.Builder();
        builder.target(ll).zoom(mCurrentZoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 首次定位很重要，选一个精度相对较高的起始点
     * 注意：如果一直显示gps信号弱，说明过滤的标准过高了，
     你可以将location.getRadius()>25中的过滤半径调大，比如>40，
     并且将连续5个点之间的距离DistanceUtil.getDistance(last, ll ) > 5也调大一点，比如>10，
     这里不是固定死的，你可以根据你的需求调整，如果你的轨迹刚开始效果不是很好，你可以将半径调小，两点之间距离也调小，
     gps的精度半径一般是10-50米
     */
    private LatLng getMostAccuracyLocation(BDLocation location){

        if (location.getRadius()>40) {//gps位置精度大于40米的点直接弃用
            return null;
        }

        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

        if (DistanceUtil.getDistance(last, ll ) > 10) {
            last = ll;
            points.clear();//有任意连续两点位置大于10，重新取点
            return null;
        }
        points.add(ll);
        last = ll;
        //有5个连续的点之间的距离小于10，认为gps已稳定，以最新的点为起始点
        if(points.size() >= 5){
            points.clear();
            return ll;
        }
        return null;
    }

    private GetStationLocationRequest mGetStationLocationRequest;
    public void getStationLocation(String id){
        if (mGetStationLocationRequest != null && !mGetStationLocationRequest.isFinish()) {
            return;
        }
        mGetStationLocationRequest = new GetStationLocationRequest();
        mGetStationLocationRequest.addUrlParam("stationId", id);
        mGetStationLocationRequest.setRequestListener(new RequestListener<getstationlocationmodel>() {
            @Override
            public void onComplete() {
            }
            @Override
            public void onSuccess(getstationlocationmodel result) {
                if (result == null) {
                    return;
                }
                LatLng point = new LatLng(result.stations.latitude, result.stations.longitude);
                //构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_basestation);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap);
                //在地图上添加Marker，并显示
                mBaiduMap.addOverlay(option);
            }
            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mGetStationLocationRequest);
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        // 取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.unRegisterLocationListener(myListener);
        if (mLocClient != null && mLocClient.isStarted()) {
            mLocClient.stop();
        }
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.getMap().clear();
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

}

