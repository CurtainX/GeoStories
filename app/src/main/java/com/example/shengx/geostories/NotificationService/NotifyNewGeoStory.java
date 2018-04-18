package com.example.shengx.geostories.NotificationService;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.shengx.geostories.Constances.Geocons;
import com.example.shengx.geostories.MainActivity;
import com.example.shengx.geostories.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;

/**
 * Created by SHENG.X on 2018-04-16.
 */

public class NotifyNewGeoStory extends IntentService {


    private static int PENDING_INTENT_RC=101;
    static FirebaseFirestore db;
    private int REQUEST_CODE=100;
    private int NOTIFICATION_ID=1;
    private String CHANNEL_ID="cid";
    NotificationManager notificationManager;
    static int oldStoryCount=0;
    static int holdon=0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotifyNewGeoStory(String name) {
        super(name);
        db=FirebaseFirestore.getInstance();
    }

    public NotifyNewGeoStory() {
        super("GeoService");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        final String city=intent.getAction();
        db=FirebaseFirestore.getInstance();
        db.collection(Geocons.DBcons.GEOSTORY_DB)
                .whereEqualTo(Geocons.STORY_CITY, city)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("Log", "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Log.d("Loggg", "New Story: " + snapshots.getDocumentChanges().size());
                                if(snapshots.getDocumentChanges().size()==1){
                                    if(!snapshots.getDocumentChanges().get(0).getDocument().get(Geocons.CLIENT_ID).toString().equals(FirebaseAuth.getInstance().getUid())){
                                        showNotifications();
                                    }
                                }
                                break;
                            }

                        }

                    }
                });
    }

    private void showNotifications() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name ="eeeeee";
            String description = "111111111";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mNofication=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setColor(Color.DKGRAY)
                .setSmallIcon(R.drawable.ic_account_box_white_24dp)
                .setLargeIcon(null)
                .setContentTitle("New Geostory Reminder")
                .setContentText("There are new geostories posted in your city")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("There are new geostories posted in your city"))
                .setContentIntent(contentIntent())
                //.addAction(ignoreReminderAction(ctx))
                .setAutoCancel(true);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT<Build.VERSION_CODES.O ) {
            mNofication.setPriority(PRIORITY_HIGH);
        }
        notificationManager.notify(NOTIFICATION_ID,mNofication.build());
    }

    private PendingIntent contentIntent() {
        Intent startMainActivity=new Intent(this, MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,PENDING_INTENT_RC,startMainActivity,PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }


}
