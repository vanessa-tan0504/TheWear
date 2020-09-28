package com.thewear.thewearapp;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static String TAG = TrainActivity.class.getSimpleName();

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Intent swap = new Intent(MainActivity.this, EditActivity.class);
                startActivity(swap);
            }
        });

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

