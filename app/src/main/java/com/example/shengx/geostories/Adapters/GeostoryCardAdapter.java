package com.example.shengx.geostories.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shengx.geostories.Comments;
import com.example.shengx.geostories.Constances.Geocons;
import com.example.shengx.geostories.Geostory;
import com.example.shengx.geostories.R;
import com.example.shengx.geostories.Utility.StoryControlUtility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import id.zelory.compressor.Compressor;

/**
 * Created by SHENG.X on 2018-03-22.
 */

public class GeostoryCardAdapter extends RecyclerView.Adapter<GeostoryCardAdapter.GeostoryHolder> {
    List<Geostory> mGeostories;
    Context context;
    FirebaseStorage storage;
    final long ONE_MEGABYTE=1024*1024;
    int story_image_counter=0;
    File storagePath,myFile;
    String clientID;




    StorageReference gsReference_profile_img,gsReference_story_img;


    public GeostoryCardAdapter(List<Geostory> mGeostories, Context context, String clientID) {
        this.mGeostories = mGeostories;
        this.context=context;
        storage=FirebaseStorage.getInstance();
        this.clientID=clientID;
        storagePath= new File(Environment.getExternalStorageDirectory(), "Geo_Images");

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public GeostoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.storycard,parent,false);
        GeostoryHolder vh=new GeostoryHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final GeostoryHolder holder, int position) {
        Drawable d = context.getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark_normal);
        holder.geostoryImage.setImageDrawable(d);
        holder.profileImage.setImageDrawable(d);
        updateProfileImage(mGeostories.get(position).getClientID(),holder.profileImage);
        updateStoryImage(mGeostories.get(position).getStoryID(),holder.geostoryImage);

        final String storyID,storyOwnerID,clientName;
        storyID=mGeostories.get(position).getStoryID();
        storyOwnerID=mGeostories.get(position).getClientID();
        clientName=mGeostories.get(position).getUsername();
        holder.username.setText(clientName);
        holder.datePosted.setText(mGeostories.get(position).getDatePosted());
        holder.geostory.setText(mGeostories.get(position).getGeostory());
        int height_in_pixels = holder.geostory.getLineCount() * holder.geostory.getLineHeight();
        holder.geostory.setHeight(height_in_pixels);
        holder.geostory.setMovementMethod(new ScrollingMovementMethod());
        StoryControlUtility.checkLiked(storyID,clientID,holder.like,context);
        StoryControlUtility.likeCounter(storyID,holder.likecounter);
        StoryControlUtility.commentCounter(storyID,holder.commnentcounter);
        holder.geostoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String photoPath = Environment.getExternalStorageDirectory() + "/Geo_Images/" + storyID + ".jpg";
                File actualprofileImage=new File(photoPath);
                if(actualprofileImage!=null){
                    Bitmap bitmap = null;
                    try {
                        bitmap = new Compressor(context).compressToBitmap(actualprofileImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showDialog(bitmap);
                }

            }
        });

        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String photoPath = Environment.getExternalStorageDirectory() + "/Geo_Images/" + storyOwnerID + ".jpg";
                File actualprofileImage=new File(photoPath);
                if(actualprofileImage!=null){
                    Bitmap bitmap = null;
                    try {
                        bitmap = new Compressor(context).compressToBitmap(actualprofileImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showProfileDialog(bitmap,clientName);
                }
            }
        });
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoryControlUtility.likeStory(storyID,clientID,holder.like,context);

            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,Comments.class);
                intent.putExtra(Geocons.COMMENT_INTENT_EXTRA,storyID);
                intent.putExtra(Geocons.CLIENT_NAME,clientName);
                context.startActivity(intent);
                Toast.makeText(v.getContext(),"comment-->"+storyID,Toast.LENGTH_LONG).show();
            }
        });
//        holder.dismiss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                StoryControlUtility.dismissStory(holder);
//                Toast.makeText(v.getContext(),"dissmiss",Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mGeostories.size();
    }

    public void addStory(List<Geostory> geostories){
        Collections.reverse(geostories);
        mGeostories.clear();
        mGeostories.addAll(geostories);

    }




    public void updateProfileImage(final String client_id, final ImageView profileImage) {
        String photoPath = Environment.getExternalStorageDirectory() + "/Geo_Images/" + client_id + ".jpg";
        File actualprofileImage=new File(photoPath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap  = null;
        try {
            if(actualprofileImage!=null){
                bitmap = new Compressor(context).compressToBitmap(actualprofileImage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            profileImage.setImageBitmap(bitmap);
        } else {
            gsReference_profile_img = storage.getReferenceFromUrl("gs://geostories-87738.appspot.com/" + client_id + ".jpg");
            if(!profileImage.getTag().equals("Updated")){
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
            profileImage.setTag("Updated");
        }
    }


    public void updateStoryImage(final String story_id, final ImageView storyImage){
        String photoPath = Environment.getExternalStorageDirectory()+"/Geo_Images/"+story_id+".jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        if(bitmap!=null){
            storyImage.setImageBitmap(bitmap);
        }else {
            gsReference_story_img = storage.getReferenceFromUrl("gs://geostories-87738.appspot.com/"+story_id+".jpg");
            if(!storyImage.getTag().equals("Updated")){
                gsReference_story_img.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Data for "images/island.jpg" is returns, use this as needed
                        if(bytes.length!=0) {

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inMutable = true;
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                            myFile = new File(storagePath,story_id+".jpg");
                            FileOutputStream fileOutputStream= null;
                            try {
                                fileOutputStream = new FileOutputStream(myFile);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

                            storyImage.setImageBitmap(bmp);
                            Log.d("Log-chec", "success story image downloaded" +story_id+"%%"+ bmp.getByteCount());
                            story_image_counter++;
                            Log.d("Log-chec-Counter",story_image_counter+"****************************8");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Log.d("Log22","No ~!!success222--------->"+story_id);
//                Drawable d = context.getResources().getDrawable(R.drawable.ic_camera_black_24dp);
//                Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
//                storyImage.setImageBitmap(null);
                    }
                });
            }
        }
        storyImage.setTag("Updated");
    }

    public class GeostoryHolder extends RecyclerView.ViewHolder{
        ImageView profileImage;
        TextView username, datePosted, geostory, likecounter,commnentcounter;
        ImageView geostoryImage;
        ImageView like, comment,dismiss;
        ConstraintLayout imageViewHolder;

        public GeostoryHolder(View itemView) {
            super(itemView);
            profileImage=(ImageView) itemView.findViewById(R.id.profileImage_cd);
            username=(TextView)itemView.findViewById(R.id.username_cd);
            datePosted=(TextView)itemView.findViewById(R.id.dateposted_cd);
            geostory=(TextView)itemView.findViewById(R.id.geostory_cd);
            geostoryImage=(ImageView)itemView.findViewById(R.id.geostoryimage_cd);
            like=(ImageView)itemView.findViewById(R.id.like_cd);
            comment=(ImageView)itemView.findViewById(R.id.comment_cd);
//            dismiss=(ImageView)itemView.findViewById(R.id.dismiss_cd);
            likecounter=(TextView)itemView.findViewById(R.id.like_counter);
            commnentcounter=(TextView)itemView.findViewById(R.id.comment_counter);
            imageViewHolder=(ConstraintLayout)itemView.findViewById(R.id.display_counter);
            geostoryImage.setTag("not updated");
            profileImage.setTag("not updated");
        }
    }





    private void showDialog(Bitmap bitmap) {
       Dialog mDialog=new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.image_dialog);
        ImageView mImage=(ImageView)mDialog.findViewById(R.id.popoutImage);
        mImage.setImageBitmap(bitmap);
        mDialog.show();
    }




    private void showProfileDialog(Bitmap bitmap, String username) {
        Dialog mDialog=new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.previewprofile);
        ImageView profile_pre=(ImageView)mDialog.findViewById(R.id.profile_preview_photo);
        TextView username_pre=(TextView)mDialog.findViewById(R.id.profile_preview_username);
        TextView aboout_pre=(TextView)mDialog.findViewById(R.id.profile_preview_about);
        StoryControlUtility.getAboutStoryOwner(username,aboout_pre);
        profile_pre.setImageBitmap(bitmap);
        username_pre.setText(username);
        //aboout_pre.setText(about);
        mDialog.show();
    }
}
