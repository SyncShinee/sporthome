package sporthome.demo.sportgym;

import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.List;

import sporthome.demo.gallery.CardAdapter;
import sporthome.demo.gallery.CardScaleHelper;

import sporthome.demo.R;
import me.leefeng.citypicker.CityPicker;
import me.leefeng.citypicker.CityPickerListener;

/**
 * 介绍在地图上绘制Marker,添加动画，响应事件
 */

public class GymMapListActivity extends AppCompatActivity implements CityPickerListener, OnGetGeoCoderResultListener {

    private CityPicker cityPicker = null;
    private TextView choseCity = null;

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

    // 搜索模块，也可去掉地图模块独立使用
    private GeoCoder mSearch = null;

    // MapView 是地图主控件
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Marker mMarkerA;
    private Marker mMarkerB;
    private Marker mMarkerC;
    private Marker mMarkerD;
    private Marker mMarkerE;
    private Marker mMarkerF;
    private Marker mMarkerG;
    private Marker mMarkerH;
    private InfoWindow mInfoWindow;
    private SeekBar mAlphaSeekBar = null;
    private CheckBox mAnimationBox = null;
    private Button btn_basket = null;
    private Button btn_football = null;
    private Button btn_baseball = null;
    private ImageButton btn_locate = null;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    private BitmapDescriptor bitmapA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
    private BitmapDescriptor bitmapB = BitmapDescriptorFactory.fromResource(R.drawable.icon_markb);
    private BitmapDescriptor bitmapC = BitmapDescriptorFactory.fromResource(R.drawable.icon_markc);
    private BitmapDescriptor bitmapD = BitmapDescriptorFactory.fromResource(R.drawable.icon_markd);
    private BitmapDescriptor bitmapE = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
    private BitmapDescriptor bitmapN = BitmapDescriptorFactory.fromResource(R.drawable.icon_blue);

    // gallery 相关
    private List<Integer> mList = new ArrayList<>();
    private List<String> sList = new ArrayList<>();
    private List<String> typList = new ArrayList<>();
    private List<LatLng> llList = new ArrayList<>();
    private List<String> disList = new ArrayList<>();
    private int showType = 0;
    private Button btn_recover = null;
    private CardScaleHelper adapter = null;
    private int mLastPos = -1;
    private List<Marker> markerList = new ArrayList<>();

    // 定位相关
    private LatLng myLoc = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        //mAlphaSeekBar = (SeekBar) findViewById(R.id.alphaBar);
        //mAlphaSeekBar.setOnSeekBarChangeListener(new SeekBarListener());
        //mAnimationBox = (CheckBox) findViewById(R.id.animation);
        btn_basket = (Button) findViewById(R.id.btn_basket);
        btn_football = (Button) findViewById(R.id.btn_football);
        btn_baseball = (Button) findViewById(R.id.btn_baseball);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(13.0f);
        mBaiduMap.setMapStatus(mapStatusUpdate);

        // gallery 相关
        btn_recover = findViewById(R.id.btn_recover);
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
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
                btn_recover.setVisibility(View.VISIBLE);
            }
        });


        btn_locate = findViewById(R.id.btn_locate);
        btn_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFirstLoc = true;
                initLocation();
            }
        });
        initListener();

//        // 获取传感器管理服务
//        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
//        // 为系统的方向传感器注册监听器
//        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
//        // 定位初始化
        initLocation();

        mList.clear();
        sList.clear();
        typList.clear();
        llList.clear();
        markerList.clear();
        disList.clear();
        LatLngBounds bounds = mBaiduMap.getMapStatus().bound;
        //TODO
        for (int i = 0; i < GYMS.length; i++) {
            if (showType ==0 || GYMS[i].getTypes().contains(showType)) {
                if (bounds.contains(GYMS[i].getLatLang())) {
                    mList.add(R.drawable.sport);
                    sList.add(GYMS[i].getName());
                    typList.add(typeToString(GYMS[i].getTypes()));
                    llList.add(GYMS[i].getLatLang());
                    disList.add("距您直线距离"+ Math.round(DistanceUtil.getDistance(GYMS[i].getLatLang(),myLoc)/10)/100.0+"公里");
                }
            }
        }
        RecyclerView recyclerView = findViewById(R.id.card_recycler);
        LinearLayoutManager m = new LinearLayoutManager(GymMapListActivity.this);
        m.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(m);
        recyclerView.setAdapter(new CardAdapter(this, mList, sList, typList, disList));
        adapter = new CardScaleHelper();
        adapter.setCurrentItemPos(0);
        adapter.attachToRecyclerView(recyclerView);
        recyclerView.setVisibility(View.INVISIBLE);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    notifyMarkerChange();
                }
            }
        });



        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        cityPicker = new CityPicker(GymMapListActivity.this, this);
        Button choseCityBtn = findViewById(R.id.chose_city);
        choseCity = findViewById(R.id.locate_text);
        choseCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityPicker.show();
                btn_recover.setVisibility(View.VISIBLE);
            }
        });
    }

    public void notifyMarkerChange() {
        //if (mLastPos == adapter.getCurrentItemPos()) return;
        //Toast.makeText(getApplicationContext(), "adc"+adapter.getCurrentItemPos(), Toast.LENGTH_SHORT).show();
        Log.e("curPos", String.valueOf(adapter.getCurrentItemPos()));
        mLastPos = adapter.getCurrentItemPos();
        initMarker_flush(llList);

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

    @Override
    public void getCity(String s) {
        choseCity.setText(s);
        // 发起Geo搜索
        String  city = s.split(" ")[1];
        String address = s.split(" ")[2];
        mSearch.geocode(new GeoCodeOption()
                .city(city)// 城市
                .address(address)); // 地址
        //Toast t = Toast.makeText(this
        //        , s.split(" ")[2]
        //        // 设置该Toast提示信息的持续时间
        //        , Toast.LENGTH_SHORT);;
        //t.show();
    }

    /**
     * 地理编码查询结果回调函数
     *
     * @param result  地理编码查询结果
     */
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(GymMapListActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }

        mBaiduMap.clear();
       // mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation()).icon(mbitmap));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
        String strInfo = String.format("纬度：%f 经度：%f", result.getLocation().latitude, result.getLocation().longitude);

        //Toast.makeText(MarkerDemo.this, strInfo, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

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

    /**
     * 添加marker
     *
     * @param latLng 经纬度
     */
    public void addMarker(LatLng latLng){
        if (latLng.latitude == 0.0 || latLng.longitude == 0.0){
            return;
        }
        MarkerOptions markerOptionsA = new MarkerOptions().position(latLng).yOffset(30).icon(bitmapA).draggable(true);
        mMarker = (Marker) mBaiduMap.addOverlay(markerOptionsA);
    }


    public void initMarker() {
        // add marker overlay
        LatLng latLngA = new LatLng(39.963175, 116.400244);
        LatLng latLngB = new LatLng(39.942821, 116.369199);
        LatLng latLngC = new LatLng(39.939723, 116.425541);
        LatLng latLngD = new LatLng(39.906965, 116.401394);
        LatLng latLngE = new LatLng(39.947821, 116.368199);
        LatLng latLngF = new LatLng(39.979223, 116.318541);
        LatLng latLngG = new LatLng(39.925965, 116.388394);

        MarkerOptions markerOptionsA = new MarkerOptions()
                .position(latLngA)
                .icon(bitmapE)// 设置 Marker 覆盖物的图标
                .zIndex(9)// 设置 marker 覆盖物的 zIndex
                .draggable(false); // 设置 marker 是否允许拖拽，默认不可拖拽
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsA.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
        mMarkerA = (Marker) (mBaiduMap.addOverlay(markerOptionsA));

        MarkerOptions markerOptionsB = new MarkerOptions().position(latLngB).icon(bitmapE).zIndex(5);
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsB.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
        mMarkerB = (Marker) (mBaiduMap.addOverlay(markerOptionsB));

        MarkerOptions markerOptionsC = new MarkerOptions().position(latLngC).icon(bitmapE).zIndex(5);
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsB.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
        mMarkerC = (Marker) (mBaiduMap.addOverlay(markerOptionsC));
        MarkerOptions markerOptionsD = new MarkerOptions().position(latLngD).icon(bitmapE).zIndex(5);
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsB.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
        mMarkerD = (Marker) (mBaiduMap.addOverlay(markerOptionsD));
        MarkerOptions markerOptionsE = new MarkerOptions().position(latLngE).icon(bitmapE).zIndex(5);
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsB.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
        mMarkerE = (Marker) (mBaiduMap.addOverlay(markerOptionsE));
        MarkerOptions markerOptionsF = new MarkerOptions().position(latLngF).icon(bitmapE).zIndex(5);
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsB.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
        mMarkerF = (Marker) (mBaiduMap.addOverlay(markerOptionsF));
        MarkerOptions markerOptionsG = new MarkerOptions().position(latLngG).icon(bitmapE).zIndex(5);
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsB.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
        mMarkerG = (Marker) (mBaiduMap.addOverlay(markerOptionsG));
//        MarkerOptions markerOptionsC = new MarkerOptions()
//                .position(latLngC)// 经纬度
//                .icon(bitmapC) // 设置 Marker 覆盖物的图标
//                .perspective(false) // 设置是否开启 marker 覆盖物近大远小效果，默认开启
//                .anchor(0.5f, 0.5f) // 设置 marker 覆盖物的锚点比例，默认（0.5f, 1.0f）水平居中，垂直下对齐
//                .rotate(30) // 设置 marker 覆盖物旋转角度，逆时针
//                .clickable(mClickMarker.isChecked()) // 设置Marker是否可点击
//                .zIndex(7); // 设置 marker 覆盖物的 zIndex
////        if (mAnimationBox.isChecked()) {
////            // 生长动画
////            markerOptionsC.animateType(MarkerOptions.MarkerAnimateType.grow);
////        }
//        mMarkerC = (Marker) (mBaiduMap.addOverlay(markerOptionsC));
//
//        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
//        giflist.add(bitmapA);
//        giflist.add(bitmapB);
//        giflist.add(bitmapC);
//        MarkerOptions markerOptionsD = new MarkerOptions()
//                .position(latLngD)
//                .icons(giflist)// 设置 Marker 覆盖物的图标，相同图案的 icon 的 marker 最好使用同一个 BitmapDescriptor 对象以节省内存空间。
//                .zIndex(0)
//                .clickable(mClickMarker.isChecked()) // 设置Marker是否可点击
//                .period(10);// 设置多少帧刷新一次图片资源，Marker动画的间隔时间，值越小动画越快 默认为20，最小为1
//        if (mAnimationBox.isChecked()) {
//            // 生长动画
//            markerOptionsD.animateType(MarkerOptions.MarkerAnimateType.grow);
//        }
//        mMarkerD = (Marker) (mBaiduMap.addOverlay(markerOptionsD));
    }

    public void initMarker_basket() {
        // add marker overlay
        LatLng latLngA = new LatLng(39.963175, 116.400244);
        LatLng latLngB = new LatLng(39.942821, 116.369199);
        LatLng latLngC = new LatLng(39.939723, 116.425541);
        LatLng latLngD = new LatLng(39.906965, 116.401394);


        MarkerOptions markerOptionsA = new MarkerOptions()
                .position(latLngA)
                .icon(bitmapE)// 设置 Marker 覆盖物的图标
                .zIndex(9)// 设置 marker 覆盖物的 zIndex
                .draggable(false); // 设置 marker 是否允许拖拽，默认不可拖拽
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsA.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
        mMarkerA = (Marker) (mBaiduMap.addOverlay(markerOptionsA));

        MarkerOptions markerOptionsB = new MarkerOptions().position(latLngB).icon(bitmapE).zIndex(5);
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsB.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
        mMarkerB = (Marker) (mBaiduMap.addOverlay(markerOptionsB));

        MarkerOptions markerOptionsC = new MarkerOptions().position(latLngC).icon(bitmapE).zIndex(5);
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsB.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
        mMarkerC = (Marker) (mBaiduMap.addOverlay(markerOptionsC));
        MarkerOptions markerOptionsD = new MarkerOptions().position(latLngD).icon(bitmapE).zIndex(5);
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsB.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
        mMarkerD = (Marker) (mBaiduMap.addOverlay(markerOptionsD));

    }

    public void initMarker_baseball() {
        // add marker overlay
        LatLng latLngE = new LatLng(39.947821, 116.368199);
        LatLng latLngF = new LatLng(39.979223, 116.318541);
        LatLng latLngG = new LatLng(39.925965, 116.388394);


        MarkerOptions markerOptionsE = new MarkerOptions().position(latLngE).icon(bitmapE).zIndex(5);
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsB.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
        mMarkerE = (Marker) (mBaiduMap.addOverlay(markerOptionsE));
        MarkerOptions markerOptionsF = new MarkerOptions().position(latLngF).icon(bitmapE).zIndex(5);
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsB.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
        mMarkerF = (Marker) (mBaiduMap.addOverlay(markerOptionsF));
        MarkerOptions markerOptionsG = new MarkerOptions().position(latLngG).icon(bitmapE).zIndex(5);
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsB.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
        mMarkerG = (Marker) (mBaiduMap.addOverlay(markerOptionsG));
//        MarkerOptions markerOptionsC = new MarkerOptions()
//                .position(latLngC)// 经纬度
//                .icon(bitmapC) // 设置 Marker 覆盖物的图标
//                .perspective(false) // 设置是否开启 marker 覆盖物近大远小效果，默认开启
//                .anchor(0.5f, 0.5f) // 设置 marker 覆盖物的锚点比例，默认（0.5f, 1.0f）水平居中，垂直下对齐
//                .rotate(30) // 设置 marker 覆盖物旋转角度，逆时针
//                .clickable(mClickMarker.isChecked()) // 设置Marker是否可点击
//                .zIndex(7); // 设置 marker 覆盖物的 zIndex
////        if (mAnimationBox.isChecked()) {
////            // 生长动画
////            markerOptionsC.animateType(MarkerOptions.MarkerAnimateType.grow);
////        }
//        mMarkerC = (Marker) (mBaiduMap.addOverlay(markerOptionsC));
//
//        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
//        giflist.add(bitmapA);
//        giflist.add(bitmapB);
//        giflist.add(bitmapC);
//        MarkerOptions markerOptionsD = new MarkerOptions()
//                .position(latLngD)
//                .icons(giflist)// 设置 Marker 覆盖物的图标，相同图案的 icon 的 marker 最好使用同一个 BitmapDescriptor 对象以节省内存空间。
//                .zIndex(0)
//                .clickable(mClickMarker.isChecked()) // 设置Marker是否可点击
//                .period(10);// 设置多少帧刷新一次图片资源，Marker动画的间隔时间，值越小动画越快 默认为20，最小为1
//        if (mAnimationBox.isChecked()) {
//            // 生长动画
//            markerOptionsD.animateType(MarkerOptions.MarkerAnimateType.grow);
//        }
//        mMarkerD = (Marker) (mBaiduMap.addOverlay(markerOptionsD));
    }

    public void initListener(){
        // 设置Marker 点击事件监听
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                Log.e("MarkerNum", String.valueOf(markerList.size()));
                for (int i = 0; i < markerList.size(); i++) {
                    if (marker == markerList.get(i)) {
                        mLastPos = i;
                        Log.e("MarkerNum", String.valueOf(i));
                        initMarker_flush(llList);
                        adapter.setCurrentItemPos(i);
                        break;
                    }
                }
//                Button button = new Button(getApplicationContext());
//                button.setBackgroundResource(R.drawable.popup);
//                InfoWindow.OnInfoWindowClickListener listener = null;
//                if (marker == mMarkerA || marker == mMarkerD) {
//                    button.setText("更改位置");
//                    button.setTextColor(Color.BLACK);
//                    button.setWidth(300);
//                    // InfoWindow点击事件监听接口
//                    listener = new InfoWindow.OnInfoWindowClickListener() {
//                        public void onInfoWindowClick() {
//                            LatLng latLng = marker.getPosition();
//                            LatLng latLngNew = new LatLng(latLng.latitude + 0.005, latLng.longitude + 0.005);
//                            marker.setPosition(latLngNew);
//                            // 隐藏地图上的所有InfoWindow
//                            mBaiduMap.hideInfoWindow();
//                        }
//                    };
//                    LatLng latLng = marker.getPosition();
//                    // 创建InfoWindow
//                    mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), latLng, -47, listener);
//                    // 显示 InfoWindow, 该接口会先隐藏其他已添加的InfoWindow, 再添加新的InfoWindow
//                    mBaiduMap.showInfoWindow(mInfoWindow);
//                } else if (marker == mMarkerB) {
//                    button.setText("更改图标");
//                    button.setTextColor(Color.BLACK);
//                    button.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            marker.setIcon(bitmapE);
//                            mBaiduMap.hideInfoWindow();
//                        }
//                    });
//                    LatLng latLng = marker.getPosition();
//                    mInfoWindow = new InfoWindow(button, latLng, -47);
//                    mBaiduMap.showInfoWindow(mInfoWindow);
//                } else if (marker == mMarkerC) {
//                    button.setText("删除");
//                    button.setTextColor(Color.BLACK);
//                    button.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            marker.remove();
//                            mBaiduMap.hideInfoWindow();
//                        }
//                    });
//                    LatLng latLng = marker.getPosition();
//                    mInfoWindow = new InfoWindow(button, latLng, -47);
//                    mBaiduMap.showInfoWindow(mInfoWindow);
//                }
                return true;
            }
        });

        // 设置 Marker 拖拽事件监听者
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {

            }

            public void onMarkerDragEnd(Marker marker) {
                Toast.makeText(GymMapListActivity.this, "拖拽结束，新位置：" + marker.getPosition().latitude + ", "
                        + marker.getPosition().longitude, Toast.LENGTH_LONG).show();
            }

            public void onMarkerDragStart(Marker marker) {

            }
        });
    }
    /**
     * 清除所有Overlay
     */
    public void clearOverlay(View view) {
        mBaiduMap.clear();
        markerList.clear();
        mMarkerA = null;
        mMarkerB = null;
        mMarkerC = null;
        mMarkerD = null;
//        mList.clear();
//        sList.clear();
//        for (int i = 0; i < 1; i++) {
//            mList.add(R.drawable.sport);
//            sList.add(DEMO0[i].desc);
//        }
//        RecyclerView recyclerView = findViewById(R.id.card_recycler);
//        LinearLayoutManager m = new LinearLayoutManager(MarkerDemo.this);
//        m.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView.setLayoutManager(m);
//        recyclerView.setAdapter(new CardAdapter(mList, sList));
        //CardScaleHelper adapter = new CardScaleHelper();
        //adapter.setCurrentItemPos(0);
        //adapter.attachToRecyclerView(recyclerView);
    }

    public void footBallOverlay(View view) {
        showType = 2;
        flushOverlay(null);
//        mList.clear();
//        sList.clear();
//        for (int i = 0; i < 1; i++) {
//            mList.add(R.drawable.sport);
//            sList.add(DEMO0[i].desc);
//        }
//        RecyclerView recyclerView = findViewById(R.id.card_recycler);
//        LinearLayoutManager m = new LinearLayoutManager(MarkerDemo.this);
//        m.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView.setLayoutManager(m);
//        recyclerView.setAdapter(new CardAdapter(mList, sList));
        //CardScaleHelper adapter = new CardScaleHelper();
        //adapter.setCurrentItemPos(0);
        //adapter.attachToRecyclerView(recyclerView);
    }

    public void locate(View view) {

    }

    /**
     * 重新添加Overlay
     */
    public void resetOverlay(View view) {
        showType = 0;
        flushOverlay(null);
//        clearOverlay(null);
//        initMarker();
//        mList.clear();
//        sList.clear();
//        for (int i = 0; i < 7; i++) {
//            mList.add(R.drawable.sport);
//            sList.add(DEMOS[i].desc);
//        }
//        RecyclerView recyclerView = findViewById(R.id.card_recycler);
//        LinearLayoutManager m = new LinearLayoutManager(MarkerDemo.this);
//        m.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView.setLayoutManager(m);
//        recyclerView.setAdapter(new CardAdapter(mList, sList));
        //CardScaleHelper adapter = new CardScaleHelper();
        //adapter.setCurrentItemPos(2);
        //adapter.attachToRecyclerView(recyclerView);
    }

    public void basketOverlay(View view) {
        showType = 1;
        flushOverlay(null);
//        clearOverlay(null);
//        initMarker_basket();
//        mList.clear();
//        sList.clear();
//        for (int i = 0; i < DEMO2.length; i++) {
//            mList.add(R.drawable.sport);
//            sList.add(DEMO2[i].desc);
//        }
//        RecyclerView recyclerView = findViewById(R.id.card_recycler);
//        LinearLayoutManager m = new LinearLayoutManager(MarkerDemo.this);
//        m.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView.setLayoutManager(m);
//        recyclerView.setAdapter(new CardAdapter(mList, sList));
        //CardScaleHelper adapter = new CardScaleHelper();
        //adapter.setCurrentItemPos(0);
        //adapter.attachToRecyclerView(recyclerView);
    }

    public void baseballOverlay(View view) {
        showType = 3;
        flushOverlay(null);
//        clearOverlay(null);
//        initMarker_baseball();
//        mList.clear();
//        sList.clear();
//        for (int i = 0; i < DEMO3.length; i++) {
//            mList.add(R.drawable.sport);
//            sList.add(DEMO3[i].desc);
//        }
//        RecyclerView recyclerView = findViewById(R.id.card_recycler);
//        LinearLayoutManager m = new LinearLayoutManager(MarkerDemo.this);
//        m.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView.setLayoutManager(m);
//        recyclerView.setAdapter(new CardAdapter(mList, sList));
        //CardScaleHelper adapter = new CardScaleHelper();
        //adapter.setCurrentItemPos(0);
        //adapter.attachToRecyclerView(recyclerView);
    }

    public void flushOverlay(View view) {
        mList.clear();
        sList.clear();
        typList.clear();
        llList.clear();
        markerList.clear();
        disList.clear();
        LatLngBounds bounds = mBaiduMap.getMapStatus().bound;
        for (int i = 0; i < GYMS.length; i++) {
            if (showType ==0 || GYMS[i].getTypes().contains(showType)) {
                if (bounds.contains(GYMS[i].getLatLang())) {
                    mList.add(R.drawable.sport);
                    sList.add(GYMS[i].getName());
                    typList.add(typeToString(GYMS[i].getTypes()));
                    llList.add(GYMS[i].getLatLang());
                    disList.add("距您直线距离"+Math.round(DistanceUtil.getDistance(GYMS[i].getLatLang(),myLoc)/10)/100.0+"公里");
                }
            }
        }
        if (mList.isEmpty()) {
            mList.add(R.drawable.sport);
            sList.add("暂无数据");
            typList.add("");
            disList.add("");
        }
        mLastPos = 0;
        initMarker_flush(llList);
        RecyclerView recyclerView = findViewById(R.id.card_recycler);
        LinearLayoutManager m = new LinearLayoutManager(GymMapListActivity.this);
        m.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(m);
        recyclerView.setAdapter(new CardAdapter(this, mList, sList, typList, disList));
        btn_recover.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        //CardScaleHelper adapter = new CardScaleHelper();
        //adapter.setCurrentItemPos(0);
        //adapter.attachToRecyclerView(recyclerView);
    }

    public void initMarker_flush(List<LatLng> l1List) {
        clearOverlay(null);
        for (int i = 0; i < l1List.size(); i++) {
            if (i == mLastPos) {
                MarkerOptions markerOptionsA = new MarkerOptions()
                        .position(l1List.get(i))
                        .icon(bitmapE)// 设置 Marker 覆盖物的图标
                        .zIndex(9)// 设置 marker 覆盖物的 zIndex
                        .draggable(false); // 设置 marker 是否允许拖拽，默认不可拖拽
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsA.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
                markerList.add((Marker) (mBaiduMap.addOverlay(markerOptionsA)));
            } else {
                MarkerOptions markerOptionsA = new MarkerOptions()
                        .position(l1List.get(i))
                        .icon(bitmapN)// 设置 Marker 覆盖物的图标
                        .zIndex(9)// 设置 marker 覆盖物的 zIndex
                        .draggable(false); // 设置 marker 是否允许拖拽，默认不可拖拽
//        if (mAnimationBox.isChecked()) {
//            // 掉下动画
//            markerOptionsA.animateType(MarkerOptions.MarkerAnimateType.drop);
//        }
                markerList.add((Marker) (mBaiduMap.addOverlay(markerOptionsA)));
            }
        }
    }

    public String typeToString(List<Integer> tList) {
        String res = "";
        for (int i = 0; i < tList.size(); i++) {
            res = res + TYPES[tList.get(i)] + " ";
        }
        return res;
    }

    /**
     * 设置marker 是否可点击
     */
    public void setMarkerClick(View view) {
        if (mMarkerA == null || mMarkerB == null || mMarkerC == null || mMarkerD == null) {
            return;
        }
        CheckBox checkBox = (CheckBox) view;
        if (checkBox.isChecked()){
            mMarkerA.setClickable(true);
            mMarkerB.setClickable(true);
            mMarkerC.setClickable(true);
            mMarkerD.setClickable(true);
        }else {
            mMarkerA.setClickable(false);
            mMarkerB.setClickable(false);
            mMarkerC.setClickable(false);
            mMarkerD.setClickable(false);
        }
    }

    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float alpha = ((float) seekBar.getProgress()) / 10;
            if (mMarkerA != null) {
                // 设置 Marker 图标的透明度
                mMarkerA.setAlpha(alpha);
            }
            if (mMarkerB != null) {
                mMarkerB.setAlpha(alpha);
            }
            if (mMarkerC != null) {
                mMarkerC.setAlpha(alpha);
            }
            if (mMarkerD != null) {
                mMarkerD.setAlpha(alpha);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // MapView的生命周期与Activity同步，当activity恢复时必须调用MapView.onResume()
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 回收bitmap资源，防止内存泄露
        bitmapA.recycle();
        bitmapB.recycle();
        bitmapC.recycle();
        bitmapD.recycle();
        bitmapE.recycle();
        // 清除所有图层
        mBaiduMap.clear();
        // MapView的生命周期与Activity同步，当activity销毁时必须调用MapView.destroy()
//        // 取消注册传感器监听
//        mSensorManager.unregisterListener(this);
//        // 退出时销毁定位
//        mLocClient.stop();
//        // 关闭定位图层
//        mBaiduMap.setMyLocationEnabled(false);
//        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView.onDestroy();
    }



    private static final Gym[] GYMS = {
            new Gym("球馆1", new LatLng(39.963175, 116.400244), new ArrayList<Integer>(){{add(1);}}),
            new Gym("球馆2", new LatLng(39.942821, 116.369199), new ArrayList<Integer>(){{add(2);}}),
            new Gym("球馆3", new LatLng(39.939723, 116.425541), new ArrayList<Integer>(){{add(3);}}),
            new Gym("球馆4", new LatLng(39.906965, 116.401394), new ArrayList<Integer>(){{add(2);}}),
            new Gym("球馆5", new LatLng(39.947821, 116.368199), new ArrayList<Integer>(){{add(1); add(3);}}),
            new Gym("球馆6", new LatLng(39.979223, 116.318541), new ArrayList<Integer>(){{add(2);}}),
            new Gym("球馆7", new LatLng(39.925965, 116.388394), new ArrayList<Integer>(){{add(3);}}),
    };

    private static final String[] TYPES = {
            "所有","篮球","足球","羽毛球"
    };
}
