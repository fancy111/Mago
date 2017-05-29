package com.monster.fancy.debug.mago;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.NaviType;
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
import com.autonavi.tbt.TrafficFacilityInfo;

public class MagoActivity extends AppCompatActivity implements AMapNaviListener {
    static final int[] hud_imgActions = new int[]{R.drawable.mago, R.drawable.mago, R.drawable.mago, R.drawable.mago, R.drawable.mago, R.drawable.mago};

    private double mFriendLatitude;
    private double mFriendLongitude;
    private double mLatitude;
    private double mLongitude;

    private String nextRoadNameTextStr;
    private String restDistanceTextStr;
    private String nextRoadDisTextStr;
    private int resId;

    private TextView nextRoadNameText;
    private TextView restDistanceText;
    private ImageView roadsignimg;
    private TextView nextRoadDistanceText;

    private AMapNavi mAMapNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mago);

        //得到导航的起点和终点
        double[] locations = getIntent().getDoubleArrayExtra("EXTRA_LOCATIONS");
        mLatitude = locations[0];
        mLongitude = locations[1];
        mFriendLatitude = locations[2];
        mFriendLongitude = locations[3];

        init();
    }


    private void init() {
        //初始化控件
        initWidgets();

        //获取AMapNavi实例
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        //添加监听回调，用于处理算路成功
        mAMapNavi.addAMapNaviListener(this);

        this.onNaviInfoUpdate(this.mAMapNavi.getNaviInfo());

    }

    /**
     * init the widgets
     */
    private void initWidgets() {
        this.restDistanceText = (TextView) findViewById(R.id.restDistanceText);
        this.nextRoadNameText = (TextView) findViewById(R.id.nextRoadNameText);
        this.nextRoadDistanceText = (TextView) findViewById(R.id.nextRoadDistanceText);
        this.roadsignimg = (ImageView) findViewById(R.id.roadsignimg);

        this.updateWidgetContent();
    }

    /**
     * 更新控件信息
     */
    private void updateWidgetContent() {
        if (this.nextRoadNameText != null) {
            this.nextRoadNameText.setText(this.nextRoadNameTextStr);
        }

        if (this.nextRoadDistanceText != null) {
            this.nextRoadDistanceText.setText(this.nextRoadDisTextStr);
        }

        if (this.restDistanceText != null) {
            this.restDistanceText.setText(this.restDistanceTextStr);
        }

        if (this.roadsignimg != null && this.resId != 0 && this.resId != 1) {
            this.roadsignimg.setImageResource(hud_imgActions[this.resId]);
        }

    }

    /**
     * 更新界面
     *
     * @param naviInfo 导航信息
     */
    private void updateMagoUI(NaviInfo naviInfo) {
        if (naviInfo == null) {
            return;
        }

        //更新导航的数据
        this.nextRoadNameTextStr = naviInfo.getNextRoadName();
        this.restDistanceTextStr = naviInfo.getPathRetainDistance() + "米";
        this.nextRoadDisTextStr = naviInfo.m_SegRemainDis + "米";
        this.resId = naviInfo.m_Icon;

        //更新界面
        this.updateWidgetContent();

    }

    //-----------------AMapNaviListener的实现--------------------

    @Override
    public void onInitNaviFailure() {

    }

    /**
     * 当 AMapNavi 对象初始化成功后，会进入 onInitNaviSuccess
     * 回调函数，在该回调函数中调用路径规划方法计算路径。
     */
    @Override
    public void onInitNaviSuccess() {
        //计算路径
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

    /**
     * 当步行路线规划成功时，会进 onCalculateRouteSuccess 回调，
     * 在该回调函数中，可以进行规划路线显示或开始导航。
     */
    @Override
    public void onCalculateRouteSuccess() {
        //开始导航
        AMapNavi.getInstance(this).startNavi(NaviType.GPS);
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
        this.updateMagoUI(naviInfo);

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

    //按下返回按钮
    public void back(View view) {
        //停止导航
        mAMapNavi.stopNavi();
        //结束界面
        finish();
    }
}
