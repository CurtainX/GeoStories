package com.example.shengx.geostories.ImageUtility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by SHENG.X on 2018-04-07.
 */

public class GallaryImage extends AppCompatActivity{
    public static final int GALLERY_REQUEST=100;
    public GallaryImage() {



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        Log.d("Img","Success");
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
            }
    }
}
