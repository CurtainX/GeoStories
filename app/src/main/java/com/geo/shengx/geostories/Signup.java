package com.geo.shengx.geostories;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Signup extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private final String TAG="Log";
    EditText username,email,password;
    Button create;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setElevation(0);
        mAuth = FirebaseAuth.getInstance();
        intent=new Intent(this,Login.class);
        email=(EditText)findViewById(R.id.email_su);
        password=(EditText)findViewById(R.id.passsword_su);
        create=(Button)findViewById(R.id.signin_su);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail=email.getText().toString();
                String mPassword=password.getText().toString();
                if(mPassword.trim().length()>5&&mEmail.trim().length()>0){
                    createUser(mEmail,mPassword);
                }
                else if(mEmail.trim().length()==0&&mPassword.trim().length()==0){
                    Toast.makeText(getApplicationContext(),"Please fill the blanks to continue",Toast.LENGTH_LONG).show();
                }
                else if(mEmail.trim().length()==0){
                    Toast.makeText(getApplicationContext(),"Please enter you email address.",Toast.LENGTH_LONG).show();

                }
                else if(mPassword.trim().length()==0){
                    Toast.makeText(getApplicationContext(),"Please enter you password.",Toast.LENGTH_LONG).show();
                }
                else if(mPassword.trim().length()<6){
                    Toast.makeText(getApplicationContext(),"Password must contain more than 6 characters.",Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Signup.this, "Authentication failed: Incorrect email address",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


}
