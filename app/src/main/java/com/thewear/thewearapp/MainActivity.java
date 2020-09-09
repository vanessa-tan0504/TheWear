package com.thewear.thewearapp;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    TextView textview;
    ImageView imageView ;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button register = (Button)findViewById(R.id.btn_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number++;
                Intent swap = new Intent(MainActivity.this, RegisterActivity.class);
                //swap.putExtra("NUMBER",number);
                startActivity(swap);
            }
        });

        Button login = (Button)findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent swap = new Intent(MainActivity.this, LoginActivity.class);
                //swap.putExtra("NUMBER",number);
                startActivity(swap);

                //retrive xml model from firebase
//                StorageReference mStorageRef;
//                mStorageRef = FirebaseStorage.getInstance().getReference("Bunhair"); // getInstance = root firebase file images= foldername
//                StorageReference ref = mStorageRef.child("Model XML");
//                File storagePath = new File(Environment.getExternalStorageDirectory(),"TrainedData");
//                // Create direcorty if not exists
//                if(!storagePath.exists()) {
//                    Log.e(TAG, "directory exsisted");
//                    storagePath.mkdirs();
//                }

              //  final File myFile = new File(storagePath,"test_model.xml");
               // ref.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                //    @Override
                //    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        //Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                      //  Log.e(TAG,"local tem file created  created "+ myFile.toString());
//                        Intent swap = new Intent(MainActivity.this, LoginActivity.class);
//                       // swap.putExtra("NUMBER",number);
//                        startActivity(swap);
//              //      }
            //    }).addOnFailureListener(new OnFailureListener() {
            //        @Override
             //       public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        // Toast.makeText(MainActivity.this, "no success", Toast.LENGTH_SHORT).show();
             //           Log.e(TAG,"file not created "+ exception.toString());
            //        }
            //    });

            }
        });


        //gender test
        imageView=findViewById(R.id.imageView);
        textview=findViewById(R.id.tvIdentifiedItem);

        final FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder() //from app model
                .setAssetFilePath("model_unquant.tflite")
                .build();

        final FirebaseCustomRemoteModel remoteModel =
                new FirebaseCustomRemoteModel.Builder("gender_predictor").build(); //from firebase
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();


        FirebaseModelInterpreter interpreter = null;

        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
                .addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isDownloaded) {
                        FirebaseModelInterpreterOptions options;
                        if (isDownloaded) {
                            options = new FirebaseModelInterpreterOptions.Builder(remoteModel).build(); //for user who browse online
                            Log.e(TAG,"FROM FIREBASE");
                        } else {
                            options = new FirebaseModelInterpreterOptions.Builder(localModel).build(); //for user who browse offline
                            Log.e(TAG,"FROM LOCAL");
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

                            BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();
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
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        textview.setText(String.format("result:\n male: %1.4f || "+probabilities[0]+ "\n female: %1.4f ||"+probabilities[1]+"\n unknown: %1.4f ||"+probabilities[2],probabilities[0],probabilities[1],probabilities[2] ) );
                                                        Log.e(TAG, String.format("%s: %1.4f", label, probabilities[i]));
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
