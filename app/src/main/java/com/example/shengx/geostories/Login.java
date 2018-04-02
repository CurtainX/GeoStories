package com.example.shengx.geostories;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    EditText  username,password;
    TextView signUp;
    Button signIn,signwithFB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setElevation(0);
        username=(EditText)findViewById(R.id.username_su);
        password=(EditText)findViewById(R.id.passsword_su);
        signIn=(Button)findViewById(R.id.signup_su);
        signUp=(TextView)findViewById(R.id.create_acc_si);
        signwithFB=(Button)findViewById(R.id.sigin_w_fb_si);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatNew();
            }
        });

        signwithFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbSignIn();
            }
        });
    }


    public void signIn(){
        //username and password check;


        //if true
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void creatNew(){
        Intent intent=new Intent(this,Signup.class);
        startActivity(intent);
    }
    public void fbSignIn(){
       //call firebase FB sign in
        Toast.makeText(this,"Not available now", Toast.LENGTH_SHORT).show();
    }
}
