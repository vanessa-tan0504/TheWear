package com.thewear.thewearapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText inputEmail, inputPass,inputFace;
    TextInputLayout reset_pw_layout;
    private Button btnSignup, btnForget;
    private CircularProgressButton btnLogin, btnReset, btnLoginFace;
    private Bitmap bitmapTick, bitmapCross;
    private static String TAG = TrainActivity.class.getSimpleName();
    private ConstraintLayout constraintLayout;
    private SwitchCompat switchCompat;
    private TextInputLayout pw_layout, email_layout,face_layout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        final Drawable black_btn = getResources().getDrawable(R.drawable.rounded_btn_black);
        final Drawable white_btn = getResources().getDrawable(R.drawable.rounded_btn_white);
        inputEmail = findViewById(R.id.login_email);
        inputPass = findViewById(R.id.login_pw);
        inputFace = findViewById(R.id.login_face);
        btnLogin = findViewById(R.id.btn_login);
        btnLoginFace = findViewById(R.id.btn_facelogin);
        btnReset = findViewById(R.id.btn_reset);
        btnSignup = findViewById(R.id.btn_signup);
        btnForget = findViewById(R.id.btn_forget);
        reset_pw_layout = findViewById(R.id.reset_pw_layout);
        Drawable tick = getResources().getDrawable(R.drawable.tick_icon);
        bitmapTick = ((BitmapDrawable) tick).getBitmap();
        Drawable cross = getResources().getDrawable(R.drawable.cross_icon);
        bitmapCross = ((BitmapDrawable) cross).getBitmap();
        constraintLayout = findViewById(R.id.login);
        switchCompat = findViewById(R.id.switchbtn);
        email_layout=findViewById(R.id.login_email_layout);
        pw_layout=findViewById(R.id.login_pw_layout);
        face_layout = findViewById(R.id.login_face_layout);




        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();

        //go to sign up interface

        btnSignup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnSignup.setBackgroundResource(R.drawable.rounded_btn_grey);
                    btnSignup.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //when button released
                    btnSignup.setBackgroundResource(R.drawable.rounded_btn_black);
                    btnSignup.setTextColor(Color.parseColor("#FFFFFF"));
                }
                return false;
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });


        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) { //face recognition method (inital)
                    Toast.makeText(LoginActivity.this, "is checked", Toast.LENGTH_SHORT).show();
                    faceLogin();

                }
                else {//usual login method
                    Toast.makeText(LoginActivity.this, "not checked", Toast.LENGTH_SHORT).show();
                    usualLogin();

                }

            }
        });

        btnLoginFace.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnLoginFace.setBackgroundResource(R.drawable.rounded_btn_grey);
                    btnLoginFace.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //when button released
                    btnLoginFace.setBackgroundResource(R.drawable.rounded_btn_white);
                    btnLoginFace.setTextColor(Color.parseColor("#000000"));
                }
                return false;
            }
        });
        btnLoginFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWindowFocusChanged(true);
                btnLoginFace.startAnimation();

                final String email_username = inputFace.getText().toString().trim();
                if (TextUtils.isEmpty(email_username)) { // if field is empty
                    inputFace.setError("Enter email or username");
                    delay_anim(white_btn, btnLoginFace); //delay and revert anim
                    return;
                }
                if (Patterns.EMAIL_ADDRESS.matcher(email_username).matches()) { // if input is email format
                    Toast.makeText(LoginActivity.this, "input is email", Toast.LENGTH_SHORT).show();
                    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                    CollectionReference usersRef = rootRef.collection("users");
                    Query query = usersRef.whereEqualTo("email", email_username);
                    query.get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        btnLoginFace.startAnimation();
                                        if (task.getResult().isEmpty()) { //if there is no result
                                            Snackbar.make(constraintLayout, "Email not existed. Have you registered?", Snackbar.LENGTH_LONG).show();
                                            btnLoginFace.doneLoadingAnimation(Color.RED, bitmapCross);
                                            delay_anim(white_btn, btnLoginFace); //delay and revert
                                        }
                                        else{ // if there is email
                                            Snackbar.make(constraintLayout, "Email existed", Snackbar.LENGTH_LONG).show();
                                            delay_anim(white_btn, btnLoginFace);
                                        }
                                    }
                                    else{
                                        Snackbar.make(constraintLayout, "Cannot read data from firebase" + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
                else{ // else input is count as username
                    Toast.makeText(LoginActivity.this, "input is username", Toast.LENGTH_SHORT).show();
                    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                    CollectionReference usersRef = rootRef.collection("users");
                    Query query = usersRef.whereEqualTo("userName", email_username);
                    query.get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        btnLoginFace.startAnimation();
                                        if (task.getResult().isEmpty()) { //if there is no result
                                            Snackbar.make(constraintLayout, "Username not existed. Have you registered?", Snackbar.LENGTH_LONG).show();
                                            btnLoginFace.doneLoadingAnimation(Color.RED, bitmapCross);
                                            delay_anim(white_btn, btnLoginFace); //delay and revert
                                        }
                                        else{ //if there is username
                                            Snackbar.make(constraintLayout, "Username existed", Snackbar.LENGTH_LONG).show();
                                            delay_anim(white_btn, btnLoginFace);

                                            db.collection("users").whereEqualTo("userName",email_username)
                                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            String UserID = document.getString("uid");
                                                            Toast.makeText(LoginActivity.this, UserID+"", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    else{
                                                        Snackbar.make(constraintLayout, "Cannot read data from firebase" + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();

                                                    }
                                                }
                                            });



                                            //retrive xml model from firebase
//                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                                            String user_name = user.getDisplayName() + "";
//                                            StorageReference mStorageRef;
//                                            mStorageRef = FirebaseStorage.getInstance().getReference(user_name); // getInstance = root firebase file images= foldername
//                                            StorageReference ref = mStorageRef.child("Model XML");
//                                            File storagePath = new File(Environment.getExternalStorageDirectory(), "TrainedData");
//                                            // Create directory if not exists
//                                            if (!storagePath.exists()) {
//                                                Log.e(TAG, "directory exsisted");
//                                                storagePath.mkdirs();
//                                            }
//                                            final File myFile = new File(storagePath, "test_model.xml");
//                                            ref.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                                                @Override
//                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                                    //Local temp file has been created
////
//                                                    Log.e(TAG, "local tem file created  created " + myFile.toString());
//                                                    new Handler().postDelayed(new Runnable() { //pause 3 second only resume to walkthru
//                                                        @Override
//                                                        public void run() {
//                                                            btnLogin.doneLoadingAnimation(Color.GREEN, bitmapTick);
//                                                        }
//                                                    }, 1000);
//                                                    new Handler().postDelayed(new Runnable() { //pause 3 second only resume to walkthru
//                                                        @Override
//                                                        public void run() {
//                                                            Intent intent = new Intent(LoginActivity.this, ShopActivity.class); //no need recognize?
//                                                            intent.putExtra("intentfrom", "login");
//                                                            startActivity(intent);
//                                                        }
//                                                    }, 2000);
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception exception) {
//                                                    btnLogin.doneLoadingAnimation(Color.RED, bitmapCross);
//                                                    delay_anim(white_btn, btnLogin); //delay and revert
//                                                    //Handle any errors
//                                                    Snackbar.make(constraintLayout, "Login Unsuccessful. Please ensure storage and camera permission is allowed", Snackbar.LENGTH_LONG).show();
//                                                    Log.e(TAG, "file not created " + exception.toString());
//                                                }
//                                            });
                                        }
                                    }
                                    else{
                                        Snackbar.make(constraintLayout, "Cannot read data from firebase" + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });

                }


            }
        });

        //end of face login
        ////login with email and password log in button settings
        btnLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btnLogin.setBackgroundResource(R.drawable.rounded_btn_grey);
                    btnLogin.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //when button released
                    btnLogin.setBackgroundResource(R.drawable.rounded_btn_white);
                    btnLogin.setTextColor(Color.parseColor("#000000"));
                }
                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWindowFocusChanged(true);

                btnLogin.startAnimation();

                final String email = inputEmail.getText().toString().trim();
                final String password = inputPass.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError("Enter email address");
                    delay_anim(white_btn, btnLogin); //delay and revert anim
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.setError("Invalid email format");
                    delay_anim(white_btn, btnLogin); //delay and revert anim
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    inputPass.setError("Enter password");
                    delay_anim(white_btn, btnLogin); //delay and revert anim
                    return;
                }

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                btnLogin.startAnimation();
                                if (!task.isSuccessful()) {//if account cannot login
                                    btnLogin.doneLoadingAnimation(Color.RED, bitmapCross);
                                    delay_anim(white_btn, btnLogin); //delay and revert
//
                                    Snackbar.make(constraintLayout, "Authentication failed. " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                } else { //if account can login
                                    //retrive xml model from firebase
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    String user_name = user.getDisplayName() + "";
                                    StorageReference mStorageRef;
                                    mStorageRef = FirebaseStorage.getInstance().getReference(user_name); // getInstance = root firebase file images= foldername
                                    StorageReference ref = mStorageRef.child("Model XML");
                                    File storagePath = new File(Environment.getExternalStorageDirectory(), "TrainedData");
                                    // Create directory if not exists
                                    if (!storagePath.exists()) {
                                        Log.e(TAG, "directory exsisted");
                                        storagePath.mkdirs();
                                    }
                                    final File myFile = new File(storagePath, "test_model.xml");
                                    ref.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            //Local temp file has been created
//
                                            Log.e(TAG, "local tem file created  created " + myFile.toString());
                                            new Handler().postDelayed(new Runnable() { //pause 3 second only resume to walkthru
                                                @Override
                                                public void run() {
                                                    btnLogin.doneLoadingAnimation(Color.GREEN, bitmapTick);
                                                }
                                            }, 1000);
                                            new Handler().postDelayed(new Runnable() { //pause 3 second only resume to walkthru
                                                @Override
                                                public void run() {
                                                    Intent intent = new Intent(LoginActivity.this, ShopActivity.class); //no need recognize?
                                                    intent.putExtra("intentfrom", "login");
                                                    startActivity(intent);
                                                }
                                            }, 2000);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            btnLogin.doneLoadingAnimation(Color.RED, bitmapCross);
                                            delay_anim(white_btn, btnLogin); //delay and revert
                                            //Handle any errors
                                            Snackbar.make(constraintLayout, "Login Unsuccessful. Please ensure storage and camera permission is allowed", Snackbar.LENGTH_LONG).show();
                                            Log.e(TAG, "file not created " + exception.toString());
                                        }
                                    });
                                }
                            }
                        });

            }
        });
//end of login with email and password button settings

        //forget pw button setting
        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.dialog_forget);

                final TextInputEditText inputReset = dialog.findViewById(R.id.reset_email);
                final CircularProgressButton btnReset = dialog.findViewById(R.id.btn_reset);

                btnReset.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            btnReset.setBackgroundResource(R.drawable.rounded_btn_grey);
                            btnReset.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            //when button released
                            btnReset.setBackgroundResource(R.drawable.rounded_btn_black);
                            btnReset.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                        return false;
                    }
                });

                btnReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onWindowFocusChanged(true);
                        btnReset.startAnimation();
                        String emailReset = inputReset.getText().toString().trim();
                        if (TextUtils.isEmpty(emailReset)) {
                            inputReset.setError("Enter email address");
                            delay_anim(black_btn, btnReset); //delay and revert anim
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailReset).matches()) {
                            inputReset.setError("Invalid email format");
                            delay_anim(black_btn, btnReset); //delay and revert anim
                        } else {
                            FirebaseAuth.getInstance().setLanguageCode("en");
                            FirebaseAuth.getInstance().sendPasswordResetEmail(emailReset)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Email sent, please check inbox", Toast.LENGTH_SHORT).show();

                                                btnReset.doneLoadingAnimation(Color.GREEN, bitmapTick);
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog.cancel();
                                                    }
                                                }, 1500);

                                            } else {
                                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                btnReset.doneLoadingAnimation(Color.RED, bitmapCross);
                                                delay_anim(black_btn, btnReset);
                                            }
                                        }
                                    });
                        }
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //remove white corners
                DisplayMetrics metrics = getResources().getDisplayMetrics(); //custom width and length of dialog box
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                dialog.getWindow().setLayout((6 * width) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                dialog.show();
            }
        });
    }

    public void faceLogin(){
        email_layout.setVisibility(View.GONE);
        inputEmail.setVisibility(View.GONE);
        pw_layout.setVisibility(View.GONE);
        inputPass.setVisibility(View.GONE);
        btnLogin.setVisibility(View.GONE);
        btnForget.setVisibility(View.GONE);

        face_layout.setVisibility(View.VISIBLE);
        inputFace.setVisibility(View.VISIBLE);
        btnLoginFace.setVisibility(View.VISIBLE);

        ConstraintSet constraintSet= new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.btn_forget,ConstraintSet.TOP,R.id.login_face_layout,ConstraintSet.BOTTOM,25);
        constraintSet.connect(R.id.btn_forget,ConstraintSet.END,R.id.login,ConstraintSet.END,82);

        constraintSet.connect(R.id.btn_signup,ConstraintSet.END,R.id.login,ConstraintSet.END,0);
        constraintSet.connect(R.id.btn_signup,ConstraintSet.START,R.id.login,ConstraintSet.START,0);
        constraintSet.connect(R.id.btn_signup,ConstraintSet.TOP,R.id.btn_facelogin,ConstraintSet.BOTTOM,64);

        constraintSet.applyTo(constraintLayout);
    }

    public void usualLogin(){ //login with email and password
        email_layout.setVisibility(View.VISIBLE);
        inputEmail.setVisibility(View.VISIBLE);
        pw_layout.setVisibility(View.VISIBLE);
        inputPass.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.VISIBLE);
        btnForget.setVisibility(View.VISIBLE);

        face_layout.setVisibility(View.GONE);
        inputFace.setVisibility(View.GONE);
        btnLoginFace.setVisibility(View.GONE);

        ConstraintSet constraintSet= new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.btn_forget,ConstraintSet.TOP,R.id.login_pw_layout,ConstraintSet.BOTTOM,25);
        constraintSet.connect(R.id.btn_forget,ConstraintSet.END,R.id.login,ConstraintSet.END,82);

        constraintSet.connect(R.id.btn_signup,ConstraintSet.END,R.id.login,ConstraintSet.END,0);
        constraintSet.connect(R.id.btn_signup,ConstraintSet.START,R.id.login,ConstraintSet.START,0);
        constraintSet.connect(R.id.btn_signup,ConstraintSet.TOP,R.id.btn_login,ConstraintSet.BOTTOM,64);
        constraintSet.applyTo(constraintLayout);
    }

    public void delay_anim(final Drawable draw_btn, final CircularProgressButton btn) {
        new Handler().postDelayed(new Runnable() { //pause 3 second only resume to walkthru
            @Override
            public void run() {
                btn.revertAnimation();
                btn.setBackground(draw_btn);
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
