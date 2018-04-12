package com.example.shengx.geostories;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class Comments extends AppCompatActivity {
    EditText commentText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentText=(EditText)findViewById(R.id.commenttext);
        commentText.setFocusableInTouchMode(true);
        commentText.setFocusable(true);
        commentText.requestFocus();

    }
}
