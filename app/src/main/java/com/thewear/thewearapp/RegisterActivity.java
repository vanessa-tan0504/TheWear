package com.thewear.thewearapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText inputEmail,inputPw,inputConfirmPw,inputUser;
    private Button btnRegister,btnLogIn;
    private ProgressBar progressBar;
    private static String TAG = RegisterActivity.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        //get firebase auth instance
        auth= FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        inputEmail=findViewById(R.id.signup_email);
        inputPw=findViewById(R.id.signup_pw);
        inputConfirmPw=findViewById(R.id.signup_confirmpw);
        inputUser=findViewById(R.id.signup_username);
        btnRegister=findViewById(R.id.btn_register);
        btnLogIn=findViewById(R.id.btn_login);
        //progressBar=findViewById(R.id.progressBar_signup);

        //register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString().trim();
                String password = inputPw.getText().toString().trim();
                String confirm_pass = inputConfirmPw.getText().toString().trim();
                String username = inputUser.getText().toString().trim();

                if(TextUtils.isEmpty(username)){
                    inputUser.setError("Enter user name");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError("Enter email address");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.setError("Invalid email format");
                    return;

                }

                if (TextUtils.isEmpty(password)) {
                    inputPw.setError("Enter password");
                    return;
                }

                if (password.length() < 6) {
                    inputPw.setError("Password too short");
                    return;
                }

                if (TextUtils.isEmpty(confirm_pass)) {
                    inputConfirmPw.setError("Enter confirm password");
                    return;
                }

                if (!password.equals(confirm_pass)) {
                    inputConfirmPw.setError("Confirm password not same with password");
                    return;
                }
//                progressBar.setVisibility(View.VISIBLE);

                //if all fields okay, create user to firebase
                auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //progressBar.setVisibility(View.INVISIBLE);

                                if(!task.isSuccessful()){//if account cannot register
                                    Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                                }
                                else {// if account register success ,store user's username
                                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(inputUser.getText().toString().trim())
                                            .build();
                                    user.updateProfile(profileChangeRequest)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                       // Toast.makeText(RegisterActivity.this, "Welcome! " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                                        if(user!=null){
                                                            User newuser = new User(user.getDisplayName(),user.getEmail());
                                                            db.collection("users").document(user.getUid()+"").set(newuser)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(RegisterActivity.this, "user updated", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                            Intent intent= new Intent(RegisterActivity.this, TrainActivity.class);
                                                            intent.putExtra("username",user.getDisplayName()+"");
                                                            startActivity(intent);
                                                        }
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //finish();
                                }
                            }
                        });
            }
        });
        //end of register button
    }
}
