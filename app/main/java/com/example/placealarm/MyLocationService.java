package com.example.placealarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

import java.text.DecimalFormat;

public class MyLocationService extends BroadcastReceiver {

    public double lat1=BackgroundActivity.currentLat;
    public double long1=BackgroundActivity.currentLong;
    public double lat2= BackgroundActivity.newLat;
    public double long2= BackgroundActivity.newLong;
    double theta;
    public static double dist;
    //MediaPlayer mediaPlayer;

    public static final String ACTION_PROCESS_UPDATE="com.example.backgroundactivity.UPDATE_LOCATION";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            final String action=intent.getAction();
            if(ACTION_PROCESS_UPDATE.equals(action)){
                LocationResult result=LocationResult.extractResult(intent);
                if(result!=null){
                    Location location=result.getLastLocation();
                    String location_string=new StringBuilder(""+location.getLatitude()).append("/")
                            .append(location.getLongitude()).toString();

                    BackgroundActivity.currentLat =  location.getLatitude();
                    BackgroundActivity.currentLong = location.getLongitude();

                    theta = long1 - long2;
                    dist = Math.sin(deg2rad(lat1))
                            * Math.sin(deg2rad(lat2))
                            + Math.cos(deg2rad(lat1))
                            * Math.cos(deg2rad(lat2))
                            * Math.cos(deg2rad(theta));
                    dist = Math.acos(dist);
                    dist = rad2deg(dist);
                    dist = (dist * 60 * 1.1515 * 2);

                    String shyam = new DecimalFormat("##.##").format(dist);

                    //String distance = new StringBuilder("" + dist).toString();

                    String distance = new StringBuilder("" + shyam).toString();

                    //BackgroundActivity.getInstance().updateTextView(distance);

                    TextView textdist = BackgroundActivity.getInstance().findViewById(R.id.distNew);

                    //TextView textlocation = BackgroundActivity.getInstance().findViewById(R.id.tex_location);

                    Toast.makeText(context, "dist is :::: " + dist, Toast.LENGTH_LONG).show();



                    if (dist <= 5) {
                        //callAlarm();
                        //Toast.makeText(context, "dist isvuuuuuuuuuuuuuuuuuuuuuuvuvvv " , Toast.LENGTH_LONG).show();
                        //mediaPlayer = MediaPlayer.create(context,R.raw.alarm);
                        BackgroundActivity.values = true;
                        BackgroundActivity.mediaPlayer.start();
                        //Intent intent1 = new Intent(context, ActivityAlarm.class);


                        //Vibrator v = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
                        //v.vibrate(10000);


                    }

                    try {

                        textdist.setText(distance);

                        //textlocation.setText(location_string);
                        //BackgroundActivity.getInstance().updateTextView(distance);
                        //BackgroundActivity.getInstance().updateTextView(location_string);



                    }catch (Exception ex){
                        Toast.makeText(context,location_string,Toast.LENGTH_SHORT).show();


                    }
                }
            }
        }
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}

