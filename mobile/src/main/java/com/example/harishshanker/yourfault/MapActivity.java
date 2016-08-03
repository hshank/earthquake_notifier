package com.example.harishshanker.yourfault;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.widget.TextView;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    public static double longi;
    public static double lati;
    public static String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(message);
        tv.setBackgroundColor(Color.parseColor("#eaea14"));
        tv.setTextColor(Color.parseColor("#35478C"));

        LatLng point = new LatLng(lati, longi);
        map.addMarker(new MarkerOptions().position(point).title(""));
        map.moveCamera(CameraUpdateFactory.newLatLng(point));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 5.0f));
    }
}