package com.monster.fancy.debug.mago;

import android.content.Intent;
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
import android.widget.Button;
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
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.monster.fancy.debug.mago.MainActivity.mClient;

/**
 * Created by rushzhou on 4/27/17.
 */

public class LocaActivity extends CheckPermissionsActivity implements LocationSource, AMapLocationListener, AMapNaviListener {
    final static int CALLEE = 0;
    final static int CALLER = 1;

    private AMap mAMap;
    private MapView mMapView;
    private TextView tvResult;
    private Button mStartNaivBtn;

    private AMapLocationClient mLocationClient;
    private OnLocationChangedListener mListener;
    private AMapLocationClientOption mLocationOption;

    private Marker marker;
    private BitmapDescriptor mBitmapDescriptor;
    private Marker friendMarker;
    private BitmapDescriptor mFriendBitmapDescriptor;

    private Bitmap bmp_me;
    private Bitmap bmp_friend;

    private double mFriendLatitude;
    private double mFriendLongitude;
    private LatLng friendLatLng;
    private double mLatitude;
    private double mLongitude;

    private AMapNavi mAMapNavi;

    private int whoAmI;
    private boolean firstTime;
    private String peerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loca);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvResult.setVisibility(View.GONE);
        mStartNaivBtn = (Button) findViewById(R.id.start_naiv_btn);
        firstTime = true;

        whoAmI = getIntent().getIntExtra("whoAmI", -1);
        peerId = getIntent().getStringExtra("peerId");

        genMarker();

        getPeerGps();

        initMap();
    }

    private void getPeerGps() {
        AVIMLocationMessage message = getIntent().getParcelableExtra("locationMessage");
        AVGeoPoint peerGps = message.getLocation();
        mFriendLatitude = peerGps.getLatitude();
        mFriendLongitude = peerGps.getLongitude();
        friendLatLng = new LatLng(mFriendLatitude, mFriendLongitude);
    }

    private Bitmap getBitmap(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200){
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * generate custom marker using user photo
     */
    private void genMarker() {
        AVQuery<AVUser> avQuery = new AVQuery<>("_User");
        avQuery.getInBackground(peerId, new GetCallback<AVUser>() {
            @Override
            public void done(AVUser friend, AVException ae) {
                Bitmap src = BitmapFactory.decodeResource(getResources(),R.drawable.photo);
                if (AVUser.getCurrentUser().getAVFile("avatar") != null)
                    src = getBitmap(AVUser.getCurrentUser().getAVFile("avatar").getUrl());
                Bitmap bmp = Bitmap.createScaledBitmap(src, 80, 80, false);

                Bitmap src_f = BitmapFactory.decodeResource(getResources(),R.drawable.photo);
                if (friend.getAVFile("avatar") != null)
                    src_f = getBitmap(friend.getAVFile("avatar").getUrl());
                Bitmap bmp_f = Bitmap.createScaledBitmap(src_f, 80, 80, false);

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
                Point d = new Point((width / 2) + (triWidth / 2), height - triHeight);
                Point e = new Point((width / 2), height);
                Point f = new Point((width / 2) - (triWidth / 2), height - triHeight);
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
                canvas1.drawBitmap(bmp, border / 2, border / 2, color);
                mBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmp_me);
                canvas2.drawPath(path, color);
                canvas2.drawBitmap(bmp_f, border / 2, border / 2, color);
                mFriendBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmp_friend);
            }
        });
    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
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
        friendMarker = mAMap.addMarker(new MarkerOptions()
                .icon(mFriendBitmapDescriptor)
                .anchor(0.5f, 1));

        setUpNavi();
    }

    private void setUpNavi() {
        //获取AMapNavi实例
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        //添加监听回调，用于处理算路成功
        mAMapNavi.addAMapNaviListener(this);
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
                if (firstTime && whoAmI==CALLEE) {
                    firstTime = false;
                    mClient.createConversation(Arrays.asList(peerId), "whatsup", null,
                            new AVIMConversationCreatedCallback() {
                                @Override
                                public void done(AVIMConversation conversation, AVIMException e) {
                                    if (e == null) {
                                        AVIMLocationMessage msg = new AVIMLocationMessage();
                                        msg.setLocation(new AVGeoPoint(mLatitude, mLongitude));
                                        // 发送消息
                                        conversation.sendMessage(msg, new AVIMConversationCallback() {
                                            @Override
                                            public void done(AVIMException e) {
                                                if (e == null) {
                                                    Log.d("whatsup", "发送成功！");
                                                }
                                                else {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                }
                LatLng latLng = new LatLng(mLatitude, mLongitude);
                mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
                mAMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                // 显示marker
                marker.setPosition(latLng);
                friendMarker.setPosition(friendLatLng);

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

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        deactivate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    // Button listeners
    public void startNavigation(View view) {
        Intent intent = new Intent(getBaseContext(), NaviActivity.class);
        double[] locations = new double[4];
        locations[0] = mLatitude;
        locations[1] = mLongitude;
        locations[2] = mFriendLatitude;
        locations[3] = mFriendLongitude;
        intent.putExtra("EXTRA_LOCATIONS", locations);
        startActivity(intent);
    }

    /**
     * methods to implement because of AMapNaviListener (below)
     */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onInitNaviFailure() {
        String errText = "InitNaviFailure";
        Log.e("AmapErr", errText);
        tvResult.setVisibility(View.VISIBLE);
        tvResult.setText(errText);
    }

    @Override
    public void onInitNaviSuccess() {
        if(whoAmI == CALLEE)
            mAMapNavi.calculateWalkRoute(new NaviLatLng(mLatitude, mLongitude), new NaviLatLng(mFriendLatitude, mFriendLongitude));
        else if(whoAmI == CALLER)
            ;
            //mAMapNavi.calculateWalkRoute(mCoordList);
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
        routeOverLay.zoomToSpan();
        mStartNaivBtn.setEnabled(true);

        if(whoAmI == CALLEE) {
            AMapNaviPath path = mAMapNavi.getNaviPath();
            List<NaviLatLng> coordList = path.getCoordList();
            Collections.reverse(coordList);
            final List<NaviLatLng> coordListReverse = coordList;
            AVIMLocationMessage message = getIntent().getParcelableExtra("locationMessage");
            String from = message.getFrom();
            mClient.createConversation(Arrays.asList(from), "what's up", null,
                    new AVIMConversationCreatedCallback() {
                        @Override
                        public void done(AVIMConversation conversation, AVIMException e) {
                            if (e == null) {
                                AVIMTextMessage msg = new AVIMTextMessage();
                                Gson gson = new Gson();
                                String jsonCoordList = gson.toJson(coordListReverse);
                                Log.d("jsonCoordList", jsonCoordList);
                                msg.setText(jsonCoordList);
                                // 发送消息
                                conversation.sendMessage(msg, new AVIMConversationCallback() {
                                    @Override
                                    public void done(AVIMException e) {
                                        if (e == null) {
                                            Log.d("what's up", "发送成功！");
                                        }
                                    }
                                });
                            }
                        }
                    });
        }
    }

    @Override
    public void onCalculateRouteFailure(int i) {
        String errText = "CalculateRouteFailure";
        Log.e("AmapErr", errText);
        tvResult.setVisibility(View.VISIBLE);
        tvResult.setText(errText);
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
        if (!b) {
            String errText = "Gps closed";
            Log.e("AmapErr", errText);
            tvResult.setVisibility(View.VISIBLE);
            tvResult.setText(errText);
        }
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
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
}

