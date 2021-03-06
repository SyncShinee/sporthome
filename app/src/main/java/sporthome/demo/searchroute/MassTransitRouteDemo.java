/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package sporthome.demo.searchroute;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.MassTransitRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.ArrayList;
import java.util.List;

import sporthome.demo.R;

/**
 * ???demo??????????????????????????????????????????????????????????????????RouteOverlay???TransitOverlay??????
 * ???????????????????????????????????????????????????
 */
public class MassTransitRouteDemo extends AppCompatActivity implements BaiduMap.OnMapClickListener,
        OnGetRoutePlanResultListener {

    // ????????????????????????
    private Button mBtnPre = null; // ???????????????
    private Button mBtnNext = null; // ???????????????
    private MassTransitRouteLine mMassTransitRouteLine = null;
    private OverlayManager mRouteOverlay = null;
    private boolean mUseDefaultIcon = false; // ??????????????????

    // ???????????????????????????MapView???MyRouteMapView???????????????touch????????????????????????
    // ???????????????touch???????????????????????????????????????MapView??????
    private MapView mMapView = null;    // ??????View
    private BaiduMap mBaidumap = null;
    // ????????????
    private RoutePlanSearch mSearch = null;   // ???????????????????????????????????????????????????
    private MassTransitRouteResult mMassTransitRouteResult = null;
    private boolean hasShowDialog = false;
    // ????????????????????????
    private MassTransitRoutePlanOption mMassTransitRoutePlanOption = null;
    private NodeUtils mNodeUtils;
    private EditText mEditStartCity;
    private EditText mEditEndCity;
    private AutoCompleteTextView mStrartNodeView;
    private AutoCompleteTextView mEndNodeView;
    private Spinner mTranstypeSpinner;
    private Spinner mIntercitySpinner;
    private Spinner mIncitySpinner;


    // ????????????
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
    // ??????????????????
    private boolean isFirstLoc = true;
    // ????????????????????????
    private boolean isLocationLayerEnable = true;
    private MyLocationData myLocationData;
    private LatLng myLoc;
    private LatLng ed;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mass_transit_route);


        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("lat", 0);
        double lng = intent.getDoubleExtra("lng", 0);
        ed = new LatLng(lat, lng);


        mEditStartCity = (EditText) findViewById(R.id.st_city);
        mStrartNodeView = (AutoCompleteTextView) findViewById(R.id.st_node);
        mEditEndCity = (EditText) findViewById(R.id.ed_city);
        mEndNodeView = (AutoCompleteTextView) findViewById(R.id.ed_node);
        mIncitySpinner = (Spinner) findViewById(R.id.tactics_incity_sp);
        mTranstypeSpinner = (Spinner) findViewById(R.id.transtype_intercity_sp);
        mIntercitySpinner = (Spinner) findViewById(R.id.tactics_intercity_sp);

        // ???????????????
        mMapView = (MapView) findViewById(R.id.map);
        mBaidumap = mMapView.getMap();


        initLocation();
        String s = intent.getStringExtra("id");
        mEndNodeView.setText(s);

        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        // ????????????????????????
        mBaidumap.setOnMapClickListener(this);
        // ??????????????????????????????????????????
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        // ????????????????????????option
        if (mMassTransitRoutePlanOption == null){
            // ?????????????????????
            mMassTransitRoutePlanOption = new MassTransitRoutePlanOption();
        }
        // ??????????????????????????????
        setTacticsIncity();
        // ??????????????????????????????
        setTacticsIntercity();
        // ??????????????????????????????
        setTransTypeIntercity();
        mNodeUtils = new NodeUtils(this, mBaidumap);

        PlanNode startNode = PlanNode.withLocation(myLoc);
        // ????????????
        PlanNode endNode = PlanNode.withLocation(ed);

        // ??????????????????????????????
        //mSearch.masstransitSearch(mMassTransitRoutePlanOption.from(startNode).to(endNode));

    }

    /**
     * ???????????????
     */
    public void initLocation(){
        // ??????????????????
        mBaidumap.setMyLocationEnabled(true);
        // ???????????????
        mLocClient = new LocationClient(this);
        MyLocationListenner myListener = new MyLocationListenner();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        // ??????gps
        option.setOpenGps(true);
        // ??????????????????
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * ??????SDK????????????
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // MapView ???????????????????????????????????????
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// ????????????????????????????????????????????????
                    .direction(location.getDirection()) // ?????????????????????????????????????????????????????????0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            // ??????????????????, ??????????????????????????????????????????????????????
            mBaidumap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                myLoc = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(myLoc).zoom(15.0f);
                mBaidumap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                //addMarker(latLng);
            }
            //if (mMarker != null){
            //    mMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
            //}
        }
    }

    /**
     *  ??????????????????????????????
     */
    private void  setTacticsIncity(){
        List<String> list = new ArrayList<>();
        list.add("??????");
        list.add("?????????");
        list.add("?????????");
        list.add("????????????");
        list.add("?????????");
        list.add("????????????");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mIncitySpinner.setAdapter(adapter);
        mIncitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // ??????
                        mMassTransitRoutePlanOption.tacticsIncity(MassTransitRoutePlanOption.TacticsIncity.ETRANS_SUGGEST);
                        break;
                    case 1:
                        // ?????????
                        mMassTransitRoutePlanOption.tacticsIncity(MassTransitRoutePlanOption.TacticsIncity.ETRANS_LEAST_TRANSFER);
                        break;
                    case 2:
                        // ?????????
                        mMassTransitRoutePlanOption.tacticsIncity(MassTransitRoutePlanOption.TacticsIncity.ETRANS_LEAST_WALK);
                        break;
                    case 3:
                        // ????????????
                        mMassTransitRoutePlanOption.tacticsIncity(MassTransitRoutePlanOption.TacticsIncity.ETRANS_NO_SUBWAY);
                        break;
                    case 4:
                        // ?????????
                        mMassTransitRoutePlanOption.tacticsIncity(MassTransitRoutePlanOption.TacticsIncity.ETRANS_LEAST_TIME);
                        break;
                    case 5:
                        // ????????????
                        mMassTransitRoutePlanOption.tacticsIncity(MassTransitRoutePlanOption.TacticsIncity.ETRANS_SUBWAY_FIRST);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * ??????????????????????????????
     */
    private void  setTacticsIntercity(){
        List<String> list = new ArrayList<>();
        list.add("?????????");
        list.add("?????????");
        list.add("?????????");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mIntercitySpinner.setAdapter(adapter);
        mIntercitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // ?????????
                        mMassTransitRoutePlanOption.tacticsIntercity(MassTransitRoutePlanOption.TacticsIntercity.ETRANS_LEAST_TIME);
                        break;
                    case 1:
                        // ?????????
                        mMassTransitRoutePlanOption.tacticsIntercity(MassTransitRoutePlanOption.TacticsIntercity.ETRANS_START_EARLY);
                        break;
                    case 2:
                        // ?????????
                        mMassTransitRoutePlanOption.tacticsIntercity(MassTransitRoutePlanOption.TacticsIntercity.ETRANS_LEAST_PRICE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * ??????????????????????????????
     */
    private void  setTransTypeIntercity(){
        List<String> list = new ArrayList<>();
        list.add("????????????");
        list.add("????????????");
        list.add("????????????");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mTranstypeSpinner.setAdapter(adapter);
        mTranstypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // ????????????
                        mMassTransitRoutePlanOption.transtypeintercity(MassTransitRoutePlanOption.TransTypeIntercity.ETRANS_TRAIN_FIRST);
                        break;
                    case 1:
                        // ????????????
                        mMassTransitRoutePlanOption.transtypeintercity(MassTransitRoutePlanOption.TransTypeIntercity.ETRANS_PLANE_FIRST);
                        break;
                    case 2:
                        // ????????????
                        mMassTransitRoutePlanOption.transtypeintercity(MassTransitRoutePlanOption.TransTypeIntercity.ETRANS_COACH_FIRST);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * ??????????????????????????????
     */
    public void searchButtonProcess(View v) {
        // ?????????????????????????????????
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.clear();

        // ????????????????????? ????????????
        PlanNode startNode;// = PlanNode.withCityNameAndPlaceName(mEditStartCity.getText().toString().trim(), mStrartNodeView.getText().toString().trim());
        // ????????????
        PlanNode endNode; //= PlanNode.withCityNameAndPlaceName(mEditEndCity.getText().toString().trim(), mEndNodeView.getText().toString().trim());

        startNode = PlanNode.withLocation(myLoc);
        // ????????????
        endNode = PlanNode.withLocation(ed);

        // ??????????????????????????????
        mSearch.masstransitSearch(mMassTransitRoutePlanOption.from(startNode).to(endNode));
    }

    /**
     * ????????????
     */
    public void nodeClick(View v) {
        if (null != mMassTransitRouteLine && null != mMassTransitRouteResult) {
            mNodeUtils.browseTransitRouteNode(v,mMassTransitRouteLine,mMassTransitRouteResult);
        }
    }

    /**
     * ?????????????????????????????????????????????
     * ????????? ?????????????????????????????????.
     */
    public void changeRouteIcon(View v) {
        if (mRouteOverlay == null) {
            return;
        }
        if (mUseDefaultIcon) {
            ((Button) v).setText("????????????????????????");
            Toast.makeText(this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
        } else {
            ((Button) v).setText("?????????????????????");
            Toast.makeText(this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
        }
        mUseDefaultIcon = !mUseDefaultIcon;
        mRouteOverlay.removeFromMap();
        mRouteOverlay.addToMap();
    }


    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {

    }

    /**
     * ????????????????????????????????????
     *
     * @param result ??????????????????????????????
     */
    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
        if (result != null && result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // ????????????????????????????????????
            Toast.makeText(MassTransitRouteDemo.this, "?????????????????????????????????????????????result.getSuggestAddrInfo()??????????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }

        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(MassTransitRouteDemo.this, "????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mMassTransitRouteResult = result;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);

            if (!hasShowDialog) {
                // ????????????
                SelectRouteDialog selectRouteDialog = new SelectRouteDialog(MassTransitRouteDemo.this,
                        result.getRouteLines(), RouteLineAdapter.Type.MASS_TRANSIT_ROUTE);
                selectRouteDialog.setTitle("?????????????????????");
                mMassTransitRouteResult = result;
                selectRouteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        hasShowDialog = false;
                    }
                });
                selectRouteDialog.setOnItemInDlgClickLinster(new SelectRouteDialog.OnItemInDlgClickListener() {
                    public void onItemClick(int position) {
                        MyMassTransitRouteOverlay overlay = new MyMassTransitRouteOverlay(mBaidumap);
                        mBaidumap.setOnMarkerClickListener(overlay);
                        mRouteOverlay = overlay;
                        mMassTransitRouteLine = mMassTransitRouteResult.getRouteLines().get(position);
                        overlay.setData(mMassTransitRouteResult.getRouteLines().get(position));
                        // ?????????????????????
                        MassTransitRouteLine line = mMassTransitRouteResult.getRouteLines().get(position);
                        overlay.setData(line);
                        if (mMassTransitRouteResult.getOrigin().getCityId() == mMassTransitRouteResult.getDestination().getCityId()) {
                            // ??????
                            overlay.setSameCity(true);
                        } else {
                            // ??????
                            overlay.setSameCity(false);
                        }
                        mBaidumap.clear();
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    }

                });

                // ???????????????????????????Activity?????????????????????Dialog??????????????????????????????????????????
                if (!isFinishing()) {
                    selectRouteDialog.show();
                    hasShowDialog = true;
                }
            }
        }
    }


    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {

    }

    private class MyMassTransitRouteOverlay extends MassTransitRouteOverlay {
        private MyMassTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (mUseDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (mUseDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public void onMapPoiClick(MapPoi poi) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ??????????????????
        if (mSearch != null) {
            mSearch.destroy();
        }
        mBaidumap.clear();
        mMapView.onDestroy();
    }
}
