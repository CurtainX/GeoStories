package com.example.shengx.geostories;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shengx.geostories.Constances.Geocons;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText  email,password;
    TextView signUp;
    Button signIn,signwithGoogle;
    GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN=100;
    private FirebaseAuth mAuth;
    private final String TAG="Log";
    Intent intent;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    FirebaseFirestore db;
    FirebaseUser current_client;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setElevation(0);
        email=(EditText)findViewById(R.id.email_si);
        password=(EditText)findViewById(R.id.passsword_si);
        signIn=(Button)findViewById(R.id.signin_si);
        signUp=(TextView)findViewById(R.id.create_acc_si);
        signwithGoogle=(Button)findViewById(R.id.sigin_w_fb_si);

        sharedPref = getApplicationContext().getSharedPreferences("Client",0);
        editor = sharedPref.edit();


        db=FirebaseFirestore.getInstance();

        intent=new Intent(Login.this,MainActivity.class);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(email.getText().toString(),password.getText().toString());
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatNew();
            }
        });

        signwithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null&&sharedPref.getBoolean(Geocons.FIRST_TIME_SIGN_IN,true)==false){
            logedIn();
        }
    }

    public void signIn(String email, String password){
        //username and password check;
        if(email.trim().length()>0&&password.trim().length()>6){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                logedIn();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(Login.this, "Authentication failed: Incorrect email/password",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
        else {
            Toast.makeText(getApplicationContext(),"Invalid email/password",Toast.LENGTH_SHORT).show();
        }
    }

    public void creatNew(){
        Intent intent=new Intent(this,Signup.class);
        startActivity(intent);
    }
    public void googleSignIn(){
       //call firebase FB sign in
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //
                            logedIn();
                            Log.d(TAG,"success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Log.d("Log","Authentication Failed.");
                        }

                    }
                });
    }

    public void logedIn(){
        current_client=FirebaseAuth.getInstance().getCurrentUser();

        final SharedPreferences.Editor editor = sharedPref.edit();
        db.collection("users").document(current_client.getUid().toString()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            editor.putString("username",documentSnapshot.get("username").toString());
                            editor.putString("about",documentSnapshot.get("about").toString());
                        }else {
                            editor.putString("username","");
                            editor.putString("about","");
                        }

                        editor.commit();
                        Log.d("Log-----","Success");

                    }
                });
        if(sharedPref.getBoolean(Geocons.FIRST_TIME_SIGN_IN,true)){
            Intent firstTimeLogin=new Intent(this, editprofile.class);
            startActivity(firstTimeLogin);
            finish();
        }
        else {
            startActivity(intent);
            finish();
        }

    }

}
