package com.thewear.thewearapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore; //store db as nosql
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference; //store as file and folders
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.Nullable;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;

import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

/**
 * Created by Assem Abozaid on 6/2/2018.
 */

public class TrainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    // private TextView textView; //added
    private static String TAG = TrainActivity.class.getSimpleName();
    private CameraBridgeViewBase openCVCamera;
    private Mat rgba, gray;
    private CascadeClassifier classifier;
    private MatOfRect faces;
    private static final int PERMS_REQUEST_CODE = 123;
    private ArrayList<Mat> images;
    // private ArrayList<Mat> cloneimages ;// added
    private ArrayList<String> imagesLabels;
    private Storage local; //self made Storage class
    private String[] uniqueLabels;
    FaceRecognizer recognize; //for LBPHRegconizer.create; .train(); . save()
    private int mAbsoluteFaceSize = 0;
    int number;
    boolean isClear = false;
    int capture_count=1;
    String username;
    private Button detect;
    private Button prestart;
    private TextView appname1,appname2,precam1,precam2;
    private ConstraintLayout prestart_screen;


    //9. after camera onStop (exit camera), store captured images, process image with regconizer
    private boolean trainfaces() {
        if(images.isEmpty())
            return false;
        List<Mat> imagesMatrix = new ArrayList<>();
        Set<String> uniqueLabelsSet = new HashSet<>(imagesLabels); // Get all unique labels
        uniqueLabels = uniqueLabelsSet.toArray(new String[uniqueLabelsSet.size()]); // Convert to String array, so we can read the values from the indices

        for (int i = 0; i < images.size(); i++){
            //added (try to get all captured+ grayscale+ crop image)
//            File croppath = new File(Environment.getExternalStorageDirectory(), "CroppedImage");
//            croppath.mkdirs();
//            String cropfilename="temp"+i+".jpg";
//            File cropfile = new File(croppath,cropfilename);
//            Boolean bool= null;
//            cropfilename= cropfile.toString();
//            bool = Imgcodecs.imwrite(cropfilename,images.get(i)); //croped /images.get(i) is Mat file
//             if(bool == true)
//            Log.i(TAG,"SUCCESS writing image to external storage");
//             else
//                 Log.i(TAG, "Fail writing image to external storage");

            //added added (try to get all captured+ grayscale+ crop image)

            imagesMatrix.add(images.get(i));
            Log.i(TAG, "imagesMatrix: "+String.valueOf(imagesMatrix.get(i)));
            //local.putImage("matrix" + i +".jpg",imagesMatrix.get(i));
        }

       // Set<String> uniqueLabelsSet = new HashSet<>(imagesLabels); // Get all unique labels
       // uniqueLabels = uniqueLabelsSet.toArray(new String[uniqueLabelsSet.size()]); // Convert to String array, so we can read the values from the indices
        int[] classesNumbers = new int[uniqueLabels.length];
        for (int i = 0; i < classesNumbers.length; i++)
            classesNumbers[i] = i + 1; // Create incrementing list for each unique label starting at 1
        int[] classes = new int[imagesLabels.size()];
        for (int i = 0; i < imagesLabels.size(); i++) {
            String label = imagesLabels.get(i);
            for (int j = 0; j < uniqueLabels.length; j++) {
                if (label.equals(uniqueLabels[j])) {
                    classes[i] = classesNumbers[j]; // Insert corresponding number
                    Log.i(TAG, "Unique Labels: "+uniqueLabels[j].toString());
                    break;
                }
            }
        }

        for(int i =0 ; i<imagesLabels.size();i++){
            local.putImage(imagesLabels.get(i)+i+".jpg",imagesLabels.get(i),images.get(i),number); //store every captured + grascaled + cropped image to external file

        }

        Mat vectorClasses = new Mat(classes.length, 1, CvType.CV_32SC1); // CV_32S == int
        vectorClasses.put(0, 0, classes); // Copy int array into a vector
        Log.i(TAG, "Vector Classes "+String.valueOf(vectorClasses.get(0,0,classes)));
        //local.putImage("vectorClasses" + ".jpg", vectorClasses);

        //LBPH regconizer settings
        //radius- greater the radius, the smoother the image but more spatial information
        //neighbors -appropriate value is to use 8 sample points, higher sample point, higher computational cost
        //grid-x - 8 is a common value, higher grid, more horizontal cell, finer grid, higher the dimensionality of feature vector
        //grid-y - 8 is a common value, higher grid, more vertical cell, finer grid, higher the dimensionality of feature vector
        //threhold - distance to nearest neighbour > threshold , return -1
        recognize = LBPHFaceRecognizer.create(3,8,8,8,200); //check out and understand it

        /**
         * recognize.train()- Trains a FaceRecognizer with given data and associated labels.
         * @param imagesMatrix The training images, that means the faces you want to learn. The data has to be given as a vector<Mat>.
         * @param vectorClasses The labels corresponding to the images have to be given either as a vector<int> or a Mat of type CV_32SC1.
         * @return
         */
        recognize.train(imagesMatrix, vectorClasses);


        if(SaveImage()){
            //start to train data
            Log.i(TAG, "finish trained face"); //if true , go to 10

            //add
           // recognize=LBPHFaceRecognizer.create();
           // recognize.train(imagesMatrix, vectorClasses);
            ((LBPHFaceRecognizer) recognize).setThreshold(0.0);
            Log.i(TAG,"Radius: "+ ((LBPHFaceRecognizer) recognize).getRadius());
            Log.i(TAG,"Neighbour: "+ ((LBPHFaceRecognizer) recognize).getNeighbors());
            Log.i(TAG,"Grid X: "+ ((LBPHFaceRecognizer) recognize).getGridX());
            Log.i(TAG,"Grid Y: "+ ((LBPHFaceRecognizer) recognize).getGridY());
            Log.i(TAG,"Threshold: "+ ((LBPHFaceRecognizer) recognize).getRadius());
            List<Mat> histograms = ((LBPHFaceRecognizer) recognize).getHistograms();
            Log.i(TAG,"Histogram Size: "+ histograms.size());


            calcHist();

            //add

            return true;
        }
        else{
            return false;
        }

    }

    //add identify image histogram
    private void calcHist() {
        String filename="",foldername="";
        for(int i=0;i<imagesLabels.size();i++){
            if(i==0){
                foldername=imagesLabels.get(i);
            }
        }
        File croppath = new File(Environment.getExternalStorageDirectory(), foldername+number);
        String path = croppath.getAbsolutePath();
        Log.e(TAG,"PATH: " +path);
        for(int i=0;i<imagesLabels.size();i++){
            if(i==0){
                filename=path+"/"+imagesLabels.get(i).toString()+"0.jpg";
            }
        }

        Mat src = Imgcodecs.imread(filename);
        if (src.empty()) {
            Log.e(TAG, "calcHist: Cannot read image: "+ filename);
            return ;
        }

        List<Mat> bgrPlanes = new ArrayList<>();
        Core.split(src, bgrPlanes);

        int histSize = 256;

        float[] range = {0, 256}; //the upper boundary is exclusive
        MatOfFloat histRange = new MatOfFloat(range);

        boolean accumulate = false;

        Mat bHist = new Mat(), gHist = new Mat(), rHist = new Mat();
        Imgproc.calcHist(bgrPlanes, new MatOfInt(0), new Mat(), bHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(1), new Mat(), gHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(2), new Mat(), rHist, new MatOfInt(histSize), histRange, accumulate);

        int histW = 512, histH = 400;
        int binW = (int) Math.round((double) histW / histSize);
        Mat histImage = new Mat( histH, histW, CvType.CV_8UC3, new Scalar( 0,0,0) );

        Core.normalize(bHist, bHist, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(gHist, gHist, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(rHist, rHist, 0, histImage.rows(), Core.NORM_MINMAX);
        float[] bHistData = new float[(int) (bHist.total() * bHist.channels())];
        bHist.get(0, 0, bHistData);
        float[] gHistData = new float[(int) (gHist.total() * gHist.channels())];
        gHist.get(0, 0, gHistData);
        float[] rHistData = new float[(int) (rHist.total() * rHist.channels())];
        rHist.get(0, 0, rHistData);
        for( int i = 1; i < histSize; i++ ) {
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(bHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(bHistData[i])), new Scalar(255, 0, 0), 2);
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(gHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(gHistData[i])), new Scalar(0, 255, 0), 2);
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(rHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(rHistData[i])), new Scalar(0, 0, 255), 2);
        }

        local.putImage("source.jpg",foldername, src,number);
        local.putImage("Demo.jpg",foldername, histImage,number);

//        toFirebaseStore(path,"source.jpg",foldername);
//        toFirebaseStore(path,"Demo.jpg",foldername);
    }
    //add

    //6. show dialog box
//    public void showLabelsDialog() {
//        Set<String> uniqueLabelsSet = new HashSet<>(imagesLabels); // Get all unique labels
//        if (!uniqueLabelsSet.isEmpty()) {
//            String[] uniqueLabels = uniqueLabelsSet.toArray(new String[uniqueLabelsSet.size()]); // Convert to String array for ArrayAdapter
//            Arrays.sort(uniqueLabels); // Sort labels alphabetically
//                    addLabel(username);
//                    Log.i(TAG, "Labels Size "+imagesLabels.size()+""); //number / photo of user's image capture
//
//        } else {
//            showEnterLabelDialog(); //6.1
//        }
//
//    }

    //6.1 dialog box for enter name (for first time snap)
//    private void showEnterLabelDialog() {
//                        String string = username;
//                            addLabel(string);
//    }

    // 7. add name
    private void addLabel(String string) {
        String label = username; // Make sure that the name is always uppercase and rest is lowercase
        imagesLabels.add(label); // Add label to list of labels
        Log.i(TAG, "Label: " + label); //this is user's name

    }

    //save trained data
    public boolean SaveImage() {
        //String username="";
        for(int i=0;i<imagesLabels.size();i++){
            if(i==0){
                username=imagesLabels.get(i);
            }
        }
//        File path = new File(Environment.getExternalStorageDirectory(), "TrainedData");
//        path.mkdirs();
//        String filename = "lbph_trained_data"+username+".xml"; //search inside phone =>TrainedData=>lbph_trained_data.xml
//        File file = new File(path, filename);
          File file = local.putXML(username); //store xml to local storage


        /**
         * recognize.save()- Saves a FaceRecognizer and its model state. Saves this model to a given filename, either as XML or YAML.
         * @param filename  The filename to store this FaceRecognizer to (either XML/YAML)
         * @return
         */
        recognize.save(file.toString());
        if(file.exists()){
            Log.i(TAG, "finish save image data");
            local.putFBXML(username,file); //store xml to firebase storage
            return true;
        }
        else
        {
            return false;
        }


    }

    //5. crop image to smaller focus face
    public void cropedImages(Mat mat) {
        Rect rect_Crop=null;
        for(Rect face: faces.toArray()) {
            rect_Crop = new Rect(face.x, face.y, face.width, face.height);
        }
        Mat croped = new Mat(mat, rect_Crop);
        //images.add(croped); //store a croped image into ArrayList<Mat>
        //add 6/6 to enhance sharpness
        Mat source = croped;
        Mat destination = new Mat(source.rows(),source.cols(),source.type());
        Imgproc.GaussianBlur(source,destination,new Size(0,0),10);
        Core.addWeighted(source,1.5,destination,-0.5, 0, destination);
        images.add(destination); //added 6/6

    }


    //1. basic setup for opencv environment, make sure gray faces area does not over by 2
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Stetho.initializeWithDefaults(this);
        Intent intent = getIntent(); //add 26/5
        final Intent intent2;
        detect = (Button)findViewById(R.id.take_picture_button);
        prestart= (Button)findViewById(R.id.prestart);
        appname1= (TextView)findViewById(R.id.appname);
        appname2= (TextView)findViewById(R.id.appname2);
        precam1= (TextView)findViewById(R.id.precam1);
        precam2= (TextView)findViewById(R.id.precam2);
        prestart_screen = (ConstraintLayout)findViewById(R.id.prestart_constraint);


        if (hasPermissions()){ //if camera permission granted=goto hasPermissions() function
            //Toast.makeText(this,"Number:"+number,Toast.LENGTH_LONG).show();
            username=intent.getStringExtra("username");
            Toast.makeText(TrainActivity.this, "Welcome! " + username, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Permission Granted Before");
            detect.setVisibility(View.INVISIBLE);
            detect.setEnabled(false);
        }
        else {
            requestPerms(); //if camera permission not granted=goto requestPerms() function
        }

        openCVCamera = (CameraBridgeViewBase)findViewById(R.id.java_camera_view);
        openCVCamera.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        openCVCamera.setVisibility(SurfaceView.VISIBLE);
        openCVCamera.setCvCameraViewListener(this);
        local = new Storage(this);

        prestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prestart.setVisibility(View.INVISIBLE);
                prestart.setEnabled(false);
                appname1.setVisibility(View.INVISIBLE);
                appname2.setVisibility(View.INVISIBLE);
                precam1.setVisibility(View.INVISIBLE);
                precam2.setVisibility(View.INVISIBLE);
                prestart_screen.setBackgroundColor(Color.parseColor("#001C1B1B"));
                detect.setVisibility(View.VISIBLE);
                detect.setEnabled(true);
            }
        });

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gray.total() == 0)
                    Toast.makeText(getApplicationContext(), "Can't Detect Faces", Toast.LENGTH_SHORT).show();
                classifier.detectMultiScale(gray,faces,1.1,3,0|CASCADE_SCALE_IMAGE, new Size(30,30)); //classify face from images
                if(!faces.empty()) {
                    if(faces.toArray().length > 1)
                        Toast.makeText(getApplicationContext(), "Multiple Faces Are not allowed", Toast.LENGTH_SHORT).show();
                    else {
                        if(gray.total() == 0) {
                            Log.i(TAG, "Empty gray image");
                            return;
                        }
                        cropedImages(gray); //5. crop image

                        addLabel(username);
                        detect.setText("CAPTURED FACE :"+capture_count +"/8");
                        capture_count++;
                        if(capture_count==3){ //9 for 8 image
                            detect.setEnabled(false);
                            Toast.makeText(TrainActivity.this, "Capture complete", Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(TrainActivity.this).setTitle("capture complete")
                                    .setMessage("Face Recognition Success")
                                    .setNeutralButton("start train", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onStop();
                                            new Handler().postDelayed(new Runnable() { //pause 3 second only resume to walkthru
                                                @Override
                                                public void run() {
                                                    Intent intent1 = new Intent(TrainActivity.this, ShopActivity.class);
                                                    startActivity(intent1);
                                                   // finish();//finish welcome activity
                                                }
                                            }, 5000);

                                            //onBackPressed();
                                        }
                                    })
                            .show();
                        }
                    }
                }else
                    Toast.makeText(getApplicationContext(), "Unknown Face", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //2.1 if camera permission granted=come to here: hasPermissions() function
    @SuppressLint("WrongConstant")
    private boolean hasPermissions(){
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.CAMERA};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    //2.2 if camera permission not granted=come to here: requestPerms() function
    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,PERMS_REQUEST_CODE);

        }
    }

    //2.3
    @Override
    public void onRequestPermissionsResult(int requestCode, @Nonnull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;
        switch (requestCode){
            case PERMS_REQUEST_CODE:
                for (int res : grantResults){
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }
        if (allowed){
            //user granted all permissions we can perform our task.
            Log.i(TAG, "Permission has been added");
        }
        else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //3 to check if opencv manager get to call back /connect with app (opencv setting)
    private BaseLoaderCallback callbackLoader = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status) {
                case BaseLoaderCallback.SUCCESS:
                    faces = new MatOfRect();
                    openCVCamera.enableView();
                    Log.i(TAG, "OpenCV loaded successfully");

                    if(number>1){
                        local.clear(); //testing
                        images = local.getListMat("images");
                        Log.i(TAG, "ArrayList<Mat> images  from getListMat" + String.valueOf(images)); //Mat(photo) [ 7744*1*CV_8UC1, isCont=true, isSubmat=false, nativeObj=0x70f41a68e0, dataAddr=0x70e41fc000 ], Mat [ 8464*1*CV_8UC1, isCont=true, isSubmat=false, nativeObj=0x70f41a6ee0, dataAddr=0x70e41cb800 ], Mat [ 7744*1*CV_8UC1, isCont=true, isSubmat=false, nativeObj=0x70f41ebbe0, dataAddr=0x70e0e57000 ]
                        imagesLabels = local.getListString("imagesLabels");
                        Log.i(TAG, "ArrayList<String> imagesLabels from getListString" + String.valueOf(imagesLabels)); //[Vanessa, Vanessa, Vanessa]
                    }
                    else {
                        //show all details when launch camera
                        images = local.getListMat("images");
                        Log.i(TAG, "ArrayList<Mat> images  from getListMat" + String.valueOf(images)); //Mat(photo) [ 7744*1*CV_8UC1, isCont=true, isSubmat=false, nativeObj=0x70f41a68e0, dataAddr=0x70e41fc000 ], Mat [ 8464*1*CV_8UC1, isCont=true, isSubmat=false, nativeObj=0x70f41a6ee0, dataAddr=0x70e41cb800 ], Mat [ 7744*1*CV_8UC1, isCont=true, isSubmat=false, nativeObj=0x70f41ebbe0, dataAddr=0x70e0e57000 ]
                        imagesLabels = local.getListString("imagesLabels");
                        Log.i(TAG, "ArrayList<String> imagesLabels from getListString" + String.valueOf(imagesLabels)); //[Vanessa, Vanessa, Vanessa]
                    }
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    //(opencv setting)
    @Override
    protected void onPause() {
        super.onPause();
        if(openCVCamera != null)
            openCVCamera.disableView();


    }

    //8. when terminate page lifecycle (opencv setting)
    @Override
    protected void onStop() {
        super.onStop();
        if (images != null && imagesLabels != null) {
            //store all details when camera stops
            local.putListMat("images", images); //put data into database
            local.putListString("imagesLabels", imagesLabels); //put data into database
            Log.i(TAG, "Images have been saved");

            //clear everything inside imagas and imageLabels List
            if(trainfaces()) { //go to 9, if true come back here as 10
                images.clear();
                imagesLabels.clear();
                //local.clear(); //will clear all sharedpreference items (to prevent one-time usage, but recognize activity seems crash
                Log.i(TAG,"ArrayList<Mat> images has clear:"+images);
                Log.i(TAG,"ArrayList<String> imagesLabels has clear:"+images);
            }
        }
    }

    //(opencv setting)
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(openCVCamera != null)
            openCVCamera.disableView();
    }

    //4. when app is moving on => show result of connection in opencv library(opencv setting)
    @Override
    protected void onResume(){
        super.onResume();
        if(OpenCVLoader.initDebug()) {
            Log.i(TAG, "System Library Loaded Successfully"); //opencv success msg
            callbackLoader.onManagerConnected(BaseLoaderCallback.SUCCESS);
        } else {
            Log.i(TAG, "Unable To Load System Library");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, callbackLoader);
        }
    }

    //start camera frame (CameraBridgeViewBase.CvCameraViewListener2 settings)
    @Override
    public void onCameraViewStarted(int width, int height) {
        rgba = new Mat();
        gray = new Mat();

        //recognize face
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
       // end of camera orientation
        gray = mGrayTmp;
        rgba = mRgbaTmp;
        Imgproc.resize(gray, gray, new Size(200,200.0f/ ((float)gray.width()/ (float)gray.height()))); //pass gray image

        return rgba;
    }

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
