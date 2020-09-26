package com.thewear.thewearapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class CheckoutActivity extends AppCompatActivity {
    private Spinner spinner;
    private TextView tv_amt,tv_qty,tv_delivery;
    private String deliverymethod;
    double grand;
    int qty;
    private Button verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity);
        spinner=findViewById(R.id.paymentspin);
        tv_amt = findViewById(R.id.payment_grand);
        tv_qty= findViewById(R.id.payment_qty);
        tv_delivery= findViewById(R.id.payment_delivery);
        verify=findViewById(R.id.verify);

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