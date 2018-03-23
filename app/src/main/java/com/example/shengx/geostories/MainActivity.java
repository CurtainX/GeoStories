package com.example.shengx.geostories;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView geostoryList;
    GeostoryCardAdapter geostoryCardAdapter;
    List<Geostory> mGeostories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Intent intent2addgeostory=new Intent(this,AddGeoStory.class);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent2addgeostory);
            }
        });
        geostoryList=(RecyclerView)findViewById(R.id.geostories_list);
        geostoryList.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        geostoryList.setLayoutManager(layoutManager);
        mGeostories=new ArrayList<Geostory>();
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        mGeostories.add(new Geostory("1231","1241","141251"));
        geostoryCardAdapter=new GeostoryCardAdapter(mGeostories);
        geostoryList.setAdapter(geostoryCardAdapter);
    }

}
