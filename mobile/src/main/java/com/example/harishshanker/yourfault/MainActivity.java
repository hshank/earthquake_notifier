package com.example.harishshanker.yourfault;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.service.carrier.CarrierMessagingService.*;
import android.widget.ListView;

import com.google.android.gms.wearable.*;
import java.util.List;

import android.widget.AdapterView;

import android.widget.ListView;
import com.google.android.gms.location.*;
import com.google.android.gms.common.*;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;
import android.location.Location;
import android.widget.ArrayAdapter;

import android.view.View;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    double lati = 0.0;
    double longi = 0.0;

    ListView lv;
    List<Earthquake> quakeList;
    ArrayAdapter<Earthquake> arrayAdapter;


    private GoogleApiClient mGoogleApiClient;

    public static String TAG = "GPSActivity";
    public static int UPDATE_INTERVAL_MS = 10;
    public static int FASTEST_INTERVAL_MS = 10;

    private static final String START_ACTIVITY = "/start_activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("WE GOT A QUAKE"));
        startService(new Intent(this, FaultFinder.class));

        lv = (ListView) findViewById(R.id.listView);
        lv.setBackgroundColor(Color.parseColor("#35478C"));
        quakeList = new ArrayList<Earthquake>();
        arrayAdapter = new ArrayAdapter<Earthquake>(
                this,
                android.R.layout.simple_list_item_1,
                quakeList);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendMessage(START_ACTIVITY, quakeList.get(position).message);
                notifyMaps(quakeList.get(position).longi, quakeList.get(position).lati, quakeList.get(position).mapMessage);
            }

        });
    }

    public void notifyMaps(double longi, double lati, String message){
        MapActivity.longi = longi;
        MapActivity.lati = lati;
        MapActivity.message = message;
        startActivity(new Intent(this, MapActivity.class));

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");

            Log.d("receiver", "Got message: " + message);
            boolean first = false;
            if (message.startsWith("!")){
                message = message.substring(1);
                first = true;
            }

            String inmag = message.split(":::")[0];
            String inlocation = message.split(":::")[1];
            double inlongi = Double.parseDouble(message.split(":::")[2]);
            double inlati = Double.parseDouble(message.split(":::")[3]);

            Location loc1 = new Location("");
            loc1.setLatitude(inlati);
            loc1.setLongitude(inlongi);

            Location loc2 = new Location("");
            loc2.setLatitude(lati);
            loc2.setLongitude(longi);

            double distance = loc1.distanceTo(loc2) / 1000;
            message = inmag+":::"+inlocation+":::"+message.split(":::")[2]+":::"+message.split(":::")[3]+":::"+Double.toString(distance);
            Earthquake e = new Earthquake(message);
            arrayAdapter.insert(e, 0);
            if (!first) {
                sendMessage(START_ACTIVITY, message);
                notifyMaps(inlongi,inlati, e.mapMessage);
            }

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnected(Bundle bundle) {

        // Build a request for continual location updates
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_INTERVAL_MS);


        // Send request for location updates
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, locationRequest, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.getStatus().isSuccess()) {
                            Log.d("YAY", "Successfully requested");
                        } else {
                            Log.e("OH NOOOO", status.getStatusMessage());
                        }
                    }
                });


       Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        onLocationChanged(location);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connResult) {}


    public void onLocationChanged(Location location) {
        longi = location.getLongitude();
        lati = location.getLatitude();
    }



    //How to send a message to the WatchListenerService
    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mGoogleApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient, node.getId(), path, text.getBytes() ).await();
                }
            }
        }).start();
    }

}

