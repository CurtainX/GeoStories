package com.example.shengx.geostories.Utility;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shengx.geostories.GeostoryCardAdapter;
import com.example.shengx.geostories.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SHENG.X on 2018-04-09.
 */
public class StoryControlUtility {


    public static void likeStory(String storyID, String userID, final Button btn, final Context context){
        final FirebaseFirestore db=FirebaseFirestore.getInstance();
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
                                btn.setText("Dislike");
                                Toast.makeText(context,"Already liked",Toast.LENGTH_SHORT).show();
                            }else {
                                db.collection("likes").add(like);
                                btn.setText("Dislike");
                                Log.d("Size---",documentSnapshots.getDocuments().size()+"");
                                Toast.makeText(context,"Liked",Toast.LENGTH_SHORT).show();

                            }
                        }else {
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                db.collection("likes").add(like);
            }
        });


    }
    public static void commentStory(){

    }
    public static void dismissStory(GeostoryCardAdapter.GeostoryHolder holder){
        holder.itemView.setVisibility(View.GONE);
    }
}
