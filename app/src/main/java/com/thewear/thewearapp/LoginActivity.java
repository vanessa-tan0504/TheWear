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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    private ProgressBar progressBar;
    private Button btnSignup;
    private CircularProgressButton btnLogin;
    private Bitmap bitmap;
    int fillcolor;
    // AVLoadingIndicatorView indicatorView;
    SpinKitView spinKitView;
    private static String TAG = TrainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        inputEmail = findViewById(R.id.login_email);
        inputPass = findViewById(R.id.login_pw);
        // progressBar=findViewById(R.id.progressBar_login);
        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress);
        Sprite sprite = new ChasingDots();
        progressBar.setIndeterminateDrawable(sprite);
        final Drawable black_btn = getResources().getDrawable(R.drawable.rounded_btn_black);
        final Drawable white_btn = getResources().getDrawable(R.drawable.rounded_btn_white);
        Drawable d = getResources().getDrawable(R.drawable.clifford0);
        bitmap = ((BitmapDrawable)d).getBitmap();

        fillcolor=Color.WHITE;

//        indicatorView = findViewById(R.id.indicator);
//        indicatorView.setIndicator("BallZigZagDeflectIndicator");
//        indicatorView.show();

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
                ProgressType progressType=ProgressType.INDETERMINATE;
                btnLogin.startAnimation();

                String email = inputEmail.getText().toString().trim();
                final String password = inputPass.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError("Enter email address");
                    delay_anim(white_btn);
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.setError("Invalid email format");
                    delay_anim(white_btn);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    inputPass.setError("Enter password");
                    delay_anim(white_btn);
                    return;
                }


                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                btnLogin.startAnimation();
                               // progressBar.setVisibility(View.INVISIBLE);
                                if (!task.isSuccessful()) {//if account cannot login
                                    Toast.makeText(LoginActivity.this, "Authentication failed. " + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else { //if account can login
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
                                                    btnLogin.doneLoadingAnimation(Color.RED,bitmap);
//                                                    Intent intent = new Intent(LoginActivity.this, RecognizeActivity.class);
//                                                    startActivity(intent);
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
}
