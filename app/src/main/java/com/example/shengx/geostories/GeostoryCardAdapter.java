package com.example.shengx.geostories;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shengx.geostories.Utility.StoryControlUtility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by SHENG.X on 2018-03-22.
 */

public class GeostoryCardAdapter extends RecyclerView.Adapter<GeostoryCardAdapter.GeostoryHolder> {
    List<Geostory> mGeostories;
    ImageView profileImage;
    TextView username, datePosted, geostory;
    ImageView geostoryImage;
    Button like, comment,dismiss;
    Context context;
    FirebaseStorage storage;
    final long ONE_MEGABYTE=1024*1024;


    StorageReference gsReference_profile_img,gsReference_story_img;


    public GeostoryCardAdapter(List<Geostory> mGeostories, Context context) {
        this.mGeostories = mGeostories;
        this.context=context;
        storage=FirebaseStorage.getInstance();
    }

    @Override
    public GeostoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.storycard,parent,false);
        GeostoryHolder vh=new GeostoryHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final GeostoryHolder holder, int position) {
//        Bitmap profile_image=mGeostories.get(position).getProfile_image();
//        Bitmap story_image=mGeostories.get(position).getStory_image();


        updateProfileImage(mGeostories.get(position).getClientID(),profileImage);
        updateStoryImage(mGeostories.get(position).getStoryID(),geostoryImage);
        username.setText(mGeostories.get(position).getUsername());
        datePosted.setText(mGeostories.get(position).getDatePosted());
        geostory.setText(mGeostories.get(position).getGeostory());
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Liked",Toast.LENGTH_LONG).show();
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"comment",Toast.LENGTH_LONG).show();
            }
        });
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoryControlUtility.dismissStory(holder);
                Toast.makeText(v.getContext(),"dissmiss",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGeostories.size();
    }


    public void updateProfileImage(String client_id, final ImageView profileImage){
        gsReference_profile_img = storage.getReferenceFromUrl("gs://geostories-87738.appspot.com/"+client_id+".jpg");
        gsReference_profile_img.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            // Data for "images/island.jpg" is returns, use this as needed
                                            if(bytes.length!=0) {
                                                BitmapFactory.Options options = new BitmapFactory.Options();
                                                options.inMutable = true;
                                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                                                profileImage.setImageBitmap(bmp);
                                                Log.d("Log-chec", "success story image downloaded" + bmp.getByteCount());
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                            Log.d("Log","No ~!!success222");
//                                            Drawable d = context.getResources().getDrawable(R.drawable.ic_camera_black_24dp);
//                                            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
//                                            profileImage.setImageBitmap(null);

                                        }
                                    });
    }


    public void updateStoryImage(String story_id, final ImageView storyImage){
        gsReference_story_img = storage.getReferenceFromUrl("gs://geostories-87738.appspot.com/"+story_id+".jpg");
        gsReference_story_img.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                if(bytes.length!=0) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                    storyImage.setImageBitmap(bmp);
                    Log.d("Log-chec", "success story image downloaded" + bmp.getByteCount());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("Log","No ~!!success222");
//                Drawable d = context.getResources().getDrawable(R.drawable.ic_camera_black_24dp);
//                Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
//                storyImage.setImageBitmap(null);

            }
        });
    }

    public class GeostoryHolder extends RecyclerView.ViewHolder{

        public GeostoryHolder(View itemView) {
            super(itemView);
            profileImage=(ImageView) itemView.findViewById(R.id.profileImage_cd);
            username=(TextView)itemView.findViewById(R.id.username_cd);
            datePosted=(TextView)itemView.findViewById(R.id.dateposted_cd);
            geostory=(TextView)itemView.findViewById(R.id.geostory_cd);
            geostoryImage=(ImageView)itemView.findViewById(R.id.geostoryimage_cd);
            like=(Button)itemView.findViewById(R.id.like_cd);
            comment=(Button)itemView.findViewById(R.id.comment_cd);
            dismiss=(Button)itemView.findViewById(R.id.dismiss_cd);
        }
    }
}
