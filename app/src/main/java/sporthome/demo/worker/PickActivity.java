/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package sporthome.demo.worker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import sporthome.demo.R;

public class PickActivity extends AppCompatActivity {

    public final static int RESULT_CODE = 2;

    // 定位相关
    private LocationClient mLocClient;
    private Marker mMarker;
    //private MyLocationListener myListener = new MyLocationListener();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    // 是否首次定位
    private boolean isFirstLoc = true;
    // 是否开启定位图层
    private boolean isLocationLayerEnable = true;
    private MyLocationData myLocationData;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Marker mMarkerA;
    private LatLng myLoc = null;
    private LatLng retLoc = null;

    private Point mScreenCenterPoint;

    private BitmapDescriptor bitmapE = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(13.0f);
        //mBaiduMap.setMapStatus(mapStatusUpdate);

        initLocation();

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                initMarker();
            }
        });



        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {

            }

            public void onMarkerDragEnd(Marker marker) {
                retLoc = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                Toast.makeText(PickActivity.this, "拖拽结束，新位置：" + marker.getPosition().latitude + ", "
                        + marker.getPosition().longitude, Toast.LENGTH_LONG).show();
            }

            public void onMarkerDragStart(Marker marker) {

            }
        });


        Button btn = findViewById(R.id.btn_recover);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double lat = 0;
                double lng = 0;
                Intent intent = new Intent();
                retLoc = mBaiduMap.getMapStatus().target;
                intent.putExtra("lat", Math.round(retLoc.latitude*10000)/10000.);
                intent.putExtra("lng", Math.round(retLoc.longitude*10000)/10000.);
                setResult(RESULT_CODE, intent);
                finish();
            }
        });
    }

    /**
     * 定位初始化
     */
    public void initLocation(){
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        MyLocationListenner myListener = new MyLocationListenner();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        // 设置坐标类型
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // MapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
                    .direction(location.getDirection()) // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            // 设置定位数据, 只有先允许定位图层后设置数据才会生效
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                myLoc = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(myLoc).zoom(15.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


                //addMarker(latLng);
            }
            //if (mMarker != null){
            //    mMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
            //}

        }
    }

    private void initMarker() {
        if (null != mBaiduMap.getMapStatus()) {
            LatLng  latLngF = mBaiduMap.getMapStatus().target;
            mScreenCenterPoint = mBaiduMap.getProjection().toScreenLocation(latLngF);
            MarkerOptions markerOptionsF = new MarkerOptions().position(latLngF).icon(bitmapE).perspective(true)
                    .fixedScreenPosition(mScreenCenterPoint);
            mMarkerA = (Marker) (mBaiduMap.addOverlay(markerOptionsF));
        }
        retLoc = mMarkerA.getPosition();
    }
}