package com.example.placealarm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private Button btnFind;

    private  final float DEFAULT_ZOOM=18;








    //public static double currentLat, currentLong;




    //public static double newLat , newLong;






    public static LatLng latLngofPlace;
    public static Location newLatLng;
    Circle circle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        materialSearchBar=findViewById(R.id.searchBar);

        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        mapView=mapFragment.getView();

        mFusedLocationProviderClient =LocationServices.getFusedLocationProviderClient(MapActivity.this);
        Places.initialize(MapActivity.this,"AIzaSyDg0BjNUG-njbrMZqHUhXon2GDIUGXDr8w");
        placesClient=Places.createClient(this);
        final AutocompleteSessionToken token=AutocompleteSessionToken.newInstance();

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(),true,null,true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {


                }else if(buttonCode == MaterialSearchBar.BUTTON_BACK){
                    materialSearchBar.disableSearch();
                }
            }
        });
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                FindAutocompletePredictionsRequest predictionsRequest= FindAutocompletePredictionsRequest.builder()
                        //  .setCountry("in")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if(task.isSuccessful()){
                            FindAutocompletePredictionsResponse predictionsResponse=task.getResult();
                            if(predictionsResponse != null){
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionList=new ArrayList<>();
                                for(int i=0;i<predictionList.size();i++){
                                    AutocompletePrediction prediction=predictionList.get(i);
                                    suggestionList.add(prediction.getFullText(null).toString());
                                }
                                materialSearchBar.updateLastSuggestions(suggestionList);
                                if(!materialSearchBar.isSuggestionsVisible()){
                                    materialSearchBar.showSuggestionsList();
                                }
                            }
                        }else {
                            Log.i("mytag","prediction fetching task unsuccessful");
                        }
                    }
                });
                materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
                    @Override
                    public void OnItemClickListener(int position, View v) {
                        if(position>=predictionList.size()){
                            return;
                        }
                        AutocompletePrediction selectedPrediction=predictionList.get(position);
                        String suggestion=materialSearchBar.getLastSuggestions().get(position).toString();
                        materialSearchBar.setText(suggestion);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                materialSearchBar.clearSuggestions();
                            }
                        }, 100);
                        materialSearchBar.clearSuggestions();
                        InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        if(imm!=null){
                            imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY);
                            String placeId=selectedPrediction.getPlaceId();
                            List<Place.Field> placeFields= Arrays.asList(Place.Field.LAT_LNG);
                            final FetchPlaceRequest fetchPlaceRequest= FetchPlaceRequest.builder(placeId,placeFields).build();
                            placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                                @Override
                                public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                                    Place place=fetchPlaceResponse.getPlace();
                                    Log.i("mytag","Place found"+place.getName());
                                     latLngofPlace=place.getLatLng();


                                     BackgroundActivity.newLat = place.getLatLng().latitude;
                                     BackgroundActivity.newLong = place.getLatLng().longitude;

                                    Toast.makeText(MapActivity.this, "new LAT is : " + BackgroundActivity.newLat + "\nnew LONG is : " + BackgroundActivity.newLong, Toast.LENGTH_LONG).show();






                                    mMap.addCircle(new CircleOptions()
                                            .center(new LatLng(BackgroundActivity.newLat, BackgroundActivity.newLong))
                                            .radius(1000)
                                            .strokeColor(Color.rgb(204, 0, 102))
                                            .fillColor(Color.rgb(153, 204, 255))).setRadius(64);


















                                    //Toast.makeText(MapActivity.this, "Example"+ latLngofPlace, Toast.LENGTH_SHORT).show();
                                    if(latLngofPlace!=null){
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngofPlace,DEFAULT_ZOOM));

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if(e instanceof ApiException){
                                        ApiException apiException=(ApiException)e;
                                        apiException.printStackTrace();
                                        int statusCode=apiException.getStatusCode();
                                        Log.i("mytag","place not found:"+e.getMessage());
                                        Log.i("mytag","status code"+statusCode);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void OnItemDeleteListener(int position, View v) {

                    }
                });

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent=new Intent(MapActivity.this,Settings.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap =googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if(mapView != null &&  mapView.findViewById(Integer.parseInt("1"))!=null){
            View locationButton=((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0,0,40,180);
        }


        LocationRequest locationRequest=LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient= LocationServices.getSettingsClient(MapActivity.this);
        Task<LocationSettingsResponse> task=settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(MapActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });
        task.addOnFailureListener(MapActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
                    ResolvableApiException resolvable=(ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MapActivity.this,51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible())
                    materialSearchBar.clearSuggestions();
                if (materialSearchBar.isSearchEnabled())
                    materialSearchBar.disableSearch();
                return false;
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultcode, Intent data){
        super.onActivityResult(requestCode,resultcode,data);
        if(requestCode==51){
            if(resultcode==RESULT_OK){
                getDeviceLocation();

            }
        }
    }
    @SuppressLint("MissingPermission")
    private void getDeviceLocation(){
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()){
                            mLastKnownLocation=task.getResult();
                            if(mLastKnownLocation!=null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()), 13));
                                BackgroundActivity.currentLat =  mLastKnownLocation.getLatitude();
                                BackgroundActivity.currentLong = mLastKnownLocation.getLongitude();
                                //Toast.makeText(MapActivity.this,"current lat is : " + currentLat + "\ncurrent long is : " + currentLong,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext() , ActivityAlarm.class);



















                                Toast.makeText(MapActivity.this,"current lat is : " + BackgroundActivity.currentLat + "\ncurrent long is : " + BackgroundActivity.currentLong,Toast.LENGTH_LONG).show();

                            }
                            else{
                                LocationRequest locationRequest=LocationRequest.create();
                                locationRequest.setFastestInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback=new LocationCallback(){
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if(locationResult==null){

                                            return;
                                        }
                                         mLastKnownLocation=locationResult.getLastLocation();


                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),DEFAULT_ZOOM));
                                        //Toast.makeText(MapActivity.this,"Location is"+mLastKnownLocation,Toast.LENGTH_SHORT).show();
                                        //newLat = mLastKnownLocation.getLatitude();
                                        //newLong = mLastKnownLocation.getLongitude();
                                        //Toast.makeText(MapActivity.this, "new LAT is : " + newLat + "new LONG is : " + newLong, Toast.LENGTH_LONG).show();




                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);

                            }
                        }else {
                            Toast.makeText(MapActivity.this,"unable to get last location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void clickPlus(View v)
    {
        //Toast.makeText(MapActivity.this , "Current:Lat" + newLat + "Current:Log" + newLong, Toast.LENGTH_LONG).show();
        //Toast.makeText(this, "Show some text on the screen.", Toast.LENGTH_LONG).show();
        //Toast.makeText(MapActivity.this,"Location is"+ latLngofPlace ,Toast.LENGTH_SHORT).show();




        Intent intent = new Intent(MapActivity.this, BackgroundActivity.class);
        startActivity(intent);
    }
}
