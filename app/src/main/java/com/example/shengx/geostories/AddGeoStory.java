package com.example.shengx.geostories;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class AddGeoStory extends AppCompatActivity {
    Intent intent;
    EditText geostoryin;
    private final int GALLERY_REQUEST=100;
    ImageButton photo_from_gallary, photo_by_camera;
    TextView story_image_prev;
    Bitmap bitmap;
    File storagePath,myFile;
    Boolean withPhoto=false;
    private int REQ_W_STORAGE_CODE=200;

    static final int REQUEST_IMAGE_CAPTURE = 300;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geo_story);
        intent = new Intent(this, MapsActivity.class);
        geostoryin = (EditText) findViewById(R.id.geostory_input);
        geostoryin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event == null) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // Capture soft enters in a singleLine EditText that is the last EditText
                        // This one is useful for the new list case, when there are no existing ListItems
                        geostoryin.clearFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        // Capture soft enters in other singleLine EditTexts
                    } else if (actionId == EditorInfo.IME_ACTION_GO) {
                    } else {
                        // Let the system handle all other null KeyEvents
                        return false;
                    }
                } else if (actionId == EditorInfo.IME_NULL) {
                    // Capture most soft enters in multi-line EditTexts and all hard enters;
                    // They supply a zero actionId and a valid keyEvent rather than
                    // a non-zero actionId and a null event like the previous cases.
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        // We capture the event when the key is first pressed.
                    } else {
                        // We consume the event when the key is released.
                        return true;
                    }
                } else {
                    // We let the system handle it when the listener is triggered by something that
                    // wasn't an enter.
                    return false;
                }
                return true;
            }
        });
        story_image_prev=(TextView)findViewById(R.id.prev_story_photo);
        photo_from_gallary=(ImageButton)findViewById(R.id.story_photo_gallary);
        photo_from_gallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStoragePermissionGranted()){
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                }
            }
        });
        photo_by_camera=(ImageButton)findViewById(R.id.story_photo_camera);
        photo_by_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        storagePath = new File(Environment.getExternalStorageDirectory(), "Geo_Images");
        // Create direcorty if not exists
        if(!storagePath.exists()) {
            storagePath.mkdirs();
            Log.d("log---G","Geo file created");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                        Log.d("Log--size",bitmap.getByteCount()+"");
                        myFile = new File(storagePath,"story.jpg");
                        FileOutputStream fileOutputStream=new FileOutputStream(myFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, fileOutputStream);
                        BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                        story_image_prev.setBackgroundDrawable(ob);
                        withPhoto=true;

                        Log.d("TAG", "Success");
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE:

                    try {
                        Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                        BitmapDrawable ob = new BitmapDrawable(getResources(), thumbnail);
                        story_image_prev.setBackgroundDrawable(ob);
                        String imageurl = getRealPathFromURI(imageUri);
                        Log.d("LOG----->",imageurl);

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        Bitmap bitmap = BitmapFactory.decodeFile(imageurl, options);


                        myFile = new File(storagePath,"story.jpg");
                        FileOutputStream fileOutputStream=new FileOutputStream(myFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        withPhoto=true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addgeostoryopmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tomap:
                if(geostoryin.getText().toString().trim().equals("")){
                    Toast.makeText(this,"Please Enter Your Geostory",Toast.LENGTH_LONG).show();
                }
                else {
                        String mGeostory=geostoryin.getText().toString();
                        intent.putExtra(Geocons.GEO_STORY,mGeostory);
                        intent.putExtra(Geocons.GEO_STORY_PHOTO_YES_NO,withPhoto);
                        startActivity(intent);
                }
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Log--p","Permission is granted");
                return true;
            } else {

                Log.v("Log--p","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},REQ_W_STORAGE_CODE );
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Log--p","Permission is granted");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("Log--p","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
        }
        else {
            Toast.makeText(getApplicationContext(),"Please grant the permission to continue",Toast.LENGTH_SHORT).show();
        }
    }


    private void dispatchTakePictureIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

}
