package com.geo.shengx.geostories;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.geo.shengx.geostories.Adapters.ClientStoryInfoAdapter;
import com.geo.shengx.geostories.Constances.Geocons;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowGeoStoryMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double latitude, longitude;
    int range;
    String profileImage_link, storyImage_link,username, postedDate, storyContent;
    int likeNum, commentNum;
    Intent intent;
    private Circle circle;
    Marker storyInfoMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_geo_story_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        intent=getIntent();
        latitude=Double.parseDouble(intent.getStringExtra(Geocons.GEO_LATITUDE));
        longitude=Double.parseDouble(intent.getStringExtra(Geocons.GEO_LONGITUDE));
        range=Integer.parseInt(intent.getStringExtra(Geocons.GEO_RANGE));
        profileImage_link=intent.getStringExtra(Geocons.STORY_OWNER_ICON_LINK);
        storyImage_link=intent.getStringExtra(Geocons.STORY_IMAGE_LINK);
        username=intent.getStringExtra(Geocons.CLIENT_NAME);
        postedDate=intent.getStringExtra(Geocons.POSTED_TIME);
        storyContent=intent.getStringExtra(Geocons.GEO_STORY);
        //likeNum=Integer.parseInt(intent.getStringExtra(Geocons.LIKE_NUM));
        //commentNum=Integer.parseInt(intent.getStringExtra(Geocons.COMMENT_NUM));

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
        mMap.setInfoWindowAdapter(new ClientStoryInfoAdapter(this,profileImage_link,storyImage_link,username,postedDate,storyContent,likeNum,commentNum,range));
        // Add a marker in Sydney and move the camera

        LatLng mystory = new LatLng(latitude, longitude);

        storyInfoMark=mMap.addMarker(new MarkerOptions().position(mystory).title("My story Point"));
        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude,longitude))
                .radius(range)
                .strokeColor(Color.RED)
                .strokeWidth(2)
                .fillColor(Color.TRANSPARENT));
        zoomAnimation(range);
        storyInfoMark.showInfoWindow();

    }



    public void zoomAnimation(int progress){
        if(progress<=30){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 19f));
        }
        else if(progress<=70){

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 18.5f));

        }
        else if(progress<=120){

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17.5f));

        }
        else if(progress<=150){

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17.0f));

        }
        else if(progress<=200){

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16.5f));

        }
        else if(progress<=300){

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.5f));

        }
        else {

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14.5f));

        }
    }
}
