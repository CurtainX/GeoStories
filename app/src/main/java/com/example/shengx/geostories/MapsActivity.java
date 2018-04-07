package com.example.shengx.geostories;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final int REQ_LOC_CODE=100;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private double latitude, longitude;
    private SeekBar rangeContorller;
    private Circle circle;
    private int initialRange=60;
    private int maxRange=400;
    FirebaseFirestore db;
    private String client_geostory="";
    private FirebaseUser currentClient;
    private String clientID="";
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        rangeContorller=(SeekBar)findViewById(R.id.skbRange);
        rangeContorller.setMax(maxRange);
        rangeContorller.setProgress(initialRange);
        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        rangeContorller.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                initialRange=progress;
                circle.setRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Intent geointent=getIntent();
        client_geostory=geointent.getStringExtra(Geocons.GEO_STORY);
        db = FirebaseFirestore.getInstance();
        currentClient = FirebaseAuth.getInstance().getCurrentUser() ;
        clientID=currentClient.getUid();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
                Log.d("Location",point.latitude+"******"+point.longitude);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(point.latitude,point.longitude))
                        .radius(initialRange)
                        .strokeColor(Color.RED)
                        .strokeWidth(2)
                        .fillColor(Color.TRANSPARENT));
            }
        });
        if(checkLocationPermission()){
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        latitude=location.getLatitude();
                        longitude=location.getLongitude();
                        Log.d("Location",latitude+"******"+longitude);
                        // Add a marker in Sydney and move the camera
                        LatLng clientLocation = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(clientLocation).title("My Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(clientLocation));
                        circle = mMap.addCircle(new CircleOptions()
                                .center(new LatLng(latitude,longitude))
                                .radius(initialRange)
                                .strokeColor(Color.RED)
                                .strokeWidth(2)
                                .fillColor(Color.TRANSPARENT));
                    }
                });
        }


    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.postgeostory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        intent=new Intent(this,MainActivity.class);
        switch (item.getItemId()) {
            case R.id.post:
                postGeostory(initialRange,longitude,latitude,client_geostory);
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }


    public boolean checkLocationPermission(){
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQ_LOC_CODE);
        int locationPermissionCode=ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION);
        return locationPermissionCode == PackageManager.PERMISSION_GRANTED;
    }



    public void postGeostory(int range, double longitude, double latitude, String story){
        Log.d("Log","Start posting");
        //send range, point, story to fire base
        Date postedTime = Calendar.getInstance().getTime();
        Map<String,Object> geostory=new HashMap<>();
        geostory.put(Geocons.CLIENT_ID,String.valueOf(clientID));
        geostory.put(Geocons.GEO_LATITUDE,String.valueOf(latitude));
        geostory.put(Geocons.GEO_LONGITUDE,String.valueOf(longitude));
        geostory.put(Geocons.GEO_STORY,String.valueOf(story));
        geostory.put(Geocons.POSTED_TIME,String.valueOf(postedTime));
        geostory.put(Geocons.VISIBLE_RANGE,String.valueOf(range));
        Log.d("Log","Start posting2");

        db.collection(Geocons.DBcons.GEOSTORY_DB)
                .add(geostory)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Log","Start posting3");

                        Toast.makeText(getApplicationContext(),"Geostory Posted.",Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Log","Start posting4");


                        Toast.makeText(getApplicationContext(),"There was a issue during posting, please try again.",Toast.LENGTH_SHORT).show();
                    }
                });

        Log.d("Log","End posting");
    }
    public boolean checkCity(Location gps, Location point){
        //check if two location is the same city
        //user only allowed to post within the same city
        //geo coding to check
        return false;
    }
}
