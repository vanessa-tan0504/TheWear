package com.thewear.thewearapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText inputEmail,inputPw,inputConfirmPw,inputUser;
    private Button btnLogIn;
    private CircularProgressButton btnRegister;
    private ProgressBar progressBar;
    private static String TAG = RegisterActivity.class.getSimpleName();
    private Bitmap bitmapTick,bitmapCross;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        //get firebase auth instance
        auth= FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        inputEmail=findViewById(R.id.signup_email);
        inputPw=findViewById(R.id.signup_pw);
        inputConfirmPw=findViewById(R.id.signup_confirmpw);
        inputUser=findViewById(R.id.signup_username);
        btnRegister=findViewById(R.id.btn_register);
        btnLogIn=findViewById(R.id.btn_login);
        final Drawable black_btn = getResources().getDrawable(R.drawable.rounded_btn_black);
        final Drawable white_btn = getResources().getDrawable(R.drawable.rounded_btn_white);
        Drawable tick = getResources().getDrawable(R.drawable.tick_icon);
        bitmapTick = ((BitmapDrawable)tick).getBitmap();
        Drawable cross = getResources().getDrawable(R.drawable.cross_icon);
        bitmapCross = ((BitmapDrawable)cross).getBitmap();

        //register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWindowFocusChanged(true);
                btnRegister.startAnimation();
                final String email = inputEmail.getText().toString().trim();
                String password = inputPw.getText().toString().trim();
                String confirm_pass = inputConfirmPw.getText().toString().trim();
                String username = inputUser.getText().toString().trim();

                if(TextUtils.isEmpty(username)){
                    inputUser.setError("Enter user name");
                    delay_anim(white_btn); //delay and revert anim
                    return;
                }

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
                    inputPw.setError("Enter password");
                    delay_anim(white_btn); //delay and revert anim
                    return;
                }

                if (password.length() < 6) {
                    inputPw.setError("Password too short");
                    delay_anim(white_btn); //delay and revert anim
                    return;
                }

                if (TextUtils.isEmpty(confirm_pass)) {
                    inputConfirmPw.setError("Enter confirm password");
                    delay_anim(white_btn); //delay and revert anim
                    return;
                }

                if (!password.equals(confirm_pass)) {
                    inputConfirmPw.setError("Confirm password not same with password");
                    delay_anim(white_btn); //delay and revert anim
                    return;
                }

                //if all fields okay, create user to firebase
                auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                btnRegister.startAnimation();

                                if(!task.isSuccessful()){//if account cannot register
                                    btnRegister.doneLoadingAnimation(Color.RED,bitmapCross);
                                    delay_anim(white_btn); //delay and revert
                                    Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                    //Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                                }
                                else {// if account register success ,store user's username
                                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(inputUser.getText().toString().trim())
                                            .build();
                                    user.updateProfile(profileChangeRequest)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                       // Toast.makeText(RegisterActivity.this, "Welcome! " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                                        if(user!=null){
                                                            User newuser = new User(user.getDisplayName(),user.getEmail(),null);
                                                            db.collection("users").document(user.getUid()+"").set(newuser)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(RegisterActivity.this, "user updated", Toast.LENGTH_SHORT).show();
                                                                            new Handler().postDelayed(new Runnable() { //pause 3 second only resume to walkthru
                                                                                @Override
                                                                                public void run() {
                                                                                    btnRegister.doneLoadingAnimation(Color.BLACK,bitmapTick);
                                                                                }
                                                                            }, 1000);
                                                                            new Handler().postDelayed(new Runnable() { //pause 3 second only resume to walkthru
                                                                                @Override
                                                                                public void run() {
                                                                                    Intent intent= new Intent(RegisterActivity.this, TrainActivity.class);
                                                                                    intent.putExtra("username",user.getDisplayName()+"");
                                                                                    startActivity(intent);
                                                                                }
                                                                            }, 2000);
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            btnRegister.doneLoadingAnimation(Color.RED,bitmapCross);
                                                                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //finish();
                                }
                            }
                        });
            }
        });
        //end of register button
    }

    public void delay_anim(final Drawable white_btn){
        new Handler().postDelayed(new Runnable() { //pause 3 second only resume to walkthru
            @Override
            public void run() {
                btnRegister.revertAnimation();
                btnRegister.setBackground(white_btn);
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
