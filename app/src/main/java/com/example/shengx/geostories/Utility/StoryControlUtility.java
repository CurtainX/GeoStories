package com.example.shengx.geostories.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shengx.geostories.Adapters.GeostoryCardAdapter;
import com.example.shengx.geostories.Constances.Geocons;
import com.example.shengx.geostories.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SHENG.X on 2018-04-09.
 */
public class StoryControlUtility {
    final static FirebaseFirestore db=FirebaseFirestore.getInstance();
    static int commentnum=0;


    public static void likeStory(String storyID, String userID, final ImageView btn, final Context context){
        final Map<String,Object>like=new HashMap<>();
        like.put("story_id",storyID);
        like.put("user_id",userID);
        db.collection("likes").whereEqualTo("story_id",storyID).whereEqualTo("user_id",userID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if(documentSnapshots!=null){
                            if(documentSnapshots.getDocuments().size()>0){
                                Log.d("Size---",documentSnapshots.getDocuments().size()+"");
                                //dislike
                                db.collection("likes").document(documentSnapshots.getDocuments().get(0).getId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),
                                                        R.drawable.ic_favorite_border_white_24dp);
                                                btn.setColorFilter(ContextCompat.getColor(context, R.color.dislikeStory));
                                                btn.setImageBitmap(bitmap);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });


                            }else {
                                db.collection("likes").add(like);
                                Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),
                                        R.drawable.ic_favorite_white_24dp);
                                btn.setColorFilter(ContextCompat.getColor(context, R.color.likeStory));
                                btn.setColorFilter(ContextCompat.getColor(context, R.color.likeStory));

                                btn.setImageBitmap(bitmap);
                                Log.d("Size---",documentSnapshots.getDocuments().size()+"");
                                Toast.makeText(context,"Liked",Toast.LENGTH_SHORT).show();

                            }
                        }else {
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });


    }

    public static void checkLiked(String storyID, String userID, final ImageView btn, final Context context){
        btn.setImageBitmap(null);
        db.collection("likes").whereEqualTo("story_id",storyID).whereEqualTo("user_id",userID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if(documentSnapshots!=null){
                            if(documentSnapshots.getDocuments().size()>0){
                                Log.d("Size---",documentSnapshots.getDocuments().size()+"");
                                Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),
                                        R.drawable.ic_favorite_white_24dp);
                                btn.setColorFilter(ContextCompat.getColor(context, R.color.likeStory));
                                btn.setImageBitmap(bitmap);
                            }else {
                            }
                        }else {
                            Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),
                                    R.drawable.ic_favorite_border_white_24dp);
                            btn.setColorFilter(ContextCompat.getColor(context, R.color.dislikeStory));
                            btn.setImageBitmap(bitmap);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void checkComments(){

    }
    public static void commentStory(String storyID, String clientID, String commentContent){


    }
    public static void dismissStory(GeostoryCardAdapter.GeostoryHolder holder){
        holder.itemView.setVisibility(View.GONE);
    }


    public static void likeCounter(String storyID, final TextView likeView){
        db.collection("likes").whereEqualTo("story_id",storyID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int likecount=0;
                String postFix="like";
                if (e != null) {
                    Log.w("Log-Like_Counter", "Listen failed.", e);
                    return;
                }

                for (DocumentSnapshot story : documentSnapshots) {
                    likecount++;
                }
                if(likecount==0){
                    likeView.setVisibility(View.GONE);
                }
                else if(likecount==1){
                    likeView.setVisibility(View.VISIBLE);
                    likeView.setText(likecount+" "+postFix);
                }
                else if(likecount>1){
                   postFix="likes";
                    likeView.setVisibility(View.VISIBLE);
                    likeView.setText(likecount+" "+postFix);
                }
                likeView.setTag(likecount);

                }
        });
    }



    public static void commentCounter(String storyID, final TextView commentView){
        db.collection(Geocons.DBcons.COMMENTS_DB).whereEqualTo(Geocons.STORY_ID,storyID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int commentcount=0;
                String postFix="comment";
                if (e != null) {
                    Log.w("Log-comment_Counter", "Listen failed.", e);
                    return;
                }

                Log.w("Log-comment_Counter", "new comment event");
                for (DocumentSnapshot story : documentSnapshots) {
                    commentcount++;
                }
                if(commentcount==0){
                    commentView.setVisibility(View.GONE);
                }
                else if(commentcount==1){
                    commentView.setVisibility(View.VISIBLE);
                    commentView.setText(commentcount+" "+postFix);
                }
                if(commentcount>1){
                    postFix="comments";
                    commentView.setVisibility(View.VISIBLE);
                    commentView.setText(commentcount+" "+postFix);
                }
                commentView.setTag(commentcount);
            }
        });
    }



    public static void getAboutStoryOwner(final String ownerName, final TextView aboutview){
        db.collection(Geocons.DBcons.USER_DB).whereEqualTo("username",ownerName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                String about="";
                for(DocumentSnapshot documentSnapshot:documentSnapshots){
                   about=documentSnapshot.get("about").toString();
                   break;
                }
                if(about.equals("")){
                    about="He/She did not put anything about him";
                }
                aboutview.setText(about);
            }
        });
        }
}
