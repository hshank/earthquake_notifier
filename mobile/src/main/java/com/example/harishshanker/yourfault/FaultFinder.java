package com.example.harishshanker.yourfault;
import java.net.*;
import java.io.*;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.content.Context;
import android.os.CountDownTimer;
import java.util.List;
import java.util.ArrayList;
/**
 * Created by harishshanker on 10/8/15.
 */
public class FaultFinder extends Service {

    List magArr = new ArrayList<Earthquake>();
    public String oldLocation = "";
    public String oldX = "";
    public String oldY = "";
    public String oldMag = "hello";
    public boolean checked = false;
    ArrayList begArr = new ArrayList<String>();

    private void createAndStartTimer() {
        int interval = 10000;
        int second = 1000;
        CountDownTimer timer = new CountDownTimer(interval, second) {
            public void onTick(long millisUntilFinished) { }
            public void onFinish() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String url = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=1";
                            URL geo = new URL(url);
                            URLConnection geoConnect = geo.openConnection();
                            BufferedReader in = new BufferedReader(new InputStreamReader(geoConnect.getInputStream()));
                            String inputLine;
                            boolean first = true;
                            while ((inputLine = in.readLine()) != null) {
                                String mag = inputLine.split("mag")[1].split(":")[1].split(",")[0];
                                String place = inputLine.split("place\":\"")[1].split("of ")[1].split("\"")[0];
                                String location = inputLine.split("coordinates\":\\[")[1];
                                String longi = location.split(",")[0];
                                String lati = location.split(",")[1];
                                String message = mag + ":::" + place + ":::" + longi + ":::" + lati;
                                if (!checked){
                                    if (!(magArr.contains(mag))){
                                        checked = true;
                                        oldMag = mag;
                                        sendOutBroadcast(message);
                                    }
                                }
                                else {
                                    if (!(mag.equals(oldMag))) {
                                        sendOutBroadcast(message);
                                        oldMag = mag;
                                    }
                                }
                            }
                            in.close();
                        } catch (Exception e) {}
                    }
                }).start();
                createAndStartTimer();
            }
        };
        timer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags,
                              int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=15";
                    URL geo = new URL(url);
                    URLConnection geoConnect = geo.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(geoConnect.getInputStream()));
                    String inputLine;
                    int count = 0;
                    while ((inputLine = in.readLine()) != null) {
                        try {
                            String mag = inputLine.split("mag")[1].split(":")[1].split(",")[0];
                            String place = inputLine.split("place\":\"")[1].split("of ")[1].split("\"")[0];
                            String location = inputLine.split("coordinates\":\\[")[1];
                            String longi = location.split(",")[0];
                            String lati = location.split(",")[1];
                            String message = mag + ":::" + place + ":::" + longi + ":::" + lati;
                            begArr.add(0,"!" + message);
                            magArr.add(mag);
                        }
                        catch(Exception e){}
                        count += 1;
                    }
                    sendOutBroadcastArr(begArr);
                    in.close();
                } catch (Exception e) {}
            }
        }).start();
        createAndStartTimer();
        return START_STICKY;
    }

    public void sendOutBroadcast(String message){
        Intent intent = new Intent("WE GOT A QUAKE");
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void sendOutBroadcastArr(ArrayList<String> arr){
        int count = 0;
        while (count < arr.size()) {
            Intent intent = new Intent("WE GOT A QUAKE");
            intent.putExtra("message", arr.get(count));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            count += 1;
        }
    }
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
}
