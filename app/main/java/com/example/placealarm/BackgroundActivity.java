package com.example.placealarm;

import androidx.annotation.IntegerRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlarmManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;


import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class BackgroundActivity extends AppCompatActivity {

    static BackgroundActivity instance;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView tex_location;
    TextView distNew;
    static MediaPlayer mediaPlayer;

    public static double currentLat, currentLong;
    final static int RQS_1 = 1;
    public static boolean values = false;

    public static double newLat , newLong;

    public EditText mEmail;
    public Button button;
    public Button button3;



    public static BackgroundActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        mediaPlayer = MediaPlayer.create(this,R.raw.alarm);
        instance = this;
        //tex_location=(TextView)findViewById(R.id.tex_location);
        distNew = (TextView)findViewById(R.id.distNew);

        mEmail = (EditText)findViewById(R.id.mailID);

        button3 = (Button)findViewById(R.id.button3);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });





        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        updateLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(BackgroundActivity.this, "your location", Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());

    }

    private PendingIntent getPendingIntent() {
        //Intent intent=new Intent(this,MyLocationService.class);

        Intent intent=new Intent(this, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

    }


    private void buildLocationRequest() {
        locationRequest =new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    public void updateTextView(final String value){
        BackgroundActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tex_location.setText(value);
                distNew.setText(value);
            }
        });
    }

    public void stop(View view){
       try {
                //if(values == true)
                //{
                    mediaPlayer.stop();
                    values = false;
                    sendMail();
                    Intent intent = new Intent(this , MapActivity.class);
                    startActivity(intent);

                //}


        }
        catch(Exception e) {

        }

//        Intent intent = new Intent(BackgroundActivity.this, MapActivity.class);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1, intent, 0);
//        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//        alarmManager.cancel(pendingIntent);


        //Intent intent = new Intent(this, MapActivity.class);
        //startActivity(intent);
    }





    private void sendMail() {

        String mail = mEmail.getText().toString().trim();

        JavaMailAPI javaMailAPI = new JavaMailAPI(BackgroundActivity.this , mail);

        javaMailAPI.execute();

    }
}








