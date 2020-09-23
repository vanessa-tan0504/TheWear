package com.thewear.thewearapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.collection.LLRBNode;
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
    private Button btnplus, btnminus, btncart;
    private TextView qty;
    private int amt = 1;
    private ScrollView scrollView;


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
        btnminus = findViewById(R.id.qyt_minus);
        btnplus = findViewById(R.id.qyt_add);
        qty = findViewById(R.id.qyt_text);
        scrollView = findViewById(R.id.idvact);
        btncart = findViewById(R.id.add_cart);




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
                        double price = document.getDouble ("price");
                        tvtitle.setText(title);
                        tvdesc.setText(desc);
                        btncart.setText(String.format("ADD TO CART\t    RM%.2f",price));

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
        //size radio listener end----------------------------------------------------------------------------

        //quantity button--------------------------------------------------
        //button gesture pressing listener
        btnplus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    //when button pressed
                    btnplus.setBackgroundResource(R.drawable.rounded_btn_black);
                    btnplus.setTextColor(Color.parseColor("#FFFFFF"));
                    amt++;
                    qty.setText(String.valueOf(amt));
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    //when button released
                    btnplus.setBackgroundResource(R.drawable.rounded_btn_whitestroke);
                    btnplus.setTextColor(Color.parseColor("#000000"));
                }
                Toast.makeText(IdvItemActivity.this, amt+"", Toast.LENGTH_SHORT).show();
                return false;

            }
        });

        btnminus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    //when button pressed
                    if(amt==1){
                        Snackbar.make(scrollView,"Quantity cannot be less than 1",Snackbar.LENGTH_LONG).show();
                    }
                    else if(amt>1){
                        btnminus.setEnabled(true);
                        btnminus.setBackgroundResource(R.drawable.rounded_btn_black);
                        btnminus.setTextColor(Color.parseColor("#FFFFFF"));
                        amt--;
                        qty.setText(String.valueOf(amt));
                    }
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    //when button released
                    btnminus.setBackgroundResource(R.drawable.rounded_btn_whitestroke);
                    btnminus.setTextColor(Color.parseColor("#000000"));
                }
                Toast.makeText(IdvItemActivity.this, amt+"", Toast.LENGTH_SHORT).show();

                return false;
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