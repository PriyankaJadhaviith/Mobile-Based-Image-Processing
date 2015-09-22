package org.opencv.samples.tutorial3;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Range;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class TSH {

	public static Mat fscs(Mat src){
		Mat dst = new Mat();
		dst= Mat.zeros(src.size(), src.type());
	
		MinMaxLocResult mmr = Core.minMaxLoc(src);
		double max=mmr.maxVal, min=mmr.maxVal;
		// Perform full scale contrast stretch
		double p, l;
		p = 255/(max - min);
		l = -1 * min*p;
		Mat tmp=new Mat(src.size(),src.type());
	    tmp=src.mul(new Mat(src.size(), src.type(), new Scalar(p)));
        Mat tmp1=new Mat(src.size(),src.type(),new Scalar(l));
	    // FSCS: J = P*I + L 
		  Core.add(tmp,tmp1,dst); 
		 return dst;
	}
	
	public static void getOptimalImgAdjustParamsFromHist(Mat src,double[] p_optminmaxidx, int p_count)
	{
	final int mHistSizeNum = 256;
    List<Mat> calcImage = new ArrayList<Mat>();
    calcImage.add(src);
    MatOfInt mchannels=new MatOfInt(0);
    Mat mask = new Mat();
    Mat hist = new Mat();
    MatOfInt mHistSize=new MatOfInt(mHistSizeNum);
    MatOfFloat mRanges = new MatOfFloat(0f, 256f);
    Imgproc.calcHist(calcImage, mchannels,mask, hist,mHistSize,mRanges);
    float mBuff[]=new float[mHistSizeNum];
		int sumlow = 0, sumhigh = 0;
		int low_idx = 0, high_idx = 0;
		hist.get(0,0,mBuff);
		for (int i = 0; i < mHistSizeNum; i++) {
			float curval=mBuff[i];
			sumlow += curval;
			if (sumlow >= p_count) {
				low_idx = i;
				break;
			}
		}
		for (int i =mHistSizeNum-1;i >= 0; i--) {
			float curval =mBuff[i];
			sumhigh += curval;
			if (sumhigh >= p_count) {
				high_idx = i;
				break;
			}
		}
		p_optminmaxidx[0] = low_idx;
		p_optminmaxidx[1] = high_idx;
	}

	public static Mat imageAdjust(Mat src)
	{
		int low_count = (int)(src.height()*src.width()* 0.01);
		double[] optminmaxidx = new double[2];
		TSH.getOptimalImgAdjustParamsFromHist(src, optminmaxidx, low_count);
		double range = optminmaxidx[1] - optminmaxidx[0];
		Mat adjustedImg = new Mat(src.size(), src.type());
		for (int i = 0; i < src.height(); i++)
			for (int j = 0; j < src.width(); j++) {
				double[] val = src.get(i, j);
			
			double newval = 0;
			if (val[0] <= optminmaxidx[0]) {
				newval = 0;
				adjustedImg.put(i,j, newval);
			}
			else if (val[0] >= optminmaxidx[1]) {
				newval = 255;
				adjustedImg.put(i,j, newval);
			}
			else {
				newval =(val[0]-optminmaxidx[0])*255/range;
				adjustedImg.put(i,j, newval);
			}
			}
		return adjustedImg;
	}
	
	
	public static double gaussian(int x,double sigma){
		return (Math.exp((- x*x / (2 * sigma*sigma))) / (Math.sqrt(2*Math.PI)*sigma));
	}

	//creating gausian kernel
	public static Mat creategaussiankernel(int ksize,double sigma){
		Mat kernel = new Mat();
		kernel=Mat.zeros(1, ksize,CvType.CV_64F);
		double ker[]=new double[ksize];
				kernel.get(0,0,ker);
		for (int i = -(ksize / 2); i <= (ksize / 2); i++){
			ker[i + ksize / 2]=gaussian(i,sigma);
		}
		double sum = 0;
		for (int i = 0; i < ksize; i++){
			sum += ker[i];
		}
		for (int i = 0; i < ksize; i++){
		 ker[i] = ker[i]/sum;
		}
		kernel.put(0,0,ker);
		return kernel;
		}
	
	
	//fill border for source image with 'bw' border width
	public static Mat fillborder(Mat src, int bw){
		int dstrows = src.height() + 2 * bw;
		int dstcols = src.width() + 2 * bw;
			Mat dst = new Mat(dstrows, dstcols, src.type());
			//top left
			for (int y = 0; y < bw; y++){
				for (int x = 0; x < bw; x++){
					dst.put(y,x,src.get(0,0));
				}
			}
			//top
			for (int y = 0; y < bw; y++){
				for (int x = bw; x < bw + src.width(); x++){
					//Scalar_<uchar> intensity = src.at<uchar>(0, x - bw);
					dst.put(y,x,src.get(0,x-bw));

				}
			}
			//toprights
			for (int y = 0; y < bw; y++){
				for (int x = bw + src.width(); x < dst.width(); x++){
					//Scalar_<uchar> intensity = src.at<uchar>(0, src.cols - 1);
					dst.put(y,x,src.get(0,src.width()-1));
				}
			}

			//left
			for (int y = bw; y < bw + src.height(); y++){
				for (int x = 0; x < bw; x++){
					//Scalar_<uchar> intensity = src.at<uchar>(y-bw,0);
					dst.put(y,x,src.get(y-bw,0));
				}
			}
			//right
			for (int y = bw; y < bw + src.height(); y++){
				for (int x = bw + src.width(); x < dst.width(); x++){
					//Scalar intensity = src.at<uchar>(y - bw, src.cols - 1); 
					dst.put(y,x,src.get(y-bw,src.width()-1));
				}
			}

			//bottom left
			for (int y = bw + src.height(); y < dst.height(); y++){
				for (int x = 0; x < bw; x++){
					//Scalar_<uchar> intensity = src.at<uchar>(src.rows - 1, 0);
					dst.put(y,x,src.get(src.height()-1,0));
				}
			}
			//bottom 
			for (int y = bw + src.height(); y < dst.height(); y++){
				for (int x = bw; x < bw + src.width(); x++){
					//Scalar_<uchar> intensity = src.at<uchar>(src.rows - 1, x - bw);
					dst.put(y,x,src.get(src.height()-1,x-bw));
				}
			}

			//bottom right
			for (int y = bw + src.height(); y < dst.height(); y++){
				for (int x = bw + src.width(); x < dst.width(); x++){
					//Scalar intensity = src.at<uchar>(src.rows - 1, src.cols - 1);
					dst.put(y,x,src.get(src.height()-1,src.width()-1));

				}
			}
			//src->dst
			for (int y = bw; y < bw + src.height(); y++){
				for (int x = bw; x < bw + src.width(); x++){
					// calculate pixValue
					dst.put(y,x,src.get(y-bw,x-bw));
				}
			}
					return dst;
		}	
	
	//applying gaussian filter
	public static Mat color_gauss(Mat src, double sigma, int ksize){
		int bw = ksize / 2;
		Mat fbsrc = new Mat();
		fbsrc=fillborder(src,bw);
		Mat kernel = new Mat();
		kernel=creategaussiankernel(ksize, sigma);
		int cn = fbsrc.channels();
		Mat dst=new Mat();
	    dst = Mat.zeros(src.size(),src.type());
		if (cn == 1){
			Mat tmp =new Mat();
			Mat tmp1 =Mat.zeros(fbsrc.size(),CvType.CV_64FC1);
			Mat tmp2 =Mat.zeros(fbsrc.size(),CvType.CV_64FC1);
			fbsrc.convertTo(tmp, CvType.CV_64FC1);
			//x-direction
			for (int y = bw; y < bw + src.rows(); y++){
				for (int x = bw; x < bw + src.cols(); x++){
					double sum[]=tmp1.get(y,x);
					  for (int h = 0; h < ksize; h++){
						    sum[0] += kernel.get(0,h)[0]* tmp.get(y,x-bw+h)[0]; 
					  }  
					  tmp1.put(y,x,sum);
					  sum[0]=0;
				}	
			}
			//y-direction
			for (int y = bw; y < bw + src.rows(); y++){
				for (int x = bw; x < bw + src.cols(); x++){
					double sum[]=tmp2.get(y,x);
					for (int h = 0; h < ksize; h++){
						sum[0] += kernel.get(0,h)[0]*tmp1.get(y-bw+h,x)[0]; 
					}
					  tmp2.put(y,x,sum);
					  dst.put(y-bw,x-bw,sum);
					  sum[0]=0;
					
				}
			}
		}
		else if (cn == 3){
			Mat tmp=new Mat();
			Mat tmp1 =new Mat();   tmp1=Mat.zeros(fbsrc.size(),CvType.CV_64FC3);
			Mat tmp2 =new Mat();   tmp2=Mat.zeros(fbsrc.size(),CvType.CV_64FC3);
			fbsrc.convertTo(tmp, CvType.CV_64FC3);
		  
			//x-direction
			for (int y = bw; y < bw + src.rows(); y++){
				for (int x = bw; x < bw + src.cols(); x++){
				 double[] sum=tmp1.get(y,x);
					  for (int h = 0; h < ksize; h++){
						     sum[0] +=kernel.get(0,h)[0]*tmp.get(y,x-bw+h)[0];
						    sum[1] += kernel.get(0,h)[0]*tmp.get(y,x-bw+h)[1]; 
						     sum[2] +=kernel.get(0,h)[0]*tmp.get(y,x-bw+h)[2];
					  }  
					  //System.out.println(tmp1val[0]+"and"+tmp1val[1]+"and"+tmp1val[2]);
					  tmp1.put(y,x,sum);
					   sum[0]=0.0;sum[1]=0.0;sum[2]=0.0;
				}	
			}
		
			
			//y-direction
			for (int y = bw; y < bw + src.rows(); y++){
				for (int x = bw; x < bw + src.cols(); x++){
					double[]sum=tmp2.get(y,x);
					for (int h = 0; h < ksize; h++){
						sum[0] += kernel.get(0,h)[0]*tmp1.get(y-bw+h,x)[0];
						sum[1] += kernel.get(0,h)[0]*tmp1.get(y-bw+h,x)[1];
					    sum[2] += kernel.get(0,h)[0]*tmp1.get(y-bw+h,x)[2];
					}
					  tmp2.put(y,x,sum);
					  dst.put(y-bw,x-bw,sum);
					  sum[0]=0;   sum[1]=0;   sum[2]=0;
					
				}
			}
			}
		return dst;
	}
	public static Mat colorgauss(Mat src, double sigma, int ksize){
		int bw = ksize / 2;
		Mat fbsrc = new Mat();
		fbsrc=fillborder(src,bw);
		Mat kernel = creategaussiankernel(ksize, sigma);
		Mat tmp=new Mat();
		Mat tmp1=new Mat();
	    //dst = Mat.zeros(src.size(),src.type());
	    Imgproc.filter2D(fbsrc,tmp,-1, kernel);
	    Imgproc.filter2D(tmp,tmp1,-1, kernel.t());
	    Mat dst=new Mat(tmp1,new Range(bw,tmp1.rows()-bw),new Range(bw,tmp1.cols()-bw));
	    System.out.println(src.rows()+"and"+dst.rows()+"and"+src.cols()+"and"+dst.cols());
	    
		return dst;
	}
	
	public static  Mat preprocessing(Mat src){
		Mat img_smooth=TSH.colorgauss(src,3.0,7);
		List<Mat> channels = new ArrayList<Mat>();
        /*for (int i = 0; i < src.channels(); i++) {
                Mat cha = new Mat(img_smooth.height(),img_smooth.width(),CvType.CV_8UC1);
                channels.add(cha);
        }*/
        Core.split(img_smooth, channels);
        List<Mat> outchannels = new ArrayList<Mat>();
        for (int i = 0; i < src.channels(); i++) {
            Mat cha = new Mat(src.height(), src.width(),CvType.CV_8UC1);
            outchannels.add(cha);
            outchannels.set(i,TSH.imageAdjust(channels.get(i)));
    }
        Mat image_eq=new Mat();
        Core.merge(outchannels,image_eq);
        final int nrows = image_eq.rows();
    	final int ncols = image_eq.cols();
    	final int ncols_first_half = (int)Math.round(0.55*ncols);
    	//final int ncols_second_half = ncols - ncols_first_half;
    	Mat img_first_half = new Mat(image_eq,new Range(0,nrows),new Range(0,ncols_first_half));
    	Mat img_second_half=new Mat(image_eq,new Range(0,nrows),new Range(ncols_first_half,ncols));
    	
    	/*Mat img_first_half = new Mat(nrows,ncols_first_half,image_eq.type());
    	Mat img_second_half = new Mat(nrows,ncols_second_half,image_eq.type());
    	for (int i = 0; i < nrows; i++){
    		for (int j = 0; j < ncols_first_half; j++){
    			img_first_half.put(i,j,image_eq.get(i,j));
    		}
    		for(int k = 0; k<ncols_second_half; k++){
    			img_second_half.put(i,k,image_eq.get(i,ncols_first_half+k));
    		}
    	}*/
    	/*converting first half to hsv image*/
    	Mat hsv=new Mat();
    	Mat gray=new Mat();
    	Mat bw_rgb=new Mat(nrows, ncols_first_half,CvType.CV_8UC1);
    	Mat bw_b=new Mat();
    	Mat bw1=new Mat();
    	Mat bw22=new Mat();
    	Imgproc.cvtColor(img_first_half, hsv,Imgproc.COLOR_BGR2HSV);
    	List<Mat> hsvchannels = new ArrayList<Mat>();
       /* for (int i = 0; i < src.channels(); i++) {
                Mat cha = new Mat(hsv.height(),hsv.width(),CvType.CV_8UC1);
                hsvchannels.add(cha);
        }*/
        Core.split(hsv, hsvchannels);
        /*To segment RGB  stripes*/
        for(int i=0;i<nrows;i++){
        	for(int j=0;j<ncols_first_half;j++){
        	if (hsvchannels.get(1).get(i,j)[0]>127){
        		double val[]=new double[1];
        		val[0]=255;
        		bw_rgb.put(i,j,val);
        	}else{
        		double val[]=new double[1];
        		val[0]=0;
        		bw_rgb.put(i,j,val);
        	}
        	}
        }
        /*for black stripe*/
        Imgproc.cvtColor(img_first_half,gray,Imgproc.COLOR_BGR2GRAY);
        double thres_val = Imgproc.threshold(gray, bw_b, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
        Imgproc.threshold(gray, bw_b, thres_val*0.6, 255,Imgproc.THRESH_BINARY_INV );
    	Core.bitwise_or(bw_rgb, bw_b, bw1);
    	bw_rgb.release();bw_b.release();
    	/*for second half*/
    	Imgproc.cvtColor(img_second_half, gray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(gray, bw22, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
    	Mat bw2 = new Mat();
    	bw2=Mat.zeros(bw22.size(), CvType.CV_8UC1);
    	int count = 0;
    	for (int i = 0; i < bw2.cols(); i++){
    		for (int j = 0; j < bw2.rows(); j++){
    			if (bw22.get(j, i)[0] == 255){
    				count++;
    			}
    		}
    		if (count >= (int)(0.8*bw22.rows())){
    			bw2.colRange(new Range(i,i+1)).setTo(new Scalar(255));
    		}
    		count = 0;
    	}
    	Mat img1=new Mat();
    	Core.hconcat(Arrays.asList(new Mat[]{bw1,bw2}), img1);
    img1.submat(new Range(0, (int)(0.25*img1.rows())),new Range(0,img1.cols())).setTo(new Scalar(0));
    img1.submat(new Range((int)(0.75*img1.rows()),img1.rows()),new Range(0,img1.cols())).setTo(new Scalar(0));
    img1.submat(new Range(0,img1.rows()),new Range(0,(int)(0.2*img1.cols()))).setTo(new Scalar(0));
    	
    	return img1 ;
    	
	}
	

}
	