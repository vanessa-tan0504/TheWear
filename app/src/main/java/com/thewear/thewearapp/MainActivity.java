package com.thewear.thewearapp;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    int number =-1;
    private static String TAG = TrainActivity.class.getSimpleName();
    private FirebaseFirestore db;
    TextView textview;
    ImageView imageView ;
    Bitmap bitmap;

    int counter_m=0;
    int counter_f=0;
    int counter_un=0 ;
    float male=0,female=0,unknown=0;
   // int gender_counter=0;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();

        Button register = (Button)findViewById(R.id.btn_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number++;
                Intent swap = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(swap);
            }
        });

        Button login = (Button)findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent swap = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(swap);

            }
        });




        imageView=findViewById(R.id.imageView);
        textview=findViewById(R.id.tvIdentifiedItem);
        //genderTest();
       // Handler handler = new Handler();
        //for(int gender_counter=0;gender_counter<3;gender_counter++){
           // Toast.makeText(this, "j:"+gender_counter, Toast.LENGTH_SHORT).show();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {

                    StorageReference mImagRef;
                    mImagRef = FirebaseStorage.getInstance().getReference("Clifford"); // getInstance = root firebase file images= foldername
                    StorageReference ref = mImagRef.child("Clifford0.jpg");
                    final long ONE_MEGABYTE = 1024 * 1024;
                    ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            final Bitmap[] bm = {BitmapFactory.decodeByteArray(bytes, 0, bytes.length)};
                            DisplayMetrics dm = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(dm);

                            imageView.setImageBitmap(bm[0]);


                            final FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder() //from app model
                                    .setAssetFilePath("model_unquant.tflite")
                                    .build();

                            final FirebaseCustomRemoteModel remoteModel =
                                    new FirebaseCustomRemoteModel.Builder("gender_predictor").build(); //from firebase

                            FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
                                    .addOnSuccessListener(new OnSuccessListener<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean isDownloaded) {
                                            FirebaseModelInterpreterOptions options;
                                            if (isDownloaded) {
                                                options = new FirebaseModelInterpreterOptions.Builder(remoteModel).build(); //for user who browse online
                                                Log.e(TAG, "FROM FIREBASE");
                                            } else {
                                                options = new FirebaseModelInterpreterOptions.Builder(localModel).build(); //for user who browse offline
                                                Log.e(TAG, "FROM LOCAL");
                                            }
                                            try {
                                                FirebaseModelInterpreter interpreter = FirebaseModelInterpreter.getInstance(options);
                                                FirebaseModelInputOutputOptions inputOutputOptions =
                                                        null;
                                                try {
                                                    inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
                                                            .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 224, 224, 3})
                                                            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 3})
                                                            // here replace 3 with no of class added in your model , for //production apps you can read the labels.txt files here and to get //no of classes dynamically
                                                            .build();
                                                } catch (FirebaseMLException e) {
                                                    e.printStackTrace();
                                                }
        /* Here we are using static image from drawable to keep the code minimum and avoid distraction,
        Recommended method would be to get the image from user by camera or device photos using the same code by handling all this logic in a method and calling that every time */

                                                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                                                Bitmap bitmap = drawable.getBitmap();
                                                bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

                                                int batchNum = 0;
                                                float[][][][] input = new float[1][224][224][3];
                                                for (int x = 0; x < 224; x++) {
                                                    for (int y = 0; y < 224; y++) {
                                                        int pixel = bitmap.getPixel(x, y);
                                                        // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                                                        // model. For example, some models might require values to be normalized
                                                        // to the range [0.0, 1.0] instead.
                                                        input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
                                                        input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
                                                        input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;
                                                    }
                                                }

                                                FirebaseModelInputs inputs = null;
                                                try {
                                                    inputs = new FirebaseModelInputs.Builder()
                                                            .add(input)  // add() as many input arrays as your model requires
                                                            .build();
                                                } catch (FirebaseMLException e) {
                                                    e.printStackTrace();
                                                }
                                                interpreter.run(inputs, inputOutputOptions)
                                                        .addOnSuccessListener(
                                                                new OnSuccessListener<FirebaseModelOutputs>() {
                                                                    @Override
                                                                    public void onSuccess(FirebaseModelOutputs result) {
                                                                        // ...
                                                                        float[][] output = result.getOutput(0);
                                                                        float[] probabilities = output[0];
                                                                        BufferedReader reader = null;
                                                                        try {
                                                                            reader = new BufferedReader(
                                                                                    new InputStreamReader(getAssets().open("labels.txt")));
                                                                        } catch (IOException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        for (int i = 0; i < probabilities.length; i++) {
                                                                            String label = null;
                                                                            try {
                                                                                label = reader.readLine();
                                                                                textview.setText(String.format("result:\n male: %1.4f || " + probabilities[0] + "\n female: %1.4f ||" + probabilities[1] + "\n unknown: %1.4f ||" + probabilities[2], probabilities[0], probabilities[1], probabilities[2]));
                                                                                Log.e(TAG, String.format("%s: %.4f", label, probabilities[i]));

                                                                                if (i == 0)
                                                                                    male = probabilities[i];
                                                                                else if (i == 1)
                                                                                    female = probabilities[i];
                                                                                else
                                                                                    unknown = probabilities[i];

                                                                            } catch (IOException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                        Log.e(TAG, "MALE:" + male);
                                                                        Log.e(TAG, "FEMALE:" + female);
                                                                        Log.e(TAG, "unknown:" + unknown);

                                                                        if (male > female && male > unknown)
                                                                            counter_m++;

                                                                        else if (female > male && female > unknown)
                                                                            counter_f++;

                                                                        else if (unknown > male && unknown > female)
                                                                            counter_un++;

                                                                        Log.e(TAG, "male count:" + counter_m + " " + "female count:" + counter_f);

                                                                        if (counter_m > counter_f) {
                                                                            Log.e(TAG, "is a male:" + counter_m + " female only have" + counter_f);
                                                                            // User newuser = new User();
                                                                            // newuser.setExpGender("female");
                                                                            db.collection("users").document(user.getUid()+"")
                                                                                    .update("expectedGender","male")
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Log.w(TAG, "Error updating document", e);
                                                                                }
                                                                            });

                                                                        } else if (counter_m < counter_f) {
                                                                            Log.e(TAG, "is a female:" + counter_f + " male only have" + counter_m);
                                                                            db.collection("users").document(user.getUid()+"")
                                                                                    .update("expectedGender","female")
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Log.w(TAG, "Error updating document", e);
                                                                                }
                                                                            });
                                                                        }

                                                                    }
                                                                })
                                                        .addOnFailureListener(
                                                                new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        // Task failed with an exception
                                                                        // ...
                                                                        Log.e(TAG, e.getMessage());
                                                                    }
                                                                });
                                            } catch (FirebaseMLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
               // }
           // }, 1500 );
        }

       // }

    public void genderTest(int j){


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

