package com.example.placealarm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.placealarm.MyLocationService.dist;

public class ActivityAlarm extends AppCompatActivity {

    public double lat1=BackgroundActivity.currentLat;
    public double long1=BackgroundActivity.currentLong;
    public double lat2= BackgroundActivity.newLat;
    public double long2= BackgroundActivity.newLong;
    double theta;
    public static double dist;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        theta = long1 - long2;

        dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        //dist = (dist * 60 * 1.1515 * 1.609344);
        dist = (dist * 60 * 1.1515 * 2);

        Toast.makeText(ActivityAlarm.this, "Alarm is ringing ......", Toast.LENGTH_LONG).show();
        Toast.makeText(ActivityAlarm.this, "distance new is :: " + dist, Toast.LENGTH_LONG).show();
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}

