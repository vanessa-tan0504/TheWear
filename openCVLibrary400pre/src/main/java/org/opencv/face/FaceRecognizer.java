//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.face;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Algorithm;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.face.PredictCollector;
import org.opencv.utils.Converters;

// C++: class FaceRecognizer
//javadoc: FaceRecognizer

public class FaceRecognizer extends Algorithm {

    //constructor
    protected FaceRecognizer(long addr) { super(addr); }

    // internal usage only
    public static FaceRecognizer __fromPtr__(long addr) { return new FaceRecognizer(addr); }

    //
    // C++:  String getLabelInfo(int label)
    //

    //javadoc: FaceRecognizer::getLabelInfo(label)
    /**
     * Gets string information by label. If an unknown label id is provided or there is no label information 
     * associated with the specified label id the method returns an empty string.
     * @param path label id
     * @return string info by label
     */
    public  String getLabelInfo(int label)
    {
        
        String retVal = getLabelInfo_0(nativeObj, label);
        
        return retVal;
    }


    //
    // C++:  int predict(Mat src)
    //

    //javadoc: FaceRecognizer::predict(src)
    public  int predict_label(Mat src)
    {
        
        int retVal = predict_label_0(nativeObj, src.nativeObj);
        
        return retVal;
    }


    //
    // C++:  vector_int getLabelsByString(String str)
    //

    //javadoc: FaceRecognizer::getLabelsByString(str)
    /**
     * Gets vector of labels by string. The function searches for the labels containing the specified 
     * sub-string in the associated string info.
     * @param str  specified sub-string in the associated string info.
     * @return vector of labels by string
     */
    public  MatOfInt getLabelsByString(String str)
    {
        
        MatOfInt retVal = MatOfInt.fromNativeAddr(getLabelsByString_0(nativeObj, str));
        
        return retVal;
    }


    //
    // C++:  void predict(Mat src, Ptr_PredictCollector collector)
    //

    //javadoc: FaceRecognizer::predict(src, collector)
    /**
     * send all result of prediction to collector that can be used for somehow custom result handling
     * @param src Sample image to get a prediction from
     * @param collector User-defined collector object that accepts all results
     * @return 
     */
    public  void predict_collect(Mat src, PredictCollector collector)
    {
        
        predict_collect_0(nativeObj, src.nativeObj, collector.getNativeObjAddr());
        
        return;
    }


    //
    // C++:  void predict(Mat src, int& label, double& confidence)
    //

    //javadoc: FaceRecognizer::predict(src, label, confidence)
    /**
     * Predicts a label and associated confidence (e.g. distance) for a given input image.
     * @param src Sample image to get a prediction from
     * @param label The predicted label for the given image.
     * @param confidence Associated confidence (e.g. distance) for the predicted label. 
     * The suffix const means that prediction does not affect the internal model state, so the method 
     * can be safely called from within different threads.  
     * @return 
     */
    public  void predict(Mat src, int[] label, double[] confidence)
    {
        double[] label_out = new double[1];
        double[] confidence_out = new double[1];
        predict_0(nativeObj, src.nativeObj, label_out, confidence_out);
        if(label!=null) label[0] = (int)label_out[0];
        if(confidence!=null) confidence[0] = (double)confidence_out[0];
        return;
    }


    //
    // C++:  void read(String filename)
    //

    //javadoc: FaceRecognizer::read(filename)
    /**
     * Loads a FaceRecognizer and its model state. 
     * Loads a persisted model and state from a given XML or YAML file . 
     * Every FaceRecognizer has to overwrite FaceRecognizer::load(FileStorage& fs) to enable loading 
     * the model state. FaceRecognizer::load(FileStorage& fs) in turn gets called 
     * by FaceRecognizer::load(const String& filename), to ease saving a model.
     * @param filename name for XML or YAML file
     * @return 
     */
    public  void read(String filename)
    {
        
        read_0(nativeObj, filename);
        
        return;
    }


    //
    // C++:  void setLabelInfo(int label, String strInfo)
    //

    //javadoc: FaceRecognizer::setLabelInfo(label, strInfo)
    /**
     * Sets string info for the specified model's label. The string info is replaced by the 
     * provided value if it was set before for the specified label.
     * @param label automatically generated
     * @param strInfo  automatically generated
     * @return 
     */
    public  void setLabelInfo(int label, String strInfo)
    {
        
        setLabelInfo_0(nativeObj, label, strInfo);
        
        return;
    }


    //
    // C++:  void train(vector_Mat src, Mat labels)
    //

    //javadoc: FaceRecognizer::train(src, labels)
    /**
     * Trains a FaceRecognizer with given data and associated labels.
     * @param src The training images, that means the faces you want to learn. The data has to be given as a vector<Mat>.
     * @param labels The labels corresponding to the images have to be given either as a vector<int> or a Mat of type CV_32SC1.
     * @return 
     */
    public  void train(List<Mat> src, Mat labels)
    {
        Mat src_mat = Converters.vector_Mat_to_Mat(src);
        train_0(nativeObj, src_mat.nativeObj, labels.nativeObj);
        
        return;
    }


    //
    // C++:  void update(vector_Mat src, Mat labels)
    //

    //javadoc: FaceRecognizer::update(src, labels)
    /**
     * Updates a FaceRecognizer with given data and associated labels.
     * @param src The training images, that means the faces you want to learn. The data has to be given as a vector<Mat>.
     * @param labels The labels corresponding to the images have to be given either as a vector<int> or a Mat of type CV_32SC1.
     * @return 
     */
    public  void update(List<Mat> src, Mat labels)
    {
        Mat src_mat = Converters.vector_Mat_to_Mat(src);
        update_0(nativeObj, src_mat.nativeObj, labels.nativeObj);
        
        return;
    }


    //
    // C++:  void write(String filename)
    //

    //javadoc: FaceRecognizer::write(filename)
    /**
     *Saves a FaceRecognizer and its model state. Saves this model to a given filename, either as XML or YAML.
     * @param filename  The filename to store this FaceRecognizer to (either XML/YAML)
     * @return 
     */
    public  void write(String filename)
    {
        
        write_0(nativeObj, filename);
        
        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  String getLabelInfo(int label)
    private static native String getLabelInfo_0(long nativeObj, int label);

    // C++:  int predict(Mat src)
    private static native int predict_label_0(long nativeObj, long src_nativeObj);

    // C++:  vector_int getLabelsByString(String str)
    private static native long getLabelsByString_0(long nativeObj, String str);

    // C++:  void predict(Mat src, Ptr_PredictCollector collector)
    private static native void predict_collect_0(long nativeObj, long src_nativeObj, long collector_nativeObj);

    // C++:  void predict(Mat src, int& label, double& confidence)
    private static native void predict_0(long nativeObj, long src_nativeObj, double[] label_out, double[] confidence_out);

    // C++:  void read(String filename)
    private static native void read_0(long nativeObj, String filename);

    // C++:  void setLabelInfo(int label, String strInfo)
    private static native void setLabelInfo_0(long nativeObj, int label, String strInfo);

    // C++:  void train(vector_Mat src, Mat labels)
    private static native void train_0(long nativeObj, long src_mat_nativeObj, long labels_nativeObj);

    // C++:  void update(vector_Mat src, Mat labels)
    private static native void update_0(long nativeObj, long src_mat_nativeObj, long labels_nativeObj);

    // C++:  void write(String filename)
    private static native void write_0(long nativeObj, String filename);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
