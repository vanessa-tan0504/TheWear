package com.thewear.thewearapp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;

public class FileUtils {
    private static String TAG = FileUtils.class.getSimpleName();
    private static boolean loadFile(Context context, String cascadeName) {
        InputStream inp = null;
        OutputStream out = null;
        boolean completed = false;
        try {
            inp = context.getResources().getAssets().open(cascadeName);
            File outFile = new File(context.getCacheDir(), cascadeName);
            out = new FileOutputStream(outFile);

            byte[] buffer = new byte[4096];
            int bytesread;
            while((bytesread = inp.read(buffer)) != -1) {
                out.write(buffer, 0, bytesread);
            }

            completed = true;
            inp.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.i(TAG, "Unable to load cascade file" + e);
        }
        return completed;
    }
    public static CascadeClassifier loadXMLS(Context context, String cascadeName) {

        CascadeClassifier classifier = null;

        if(loadFile(context, cascadeName)) {
            File cascade = new File(context.getCacheDir(), cascadeName);
            classifier = new CascadeClassifier(cascade.getAbsolutePath());
            Log.i(TAG, "Cascade File Loaded Successfully");
            cascade.delete();
        } else {
            Log.i(TAG, "Path Direction May Be Wrong");
        }
        return classifier;
    }
    public static String loadTrained() {

        //after retrive xml from firebase then save as localfile, then retrive the local file
        File file = new File(Environment.getExternalStorageDirectory(), "TrainedData/test_model.xml");
        return file.toString();
    }

}
