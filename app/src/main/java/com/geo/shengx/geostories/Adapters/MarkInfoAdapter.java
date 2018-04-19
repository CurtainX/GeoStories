package com.geo.shengx.geostories.Adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.geo.shengx.geostories.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MarkInfoAdapter implements GoogleMap.InfoWindowAdapter{

    Context context;
    LayoutInflater inflater;
    public MarkInfoAdapter(){

    }
    public MarkInfoAdapter(Context context) {
        this.context = context;
    }
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // R.layout.echo_info_window is a layout in my
        // res/layout folder. You can provide your own
        View v = inflater.inflate(R.layout.activity_mark_info_adapter ,null);

        TextView visibleRange = (TextView) v.findViewById(R.id.visible_range_marker);
        visibleRange.setText(marker.getTitle());
        return v;
    }
}