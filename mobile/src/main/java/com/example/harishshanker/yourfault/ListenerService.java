package com.example.harishshanker.yourfault;
import java.net.*;
import java.io.*;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.os.IBinder;
import java.nio.charset.*;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.*;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListenerService extends WearableListenerService {
    private static final String START_PHOTO = "/start_photo";
    private static final String STOP_PHOTO = "/stop_photo";
    private static final String START_INFO = "/start_info";

    private GoogleApiClient mGoogleApiClient;
    public static String current = "";
    public static int counter = 0;
    static List<String> links = new ArrayList<String>();
    static List<String> mlinks = new ArrayList<String>();

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equalsIgnoreCase(START_PHOTO)) {

            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            startInsta(value);
        }
        else if (messageEvent.getPath().equalsIgnoreCase(STOP_PHOTO)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }


    public void startInsta(String message) {
        if (message.equals(current)){
            if (counter >= links.size()){
                sendOutBroadcast("NO MORE PICS");
                sendMessage(START_INFO, "No More Pictures!");
            }
            else{
                sendOutBroadcast(links.get(counter));
                sendMessage(START_INFO, mlinks.get(counter));
                counter += 1;
            }
        }
        else {
            try {
                current = message;
                links = new ArrayList<String>();;
                mlinks = new ArrayList<String>();
                counter = 0;
                String clientId = "9a642dd4d75847dcac145f85f797dfeb";
                String longi = current.split(":::")[2];
                String lati = current.split(":::")[3];
                URL insta = new URL("https://api.instagram.com/v1/media/search?lat=" + lati + "&lng=" + longi + "&client_id=" + clientId+"&distance=50000");
                URLConnection instaConnect = insta.openConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(instaConnect.getInputStream()));
                String inputLine;
                inputLine = in.readLine();
                boolean first = true;

                String mArr[] = inputLine.split("caption");
                int mcount = 0;
                while (mcount < mArr.length) {
                    if (mcount != 0) {
                        if (mArr[mcount].split("text\":\"").length != 1)
                        {
                            String y = mArr[mcount].split("text\":\"")[1];
                            String x = y.split("\",")[0];
                            mlinks.add(x);
                        }

                    }
                    mcount += 1;
                }
                if (mlinks.size() > counter) {
                    sendMessage(START_INFO, mlinks.get(counter));
                }
                else {
                    sendMessage(START_INFO, "No More Pictures!");
                }

                String arr[] = inputLine.split("standard_resolution\"");
                int count = 0;
                while (count < arr.length) {
                    if (count != 0) {
                        String x = arr[count].split("\",\"width")[0].split("url\":\"")[1];
                        x = x.replace("\\/", "/");
                        links.add(x);
                    }
                    count += 1;
                }
                if (links.size() > 0) {
                    sendOutBroadcast(links.get(counter));
                }
                else {
                    sendOutBroadcast("NO MORE PICS");
                }
                counter += 1;

            } catch (Exception e) {}
        }
    }

    public void sendOutBroadcast(String message){
        Intent intent = new Intent(this, PictureActivity.class);
        intent.putExtra("message", message);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);

    }

    private void sendMessage( final String path, final String text ) {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint){
                    }
                    @Override
                    public void onConnectionSuspended(int cause){
                    }
                })
                .build();
        mGoogleApiClient.connect();

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