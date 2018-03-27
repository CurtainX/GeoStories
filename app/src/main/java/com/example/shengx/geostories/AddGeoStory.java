package com.example.shengx.geostories;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class AddGeoStory extends AppCompatActivity {
    Intent intent;
    EditText geostoryin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geo_story);
        intent = new Intent(this, MapsActivity.class);
        geostoryin=(EditText)findViewById(R.id.geostory_input);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addgeostoryopmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tomap:
                if(geostoryin.getText().toString().trim().equals("")){
                    Toast.makeText(this,"Please Enter Your Geostory",Toast.LENGTH_LONG).show();
                }
                else {
                    startActivity(intent);
                }
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }
}
