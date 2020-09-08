//
// This file is auto-generated. Please don't modify it! (cv::face::LBPHFaceRecognizer Class Reference in javadoc)
//
package org.opencv.face;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.utils.Converters;

// C++: class LBPHFaceRecognizer
//javadoc: LBPHFaceRecognizer

public class LBPHFaceRecognizer extends FaceRecognizer {

    //constructer
    protected LBPHFaceRecognizer(long addr) { super(addr); }

    // internal usage only
    public static LBPHFaceRecognizer __fromPtr__(long addr) { return new LBPHFaceRecognizer(addr); }

    //
    // C++:  Mat getLabels()
    //

    //javadoc: LBPHFaceRecognizer::getLabels()
    public  Mat getLabels()
    {
        
        Mat retVal = new Mat(getLabels_0(nativeObj));
        
        return retVal;
    }


    //
    // C++: static Ptr_LBPHFaceRecognizer create(int radius = 1, int neighbors = 8, int grid_x = 8, int grid_y = 8, double threshold = DBL_MAX)
    //

    //javadoc: LBPHFaceRecognizer::create(radius, neighbors, grid_x, grid_y, threshold)
    /**
     * 
     * @param radius The radius used for building the Circular Local Binary Pattern. The greater the radius, the smoother the image but more spatial information you can get.
     * @param neighbors The number of sample points to build a Circular Local Binary Pattern from. An appropriate value is to use 8 sample points. Keep in mind: the more sample points you include, the higher the computational cost.
     * @param grid_x The number of cells in the horizontal direction, 8 is a common value used in publications. The more cells, the finer the grid, the higher the dimensionality of the resulting feature vector.
     * @param grid_y The number of cells in the vertical direction, 8 is a common value used in publications. The more cells, the finer the grid, the higher the dimensionality of the resulting feature vector.
     * @param threshold The threshold applied in the prediction. If the distance to the nearest neighbor is larger than the threshold, this method returns -1
     * @return automatically generated
     */
    public static LBPHFaceRecognizer create(int radius, int neighbors, int grid_x, int grid_y, double threshold)
    {
        
        LBPHFaceRecognizer retVal = LBPHFaceRecognizer.__fromPtr__(create_0(radius, neighbors, grid_x, grid_y, threshold));
        
        return retVal;
    }

    //javadoc: LBPHFaceRecognizer::create()
    /**
     * 
     * radius, the smoother the image but more spatial information you can get. 
     * appropriate value is to use 8 sample points. 
     * Keep in mind: the more sample points you include, the higher the computational cost. publications. 
     * The more cells, the finer the grid, the higher the dimensionality of the resulting feature vector. 
     * publications. is larger than the threshold, this method returns -1. 
     * @return automatically generated
     */
    public static LBPHFaceRecognizer create()
    {
        
        LBPHFaceRecognizer retVal = LBPHFaceRecognizer.__fromPtr__(create_1());
        
        return retVal;
    }


    //
    // C++:  double getThreshold()
    //

    //javadoc: LBPHFaceRecognizer::getThreshold()
    /**
     * 
     * @return threhold value
     */
    public  double getThreshold()
    {
        
        double retVal = getThreshold_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  int getGridX()
    //

    //javadoc: LBPHFaceRecognizer::getGridX()
    /**
     * 
     * @return grid x value
     */
    public  int getGridX()
    {
        
        int retVal = getGridX_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  int getGridY()
    //

    //javadoc: LBPHFaceRecognizer::getGridY()
    /**
     * 
     * @return grid y value
     */
    public  int getGridY()
    {
        
        int retVal = getGridY_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  int getNeighbors()
    //

    //javadoc: LBPHFaceRecognizer::getNeighbors()
    /**
     * 
     * @return neightbour value
     */
    public  int getNeighbors()
    {
        
        int retVal = getNeighbors_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  int getRadius()
    //

    //javadoc: LBPHFaceRecognizer::getRadius()
    /**
     * 
     * @return radius value
     */
    public  int getRadius()
    {
        
        int retVal = getRadius_0(nativeObj);
        
        return retVal;
    }


    //
    // C++:  vector_Mat getHistograms()
    //

    //javadoc: LBPHFaceRecognizer::getHistograms()
    public  List<Mat> getHistograms()
    {
        List<Mat> retVal = new ArrayList<Mat>();
        Mat retValMat = new Mat(getHistograms_0(nativeObj));
        Converters.Mat_to_vector_Mat(retValMat, retVal);
        return retVal;
    }


    //
    // C++:  void setGridX(int val)
    //

    //javadoc: LBPHFaceRecognizer::setGridX(val)
    /**
     * 
     * @param val grid x value
     * @return 
     */
    public  void setGridX(int val)
    {
        
        setGridX_0(nativeObj, val);
        
        return;
    }


    //
    // C++:  void setGridY(int val)
    //

    //javadoc: LBPHFaceRecognizer::setGridY(val)
    /**
     * 
     * @param val grid y value
     * @return 
     */
    public  void setGridY(int val)
    {
        
        setGridY_0(nativeObj, val);
        
        return;
    }


    //
    // C++:  void setNeighbors(int val)
    //

    //javadoc: LBPHFaceRecognizer::setNeighbors(val)
    /**
     * 
     * @param val neighbour value
     * @return 
     */
    public  void setNeighbors(int val)
    {
        
        setNeighbors_0(nativeObj, val);
        
        return;
    }


    //
    // C++:  void setRadius(int val)
    //

    //javadoc: LBPHFaceRecognizer::setRadius(val)
    /**
     * 
     * @param val radius value
     * @return 
     */
    public  void setRadius(int val)
    {
        
        setRadius_0(nativeObj, val);
        
        return;
    }


    //
    // C++:  void setThreshold(double val)
    //

    //javadoc: LBPHFaceRecognizer::setThreshold(val)
    /**
     * 
     * @param val threshold value
     * @return 
     */
    public  void setThreshold(double val)
    {
        
        setThreshold_0(nativeObj, val);
        
        return;
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  Mat getLabels()
    private static native long getLabels_0(long nativeObj);

    // C++: static Ptr_LBPHFaceRecognizer create(int radius = 1, int neighbors = 8, int grid_x = 8, int grid_y = 8, double threshold = DBL_MAX)
    private static native long create_0(int radius, int neighbors, int grid_x, int grid_y, double threshold);
    private static native long create_1();

    // C++:  double getThreshold()
    private static native double getThreshold_0(long nativeObj);

    // C++:  int getGridX()
    private static native int getGridX_0(long nativeObj);

    // C++:  int getGridY()
    private static native int getGridY_0(long nativeObj);

    // C++:  int getNeighbors()
    private static native int getNeighbors_0(long nativeObj);

    // C++:  int getRadius()
    private static native int getRadius_0(long nativeObj);

    // C++:  vector_Mat getHistograms()
    private static native long getHistograms_0(long nativeObj);

    // C++:  void setGridX(int val)
    private static native void setGridX_0(long nativeObj, int val);

    // C++:  void setGridY(int val)
    private static native void setGridY_0(long nativeObj, int val);

    // C++:  void setNeighbors(int val)
    private static native void setNeighbors_0(long nativeObj, int val);

    // C++:  void setRadius(int val)
    private static native void setRadius_0(long nativeObj, int val);

    // C++:  void setThreshold(double val)
    private static native void setThreshold_0(long nativeObj, double val);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
