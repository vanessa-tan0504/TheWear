package com.thewear.thewearapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.List;

public class IdvItemActivity extends AppCompatActivity {


    FirebaseFirestore db;
    ImageView back;
    float rad=300f; //radiius
    private DotsIndicator dotsIndicator;
    TextView tvtitle, tvdesc;
    private CustomRadioBtn rad_s,rad_m,rad_l,rad_x,rad_white,rad_blue,rad_black;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idv_item);
        final ViewPager viewPager = findViewById(R.id.viewpager);

        Intent intent = getIntent();
        String itemid = intent.getExtras().getString("ItemID");
        back= findViewById(R.id.backbtn);
        dotsIndicator=findViewById(R.id.dots_indicator);
        tvtitle=findViewById(R.id.item_title);
        tvdesc = findViewById(R.id.item_desc);
        rad_s=findViewById(R.id.rad_s);
        rad_m=findViewById(R.id.rad_m);
        rad_l=findViewById(R.id.rad_l);
        rad_x=findViewById(R.id.rad_x);
        rad_black=findViewById(R.id.rad_black);
        rad_blue=findViewById(R.id.rad_blue);
        rad_white=findViewById(R.id.rad_white);



        //back button on top---------------------------------------
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //item viewpager --------------------------------------------------------------

        //viewpager corner
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewPager.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect((int)(0-rad),(int)(0-rad), view.getWidth(), (view.getHeight()),rad);
                }
            });
            viewPager.setClipToOutline(true);
        }

        //viewpager get item url
        db=FirebaseFirestore.getInstance();
        db.collection("items").document(itemid+"").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();

                        //viewpager get itemurl and set adapter and indicator
                        List<String> url = (List<String>)document.get("itemURL");
                        IdvItemImageAdapter adapter = new IdvItemImageAdapter(getApplicationContext(),url);
                        viewPager.setAdapter(adapter);
                        dotsIndicator.setViewPager(viewPager);

                        //title and desc
                        String title = document.getString("title");
                        String desc = document.getString("desc");
                        tvtitle.setText(title);
                        tvdesc.setText(desc);

                    }
                });

        //viewpager transition
        FadeOutTransformation fadeOutTransformation = new FadeOutTransformation();
        viewPager.setPageTransformer(true, fadeOutTransformation);

        //item viewpager end--------------------------------------------------------------

        //size radio listener-------------------------------------------------------------------------------

        rad_s.setOwnOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(IdvItemActivity.this, "clicked"+isChecked, Toast.LENGTH_SHORT).show();
            }
        });
        rad_m.setOwnOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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