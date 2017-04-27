package com.monster.fancy.debug.mago;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.RouteOverlayOptions;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.TrafficFacilityInfo;

/**
 * Created by rushzhou on 4/27/17.
 */

public class LocaActivity extends CheckPermissionsActivity implements LocationSource, AMapLocationListener, AMapNaviListener {

    private AMap mAMap;
    private MapView mMapView;
    private TextView tvResult;

    private AMapLocationClient mLocationClient;
    private OnLocationChangedListener mListener;
    private AMapLocationClientOption mLocationOption;

    private Marker marker;
    private BitmapDescriptor mBitmapDescriptor;
    private Marker friendMarker;
    private BitmapDescriptor mFriendBitmapDescriptor;

    private Bitmap bmp_me;
    private Bitmap bmp_friend;

    private double mFriendLatitude = 38.98587022569444;
    private double mFriendLongitude = 117.34032253689236;
    private LatLng friendLatLng = new LatLng(mFriendLatitude, mFriendLongitude);
    private double mLatitude;
    private double mLongitude;

    private AMapNavi mAMapNavi;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loca);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvResult.setVisibility(View.GONE);

        // generate custom marker using user photo
        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.photo);
        Bitmap bmp = Bitmap.createScaledBitmap(src, 80, 80, false);

        int border = 10;
        int triWidth = 20;
        int triHeight = 20;
        int width = 80 + border;
        int height = 80 + border + triHeight;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        bmp_me = Bitmap.createBitmap(width, height, conf);
        bmp_friend = Bitmap.createBitmap(width, height, conf);

        Canvas canvas1 = new Canvas(bmp_me);
        Canvas canvas2 = new Canvas(bmp_friend);

        Point a = new Point(0, 0);
        Point b = new Point(width, 0);
        Point c = new Point(width, height - triHeight);
        Point d = new Point((width/2)+(triWidth/2), height - triHeight);
        Point e = new Point((width/2), height);
        Point f = new Point((width/2)-(triWidth/2), height - triHeight);
        Point g = new Point(0, height - triHeight);

        Path path = new Path();
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(d.x, d.y);
        path.lineTo(e.x, e.y);
        path.lineTo(f.x, f.y);
        path.lineTo(g.x, g.y);

        Paint color = new Paint();
        color.setColor(Color.WHITE);

        canvas1.drawPath(path, color);
        canvas1.drawBitmap(bmp, border/2, border/2, color);
        mBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmp_me);
        canvas2.drawPath(path, color);
        canvas2.drawBitmap(bmp, border/2, border/2, color);
        mFriendBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmp_friend);

        init();
    }

    void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.moveCamera(CameraUpdateFactory.zoomBy(6));
            setUpMap();
        }
    }

    private void setUpMap() {
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        // 自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(0);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        //连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）This is the default mode。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(2000);
        // 将自定义的 myLocationStyle 对象添加到地图上
        mAMap.setMyLocationStyle(myLocationStyle);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        mAMap.setMyLocationEnabled(true);
        // setup marker
        marker = mAMap.addMarker(new MarkerOptions()
                .icon(mBitmapDescriptor)
                .anchor(0.5f, 1));
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mLocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 单次定位
            mLocationOption.setOnceLocation(false);
            //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
            mLocationOption.setInterval(1000);
            // 设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                tvResult.setVisibility(View.GONE);
                // 显示系统小蓝点
                mListener.onLocationChanged(aMapLocation);
                mLatitude = aMapLocation.getLatitude();
                mLongitude = aMapLocation.getLongitude();
                LatLng latLng = new LatLng(mLatitude, mLongitude);
                mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
                mAMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                // 显示marker
                marker.setPosition(latLng);

                String gpsText = "定位: " + aMapLocation.getLatitude() + ", " + aMapLocation.getLongitude();
                tvResult.setVisibility(View.VISIBLE);
                tvResult.setText(gpsText);
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": "
                        + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                tvResult.setVisibility(View.VISIBLE);
                tvResult.setText(errText);
            }
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    // Button listeners
    public void onFriendAnswerCall(View view) {
        friendMarker = mAMap.addMarker(new MarkerOptions()
                .position(friendLatLng)
                .icon(mFriendBitmapDescriptor)
                .anchor(0.5f, 1));

        //获取AMapNavi实例
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        //添加监听回调，用于处理算路成功
        mAMapNavi.addAMapNaviListener(this);

    }
    // Button listeners
    public void startNavigation(View view) {

    }

    // methods to implement because of AMapNaviListener
    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {
        mAMapNavi.calculateWalkRoute(new NaviLatLng(mLatitude, mLongitude), new NaviLatLng(mFriendLatitude, mFriendLongitude));
    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteSuccess() {
        marker.setVisible(false);
        friendMarker.setVisible(false);
        RouteOverLay routeOverLay = new RouteOverLay(mAMap, mAMapNavi.getNaviPath(), this);
        routeOverLay.setStartPointBitmap(bmp_me);
        routeOverLay.setEndPointBitmap(bmp_friend);
        routeOverLay.removeFromMap();
        routeOverLay.addToMap();
        // routeOverLay.zoomToSpan();
    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }
}

