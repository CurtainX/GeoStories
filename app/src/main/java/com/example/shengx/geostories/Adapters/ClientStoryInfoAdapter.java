package com.example.shengx.geostories.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shengx.geostories.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by SHENG.X on 2018-04-16.
 */

public class ClientStoryInfoAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater;
    Context context;
    String profileImage_link, storyImage_link,username, date, storyContent;
    int likeNum, commentNum, rangeNum;
    public ClientStoryInfoAdapter(Context context, String profileImage_link, String storyImage_link, String username, String date, String storyContent, int likeNum, int commentNum, int rangeNum) {
        this.context = context;
        this.profileImage_link = profileImage_link;
        this.storyImage_link = storyImage_link;
        this.username = username;
        this.date = date;
        this.storyContent = storyContent;
        this.likeNum = likeNum;
        this.commentNum = commentNum;
        this.rangeNum = rangeNum;
    }



//    public ClientStoryInfoAdapter(Context context) {
//        this.context = context;
//    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // R.layout.echo_info_window is a layout in my
        // res/layout folder. You can provide your own
        View v = inflater.inflate(R.layout.client_story_on_map ,null);
        ImageView profile_image, story_image;
        TextView username, postedDate, story, range, likenum, commentnum;
        profile_image=(ImageView)v.findViewById(R.id.profileImage_mp);
        story_image=(ImageView)v.findViewById(R.id.geostoryimage_mp);
        username=(TextView)v.findViewById(R.id.username_mp);
        postedDate=(TextView)v.findViewById(R.id.dateposted_mp);
        story=(TextView)v.findViewById(R.id.geostory_mp);
        range=(TextView)v.findViewById(R.id.mRange_mp);
        likenum=(TextView)v.findViewById(R.id.like_counter_mp);
        commentnum=(TextView)v.findViewById(R.id.comment_counter_mp);
        BitmapFactory.Options options = new BitmapFactory.Options();
        String profileImagePath = Environment.getExternalStorageDirectory() + "/Geo_Images/" + profileImage_link + ".jpg";
        String storyImagePath= Environment.getExternalStorageDirectory() + "/Geo_Images/" + storyImage_link + ".jpg";
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap profileImage = BitmapFactory.decodeFile(profileImagePath, options);
        Bitmap storyImage=BitmapFactory.decodeFile(storyImagePath, options);
        profile_image.setImageBitmap(profileImage);
        story_image.setImageBitmap(storyImage);
        username.setText(this.username);
        postedDate.setText(this.date);
        story.setText(this.storyContent);
        range.setText(String.valueOf(this.rangeNum)+"m");
        //likenum.setText(this.likeNum);
        //commentnum.setText(this.commentNum);
        return v;
    }
}
