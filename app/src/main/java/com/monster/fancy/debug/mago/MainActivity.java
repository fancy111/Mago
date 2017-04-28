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
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;

public class MainActivity extends CheckPermissionsActivity implements LocationSource, AMapLocationListener {


    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawer;
    private TextView username_text;

    private AMap mAMap;
    private MapView mMapView;
    private TextView tvResult;

    private AMapLocationClient mLocationClient;
    private OnLocationChangedListener mListener;
    private AMapLocationClientOption mLocationOption;

    private Marker marker;
    private BitmapDescriptor mBitmapDescriptor;

    private Bitmap bmp_me;

    private double mLatitude;
    private double mLongitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"xRBlai1ATNmdmRvpFtzOO4fj-gzGzoHsz","D4hgUa86CD1X0WJ7bsbOkyc3");
        // 测试 LeanCloud SDK 是否正常工作的代码
//        AVObject testObject = new AVObject("TestObject");
//        testObject.put("words","Hello World!");
//        testObject.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(AVException e) {
//                if(e == null){
//                    Log.d("saved","success!");
//                }
//            }
//        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer = (LinearLayout) findViewById(R.id.left_drawer);

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

        init();
    }

    void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.moveCamera(CameraUpdateFactory.zoomTo(16));
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
        // myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);
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
                // mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
                // mAMap.moveCamera(CameraUpdateFactory.zoomTo(16));
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

    public void pageJump(View view) {
        int id = view.getId();
        Intent intent = null;
        switch (id) {
            case R.id.myfriend_text:
                intent = new Intent(MainActivity.this, AdressListActivity.class);
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
            case R.id.callrecord_text:
                intent = new Intent(MainActivity.this, CallRecordsActivity.class);
                startActivity(intent);
                break;
            case R.id.logout_text:
                AVUser.logOut();
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
