package com.thewear.thewearapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CheckoutActivity extends AppCompatActivity {
    private Spinner spinner;
    private TextView tv_amt,tv_qty,tv_delivery,tv_add;
    private String deliverymethod;
    double grand;
    int qty;
    private Button verify;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ScrollView paymentview;
    private ConstraintLayout backview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity);
        spinner=findViewById(R.id.paymentspin);
        tv_amt = findViewById(R.id.payment_grand);
        tv_qty= findViewById(R.id.payment_qty);
        tv_delivery= findViewById(R.id.payment_delivery);
        verify=findViewById(R.id.verify);
        tv_add=findViewById(R.id.address);
        paymentview=findViewById(R.id.paymentview);
        backview=findViewById(R.id.back_layout);

        //back
        backview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //set firebase instance
        db= FirebaseFirestore.getInstance();

        //get firebase auth instance
        auth= FirebaseAuth.getInstance();
        final FirebaseUser user=auth.getCurrentUser();


        db.collection("address").document(user.getUid()+"").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        String add= document.getString("address");
                        String pos=document.getString("postal");
                        String country = document.getString("country");

                        if(!task.isSuccessful()) {
                            tv_add.setText("No address added.\nPlease updated address in profile settings.");
                            verify.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Snackbar.make(paymentview, "No address found", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        if(add==null||pos==null||country==null){
                            tv_add.setText("No address added.\nPlease updated address in profile settings.");
                            tv_add.setTextColor(Color.RED);
                            verify.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Snackbar.make(paymentview, "No address found", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        tv_add.setText(add+"\n"+pos+"\n"+country);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tv_add.setText("No address added.\nPlease updated address in profile settings.");
                verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(paymentview, "No address found", Snackbar.LENGTH_SHORT).show();
                    }
                });
                Log.e("Address",e.getMessage());
            }
        });

        //get payment summary
        Intent intent = getIntent();
        deliverymethod=intent.getStringExtra("delivery");
        grand=intent.getDoubleExtra("price",0);
        qty=intent.getIntExtra("qty",0);

        tv_amt.setText(String.format("Grand Price: RM%.2f",grand));
        tv_delivery.setText("Delivery Method: "+deliverymethod);
        tv_qty.setText("Item Quantity: "+qty);

       //payment method spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.payment,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        verify.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    verify.setBackgroundResource(R.drawable.rounded_btn_grey);
                    verify.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    //when button released
                    verify.setBackgroundResource(R.drawable.rounded_btn_black);
                    verify.setTextColor(Color.parseColor("#FFFFFF"));
                }
                return false;
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutActivity.this, RecognizeActivity.class);
                intent.putExtra("intentfrom","checkout");
                startActivity(intent);

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