package com.example.maskmap;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Menu3Activity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.CurrentLocationEventListener, CalloutBalloonAdapter {


    private MapView mapView;
    private MapPoint currentPoint;

    private Button btn_id_tracking_on_off;
    private Button btn_id_currentLocation;
    private Button btn_id_road;
    private Button btn_id_hybrid;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu3);

        // 카카오맵
        createKakaoMap();


        // 액션바 타이틀 변경
        getSupportActionBar().setTitle(R.string.menu_3);
        // 액션바 배경색 변경
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFF6666));



        btn_id_tracking_on_off=findViewById(R.id.btn_id_tracking_on_off);
        btn_id_currentLocation=findViewById(R.id.btn_id_currentLocation);
        btn_id_road=findViewById(R.id.btn_id_road);
        btn_id_hybrid=findViewById(R.id.btn_id_hybrid);

        btn_id_tracking_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapView.getCurrentLocationTrackingMode() != MapView.CurrentLocationTrackingMode.TrackingModeOff) {
                    // 현위치 트래킹 기능을 Off한 후에 현위치 아이콘을 지도 화면에서 제거
                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                    mapView.setShowCurrentLocationMarker(false);
                    btn_id_tracking_on_off.setBackgroundResource(R.color.buttons);
                }
                else {
                    // 트래킹 기능 On
                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                    btn_id_tracking_on_off.setBackgroundResource(R.color.trackingButtonOn);
                }
            }
        });

        btn_id_currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPoint != null) mapView.setMapCenterPoint(currentPoint, false);
            }
        });

        btn_id_road.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setMapType(MapView.MapType.Standard);
            }
        });

        btn_id_hybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setMapType(MapView.MapType.Hybrid);
            }
        });


    }

    private void createKakaoMap() {
        // 카카오맵 객체 생성
        mapView = new MapView(this);

        // 카카오맵 View Event
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapView.setCurrentLocationEventListener(this);
        // 구현한 CalloutBalloonAdapter 등록
        mapView.setCalloutBalloonAdapter(this);

        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);


        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);


        // 카카오맵을 띄울 View 객체 생성
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.removeAllPOIItems();
        // 현위치 트래킹 기능을 Off한 후에 현위치 아이콘을 지도 화면에서 제거
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView.setShowCurrentLocationMarker(false);
    }


    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        if(mapPoint!=null) currentPoint=mapPoint;

    }


    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }





    private void fetchStoreSale(double lat, double lng, int m) {
        // Retrofit api 객체 생성
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json/").addConverterFactory(GsonConverterFactory.create()).build();
        MaskApi maskApi = retrofit.create(MaskApi.class);
        maskApi.getStoresByGeo(lat, lng, m).enqueue((new Callback<StoreSaleResult>() {
            @Override
            public void onResponse(Call<StoreSaleResult> call, Response<StoreSaleResult> response) {
                if (response.code() == 200) {
                    StoreSaleResult result = response.body();
                    updateMapMarkers(result);
                }
            }

            @Override
            public void onFailure(Call<StoreSaleResult> call, Throwable t) {

            }
        }));
    }

    private void updateMapMarkers(StoreSaleResult result) {
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

        int width = dm.widthPixels;

        int height = dm.heightPixels;


        if (result.stores != null && result.stores.size() > 0) {
            for (Store store : result.stores) {
                if(new MapPointBounds(MapPoint.mapPointWithScreenLocation(0, height),MapPoint.mapPointWithScreenLocation(width, 0)).contains(MapPoint.mapPointWithGeoCoord(store.lat, store.lng))) return;
                // 마커 객체 생성
                MapPOIItem marker = new MapPOIItem();
                // 마커 객체 저장
                marker.setUserObject(store);
                // 마커 경로 지정
                marker.setMapPoint(MapPoint.mapPointWithGeoCoord(store.lat, store.lng));
                marker.setItemName(store.name);
                // 마커타입을 커스텀 마커로 지정
                marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                if ("plenty".equalsIgnoreCase(store.remain_stat)) {
                    marker.setCustomImageResourceId(R.drawable.marker_green);
                } else if ("some".equalsIgnoreCase(store.remain_stat)) {
                    marker.setCustomImageResourceId(R.drawable.marker_yellow);
                } else if ("few".equalsIgnoreCase(store.remain_stat)) {
                    marker.setCustomImageResourceId(R.drawable.marker_red);
                } else {
                    marker.setCustomImageResourceId(R.drawable.marker_gray);
                }
                // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정
                marker.setCustomImageAnchor(0.5f, 1.0f);
                // 마커 생성
                mapView.addPOIItem(marker);

            }
        }
    }



    @Override
    public void onMapViewInitialized(MapView mapView) {

    }


    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    // 줌 레벨 4 이하 제한하기
    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
        if(i>4) {
            mapView.zoomIn(false);
        }
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

        fetchStoreSale(mapPoint.getMapPointGeoCoord().latitude, mapPoint.getMapPointGeoCoord().longitude, 5000);
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }


    @Override
    public View getCalloutBalloon(MapPOIItem marker) {
        Store store = (Store)marker.getUserObject();
        View view = getLayoutInflater().inflate(R.layout.activity_custom_callout_balloon_adapter, null);
        ((TextView)view.findViewById(R.id.name)).setText(store.name);

        if("plenty".equalsIgnoreCase(store.remain_stat)) {
            ((TextView)view.findViewById(R.id.stock)).setText("100개 이상");
        } else if("some".equalsIgnoreCase(store.remain_stat)) {
            ((TextView)view.findViewById(R.id.stock)).setText("30개 이상 100개 미만");
        } else if("few".equalsIgnoreCase(store.remain_stat)) {
            ((TextView)view.findViewById(R.id.stock)).setText("2개 이상 30개 미만");
        } else if("empty".equalsIgnoreCase(store.remain_stat)) {
            ((TextView)view.findViewById(R.id.stock)).setText("1개 이하");
        } else if("break".equalsIgnoreCase(store.remain_stat)) {
            ((TextView)view.findViewById(R.id.stock)).setText("판매 중지");
        } else {
            ((TextView)view.findViewById(R.id.stock)).setText(null);
        }

        ((TextView)view.findViewById(R.id.time)).setText("입고 : "+store.stock_at);

        return view;
    }

    @Override
    public View getPressedCalloutBalloon(MapPOIItem mapPOIItem) {
        return null;
    }
}

