package com.monster.fancy.debug.mago;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
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
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.monster.fancy.debug.util.MyLeanCloudApp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import static com.monster.fancy.debug.mago.MainActivity.mClient;

/**
 * Created by rushzhou on 4/27/17.
 */

public class LocaActivitySim extends CheckPermissionsActivity implements LocationSource, AMapLocationListener, AMapNaviListener {
    final static int CALLEE = 0;
    final static int CALLER = 1;

    private AMap mAMap;
    private MapView mMapView;

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

    private int whoAmI;
    private boolean firstTime;
    private String peerId;

    private TextView restDistanceTextView;

    private boolean isClosed;

    private AMapNavi mAMapNavi;

    private int reachDistance = 50;

    private class CustomMessageHandler extends AVIMMessageHandler {
        //接收到消息后的处理逻辑
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            if (message instanceof AVIMTextMessage) {
                String text = ((AVIMTextMessage) message).getText();
                if (text.equals("Imout") || text.equals("cancelFromCaller")){
                    Toast.makeText(getApplicationContext(), "您的好友已挂断，失去连接", Toast.LENGTH_SHORT).show();
                    isClosed = true;
                    mAMapNavi.stopNavi();
                    finish();
                    MyLeanCloudApp.isBusy = false;
                }
                else if (text.equals("reachFriend")) {
                    if (!isClosed) {
                        reachFriend();
                    }
                }
            }
            else if (message instanceof AVIMLocationMessage) {
                AVGeoPoint peerGps = ((AVIMLocationMessage) message).getLocation();
                mFriendLatitude = peerGps.getLatitude();
                mFriendLongitude = peerGps.getLongitude();
                friendLatLng = new LatLng(mFriendLatitude, mFriendLongitude);
                moveMarker();

                int distance = (int)AMapUtils.calculateLineDistance(
                        new LatLng(mLatitude, mLongitude),
                        new LatLng(mFriendLatitude, mFriendLongitude));
                restDistanceTextView.setText(distance + "米");

                if (distance <= reachDistance){
                    reachFriend();
                }
            }
        }
    }

    private void reachFriend(){
        if (isClosed){
//            MyLeanCloudApp.isBusy = false;
//            Toast.makeText(this, "您已到达好友附近", Toast.LENGTH_SHORT).show();
//            finish();
            return;
        }
        isClosed = true;
        new AlertDialog.Builder(this).setTitle("您已到达好友附近")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyLeanCloudApp.isBusy = false;
                        mClient.createConversation(Arrays.asList(peerId), "reachFriend", null,
                                new AVIMConversationCreatedCallback() {
                                    @Override
                                    public void done(AVIMConversation conversation, AVIMException e) {
                                        if (e == null) {
                                            AVIMTextMessage msg = new AVIMTextMessage();
                                            msg.setText("reachFriend");
                                            // 发送消息
                                            conversation.sendMessage(msg, new AVIMConversationCallback() {
                                                @Override
                                                public void done(AVIMException e) {
                                                    if (e == null) {
                                                        Log.d("reachFriend", "发送成功！");
                                                    }
                                                    else {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                        mAMapNavi.stopNavi();
                        finish();
                    }
                }).show();
    }

    //按下返回按钮
    public void back(View view) {
        onBackPressed();
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("结束导航？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        MyLeanCloudApp.isBusy = false;
                        mClient.createConversation(Arrays.asList(peerId), "Imout", null,
                                new AVIMConversationCreatedCallback() {
                                    @Override
                                    public void done(AVIMConversation conversation, AVIMException e) {
                                        if (e == null) {
                                            AVIMTextMessage msg = new AVIMTextMessage();
                                            msg.setText("Imout");
                                            // 发送消息
                                            conversation.sendMessage(msg, new AVIMConversationCallback() {
                                                @Override
                                                public void done(AVIMException e) {
                                                    if (e == null) {
                                                        Log.d("Imout", "发送成功！");
                                                    }
                                                    else {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                        else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loca_sim);

        AVIMMessageManager.registerMessageHandler(AVIMMessage.class, new CustomMessageHandler());

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        firstTime = true;
        isClosed = false;

        restDistanceTextView = (TextView) findViewById(R.id.restDistanceText);

        whoAmI = getIntent().getIntExtra("whoAmI", -1);
        peerId = getIntent().getStringExtra("peerId");

        getPeerGps();
    }

    private void getPeerGps() {
        AVIMLocationMessage message = getIntent().getParcelableExtra("locationMessage");
        AVGeoPoint peerGps = message.getLocation();
        mFriendLatitude = peerGps.getLatitude();
        mFriendLongitude = peerGps.getLongitude();
        friendLatLng = new LatLng(mFriendLatitude, mFriendLongitude);

        genMarker();
    }

    private Bitmap getBitmap(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200){
                InputStream inputStream = conn.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            }
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
                Log.d("src==null", "" + (src == null));
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
                Log.d("mBitmapDescriptor==null", "" + (mBitmapDescriptor == null));

                initMap();
            }
        });
    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.getUiSettings().setRotateGesturesEnabled(false);

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
            //连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动
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
            //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms
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
                else {
                    mClient.createConversation(Arrays.asList(peerId), "updateGps", null,
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
                                                    Log.d("updateGps", "发送成功！");
                                                }
                                                else {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                        int distance = (int)AMapUtils.calculateLineDistance(
                                                new LatLng(mLatitude, mLongitude),
                                                new LatLng(mFriendLatitude, mFriendLongitude));
                                        restDistanceTextView.setText(distance + "米");

                                        if (distance <= reachDistance){
                                            reachFriend();
                                        }
                                    }
                                }
                            });
                }
                moveMarker();
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": "
                        + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    private void moveMarker(){
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        marker.setPosition(latLng);
        friendMarker.setPosition(friendLatLng);
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
    protected void onDestroy() {
        super.onDestroy();
        AVIMMessageManager.unregisterMessageHandler(AVIMMessage.class, new CustomMessageHandler());
        mMapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {
        if (whoAmI == CALLER)
            mAMapNavi.calculateWalkRoute(new NaviLatLng(mLatitude, mLongitude), new NaviLatLng(mFriendLatitude, mFriendLongitude));
        else if (whoAmI == CALLEE)
            mAMapNavi.calculateWalkRoute(new NaviLatLng(mFriendLatitude, mFriendLongitude), new NaviLatLng(mLatitude, mLongitude));
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
        RouteOverLay routeOverLay = new RouteOverLay(mAMap, mAMapNavi.getNaviPath(), this);

        routeOverLay.setEndPointBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.transparent));
        routeOverLay.setStartPointBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.transparent));

        routeOverLay.removeFromMap();
        routeOverLay.addToMap();
        routeOverLay.zoomToSpan();
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

