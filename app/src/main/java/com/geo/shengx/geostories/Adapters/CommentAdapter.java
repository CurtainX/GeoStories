package com.geo.shengx.geostories.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geo.shengx.geostories.Comment;
import com.geo.shengx.geostories.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import id.zelory.compressor.Compressor;

/**
 * Created by SHENG.X on 2018-04-11.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    List<Comment> mComments;
    Context context;
    FirebaseStorage storage;
    StorageReference gsReference_profile_img;
    private long ONE_MEGABYTE=1024*1024;
    File storagePath, myFile;

    public CommentAdapter(List<Comment> mComments, Context context) {
        this.mComments = mComments;
        this.context=context;
        storagePath= new File(Environment.getExternalStorageDirectory(), "Geo_Images");
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View v=inflater.inflate(R.layout.usercomment,parent,false);
        CommentHolder vh=new CommentHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        updateProfileImage(mComments.get(position).getClientID(),holder.profileImage);
        holder.username.setText(mComments.get(position).getClientName());
        holder.commentDate.setText(mComments.get(position).getCommentDate());
        holder.commentContent.setText(mComments.get(position).getCommnetContent());
        holder.commentContent.setMovementMethod(new ScrollingMovementMethod());
        int height_in_pixels = holder.commentContent.getLineCount() * holder.commentContent.getLineHeight();
        holder.commentContent.setHeight(height_in_pixels);
    }


    @Override
    public int getItemCount() {
        return mComments.size();
    }
    public void updateProfileImage(final String client_id, final ImageView profileImage) {
        String photoPath = Environment.getExternalStorageDirectory() + "/Geo_Images/" + client_id + ".jpg";
        File actualprofileImage=new File(photoPath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap  = null;
        try {
            bitmap = new Compressor(context).compressToBitmap(actualprofileImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            profileImage.setImageBitmap(bitmap);
        } else {
            try{
                gsReference_profile_img = storage.getReferenceFromUrl("gs://geostories-87738.appspot.com/" + client_id + ".jpg");
                gsReference_profile_img.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Data for "images/island.jpg" is returns, use this as needed
                        if (bytes.length != 0) {

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inMutable = true;
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                            myFile = new File(storagePath,client_id+".jpg");
                            FileOutputStream fileOutputStream= null;
                            try {
                                fileOutputStream = new FileOutputStream(myFile);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                            profileImage.setImageBitmap(bmp);
                            Log.d("Log-chec", "success story image downloaded" + bmp.getByteCount());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Log.d("Log", "No ~!!success222");
//                                            Drawable d = context.getResources().getDrawable(R.drawable.ic_camera_black_24dp);
//                                            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
//                                            profileImage.setImageBitmap(null);

                    }
                });
            }
            catch (Exception e){

            }
        }
    }


    public class CommentHolder extends RecyclerView.ViewHolder{
        ImageView profileImage;
        TextView username, commentDate, commentContent;
        public CommentHolder(View itemView) {
            super(itemView);
            profileImage=(ImageView)itemView.findViewById(R.id.profile_img_comment);
            username=(TextView)itemView.findViewById(R.id.username_comment);
            commentContent=(TextView)itemView.findViewById(R.id.content_comment);
            commentDate=(TextView)itemView.findViewById(R.id.date_comment);
        }
    }
}
