package com.thewear.thewearapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class EditActivity extends AppCompatActivity {

    private ConstraintLayout expandableView,expandableView2;
    private CircularProgressButton btn_add,btn_chg_pw;
    private CardView cardView,cardView2;
    private TextView desc,desc2;
    private EditText edit_oldpw,edit_add,edit_postal;
    private Spinner edit_country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        expandableView = findViewById(R.id.expandableView);
        expandableView2 = findViewById(R.id.expandableView2);
        desc=findViewById(R.id.desc);
        desc2=findViewById(R.id.desc2);
        btn_add=findViewById(R.id.btn_add);
        btn_chg_pw=findViewById(R.id.btn_chg_pw);
        edit_oldpw=findViewById(R.id.input_pw);
        edit_add=findViewById(R.id.input_add);
        edit_postal=findViewById(R.id.input_postal);
        edit_country=findViewById(R.id.input_country_spin);


        //expandable settings-----------------------------------------------------------------------------------------------------------
        // hide until its title is clicked (make expandable view)
        expandableView.setVisibility(View.GONE);
        expandableView2.setVisibility(View.GONE);

        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //expandableView.setVisibility( expandableView.isShown() ? View.GONE : View.VISIBLE );
                if(expandableView.getVisibility()==View.VISIBLE){
                    expandableView.setVisibility(View.GONE);
                }
                else{
                    expandableView.setVisibility(View.VISIBLE);
                }

                if(expandableView2.getVisibility()==View.VISIBLE){
                    expandableView2.setVisibility(View.GONE);
                }
            }
        });

        desc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //expandableView.setVisibility( expandableView.isShown() ? View.GONE : View.VISIBLE );
                if(expandableView2.getVisibility()==View.VISIBLE){
                    expandableView2.setVisibility(View.GONE);
                }
                else{
                    expandableView2.setVisibility(View.VISIBLE);
                }

                if(expandableView.getVisibility()==View.VISIBLE){
                    expandableView.setVisibility(View.GONE);
                }
            }
        });
        //expandable settings-----------------------------------------------------------------------------------------------------------

        //chg pw------------------------------------------------------------------------------
        btn_chg_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWindowFocusChanged(true);

                final String password = edit_oldpw.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    edit_oldpw.setError("Enter old");
                    //delay_anim(white_btn,btnLogin); //delay and revert anim
                    return;
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWindowFocusChanged(true);

                final String address = edit_add.getText().toString();
                final String postal = edit_postal.getText().toString();

                if (TextUtils.isEmpty(address)) {
                    edit_oldpw.setError("Enter address");
                    //delay_anim(white_btn,btnLogin); //delay and revert anim
                    return;
                }
                if (TextUtils.isEmpty(postal)) {
                    edit_oldpw.setError("Enter address");
                    //delay_anim(white_btn,btnLogin); //delay and revert anim
                    return;
                }
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

    public void delay_anim(final Drawable draw_btn, final CircularProgressButton btn){
        new Handler().postDelayed(new Runnable() { //pause 3 second only resume to walkthru
            @Override
            public void run() {
                btn.revertAnimation();
                btn.setBackground(draw_btn);
            }
        }, 1000);
    }
}