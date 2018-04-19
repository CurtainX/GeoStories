package com.geo.shengx.geostories;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.geo.shengx.geostories.Adapters.ClientGeoSotryAdapter;
import com.geo.shengx.geostories.Constances.Geocons;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyGeoStories extends AppCompatActivity {

    FirebaseFirestore db;

    FirebaseUser client;
    String clientID;

    List<Geostory> mGeostories=new ArrayList<>();

    ClientGeoSotryAdapter clientGeoSotryAdapter;
    RecyclerView clientStories;
    RecyclerView.LayoutManager layoutManager;

    Activity mActivity;


    @Override
    protected void onStart() {
        super.onStart();
//        db.collection(Geocons.DBcons.GEOSTORY_DB)
//                .whereEqualTo(Geocons.CLIENT_ID,clientID)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot documentSnapshots) {
//                        mGeostories.clear();
//                        for(DocumentSnapshot story:documentSnapshots){
//                            mGeostories.add(new Geostory(String.valueOf(story.getId()),
//                                    String.valueOf(story.get(Geocons.CLIENT_ID)),
//                                    null,
//                                    null,
//                                    String.valueOf(story.get(Geocons.CLIENT_NAME)),
//                                    String.valueOf(story.get(Geocons.POSTED_TIME)),
//                                    String.valueOf(story.get(Geocons.GEO_STORY)),
//                                    String.valueOf(story.get(Geocons.GEO_LATITUDE)),
//                                    String.valueOf(story.get(Geocons.GEO_LONGITUDE)),
//                                    String.valueOf(story.get(Geocons.GEO_RANGE))));
//                            Log.d("My--","updated");
//                        }
////                        clientGeoSotryAdapter.addClientStories(mGeostories);
//
//                        clientGeoSotryAdapter=new ClientGeoSotryAdapter(mGeostories,mActivity);
//                        clientStories.setAdapter(clientGeoSotryAdapter);
////                        clientGeoSotryAdapter.notifyDataSetChanged();
//
//                    }
//                });

                db.collection(Geocons.DBcons.GEOSTORY_DB)
                .whereEqualTo(Geocons.CLIENT_ID,clientID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Log", "Listen failed.", e);
                            return;
                        }
                        mGeostories.clear();
                        for(DocumentSnapshot story:documentSnapshots){
                            mGeostories.add(new Geostory(String.valueOf(story.getId()),
                                    String.valueOf(story.get(Geocons.CLIENT_ID)),
                                    null,
                                    null,
                                    String.valueOf(story.get(Geocons.CLIENT_NAME)),
                                    String.valueOf(story.get(Geocons.POSTED_TIME)),
                                    String.valueOf(story.get(Geocons.GEO_STORY)),
                                    String.valueOf(story.get(Geocons.GEO_LATITUDE)),
                                    String.valueOf(story.get(Geocons.GEO_LONGITUDE)),
                                    String.valueOf(story.get(Geocons.GEO_RANGE))));
                            Log.d("My--","updated");
                        }
                        clientGeoSotryAdapter=new ClientGeoSotryAdapter(mGeostories,mActivity);
                        clientStories.setAdapter(clientGeoSotryAdapter);
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_geo_stories);
        client= FirebaseAuth.getInstance().getCurrentUser();
        clientID=client.getUid();

        db=FirebaseFirestore.getInstance();

        mActivity=this;

        layoutManager=new LinearLayoutManager(this);
        clientGeoSotryAdapter=new ClientGeoSotryAdapter(mGeostories,this);
        clientStories=(RecyclerView)findViewById(R.id.my_geostories_list);
        clientStories.setHasFixedSize(true);
        clientStories.setLayoutManager(layoutManager);
        clientStories.setAdapter(clientGeoSotryAdapter);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }

}
