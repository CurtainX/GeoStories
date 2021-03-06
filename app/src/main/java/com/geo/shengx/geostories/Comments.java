package com.geo.shengx.geostories;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.drm.DrmStore;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geo.shengx.geostories.Adapters.CommentAdapter;
import com.geo.shengx.geostories.Constances.Geocons;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Comments extends AppCompatActivity {
    EditText commentText;
    RecyclerView commentList;

    Intent commentIntent;
    String storyID;

    FirebaseFirestore db;
    String clientID,clientName;
    List<Comment> mComments;
    CommentAdapter commentAdapter;
    Activity mActivity;

    TextView send_comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);



        commentText=(EditText)findViewById(R.id.commenttext);
        commentText.setFocusableInTouchMode(true);
        commentText.setFocusable(true);
        commentText.requestFocus();

        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if(commentText.getText().toString().trim().length()>0){
                    send_comment.setAlpha(1);
                }
                else {
                    send_comment.setAlpha((float) 0.5);
                }
            }
        });


        send_comment=(TextView)findViewById(R.id.send_comment);
        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isComment()){
                    addComment(commentText.getText().toString());
                    //send_comment.setAlpha((float) 0.5);
                }
            }
        });
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);

        commentList=(RecyclerView)findViewById(R.id.comment_list);
        commentList.setHasFixedSize(true);
        commentList.setLayoutManager(layoutManager);

        mActivity=this;

        commentIntent=getIntent();
        storyID=commentIntent.getStringExtra(Geocons.COMMENT_INTENT_EXTRA);
        clientName=commentIntent.getStringExtra(Geocons.CLIENT_NAME);

        db=FirebaseFirestore.getInstance();
        clientID= FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection(Geocons.DBcons.COMMENTS_DB)
                .whereEqualTo(Geocons.STORY_ID,storyID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        mComments=new ArrayList<>();

                        if (e != null) {
                            Log.w("lOG", "Listen failed.", e);
                            return;
                        }
                        for (DocumentSnapshot comment : documentSnapshots) {
                            mComments.add(new Comment(
                                    String.valueOf(comment.get(Geocons.CLIENT_ID)),
                                    String.valueOf(comment.get(Geocons.CLIENT_NAME)),
                                    String.valueOf(comment.get(Geocons.STORY_ID)),
                                    String.valueOf(comment.get(Geocons.COMMENT_CONTENT)),
                                    String.valueOf(comment.get(Geocons.COMMENT_DATE))));
                        }
                        commentAdapter=new CommentAdapter(mComments,mActivity);
                        commentList.setAdapter(commentAdapter);
                    }
                });



    }


    public void addComment(String comment){
        HashMap<String,Object> mComment=new HashMap<>();
        mComment.put(Geocons.CLIENT_ID,clientID);
        mComment.put(Geocons.CLIENT_NAME,clientName);
        mComment.put(Geocons.STORY_ID,storyID);
        mComment.put(Geocons.COMMENT_CONTENT,comment);
        mComment.put(Geocons.COMMENT_DATE,String.valueOf(Calendar.getInstance().getTime()));
        db.collection(Geocons.DBcons.COMMENTS_DB).add(mComment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                commentText.setText("");
                commentText.setHint("Add a comment here");
                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isComment(){
        if(commentText.getText().toString().trim().length()>0){
            return true;
        }else {
            Toast.makeText(getApplicationContext(),"Empty comment",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
