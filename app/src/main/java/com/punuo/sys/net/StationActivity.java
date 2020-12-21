package com.punuo.sys.net;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.punuo.sys.net.clusterutil.clustering.Cluster;
import com.punuo.sys.net.clusterutil.clustering.ClusterManager;
import com.punuo.sys.net.push.GetStationsModel;
import com.punuo.sys.net.push.GetStationsRequest;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import top.androidman.SuperButton;

import static org.greenrobot.eventbus.EventBus.TAG;

public class StationActivity extends Activity implements View.OnClickListener {
    public LocationClient mLocationClient;

    private TextView positionText;

    private MapView mapView;
    private SuperButton superButton;
    private BaiduMap baiduMap;
    private ClusterManager<MyItem> mClusterManager;
    private boolean isFirstLocate = true;
    private List<MyItem> items = new ArrayList<MyItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_station);
        mapView = (MapView) findViewById(R.id.bmapView);
        superButton = findViewById(R.id.r_button);
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
        if (ContextCompat.checkSelfPermission(StationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(StationActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(StationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(StationActivity.this, permissions, 1);
        } else {
            requestLocation();
        }
    }

    private void addMarkers() {

    }

    private void initCluster() {
        Log.i("ww", "成功2");
        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<MyItem>(this,baiduMap);
        baiduMap.setOnMapStatusChangeListener(mClusterManager);
        baiduMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                Toast.makeText(StationActivity.this, "有" + cluster.getSize() + "个点",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mClusterManager.setOnClusterItemClickListener(
                new ClusterManager.OnClusterItemClickListener<MyItem>() {
                    @Override
                    public boolean onClusterItemClick(MyItem item) {
                        Toast.makeText(StationActivity.this, "点击单个Item", Toast.LENGTH_SHORT)
                                .show();
                        return false;
                    }
                });
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
                initCluster();
                for (int i = 0; i <  result.stationsList.size(); i++) {
                    LatLng point = new LatLng(result.stationsList.get(i).latitude, result.stationsList.get(i).longitude);
                    items.add(new MyItem(point));
                    Log.i("ww", "onSuccess: "+i);
                    //构建Marker图标
                    /*BitmapDescriptor bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_basestation);
                    //构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions()
                            .position(point)
                            .icon(bitmap);
                    //在地图上添加Marker，并显示
                    baiduMap.addOverlay(option);*/
                }
                mClusterManager.addItems(items);
                /*LatLng point = new LatLng(result.stations.latitude, result.stations.longitude);
                //构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_basestation);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap);
                //在地图上添加Marker，并显示
                baiduMap.addOverlay(option);*/
            }

            @Override
            public void onError(Exception e) {
                Log.i(TAG, "onError:");
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
        Intent intent = new Intent(StationActivity.this,ChooseActivity.class);
        startActivity(intent);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
//            StringBuilder currentPosition = new StringBuilder();
//            currentPosition.append("纬度：").append(location.getLatitude()).append("\n");
//            currentPosition.append("经线：").append(location.getLongitude()).append("\n");
//            currentPosition.append("国家：").append(location.getCountry()).append("\n");
//            currentPosition.append("省：").append(location.getProvince()).append("\n");
//            currentPosition.append("市：").append(location.getCity()).append("\n");
//            currentPosition.append("区：").append(location.getDistrict()).append("\n");
//            currentPosition.append("街道：").append(location.getStreet()).append("\n");
//            currentPosition.append("定位方式：");
//            if (location.getLocType() == BDLocation.TypeGpsLocation) {
//                currentPosition.append("GPS");
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                currentPosition.append("网络");
//            }
//            positionText.setText(currentPosition);
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(location);
            }
        }

    }
}