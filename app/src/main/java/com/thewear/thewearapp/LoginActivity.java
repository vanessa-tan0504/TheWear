package com.thewear.thewearapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText inputEmail,inputPass;
    private Button btnSignup,btnForget;
    private CircularProgressButton btnLogin;
    private Bitmap bitmapTick,bitmapCross;
    private static String TAG = TrainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        inputEmail = findViewById(R.id.login_email);
        inputPass = findViewById(R.id.login_pw);
        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);
        btnForget=findViewById(R.id.btn_forget);
        final Drawable black_btn = getResources().getDrawable(R.drawable.rounded_btn_black);
        final Drawable white_btn = getResources().getDrawable(R.drawable.rounded_btn_white);
        Drawable tick = getResources().getDrawable(R.drawable.tick_icon);
        bitmapTick = ((BitmapDrawable)tick).getBitmap();
        Drawable cross = getResources().getDrawable(R.drawable.cross_icon);
        bitmapCross = ((BitmapDrawable)cross).getBitmap();

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //go to sign up interface
//        btnSignup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LogInActivity.this,SignUpActivity.class));
//                finish();
//                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//            }
//        });
        //end of go to sign up interface


        //log in button settings
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWindowFocusChanged(true);

                btnLogin.startAnimation();

                String email = inputEmail.getText().toString().trim();
                final String password = inputPass.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError("Enter email address");
                    delay_anim(white_btn); //delay and revert anim
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.setError("Invalid email format");
                    delay_anim(white_btn); //delay and revert anim
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    inputPass.setError("Enter password");
                    delay_anim(white_btn); //delay and revert anim
                    return;
                }

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                btnLogin.startAnimation();
                                if (!task.isSuccessful()) {//if account cannot login
                                    btnLogin.doneLoadingAnimation(Color.RED,bitmapCross);
                                    delay_anim(white_btn); //delay and revert
                                    Toast.makeText(LoginActivity.this, "Authentication failed. " + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                                else { //if account can login
                                    //retrive xml model from firebase
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    String user_name = user.getDisplayName()+"";
                                    StorageReference mStorageRef;
                                    mStorageRef = FirebaseStorage.getInstance().getReference(user_name); // getInstance = root firebase file images= foldername
                                    StorageReference ref = mStorageRef.child("Model XML");
                                    File storagePath = new File(Environment.getExternalStorageDirectory(),"TrainedData");
                                    // Create directory if not exists
                                    if(!storagePath.exists()) {
                                        Log.e(TAG, "directory exsisted");
                                        storagePath.mkdirs();
                                    }
                                      final File myFile = new File(storagePath,"test_model.xml");
                                     ref.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                     //Local temp file has been created
                                            Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG,"local tem file created  created "+ myFile.toString());
                                            new Handler().postDelayed(new Runnable() { //pause 3 second only resume to walkthru
                                                @Override
                                                public void run() {
                                                    btnLogin.doneLoadingAnimation(Color.BLACK,bitmapTick);
                                                }
                                                }, 1000);
                                            new Handler().postDelayed(new Runnable() { //pause 3 second only resume to walkthru
                                                @Override
                                                public void run() {
                                                    Intent intent = new Intent(LoginActivity.this, RecognizeActivity.class);
                                                    startActivity(intent);
                                                }
                                            }, 2000);
                                        }}).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                           public void onFailure(@NonNull Exception exception) {
                                                btnLogin.doneLoadingAnimation(Color.RED,bitmapCross);
                                                delay_anim(white_btn); //delay and revert
                                                //Handle any errors
                                                Toast.makeText(LoginActivity.this, "no success", Toast.LENGTH_SHORT).show();
                                               Log.e(TAG,"file not created "+ exception.toString());
                                            }
                                        });
                        }
            }
        });
        //end of log in button settings
            }
            });

        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String user_email = user.getEmail()+"";
                //String user_email="vminhui@yahoo.com";
                Toast.makeText(LoginActivity.this, "pw reset waiting...", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().setLanguageCode("en");
                FirebaseAuth.getInstance().sendPasswordResetEmail(user_email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Firebase Console received", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });

    }

    public void delay_anim(final Drawable white_btn){
        new Handler().postDelayed(new Runnable() { //pause 3 second only resume to walkthru
            @Override
            public void run() {
                btnLogin.revertAnimation();
                btnLogin.setBackground(white_btn);
            }
        }, 1000);
    }

    //hide status bar and below softkey
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
