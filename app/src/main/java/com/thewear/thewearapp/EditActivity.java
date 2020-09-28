package com.thewear.thewearapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class EditActivity extends AppCompatActivity {
    Intent intent;
    private CardView header,content,header2,content2;
    ImageButton imgexp1,imgexp2;
    LinearLayout linearLayout;

    ConstraintLayout expandableView;
    Button arrowBtn;
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
//        header=findViewById(R.id.expheader1);
//        content=findViewById(R.id.expcontent1);
//        imgexp1=findViewById(R.id.btnexp);
//        linearLayout=findViewById(R.id.main);
        //collapse2=findViewById(R.id.layout_option2_collapse);
        //expand2=findViewById(R.id.layout_option2_expand);

        expandableView = findViewById(R.id.expandableView);
        arrowBtn = findViewById(R.id.arrowBtn);
        cardView = findViewById(R.id.cardView);

        arrowBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (expandableView.getVisibility()==View.GONE){
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    expandableView.setVisibility(View.VISIBLE);
                    arrowBtn.setBackgroundResource(R.drawable.common_full_open_on_phone);
                } else {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    expandableView.setVisibility(View.GONE);
                    arrowBtn.setBackgroundResource(R.drawable.alarm);
                }
            }
        });

        // hide until its title is clicked (make expandable view)
        //content.setVisibility(View.GONE);

    }

//    public void expand_content(View view){
//        content.setVisibility( content.isShown() ? View.GONE : View.VISIBLE );
//        if(content.isShown()){//if view got expand
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
//            }
//            content.setVisibility(View.GONE);
//            imgexp1.setBackgroundResource(R.drawable.cross_icon);
//        }
//        else{
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
//            }
//            content.setVisibility(View.VISIBLE);
//            imgexp1.setBackgroundResource(R.drawable.tick_icon);
//        }
//
//    }
}