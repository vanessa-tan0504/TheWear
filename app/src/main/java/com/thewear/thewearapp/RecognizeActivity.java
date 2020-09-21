package com.thewear.thewearapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.stetho.Stetho;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static android.widget.Toast.LENGTH_SHORT;
import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

/**
 * Created by Assem Abozaid on 6/2/2018.
 */

public class RecognizeActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static String TAG = TrainActivity.class.getSimpleName();
    private CameraBridgeViewBase openCVCamera;
    private Mat rgba, gray;
    private CascadeClassifier classifier, eye_cascade;
    private MatOfRect faces, eyes;
    private ArrayList<String> imagesLabels;
    private int label[] = new int[1];
    private double predict[] = new double[1];
    private Storage local;
    private FaceRecognizer recognize; //LBPHFaceRecognizer.create() ; .read() ; .predict
    int number;

    //b.1 to check if opencv library get to call back /connect (opencv setting)
    private BaseLoaderCallback callbackLoader = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    faces = new MatOfRect();
                    eyes = new MatOfRect();
                    openCVCamera.enableView();
                    //to capture image in recognize activity for compare purpose
                    recognize = LBPHFaceRecognizer.create(3, 8, 8, 8, 200);
                    imagesLabels = local.getListString("imagesLabels");
                    Log.i(TAG, "RECOGNIZE: ArrayList<String> imagesLabels from getListString" + String.valueOf(imagesLabels)); //eg: [Vanessa, Vanessa, Vanessa]

                    if (loadData()) //if load training model success, goto b.2
                        Log.i(TAG, "RECOGNIZE: Trained data loaded successfully");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    //b.2 load trained model (lbph_trained_data.xml), if file is empty return false ; else load model
    private boolean loadData() {
        String filename = FileUtils.loadTrained();
        if (filename.isEmpty()) {  //31/5 error at here
            Log.e(TAG, "RECOGNIZE: Trained data not found");
            return false;
        } else {
            recognize.read(filename); //Loads a FaceRecognizer and its model state from lbph_trained_data.xml
            return true;
        }
    }

    //a.2. process single detected face if matched with prev trained face by CROPPING face and PREDICT
    private void recognizeImage(Mat mat) {
        //crop image to smaller focus face
        Rect rect_Crop = null;
        for (Rect face : faces.toArray()) {
            rect_Crop = new Rect(face.x, face.y, face.width, face.height);
        }
        Mat croped = new Mat(mat, rect_Crop);

        //added 6/6 enhance sharpness
        Mat source = croped;
        Mat destination = new Mat(source.rows(), source.cols(), source.type());
        Imgproc.GaussianBlur(source, destination, new Size(0, 0), 10);
        Core.addWeighted(source, 1.5, destination, -0.5, 0, destination);

        //added 6/6


        //added (can only store 1 image at a time, might get overwrite)
        File croppath = new File(Environment.getExternalStorageDirectory(), "CroppedImageRecognize");
        croppath.mkdirs();
        String cropfilename = "recognize.jpg";
        File cropfile = new File(croppath, cropfilename);
        Boolean bool = null;
        cropfilename = cropfile.toString();
        bool = Imgcodecs.imwrite(cropfilename, croped); //croped is Mat file
        if (bool == true)
            Log.i(TAG, "RECOGNIZE: SUCCESS writing image to external storage");
        else
            Log.i(TAG, "RECOGNIZE: Fail writing image to external storage");
        //added
        //added (can only store 1 image at a time, might get overwrite)

        String cropfilename2 = "recognize2.jpg";
        File cropfile2 = new File(croppath, cropfilename2);
        Boolean bool2 = null;
        cropfilename2 = cropfile2.toString();
        bool2 = Imgcodecs.imwrite(cropfilename2, destination); //croped is Mat file
        if (bool2 == true)
            Log.i(TAG, "RECOGNIZE: SUCCESS writing image to external storage");
        else
            Log.i(TAG, "RECOGNIZE: Fail writing image to external storage");
        //added

        /**
         * recognize.predict()- Predicts a label and associated confidence (e.g. distance) for a given input image.
         * @param croped Cropped images taken in recgonize activity.
         * @param label The predicted label for the given image.(Name of person from cropped image.)
         * @param predict Associated confidence (e.g. distance) for the predicted label. (smaller the distance the btr, 0 is perfect match)
         * @return
         */
        //recognize.predict(croped, label, predict);
        recognize.predict(destination, label, predict);
        if (label[0] != -1 && (int) predict[0] < 125) { //"predict" need to be small value as possible to get high confidence
            showLoader();
            new Handler().postDelayed(new Runnable() { //pause 3 second only resume to shop
                @Override
                public void run() {
                    Intent intent = new Intent(RecognizeActivity.this, ShopActivity.class);
                    startActivity(intent);
                }
            }, 3000);

        } else if (predict[0] >= 125)
            Toast.makeText(getApplicationContext(), "You're not the right person " + predict[0], LENGTH_SHORT).show();

        Log.i(TAG, "RECOGNIZE: label value is " + label[0]);
        Log.i(TAG, "RECOGNIZE: predict value is " + predict[0]);


    }

    ////a.1 basic setup for opencv environment, make sure gray faces area does not over by 2 etc.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recognize_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Stetho.initializeWithDefaults(this);
        Intent intent = getIntent(); //add 26/5
        number = intent.getIntExtra("NUMBER", 0); //add 26/5

        openCVCamera = (CameraBridgeViewBase) findViewById(R.id.java_camera_view2);
        openCVCamera.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        openCVCamera.setVisibility(SurfaceView.VISIBLE);
        openCVCamera.setCvCameraViewListener(this);
        local = new Storage(this);

        final Button recogniz = (Button) findViewById(R.id.recognize_button);
        recogniz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gray.total() == 0)
                    Toast.makeText(getApplicationContext(), "Can't Detect Faces", LENGTH_SHORT).show();
                classifier.detectMultiScale(gray, faces, 1.1, 3, 0 | CASCADE_SCALE_IMAGE, new Size(30, 30)); //get face to chg to gray scale image

                if (!faces.empty()) {
                    if (faces.toArray().length > 1)
                        Toast.makeText(getApplicationContext(), "Multiple Faces Are not allowed", LENGTH_SHORT).show();
                    else {
                        if (gray.total() == 0) {
                            Log.i(TAG, "Empty gray image");
                            return;
                        }
                        recognizeImage(gray); //if got only 1 face detected goto 2.
                    }
                } else
                    Toast.makeText(getApplicationContext(), "Unknown Face", LENGTH_SHORT).show();
            }
        });
    }

    //(opencv setting)
    @Override
    protected void onPause() {
        super.onPause();
        if (openCVCamera != null)
            openCVCamera.disableView();


    }

    //when terminate page lifecycle (opencv setting)
    @Override
    protected void onStop() {
        super.onStop();
    }

    //(opencv setting)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (openCVCamera != null)
            openCVCamera.disableView();
    }

    //(opencv setting) when app is moving on => show result of connection in opencv library(opencv setting)
    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "RECOGNIZE: System Library Loaded Successfully");
            callbackLoader.onManagerConnected(BaseLoaderCallback.SUCCESS);
        } else {
            Log.i(TAG, "RECOGNIZE: Unable To Load System Library");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, callbackLoader);
        }
    }

    //start camera frame (CameraBridgeViewBase.CvCameraViewListener2 settings)
    @Override
    public void onCameraViewStarted(int width, int height) {
        rgba = new Mat();
        gray = new Mat();
        classifier = FileUtils.loadXMLS(this, "lbpcascade_frontalface_improved.xml");
    }

    //stop camera frame (CameraBridgeViewBase.CvCameraViewListener2 settings)
    @Override
    public void onCameraViewStopped() {
        rgba.release();
        gray.release();
    }

    //during on camera frame, when camera is on (CameraBridgeViewBase.CvCameraViewListener2 settings)
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat mGrayTmp = inputFrame.gray();
        Mat mRgbaTmp = inputFrame.rgba();


        //for camera screen orientation
        int orientation = openCVCamera.getScreenOrientation();
        if (openCVCamera.isEmulator()) // Treat emulators as a special case
            Core.flip(mRgbaTmp, mRgbaTmp, 1); // Flip along y-axis
        else {
            switch (orientation) { // RGB image
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    Core.flip(mRgbaTmp, mRgbaTmp, 0); // Flip along x-axis
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    Core.flip(mRgbaTmp, mRgbaTmp, 1); // Flip along y-axis
                    break;
            }
            switch (orientation) { // Grayscale image
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    Core.transpose(mGrayTmp, mGrayTmp); // Rotate image
                    Core.flip(mGrayTmp, mGrayTmp, -1); // Flip along both axis
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    Core.transpose(mGrayTmp, mGrayTmp); // Rotate image
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                    Core.flip(mGrayTmp, mGrayTmp, 1); // Flip along y-axis
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    Core.flip(mGrayTmp, mGrayTmp, 0); // Flip along x-axis
                    break;
            }
        }
        //end for camera screen orientation

        gray = mGrayTmp;
        rgba = mRgbaTmp;
        Imgproc.resize(gray, gray, new Size(200, 200.0f / ((float) gray.width() / (float) gray.height())));
        return rgba;
    }

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

    public void showLoader(){
        final Dialog dialog= new Dialog(this);
        dialog.setContentView(R.layout.dialog_loading);

        TextView dialog_text = dialog.findViewById(R.id.dialog_text);
        LottieAnimationView dialog_anim = dialog.findViewById(R.id.dialog_anim);

        dialog_text.setText("Profile Verified. Welcome Back"); //for regconize
        dialog_anim.setAnimation(R.raw.selfie_cam); //for regconize
        dialog_anim.setSpeed(2f);
        dialog_anim.playAnimation();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //remove white corners
        dialog.setCanceledOnTouchOutside(false); // touch outsie cnnt cancel dialogbox
        dialog.setCancelable(false);
        dialog.show();
    }
}
