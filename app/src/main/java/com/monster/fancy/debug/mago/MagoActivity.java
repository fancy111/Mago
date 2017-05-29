package com.monster.fancy.debug.mago;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.IconType;
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
    //static final int[] hud_imgActions = new int[31];

    private double mFriendLatitude;
    private double mFriendLongitude;
    private double mLatitude;
    private double mLongitude;

    private String nextRoadNameTextStr;
    private String restDistanceTextStr;
    private String nextRoadDisTextStr;
    private int resId;
    private int lastResId;

    private TextView nextRoadNameText;
    private TextView restDistanceText;
    private ImageView roadsignImg;
    private TextView nextRoadDistanceText;

    private AMapNavi mAMapNavi;

    //MAGO的动画
    Animation rotateAnimBack = null;


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

    //rotate the mago
    private void rotateImage(int resId){
        //初始化动画
        Animation rotateAnim = null;

        switch (resId){
            case IconType.LEFT:
                //set the last time resId;
                lastResId = resId;
                rotateAnim = AnimationUtils.loadAnimation(this,R.anim.left);
                break;
            case IconType.RIGHT:
                //set the last time resId;
                lastResId = resId;
                rotateAnim = AnimationUtils.loadAnimation(this,R.anim.right);
                break;
            case IconType.LEFT_FRONT:
                //set the last time resId;
                lastResId = resId;
                rotateAnim = AnimationUtils.loadAnimation(this,R.anim.leftfront);
                break;
            case IconType.RIGHT_FRONT:
                //set the last time resId;
                lastResId = resId;
                rotateAnim = AnimationUtils.loadAnimation(this,R.anim.rightfront);
                break;
            case IconType.LEFT_BACK:
                //set the last time resId;
                lastResId = resId;
                rotateAnim = AnimationUtils.loadAnimation(this,R.anim.leftback);
                break;
            case IconType.RIGHT_BACK:
                //set the last time resId;
                lastResId = resId;
                rotateAnim = AnimationUtils.loadAnimation(this,R.anim.rightback);
                break;
            case IconType.STRAIGHT:
                //if now the sig is go straight,rotate back the mago
                rotateBackMago(lastResId);
                //set the last time resId;
                lastResId = -1;
            default:
                lastResId = -1;
                break;
        }
        //start the animation
        if(rotateAnim != null)
        {
            //动画为线性的
            rotateAnim.setInterpolator(new LinearInterpolator());

            rotateAnim.setFillAfter(true);

            roadsignImg.setImageResource(R.drawable.magoline);

            //开始动画
            roadsignImg.startAnimation(rotateAnim);
        }
    }

    private void rotateBackMago(int id){
        Toast.makeText(getApplicationContext(),"back :"+id,Toast.LENGTH_SHORT).show();
        switch (id){
            case IconType.LEFT:
                rotateAnimBack = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.tleft);
                break;
            case IconType.RIGHT:
                rotateAnimBack = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.tright);
                break;
            case IconType.LEFT_FRONT:
                rotateAnimBack = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.tleftfront);
                break;
            case IconType.RIGHT_FRONT:
                rotateAnimBack = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.trightfront);
                break;
            case IconType.LEFT_BACK:
                rotateAnimBack = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.tleftback);
                break;
            case IconType.RIGHT_BACK:
                rotateAnimBack = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.trightback);
                break;
            default:
                break;
        }
        if(rotateAnimBack != null)
        {
            lastResId = -1;
            rotateAnimBack.setInterpolator(new LinearInterpolator());
            roadsignImg.startAnimation(rotateAnimBack);
            roadsignImg.setImageResource(R.drawable.mago);
        }
    }


    private void init(){
        Log.d("mago activity","now is in init function");
        //初始化控件
        initWidgets();

        //获取AMapNavi实例
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        //添加监听回调，用于处理算路成功
        mAMapNavi.addAMapNaviListener(this);

        //计算路径
        mAMapNavi.calculateWalkRoute(new NaviLatLng(mLatitude, mLongitude), new NaviLatLng(mFriendLatitude, mFriendLongitude));


        this.onNaviInfoUpdate(this.mAMapNavi.getNaviInfo());
        Log.d("mago activity","now init function is over");
    }

    /**
     * init the widgets
     */
    private void initWidgets(){
        Log.d("mago activity","now is in initWidgets function");
        this.restDistanceText = (TextView) findViewById(R.id.restDistanceText);
        this.nextRoadNameText = (TextView) findViewById(R.id.nextRoadNameText);
        this.nextRoadDistanceText = (TextView) findViewById(R.id.nextRoadDistanceText);
        this.roadsignImg = (ImageView) findViewById(R.id.roadsignimg);

        this.updateWidgetContent();
        Log.d("mago activity","now initWidgets function is over");
    }

    /**
     * 更新控件信息
     */
    private void updateWidgetContent(){
        Log.d("mago activity","now is in updateWidgetContent function");
        if(this.nextRoadNameText != null) {
            this.nextRoadNameText.setText(this.nextRoadNameTextStr);
            Log.d("mago activity","nextRoadNameTextStr is "+ nextRoadNameTextStr);
        }

        if(this.nextRoadDistanceText != null) {
            this.nextRoadDistanceText.setText(this.nextRoadDisTextStr);
            Log.d("mago activity","nextRoadDisTextStr is "+ nextRoadDisTextStr);
        }

        if(this.restDistanceText != null) {
            this.restDistanceText.setText(this.restDistanceTextStr);
            Log.d("mago activity","restDistanceTextStr is "+ restDistanceTextStr);
        }

        if(this.roadsignImg != null && this.resId != 0 && this.resId != 1) {
            Log.d("mago activity","resId is "+ resId);
            rotateImage(resId);

            //clear the rotate animation
            roadsignImg.clearAnimation();

            //this.roadsignimg.setImageResource(hud_imgActions[this.resId]);
        }

        Log.d("mago activity","now updateWidgetContent function is over");
    }

    /**
     * 更新界面
     * @param naviInfo 导航信息
     */
    private void updateMagoUI(NaviInfo naviInfo) {
        if(naviInfo == null) {
            return;
        }

        //更新导航的数据
        this.nextRoadNameTextStr = naviInfo.getNextRoadName();
        this.restDistanceTextStr = naviInfo.getPathRetainDistance()+"米";
        this.nextRoadDisTextStr = naviInfo.m_SegRemainDis+"米";
        this.resId = naviInfo.getIconType();
        //更新界面
        this.updateWidgetContent();

    }

    //-----------------AMapNaviListener的实现--------------------

    @Override
    public void onInitNaviFailure() {

    }

    /**
     *当 AMapNavi 对象初始化成功后，会进入 onInitNaviSuccess
     * 回调函数，在该回调函数中调用路径规划方法计算路径。
     */
    @Override
    public void onInitNaviSuccess() {
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

    //测试磁铁旋转用的
    public void rotate(View view) {
        switch (view.getId()){
            case R.id.leftBtn:
                rotateImage(IconType.LEFT);
                break;
            case R.id.rightBtn:
                rotateImage(IconType.RIGHT);
                break;
            case R.id.leftFrontBtn:
                rotateImage(IconType.LEFT_FRONT);
                break;
            case R.id.rightFrontBtn:
                rotateImage(IconType.RIGHT_FRONT);
                break;
            case R.id.leftBackBtn:
                rotateImage(IconType.LEFT_BACK);
                break;
            case R.id.rightBackBtn:
                rotateImage(IconType.RIGHT_BACK);
                break;
            case R.id.straightBtn:
                rotateImage(IconType.STRAIGHT);
                break;

        }
    }
}
