package com.example.placealarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;

    //public static double currentLat, currentLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (haveNetwork())
                {
                    Intent homeIntent = new Intent(MainActivity.this , SignIn.class);
                    startActivity(homeIntent);
                    finish();
                }

                else

                {
                    Toast.makeText(MainActivity.this, "Network Connection is not availabel!", Toast.LENGTH_SHORT).show();

                }
            }
        },SPLASH_TIME_OUT);

        //Intent intent = new Intent(MainActivity.this, BackGround.class);
        //startActivity(intent);

        //Toast.makeText(this, "Main HELLO !!!!!!!!", Toast.LENGTH_LONG).show();

//        Intent i = new Intent();
//        i.setAction(Intent.ACTION_MAIN);
//        i.addCategory(Intent.CATEGORY_HOME);
//        this.startActivity(i);

        Intent i = new Intent(MainActivity.this, BackgroundActivity.class);
        startService(i);

    }



    private boolean haveNetwork()
    {
        boolean haveWife = false;
        boolean haveMobileData = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for(NetworkInfo info: networkInfos)
        {
            if(info.getTypeName().equalsIgnoreCase("WIFI"))
                if(info.isConnected())
                    haveWife = true;

            if(info.getTypeName().equalsIgnoreCase("MOBILE"))
                if(info.isConnected())
                    haveMobileData = true;
        }
        return haveMobileData || haveWife;
    }

}


