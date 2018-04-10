package com.example.shengx.geostories.Utility;

import android.view.View;

import com.example.shengx.geostories.GeostoryCardAdapter;

/**
 * Created by SHENG.X on 2018-04-09.
 */

public class StoryControlUtility {
    public static void likeStory(){

    }
    public static void commentStory(){

    }
    public static void dismissStory(GeostoryCardAdapter.GeostoryHolder holder){
        holder.itemView.setVisibility(View.GONE);
    }
}
