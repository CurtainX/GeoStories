package com.example.shengx.geostories;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    RecyclerView geostoryList;
    GeostoryCardAdapter geostoryCardAdapter;
    List<Geostory> mGeostories;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final int REQ_LOC_CODE = 100;
    private final int PERMISSIONS_ALL=200;
    private String TAG = "FLOG";
    private GoogleApiClient googleApiClient;

    private Location clientLocation;


    private DrawerLayout mDrawerLayout;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;


    TextView userimage, username,userabout;
    SharedPreferences sharedPreferences;


    FirebaseStorage storage;
    StorageReference gsReference_profile_img,gsReference_story_img;

    FirebaseFirestore db;


    List<Bitmap> profile_images=new ArrayList<>();
    List<Bitmap> story_images=new ArrayList<>();
    List<String> story_ids=new ArrayList<>();
    List<String> client_ids=new ArrayList<>();
    List<String> posted_dates=new ArrayList<>();
    List<String> storys=new ArrayList<>();
    List<String> client_names=new ArrayList<>();

    List<Geostory> downloadedGeostories_image1,downloadedGeostories_image2;

    int profile_image_counter=0,story_image_counter=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkLocationPermission();
        CheckEnableGPS();


        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);


        db=FirebaseFirestore.getInstance();


        // Get the default bucket from a custom FirebaseApp
        storage = FirebaseStorage.getInstance();






        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);


        final Intent intent2addgeostory = new Intent(this, AddGeoStory.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckEnableGPS()) {
                    startActivity(intent2addgeostory);
                }
            }
        });

        geostoryList = (RecyclerView) findViewById(R.id.geostories_list);
        geostoryList.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        geostoryList.setLayoutManager(layoutManager);
        mGeostories = new ArrayList<Geostory>();

        geostoryList.setItemViewCacheSize(5);
        geostoryList.setNestedScrollingEnabled(false);

        geostoryCardAdapter = new GeostoryCardAdapter(mGeostories,this);
        geostoryList.setAdapter(geostoryCardAdapter);

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("cities").document("SF");
        db.collection("geostorys").document("BpH7nx2UcWrNeIvYnbxK").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData().get("geostory"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_signout:
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getApplicationContext(), Login.class);

                                startActivity(intent);
                                break;
                            case R.id.nav_editprofile:
                                Intent intent2 = new Intent(getApplicationContext(), editprofile.class);
                                startActivity(intent2);
                                break;
                        }

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });


       mLocationRequest=LocationRequest.create()
               .setExpirationDuration(LocationRequest.PRIORITY_HIGH_ACCURACY)
               .setInterval(500)
               .setFastestInterval(500);
       mLocationCallback=new LocationCallback(){
           @Override
           public void onLocationResult(LocationResult locationResult) {
               super.onLocationResult(locationResult);
               clientLocation=locationResult.getLastLocation();

               Log.d("Locaiton Client",clientLocation.getLatitude()+"$$$$$"+clientLocation.getLongitude());
           }

           @Override
           public void onLocationAvailability(LocationAvailability locationAvailability) {
               super.onLocationAvailability(locationAvailability);
               Log.d("Locaiton Client",locationAvailability.toString());
           }
       };
        sharedPreferences=getApplicationContext().getSharedPreferences("Client",0);
        userabout=(TextView)navigationView.getHeaderView(0).findViewById(R.id.about_m);
        username=(TextView)navigationView.getHeaderView(0).findViewById(R.id.username_m);
        userimage=(TextView)navigationView.getHeaderView(0).findViewById(R.id.user_img_m);
        String photoPath = Environment.getExternalStorageDirectory()+"/Geo_Images/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);

        if(bitmap!=null){
            BitmapDrawable ob=new BitmapDrawable(getResources(),bitmap);
            userimage.setBackgroundDrawable(ob);
        }
        username.setText(sharedPreferences.getString("username",""));
        userabout.setText(sharedPreferences.getString("about",""));
        Log.d("Log","success-->");




    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        String photoPath = Environment.getExternalStorageDirectory()+"/Geo_Images/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);


        if(bitmap!=null){
            BitmapDrawable ob=new BitmapDrawable(getResources(),bitmap);
            userimage.setBackgroundDrawable(ob);
        }
        username.setText(sharedPreferences.getString("username",""));
        userabout.setText(sharedPreferences.getString("about",""));
    }

    public boolean checkLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOC_CODE);
        int locationPermissionCode = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        return locationPermissionCode == PackageManager.PERMISSION_GRANTED;
    }



    private  boolean checkAndRequestPermissions() {
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for(String p:PERMISSIONS){
            if(ActivityCompat.checkSelfPermission(this,p)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{p},PERMISSIONS_ALL);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_LOC_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location==null){
                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                Log.d("Location Client","Client has no Last location");

                                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);

                            }else{
                                Log.d("Location Client","Client has Last location");
                                clientLocation=location;
                                getStoies();

                            }
                        }
                    });

                    Log.d("Location", "Permission Granted");
                } else {
                    Log.d("Location: ", "Location Permission Required");
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOC_CODE);
                }
        }
    }



    private boolean CheckEnableGPS(){
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.equals("")){
            //GPS Enabled
            return true;
        }else{
           turnGPSOn();
        }
        return false;
    }

    private void turnGPSOn() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                }).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }


    public void getClientGPSLocation(){

    }
    public void getStoryFromFireBase(Location location){

    }
    public void updateStory(){

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    public void getStoies(){
        Log.d("Log---Main--testing","Called");
        Geocoder gcd=new Geocoder(getApplicationContext(), Locale.getDefault());
        String mclient_city;
        try{
            List<Address> client_city=gcd.getFromLocation(clientLocation.getLatitude(),clientLocation.getLongitude(),1);
            mclient_city=client_city.get(0).getLocality();

            final long ONE_MEGABYTE = 1024 * 1024;
            db.collection(Geocons.DBcons.GEOSTORY_DB)
                    .whereEqualTo(Geocons.STORY_CITY,mclient_city)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, final FirebaseFirestoreException e) {
                            profile_images=new ArrayList<>();
                            story_images=new ArrayList<>();
                            story_ids=new ArrayList<>();
                            client_ids=new ArrayList<>();
                            posted_dates=new ArrayList<>();
                            storys=new ArrayList<>();
                            client_names=new ArrayList<>();

                            downloadedGeostories_image1=new ArrayList<Geostory>();
                            downloadedGeostories_image1.clear();



                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }


                            for (DocumentSnapshot story : documentSnapshots) {
                                if (story.getId()!= null) {
                                    story_ids.add(String.valueOf(story.getId()));
                                    client_ids.add(String.valueOf(story.get(Geocons.CLIENT_ID)));
                                    posted_dates.add(String.valueOf(story.get(Geocons.POSTED_TIME)));
                                    storys.add(String.valueOf(story.get(Geocons.GEO_STORY)));
                                    client_names.add(String.valueOf(story.get(Geocons.CLIENT_NAME)));

                                    Log.d("Log----check@@@",story.getId()+".jpg");


                                    downloadedGeostories_image1.add(new Geostory(String.valueOf(story.getId()),
                                                                String.valueOf(story.get(Geocons.CLIENT_ID)),
                                                                null,
                                                                null,
                                            String.valueOf(story.get(Geocons.CLIENT_NAME)),
                                            String.valueOf(story.get(Geocons.POSTED_TIME)),String.valueOf(story.get(Geocons.GEO_STORY))
                                                        ));

                                }

                            }

                            geostoryCardAdapter=new GeostoryCardAdapter(downloadedGeostories_image1,getApplicationContext());
                            geostoryList.setAdapter(geostoryCardAdapter);
                            Log.d("Logcheck---point",story_ids.size()+"@@@@@@@@@@"+storys.size());
                            Log.d(TAG, "story set");
                        }
                    });

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


}

