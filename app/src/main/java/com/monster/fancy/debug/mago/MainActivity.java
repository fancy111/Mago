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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.monster.fancy.debug.util.MyLeanCloudApp;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends CheckPermissionsActivity implements LocationSource, AMapLocationListener {


    private ImageView avatarImg;
    private TextView usernameText;
    private TextView phonenumText;

    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawer;

    private AMap mAMap;
    private MapView mMapView;

    private AMapLocationClient mLocationClient;
    private OnLocationChangedListener mListener;
    private AMapLocationClientOption mLocationOption;

    private Marker marker;
    private BitmapDescriptor mBitmapDescriptor;

    private Bitmap bmp_me;

    private double mLatitude;
    private double mLongitude;

    private AVUser mAVUser;
    static public AVIMClient mClient;

    private AVFile avatarFile;

    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        avatarImg = (ImageView) findViewById(R.id.mainAvatar_img);
        usernameText = (TextView) findViewById(R.id.mainUsername_text);
        phonenumText = (TextView) findViewById(R.id.mainPhoneNum_text);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer = (LinearLayout) findViewById(R.id.left_drawer);

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mAVUser = AVUser.getCurrentUser();

        new AlertDialog.Builder(this).setTitle("请保持网络畅通，这对于程序正确运行很重要")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

        setMyInfo();

        clientLogin();

        genMarker();

        initMap();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * set my basic info
     */
    private void setMyInfo() {
        usernameText.setText(mAVUser.getUsername());
        phonenumText.setText(mAVUser.getMobilePhoneNumber());
        if (mAVUser.getAVFile("avatar") != null) {
            avatarFile = mAVUser.getAVFile("avatar");
            Picasso.with(MainActivity.this).load(avatarFile.getUrl()).into(avatarImg);
        }
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
     * Generate custom marker using user photo
     */
    private void genMarker(){
        Bitmap src = BitmapFactory.decodeResource(getResources(),R.drawable.photo);
        if (avatarFile != null)
            src = getBitmap(avatarFile.getUrl());
        Bitmap bmp = Bitmap.createScaledBitmap(src, 80, 80, false);

        int border = 10;
        int triWidth = 20;
        int triHeight = 20;
        int width = 80 + border;
        int height = 80 + border + triHeight;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        bmp_me = Bitmap.createBitmap(width, height, conf);

        Canvas canvas1 = new Canvas(bmp_me);

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
    }

    private void clientLogin(){
        AVIMClient client = AVIMClient.getInstance(mAVUser.getObjectId());
        client.open(new AVIMClientCallback(){
            @Override
            public void done(AVIMClient client,AVIMException e){
                if(e == null){
                    mClient = client;
                    Log.d(mAVUser.getObjectId(), "登录成功！");
                }
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
            myLocationStyle.interval(1000);
            // 将自定义的 myLocationStyle 对象添加到地图上
            mAMap.setMyLocationStyle(myLocationStyle);
            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            mAMap.setMyLocationEnabled(true);
            // setup marker
            marker = mAMap.addMarker(new MarkerOptions()
                    .icon(mBitmapDescriptor)
                    .anchor(0.5f, 1));
        }
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
                LatLng latLng = new LatLng(mLatitude, mLongitude);
                // 显示marker
                marker.setPosition(latLng);
                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": "
                        + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    protected void onResume() {
        Log.d("resume","in on resume func");
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

    public void pageJump(View view) {
        int id = view.getId();
        Intent intent = null;
        switch (id) {
            case R.id.myfriend_text:
                intent = new Intent(MainActivity.this, AdressListActivity.class);
                intent.putExtra("myGps", new double[]{mLatitude, mLongitude});
                startActivity(intent);
                break;
            case R.id.setting_text:
                intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.help_text:
                intent = new Intent(MainActivity.this, SystemHelpActivity.class);
                startActivity(intent);
                break;
//            case R.id.callrecord_text:
//                intent = new Intent(MainActivity.this, CallRecordsActivity.class);
//                startActivity(intent);
//                break;
            case R.id.logout_text:
                //user log out
                AVUser.getCurrentUser().logOut();
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
