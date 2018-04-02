package com.example.shengx.geostories;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {
    EditText  username,password;
    TextView signUp;
    Button signIn,signwithFB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setElevation(0);
        username=(EditText)findViewById(R.id.username_si);
        password=(EditText)findViewById(R.id.passsword_si);
        signIn=(Button)findViewById(R.id.siginbtn_si);
        signUp=(TextView)findViewById(R.id.create_acc_si);
        signwithFB=(Button)findViewById(R.id.sigin_w_fb_si);

        
    }
}
