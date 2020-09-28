package com.example.placealarm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class Settings extends AppCompatActivity {

    private  static final String TAG = "Settings";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ListView list = (ListView)findViewById(R.id.theList);
        Log.d(TAG,"OnCreate: Started");
        ArrayList <String> names = new ArrayList<>();
        names.add("Profile");

        ArrayAdapter adapter = new ArrayAdapter(this , android.R.layout.simple_list_item_1,names);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Settings.this , ProfileActivity.class);
                startActivity(intent);


            }
        });
    }
}
