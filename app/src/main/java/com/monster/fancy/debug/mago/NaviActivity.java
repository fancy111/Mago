package com.monster.fancy.debug.mago;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;

/**
 * Created by rushzhou on 4/24/17.
 */

public class NaviActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAMapNavi = AMapNavi.getInstance(this);
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.setEmulatorNaviSpeed(150);

        double[] locations = getIntent().getDoubleArrayExtra("EXTRA_LOCATIONS");
        mLatitude = locations[0];
        mLongitude = locations[1];
        mFriendLatitude = locations[2];
        mFriendLongitude = locations[3];

        setContentView(R.layout.activity_navi);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
        mAMapNavi.calculateWalkRoute(new NaviLatLng(mLatitude, mLongitude), new NaviLatLng(mFriendLatitude, mFriendLongitude));
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        mAMapNavi.calculateWalkRoute(new NaviLatLng(mLatitude, mLongitude), new NaviLatLng(mFriendLatitude, mFriendLongitude));
    }

    @Override
    public void onCalculateRouteSuccess() {
        super.onCalculateRouteSuccess();
        mAMapNavi.startNavi(NaviType.GPS);
    }

    // button listener
    public void startHud(View view) {
        Intent intent = new Intent(getBaseContext(), MagoActivity.class);
        double[] locations = new double[4];
        locations[0] = mLatitude;
        locations[1] = mLongitude;
        locations[2] = mFriendLatitude;
        locations[3] = mFriendLongitude;
        intent.putExtra("EXTRA_LOCATIONS", locations);
        startActivity(intent);
    }
}
