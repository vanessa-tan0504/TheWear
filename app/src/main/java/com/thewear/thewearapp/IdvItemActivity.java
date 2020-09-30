package com.thewear.thewearapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class IdvItemActivity extends AppCompatActivity {


    FirebaseFirestore db,db_price;
    ImageView back;
    float rad=300f; //radiius
    private DotsIndicator dotsIndicator;
    TextView tvtitle, tvdesc;
    private CustomRadioBtn rad_s,rad_m,rad_l,rad_x,rad_white,rad_blue,rad_black;
    private Button btnplus, btnminus, btncart;
    private TextView qty;
    private int amt = 1;
    private ScrollView scrollView;
    private RadioGroup radioGroupSize,radioGroupCol;
    String userID,color,size;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idv_item);
        final ViewPager viewPager = findViewById(R.id.item_img);

        Intent intent = getIntent();
        String itemid = intent.getExtras().getString("ItemID");
        back= findViewById(R.id.backbtn);
        dotsIndicator=findViewById(R.id.dots_indicator);
        tvtitle=findViewById(R.id.item_title);
        tvdesc = findViewById(R.id.item_desc);
        btnminus = findViewById(R.id.qyt_minus);
        btnplus = findViewById(R.id.qyt_add);
        qty = findViewById(R.id.qyt_text);
        scrollView = findViewById(R.id.idvact);
        btncart = findViewById(R.id.add_cart);
        radioGroupSize = findViewById(R.id.radioGroupsize);
        radioGroupCol = findViewById(R.id.radioGroupcol);
        userID= getInstance().getCurrentUser().getUid();


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
                        final String idvurl= document.getString("coverURL");
                        //title and desc
                        final String title = document.getString("title");
                        String desc = document.getString("desc");
                        final double price = document.getDouble("price");

                        tvtitle.setText(title);
                        tvdesc.setText(desc);
                        btncart.setText(String.format("ADD TO CART\t\tRM %.2f",price*amt));

                        // radio listener-------------------------------------------------------------------------------
                        radioGroupSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                switch (checkedId){
                                    case R.id.rad_s:
                                        size="S";
                                        break;
                                    case R.id.rad_m:
                                        size="M";
                                        break;
                                    case R.id.rad_l:
                                        size="L";
                                        break;
                                    case R.id.rad_x:
                                        size="XL";
                                        break;
                                }
                            }
                        });

                        radioGroupCol.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                switch (checkedId){
                                    case R.id.rad_black:
                                        color="Black";
                                        break;
                                    case R.id.rad_blue:
                                        color="Blue";
                                        break;
                                    case R.id.rad_white:
                                        color="White";
                                        break;

                                }
                            }
                        });
                        // radio listener end----------------------------------------------------------------------------

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
                                btncart.setText(String.format("ADD TO CART\t\tRM %.2f",price*amt));
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
                                btncart.setText(String.format("ADD TO CART\t\tRM %.2f",price*amt));
                                return false;
                            }
                        });

                        btncart.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if(event.getAction()== MotionEvent.ACTION_DOWN){
                                    btncart.setBackgroundResource(R.drawable.rounded_btn_grey);
                                    btncart.setTextColor(Color.parseColor("#FFFFFF"));
                                }
                                if(event.getAction()==MotionEvent.ACTION_UP){
                                    //when button released
                                    btncart.setBackgroundResource(R.drawable.rounded_btn_black);
                                    btncart.setTextColor(Color.parseColor("#FFFFFF"));
                                }
                                return false;
                            }
                        });
                        btncart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DecimalFormat df = new DecimalFormat("#.##");
                                double total = Double.parseDouble(df.format(price*amt));
                               //generating order id based on timestamp
                                Date date = new Date();
                                DateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss.SSS", Locale.getDefault());
                                String orderID = sdf.format(date);

                                if(size==null || color==null){
                                    Snackbar.make(scrollView,"Size and color must be chosen",Snackbar.LENGTH_LONG).show();
                                }
                                else {
                                    //create new order (init ispaid is false)
                                    final Order neworder = new Order(orderID, title, size, color, amt, total, userID, false, idvurl);

                                    db.collection("orders").document(neworder.getOrderID() + "").set(neworder)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    final Dialog dialog = new Dialog(IdvItemActivity.this);
                                                    dialog.setContentView(R.layout.dialog_cart);

                                                    TextView dialog_text = dialog.findViewById(R.id.dialog_text);
                                                    LottieAnimationView dialog_anim = dialog.findViewById(R.id.dialog_anim);
                                                    final Button btn_home = dialog.findViewById(R.id.btn_home);
                                                    final Button btn_shop = dialog.findViewById(R.id.btn_shop);
                                                    dialog_anim.setAnimation(R.raw.cart);

                                                    //dialog anim
                                                    dialog_anim.setColorFilter(R.color.colorAccent);
                                                    dialog_anim.setSpeed(0.8f);
                                                    dialog_anim.playAnimation();

                                                    //dialog buttons
                                                    btn_home.setOnTouchListener(new View.OnTouchListener() {
                                                        @Override
                                                        public boolean onTouch(View v, MotionEvent event) {
                                                            if(event.getAction()== MotionEvent.ACTION_DOWN){
                                                                btn_home.setBackgroundResource(R.drawable.rounded_btn_grey);
                                                                btn_home.setTextColor(Color.parseColor("#FFFFFF"));
                                                            }
                                                            if(event.getAction()==MotionEvent.ACTION_UP){
                                                                //when button released
                                                                btn_home.setBackgroundResource(R.drawable.rounded_btn_black);
                                                                btn_home.setTextColor(Color.parseColor("#FFFFFF"));
                                                            }
                                                            return false;
                                                        }
                                                    });

                                                    btn_home.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            onBackPressed();
                                                        }
                                                    });

                                                    btn_shop.setOnTouchListener(new View.OnTouchListener() {
                                                        @Override
                                                        public boolean onTouch(View v, MotionEvent event) {
                                                            if(event.getAction()== MotionEvent.ACTION_DOWN){
                                                                btn_shop.setBackgroundResource(R.drawable.rounded_btn_grey);
                                                                btn_shop.setTextColor(Color.parseColor("#FFFFFF"));
                                                            }
                                                            if(event.getAction()==MotionEvent.ACTION_UP){
                                                                //when button released
                                                                btn_shop.setBackgroundResource(R.drawable.rounded_btn_black);
                                                                btn_shop.setTextColor(Color.parseColor("#FFFFFF"));
                                                            }
                                                            return false;
                                                        }
                                                    });

                                                    btn_shop.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            //refresh page without animation
                                                            finish();
                                                            overridePendingTransition( 0, 0);
                                                            startActivity(getIntent());
                                                            overridePendingTransition( 0, 0);
                                                        }
                                                    });

                                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //remove white corners
                                                    dialog.setCanceledOnTouchOutside(false);
                                                    dialog.setCancelable(false);
                                                    dialog.show();
                                                    DisplayMetrics displayMetrics = new DisplayMetrics();
                                                    WindowManager wm = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                                                    wm.getDefaultDisplay().getMetrics(displayMetrics);

                                                    int displayWidth = displayMetrics.widthPixels;

                                                    int displayHeight = displayMetrics.heightPixels;

                                                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

                                                    layoutParams.copyFrom(dialog.getWindow().getAttributes());

                                                    int dialogWindowWidth = (int) (displayWidth * 0.9f);
                                                    int dialogWindowHeight = (int) (displayHeight * 0.4f);

                                                    layoutParams.width = dialogWindowWidth;
                                                    layoutParams.height = dialogWindowHeight;

                                                    dialog.getWindow().setAttributes(layoutParams);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(scrollView, "failed to cart_icon", Snackbar.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            }
                        });

                    }
                });

        //viewpager transition
        FadeOutTransformation fadeOutTransformation = new FadeOutTransformation();
        viewPager.setPageTransformer(true, fadeOutTransformation);

        //item viewpager end--------------------------------------------------------------
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