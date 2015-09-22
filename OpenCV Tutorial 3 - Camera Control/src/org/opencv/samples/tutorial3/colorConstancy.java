package org.opencv.samples.tutorial3;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point3;
import org.opencv.core.Range;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class colorConstancy {
public static Mat greyWorld(Mat src1){
	Mat dst = new Mat();
	Mat src=new Mat();
	src1.convertTo(src,CvType.CV_64FC3);
	List<Mat> channels=new ArrayList<Mat>();
	Core.split(src, channels);
	Scalar Rmean=Core.mean(channels.get(2));
	Scalar Gmean=Core.mean(channels.get(1));
	Scalar Bmean=Core.mean(channels.get(0));
	double Avg=(Rmean.val[0]+Gmean.val[0]+Bmean.val[0])/3;
    double Kr = Avg/Rmean.val[0];
    double Kg = Avg/Gmean.val[0];
    double Kb = Avg/Bmean.val[0];
    List<Mat> outchannels=new ArrayList<Mat>();
    for (int i = 0; i < src.channels(); i++) {
        Mat cha = new Mat(src.size(),CvType.CV_64FC1);
        outchannels.add(cha);
       }
    Mat temp=new Mat();    Mat temp1=new Mat();    Mat temp2=new Mat();
    Core.multiply(channels.get(0),new Mat(src.size(),CvType.CV_64FC1,new Scalar(Kb)),temp);outchannels.set(0,temp);
    Core.multiply(channels.get(1),new Mat(src.size(),CvType.CV_64FC1,new Scalar(Kg)),temp1);outchannels.set(1,temp1);
    Core.multiply(channels.get(2),new Mat(src.size(),CvType.CV_64FC1,new Scalar(Kr)),temp2);outchannels.set(2,temp2);
    Mat tmp=new Mat();
    Core.merge(outchannels,tmp);
    tmp.convertTo(dst,CvType.CV_8UC3);
	return dst;
}
public static void getColorValues(Mat mRGBA,ArrayList<Point3> rgbarray){
	Mat mGray =new Mat();
	 Imgproc.cvtColor(mRGBA, mGray, Imgproc.COLOR_RGBA2GRAY);
	 Imgproc.equalizeHist(mGray,mGray);
	 Size sizemGray=mGray.size();
	 //System.out.println(sizemGray);
	//Splitting the Matrix and converting to binary
       Mat temp1=  new Mat(mGray, new Range(0, (int)sizemGray.height),new Range(0, (int)sizemGray.width/2));
       Mat temp2=  new Mat(mGray, new Range(0, (int)sizemGray.height),new Range((int)sizemGray.width/2,(int)sizemGray.width));
       Imgproc.threshold(temp1, temp1, 180, 255, Imgproc.THRESH_BINARY);
       Imgproc.threshold(temp2, temp2, 125, 255, Imgproc.THRESH_BINARY);
		//Locating the control and reference stripes
       int[] pixels = new int[4];
       pixels[0] = -1;
       pixels[1] = -1;
       pixels[2] = -1;
       pixels[3] = -1;
		
       Size sizetemp1 = temp1.size();
       Size sizetemp2 = temp2.size();
       //System.out.println(sizetemp1);
       //System.out.println(sizetemp2);
       for (int i = 0; i < sizetemp1.width; i++) {
       	double sum=0;
       	
           for (int j = 0; j < sizetemp1.height; j++){
           	double[] data = new double[1];
           	data = temp1.get(j, i);
           	sum = sum + (data[0]/255);
           }
           if(sum <= sizetemp1.height/8) {
           	pixels[0] = i;
           	break;
           }
       }
     //System.out.println(pixels[0]);
     for (int i = (int) (sizetemp1.width-1) ; i >0; i--) {
       	double sum=0;
           for (int j = 0; j < sizetemp1.height; j++){
           	double[] data = new double[1];
           	data = temp1.get(j, i);
           	sum = sum + (data[0]/255);
           }
           if(sum <= sizetemp1.height/8) {
	            pixels[1] = i;
           	break;
           }
       }
     //System.out.println(pixels[1]);
       for (int i = 0 ; i < sizetemp2.width; i++) {
       	double sum=0;
       	
           for (int j = 0; j < sizetemp2.height; j++){
           	double[] data = new double[1];
           	data = temp2.get(j, i);
           	sum = sum + (data[0]/255);
           }
           
           if(sum <= sizetemp2.height/4) {
           	pixels[2] = i;
           	break;
           }
       }
       //System.out.println(pixels[2]);

       for (int i = (int) (sizetemp2.width-1); i >0; i--) {
       	double sum=0;
       	
           for (int j = 0; j < sizetemp2.height; j++){
           	double[] data = new double[1];
           	data = temp2.get(j, i);
           	sum = sum + (data[0]/255);
           }
           
           if(sum <= sizetemp2.height/4) {
           	pixels[3] = i;
           	break;
           }
       }
       //System.out.println(pixels[3]);
       if(pixels[1]-pixels[0]>10){
       	//Cropping control and reference stripes
       	Mat temp3=  new Mat(mRGBA, new Range(0, (int)sizemGray.height),new Range(0, (int)sizemGray.width/2));
	        Mat temp4=  new Mat(mRGBA, new Range(0, (int)sizemGray.height),new Range((int)sizemGray.width/2,(int)sizemGray.width));
	        Mat Stripe1=new Mat(temp3, new Range(0, (int)sizemGray.height),new Range(pixels[0], pixels[1]));
	        Mat Stripe2=new Mat(temp4, new Range(0, (int)sizemGray.height),new Range(pixels[2], pixels[3]));
			List<Point3> rgblist= new ArrayList<Point3>();
			for(int i=(int)(0.2*Stripe1.cols());i<(int)(0.8*Stripe1.cols());i++){
				for(int j=0;j<Stripe1.rows();j++){
					Point3 p=new Point3(Stripe1.get(j,i));
				    rgblist.add(p);
				}
			}	
			  Collections.shuffle(rgblist);
			// List<Point3> rgbarray= new ArrayList<Point3>();
		      for(int i=0;i<100;i++){
			rgbarray.add(rgblist.get(i));
			 } 
       }
}
}
