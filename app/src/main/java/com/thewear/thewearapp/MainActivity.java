package com.thewear.thewearapp;
import android.Manifest;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static String TAG = TrainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 1;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //for first-time user permission
        if(Build.VERSION.SDK_INT >= 23){
            if(checkPermission()){
                //Toast.makeText(MainActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
                Log.e(TAG,"permission granted");
            }
            else{
                requestPermission();
            }
        }

        //if user already login before (non first=-time user)
        if(user!=null){
            Intent swap = new Intent(MainActivity.this, ShopActivity.class);
            startActivity(swap);
            finish();
        }


        //register button---------------
        final Button register = (Button) findViewById(R.id.btn_register);
        register.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    register.setBackgroundResource(R.drawable.rounded_btn_grey);
                    register.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    //when button released
                    register.setBackgroundResource(R.drawable.rounded_btn_white);
                    register.setTextColor(Color.parseColor("#000000"));
                }
                return false;
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent swap = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(swap);
            }
        });


        //login button---------------
        final Button login = (Button) findViewById(R.id.btn_login);

        login.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    login.setBackgroundResource(R.drawable.rounded_btn_grey);
                    login.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    //when button released
                    login.setBackgroundResource(R.drawable.rounded_btn_black);
                    login.setTextColor(Color.parseColor("#FFFFFF"));
                }
                return false;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent swap = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(swap);

            }
        });

    }
//permission settings-----------------
    private boolean checkPermission() {
        //Check for READ_EXTERNAL_STORAGE access, using ContextCompat.checkSelfPermission()//
        int storage = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int camera=ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA);
        //If the app does have this permission, then return true
        if (storage == PackageManager.PERMISSION_GRANTED && camera == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            //If the app doesn’t have this permission, then return false//
            return false;
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED&&
                        perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(MainActivity.this, "Permission accepted", Toast.LENGTH_LONG).show();
                    Log.e(TAG,"Permissions granted");
                }
                else {
                    Toast.makeText(MainActivity.this, "Some Permission is Denied.", Toast.LENGTH_LONG).show();
                    closeapp();

                }
                break;
        }
    }

    private void closeapp() { //close app to device settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            finishAffinity();
            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null)));
        }
        else
        {
            finish();
            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null)));
        }
    }
    //permission settings end-----------------

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


    @Override
    public void onBackPressed() { //if press 2 times close app
        if (doubleBackToExitPressedOnce) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                finishAffinity();
            else
                finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}

