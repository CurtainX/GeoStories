package com.example.shengx.geostories;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by SHENG.X on 2018-04-11.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {


    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CommentHolder extends RecyclerView.ViewHolder{

        public CommentHolder(View itemView) {
            super(itemView);
        }
    }
}
