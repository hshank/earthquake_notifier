package com.example.harishshanker.yourfault;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageView;
import android.graphics.Bitmap;
import java.net.URL;
import java.io.*;
import android.graphics.BitmapFactory;
import java.net.MalformedURLException;
import android.os.StrictMode;

public class PictureActivity extends Activity {

    //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    //StrictMode.setThreadPolicy(policy);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String message = extras.getString("message");
            Log.d("receiver", "Got message: " + message);

            ImageView iv = (ImageView) findViewById(R.id.pic);
            if (message.equals("NO MORE PICS")){
                iv.setImageBitmap(getBitmapFromURL("http://www.ovaali.org/ossij/sardin/pics/nopicture.gif"));
            }
            else {
                iv.setImageBitmap(getBitmapFromURL(message));
            }

        }

    }


    public static Bitmap getBitmapFromURL(String src) {
        Bitmap bitMap = null;
        Log.d("src", src);
        try {
            URL url = new URL(src);
            InputStream is = url.openConnection().getInputStream();
            bitMap = BitmapFactory.decodeStream(is);

        } catch (MalformedURLException e) {


            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        //Log.d("return", bitMap.toString());
        return bitMap;
    }
}