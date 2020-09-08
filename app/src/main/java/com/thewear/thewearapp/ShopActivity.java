package com.thewear.thewearapp;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class ShopActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Toast.makeText(this, "shop in "+user.getDisplayName(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "no data", Toast.LENGTH_SHORT).show();
        }
    }
}
