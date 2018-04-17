package com.example.shengx.geostories.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

import com.example.shengx.geostories.Constances.Geocons;
import com.example.shengx.geostories.Geostory;
import com.example.shengx.geostories.R;
import com.example.shengx.geostories.ShowGeoStoryMap;
import com.example.shengx.geostories.Utility.StoryControlUtility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import id.zelory.compressor.Compressor;

/**
 * Created by ShengXiao on 2018-04-15.
 */

public class ClientGeoSotryAdapter extends RecyclerView.Adapter<ClientGeoSotryAdapter.ClientStoryViewHolder>{

    List<Geostory>mGeostories;
    Context context;

    FirebaseStorage storage;
    final long ONE_MEGABYTE=1024*1024;
    int story_image_counter=0;
    File storagePath,myFile;
    StorageReference gsReference_profile_img,gsReference_story_img;

    FirebaseFirestore db;

    public ClientGeoSotryAdapter(List<Geostory> mGeostories,Context context) {
        this.mGeostories = mGeostories;
        this.context=context;
        storage=FirebaseStorage.getInstance();
        db=FirebaseFirestore.getInstance();
        storagePath= new File(Environment.getExternalStorageDirectory(), "Geo_Images");
    }

    @Override
    public ClientStoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.client_stories,parent,false);
        ClientStoryViewHolder vh=new ClientStoryViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ClientStoryViewHolder holder, final int position) {
        Log.d("MY-POSITION",position+"");
        Drawable d = context.getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark_normal);
        holder.geostoryImage.setImageDrawable(d);
        holder.profileImage.setImageDrawable(d);
        updateProfileImage(mGeostories.get(position).getClientID(),holder.profileImage);
        updateStoryImage(mGeostories.get(position).getStoryID(),holder.geostoryImage);

        final String storyID,storyOwnerID,clientName, latitude,longitude,range,postedDate,geostory;
        storyID=mGeostories.get(position).getStoryID();
        storyOwnerID=mGeostories.get(position).getClientID();
        clientName=mGeostories.get(position).getUsername();
        postedDate=mGeostories.get(position).getDatePosted();
        geostory=mGeostories.get(position).getGeostory();
        holder.username.setText(clientName);
        holder.datePosted.setText(mGeostories.get(position).getDatePosted());
        holder.geostory.setText(mGeostories.get(position).getGeostory());
        int height_in_pixels = holder.geostory.getLineCount() * holder.geostory.getLineHeight();
        holder.geostory.setHeight(height_in_pixels);

        StoryControlUtility.likeCounter(storyID,holder.likecount);
        StoryControlUtility.commentCounter(storyID,holder.commentcount);


        holder.geostory.setMovementMethod(new ScrollingMovementMethod());
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
        latitude=mGeostories.get(position).getLatitude();
        longitude=mGeostories.get(position).getLongitude();
        range=mGeostories.get(position).getRange();
        holder.showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ShowGeoStoryMap.class);
                intent.putExtra(Geocons.GEO_LATITUDE,latitude);
                intent.putExtra(Geocons.GEO_LONGITUDE,longitude);
                intent.putExtra(Geocons.GEO_RANGE,range);
                intent.putExtra(Geocons.CLIENT_NAME,clientName);
                intent.putExtra(Geocons.STORY_OWNER_ICON_LINK,storyOwnerID);
                intent.putExtra(Geocons.STORY_IMAGE_LINK,storyID);
                intent.putExtra(Geocons.GEO_STORY,geostory);
                intent.putExtra(Geocons.POSTED_TIME,postedDate);
                //intent.putExtra(Geocons.LIKE_NUM,holder.likecount.getTag().toString());
                //intent.putExtra(Geocons.COMMENT_NUM,holder.commentcount.getTag().toString());
                context.startActivity(intent);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deleteMystory(storyID);
                showConfirmDialog(storyID);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mGeostories.size();
    }

    public void addClientStories(List<Geostory> geostories){
        mGeostories.clear();
        mGeostories.addAll(geostories);
    }

    private void showDialog(Bitmap bitmap) {
        Dialog mDialog=new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.image_dialog);
        ImageView mImage=(ImageView)mDialog.findViewById(R.id.popoutImage);
        mImage.setImageBitmap(bitmap);
        if(bitmap!=null){
            mDialog.show();
        }
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


    public void showConfirmDialog(final String storyID){
        new AlertDialog.Builder(context)
                .setTitle("GeoStory")
                .setMessage("Do you really want to delete?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteMystory(storyID);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void deleteMystory(String storyID){
        db.collection(Geocons.DBcons.GEOSTORY_DB)
                .document(storyID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Story Deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Network Issue Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public class ClientStoryViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImage;
        TextView username, datePosted, geostory, likecount, commentcount;
        ImageView geostoryImage;
        ImageView showMap, delete;
        public ClientStoryViewHolder(View itemView) {
            super(itemView);
            profileImage=(ImageView) itemView.findViewById(R.id.my_profileImage_cd);
            username=(TextView)itemView.findViewById(R.id.my_username_cd);
            datePosted=(TextView)itemView.findViewById(R.id.my_dateposted_cd);
            geostory=(TextView)itemView.findViewById(R.id.my_geostory_cd);
            geostoryImage=(ImageView)itemView.findViewById(R.id.my_geostoryimage_cd);
            geostoryImage.setTag("not updated");
            profileImage.setTag("not updated");
            showMap=(ImageView)itemView.findViewById(R.id.my_show_on_map_cd);
            delete=(ImageView)itemView.findViewById(R.id.my_delete);
            likecount=(TextView)itemView.findViewById(R.id.like_counter_mp);
            commentcount=(TextView)itemView.findViewById(R.id.comment_counter_mp);
        }
    }
}
