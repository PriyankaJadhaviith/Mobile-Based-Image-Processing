package org.opencv.samples.tutorial3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point3;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.Highgui;
import org.opencv.ml.CvSVM;
import org.opencv.ml.CvSVMParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ccsvmActivity extends Activity {
	private static final String TAG ="ccSVM::Launcher_activity";
	private static final int SELECT_PICTURE = 1;
	private Button nocc,withcc,svm_nocc,svm_withcc, selectimg;
	private ImageView image;
	private File filetsh;
	private File file0level;
	private File file25level;
	private File file100level;
	private File file250level;
	private File[] listfile25;
	private File[] listfile100;
	private File[] listfile250;
	private String[] FilePathStrings;
	private String[] FileNameStrings;
	private int l_25,l_100,l_250,var1,var2;
	private String sFileName1="colorvalues_nocc.txt";
	private String sFileName2="colorvalues_withcc.txt";
	private File fileCV1,fileCV2;
	private TextView tv;
	private String testFile;
	///--------------select image variables-----------------/////
	 Button buttonOpenDialog1;
	 Button buttonUp1;
	 TextView textFolder1;
	 //ImageView image1;
	 File selected1;
	 String KEY_TEXTPSS = "TEXTPSS";
	 static final int CUSTOM_DIALOG_ID1 = 0;
	  
	 ListView dialog_ListView1;
	  
	 File root1;
	 File curFolder1;
	  
	 private List<String> fileList1 = new ArrayList<String>();
	 //////-------------------------------------///////////

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 Log.i(TAG, "Called onCreate");
		Toast.makeText(getApplicationContext(), "OnCreateCalled", Toast.LENGTH_SHORT).show();
		setContentView(R.layout.ccsvm_layout);
		//check for SD card
		if(!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)){
			Toast.makeText(this,"Error! No SDCARD Found",Toast.LENGTH_LONG).show();
		}else {
			// Locate the image folder in your SD Card
			filetsh = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "TSH_Images");
			// Create a new folder if no folder named SDImageTutorial exist
			filetsh.mkdirs();filetsh.setExecutable(true);filetsh.setReadable(true);filetsh.setWritable(true);
		}
		
		if (filetsh.isDirectory()) {
			file0level=new File(filetsh.getAbsolutePath()+File.separator+"level_0"); file0level.mkdirs();
			file0level.setExecutable(true);file0level.setReadable(true);file0level.setWritable(true);
		    file25level=new File(filetsh.getAbsolutePath()+File.separator+"level_25"); file25level.mkdirs();
		    file25level.setExecutable(true);file25level.setReadable(true);file25level.setWritable(true);
		    file100level=new File(filetsh.getAbsolutePath()+File.separator+"level_100"); file100level.mkdirs();
		    file100level.setExecutable(true);file100level.setReadable(true);file100level.setWritable(true);
		    file250level=new File(filetsh.getAbsolutePath()+File.separator+"level_250"); file250level.mkdirs();
		    file250level.setExecutable(true);file250level.setReadable(true);file250level.setWritable(true);
		    if(file25level.isDirectory()){listfile25=file25level.listFiles();}
		    if(file100level.isDirectory()){listfile100=file100level.listFiles();}
		    if(file25level.isDirectory()){listfile250=file250level.listFiles();}
		    l_25=listfile25.length;    l_100=listfile100.length;  l_250=listfile250.length;
		    var1=l_25+l_100; var2=l_25+l_100+l_250;
			// Create a String array for FilePathStrings
			FilePathStrings = new String[var2];
			// Create a String array for FileNameStrings
			FileNameStrings = new String[var2];
			for (int i = 0; i <l_25; i++) {
				// Get the path of the image file
				FilePathStrings[i] = listfile25[i].getAbsolutePath();
				// Get the name image file
				FileNameStrings[i] = listfile25[i].getName();
			}
			for (int i = 0; i <l_100; i++) {
				// Get the path of the image file
				FilePathStrings[i+l_25] = listfile100[i].getAbsolutePath();
				System.out.println(FilePathStrings[i]);
				// Get the name image file
				FileNameStrings[i+l_25] = listfile100[i].getName();
			}
			for (int i = 0; i <l_250; i++) {
				// Get the path of the image file
				FilePathStrings[i+var1] = listfile250[i].getAbsolutePath();
				// Get the name image file
				FileNameStrings[i+var1] = listfile250[i].getName();
			}	
		}
		//Toast.makeText(getApplicationContext(),String.format("size is %d",var2),Toast.LENGTH_LONG).show();
		image = (ImageView)findViewById(R.id.imageView1);
		nocc=(Button)findViewById(R.id.button_nocc);
		withcc=(Button)findViewById(R.id.button_withcc);
		svm_nocc=(Button)findViewById(R.id.button_svm1);
		svm_withcc=(Button)findViewById(R.id.button_svm2);

		tv=(TextView)findViewById(R.id.svm_tv);
	
			/*-----------------------nocc button click-----------------------*/
		nocc.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<Point3> list25=new ArrayList<Point3>();
				ArrayList<Point3> list100=new ArrayList<Point3>();
				ArrayList<Point3> list250=new ArrayList<Point3>();
				Bitmap bmp = BitmapFactory.decodeFile(FilePathStrings[1]);
				//image.setImageBitmap(bmp);
				for(int i=0;i<l_25;i++){
			   Mat mRGBA=new Mat();
			  mRGBA= Highgui.imread(FilePathStrings[i]);
					ArrayList<Point3> list =new ArrayList<Point3>();
					colorConstancy.getColorValues(mRGBA,list);
					list25.addAll(list);
				}
				Collections.shuffle(list25);
				ArrayList<Point3> list25Random=new ArrayList<Point3>();
				list25Random.addAll(list25.subList(0,100));
				for(int i=0;i<l_100;i++){
					Mat mRGBA=new Mat();
							mRGBA=Highgui.imread(FilePathStrings[i]);
					ArrayList<Point3> list =new ArrayList<Point3>();
					colorConstancy.getColorValues(mRGBA,list);
					list100.addAll(list);
				}
				System.out.println(list100);
				System.out.println(list100.size());
				Collections.shuffle(list100);
				ArrayList<Point3> list100Random=new ArrayList<Point3>();
				list100Random.addAll(list100.subList(0,100));
				for(int i=0;i<l_250;i++){
					Mat mRGBA=new Mat();
					mRGBA=Highgui.imread(FilePathStrings[i]);
					ArrayList<Point3> list =new ArrayList<Point3>();
					colorConstancy.getColorValues(mRGBA,list);
					list250.addAll(list);
				}
				Collections.shuffle(list250);
				ArrayList<Point3> list250Random=new ArrayList<Point3>();
				list250Random.addAll(list250.subList(0,100));
				ArrayList<Point3> listRandom=new ArrayList<Point3>();
				listRandom.addAll(list25Random);listRandom.addAll(list100Random);listRandom.addAll(list250Random);				
				//Toast.makeText(getApplicationContext(),String.format("size is %d",list25Random.size()),Toast.LENGTH_LONG).show();
				try{
				fileCV1=new File(filetsh,sFileName1);
				 if (!fileCV1.exists())
	                {
	                    fileCV1.createNewFile();
	                }
				FileWriter fw =new FileWriter(fileCV1);
				for(int i=0;i<listRandom.size();i++){
					Point3 p=new Point3();
					p=listRandom.get(i);
					fw.write(p.x+" "+p.y+" "+p.z);
					if(i!=listRandom.size()-1){
						fw.write("\n");
					}
				}
				fw.flush();
				fw.close();
				}catch(IOException e){
				         e.printStackTrace();
				    }
				Toast.makeText(ccsvmActivity.this, "Color values successfully saved", Toast.LENGTH_LONG).show();
			}
			
		});
		
		/*-----------------------withcc button click-----------------------*/
		withcc.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<Point3> list25=new ArrayList<Point3>();
				ArrayList<Point3> list100=new ArrayList<Point3>();
				ArrayList<Point3> list250=new ArrayList<Point3>();
				Bitmap bmp = BitmapFactory.decodeFile(FilePathStrings[1]);
				//image.setImageBitmap(bmp);
				for(int i=0;i<l_25;i++){
			   Mat mRGBA1=new Mat();
			  mRGBA1= Highgui.imread(FilePathStrings[i]);
					ArrayList<Point3> list =new ArrayList<Point3>();
					Mat mRGBA=new Mat();
					mRGBA=colorConstancy.greyWorld(mRGBA1);
					colorConstancy.getColorValues(mRGBA,list);
					list25.addAll(list);
				}
				Collections.shuffle(list25);
				ArrayList<Point3> list25Random=new ArrayList<Point3>();
				list25Random.addAll(list25.subList(0,100));
				for(int i=0;i<l_100;i++){
					Mat mRGBA=new Mat();
							mRGBA=Highgui.imread(FilePathStrings[i]);
					ArrayList<Point3> list =new ArrayList<Point3>();
					colorConstancy.getColorValues(mRGBA,list);
					list100.addAll(list);
				}
				System.out.println(list100);
				System.out.println(list100.size());
				Collections.shuffle(list100);
				ArrayList<Point3> list100Random=new ArrayList<Point3>();
				list100Random.addAll(list100.subList(0,100));
				for(int i=0;i<l_250;i++){
					Mat mRGBA=new Mat();
					mRGBA=Highgui.imread(FilePathStrings[i]);
					ArrayList<Point3> list =new ArrayList<Point3>();
					colorConstancy.getColorValues(mRGBA,list);
					list250.addAll(list);
				}
				Collections.shuffle(list250);
				ArrayList<Point3> list250Random=new ArrayList<Point3>();
				list250Random.addAll(list250.subList(0,100));
				ArrayList<Point3> listRandom=new ArrayList<Point3>();
				listRandom.addAll(list25Random);listRandom.addAll(list100Random);listRandom.addAll(list250Random);				
				//Toast.makeText(getApplicationContext(),String.format("size is %d",list25Random.size()),Toast.LENGTH_LONG).show();
				try{
				fileCV2=new File(filetsh,sFileName2);
				 if (!fileCV2.exists())
	                {
	                    fileCV2.createNewFile();
	                }
				FileWriter fw =new FileWriter(fileCV2);
				for(int i=0;i<listRandom.size();i++){
					Point3 p=new Point3();
					p=listRandom.get(i);
					fw.write(p.x+" "+p.y+" "+p.z);
					if(i!=listRandom.size()-1){
						fw.write("\n");
					}
				}
				fw.flush();
				fw.close();
				}catch(IOException e){
				         e.printStackTrace();
				    }
				Toast.makeText(ccsvmActivity.this, "Color values successfully saved", Toast.LENGTH_LONG).show();
			}
		});
		
 		/*---------svm_nocc clicked------*/
	svm_nocc.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Mat testimage=Highgui.imread(FilePathStrings[23]);
		
			Mat testimage=Highgui.imread(selected1.getAbsolutePath());
			
			 // Set up training data
	        int[] labels=new int[300];
	        for(int i=0; i<300; i++){
	        	if(i<100)
	        		labels[i]=-1;
	        	if(i>=100 && i<200)
	        		labels[i]=0;
	        	if(i>=200 && i<300)
	        		labels[i]=1;
	        }

			//Converting to Mat
	        Mat labelsMat=new Mat(300, 1, CvType.CV_32FC1);
	        for(int i=0;i<300;i++){
        		labelsMat.put(i, 0, labels[i]);
	        }
			
			//Training data 
			  Mat trainingDataMat=new Mat(300,3, CvType.CV_32FC1);
			
			try{
				Scanner inFile = new Scanner(new File(filetsh,sFileName1));
				int i=0;
			while(inFile.hasNextLine()){
			String[] s=inFile.nextLine().split(" ");
			  trainingDataMat.put(i,0,Float.parseFloat(s[0]));
			  trainingDataMat.put(i,1,Float.parseFloat(s[1]));
			  trainingDataMat.put(i,2,Float.parseFloat(s[2]));
			  i++;
			}
			  inFile.close();
			  System.out.println(i);
			
			}
			catch(IOException e){
				e.printStackTrace();  
			}
			 // Set up SVM's parameters	        
	        CvSVMParams params = new CvSVMParams();
	        params.set_svm_type(CvSVM.C_SVC);
	        params.set_kernel_type(CvSVM.LINEAR);
	        params.set_term_crit(new TermCriteria(TermCriteria.EPS, 100, 1e-6)); 
	     // Train the SVM
	        CvSVM SVM = new CvSVM();
	        SVM.train(trainingDataMat, labelsMat, new Mat(), new Mat(), params);
	        //test ArrayList
	        ArrayList<Point3>testlist=new ArrayList<Point3>();
	        colorConstancy.getColorValues(testimage,testlist);
	        if(testlist.size()!=0){
	      //Predicting
	        double count25=0,count100=0,count250=0;
	        Mat sample=new Mat(1,3,CvType.CV_32FC1);
	        float response=0;
	        for(int i=0; i<testlist.size(); i++){
	        	sample.put(0,0,testlist.get(i).x);
	            sample.put(0,1,testlist.get(i).y);
	            sample.put(0,2,testlist.get(i).z);
	        	response = SVM.predict(sample);
	        	if(response==-1)
	        		count25++;
	        	if(response==0)
	        		count100++;
	        	if(response==1)
	        		count250++;
	        	String s=String.valueOf(response);
	        	Log.i("sur",s);
	        }
	        if(count25>count100){
	        	if(count25>count250)
	        	tv.setText(String.format("25 concentration"));
	        	else
	        	tv.setText(String.format("250 concentration"));
	        }
	        else{
	        	if(count100>count250/2)
	        	tv.setText(String.format("100 concentration"));
	        	else
	        	tv.setText(String.format("250 concentration"));
	        }
	        }else{
	        	tv.setText(String.format("0 Concentration"));	
	        }
		}
	});
	/*---------svm_withcc clicked------*/
	svm_withcc.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Mat testimage=Highgui.imread(FilePathStrings[23]);
		
			Mat testimage1=Highgui.imread(selected1.getAbsolutePath());
			Mat testimage =new Mat();
			testimage=colorConstancy.greyWorld(testimage1);
			
			
			 // Set up training data
	        int[] labels=new int[300];
	        for(int i=0; i<300; i++){
	        	if(i<100)
	        		labels[i]=-1;
	        	if(i>=100 && i<200)
	        		labels[i]=0;
	        	if(i>=200 && i<300)
	        		labels[i]=1;
	        }

			//Converting to Mat
	        Mat labelsMat=new Mat(300, 1, CvType.CV_32FC1);
	        for(int i=0;i<300;i++){
        		labelsMat.put(i, 0, labels[i]);
	        }
			
			//Training data 
			  Mat trainingDataMat=new Mat(300,3, CvType.CV_32FC1);
			
			try{
				Scanner inFile = new Scanner(new File(filetsh,sFileName2));
				int i=0;
			while(inFile.hasNextLine()){
			String[] s=inFile.nextLine().split(" ");
			  trainingDataMat.put(i,0,Float.parseFloat(s[0]));
			  trainingDataMat.put(i,1,Float.parseFloat(s[1]));
			  trainingDataMat.put(i,2,Float.parseFloat(s[2]));
			  i++;
			}
			  inFile.close();
			  System.out.println(i);
			
			}
			catch(IOException e){
				e.printStackTrace();  
			}
			 // Set up SVM's parameters	        
	        CvSVMParams params = new CvSVMParams();
	        params.set_svm_type(CvSVM.C_SVC);
	        params.set_kernel_type(CvSVM.LINEAR);
	        params.set_term_crit(new TermCriteria(TermCriteria.EPS, 100, 1e-6)); 
	     // Train the SVM
	        CvSVM SVM = new CvSVM();
	        SVM.train(trainingDataMat, labelsMat, new Mat(), new Mat(), params);
	        //test ArrayList
	        ArrayList<Point3>testlist=new ArrayList<Point3>();
	        colorConstancy.getColorValues(testimage,testlist);
	        if(testlist.size()!=0){
	      //Predicting
	        double count25=0,count100=0,count250=0;
	        Mat sample=new Mat(1,3,CvType.CV_32FC1);
	        float response=0;
	        for(int i=0; i<testlist.size(); i++){
	        	sample.put(0,0,testlist.get(i).x);
	            sample.put(0,1,testlist.get(i).y);
	            sample.put(0,2,testlist.get(i).z);
	        	response = SVM.predict(sample);
	        	if(response==-1)
	        		count25++;
	        	if(response==0)
	        		count100++;
	        	if(response==1)
	        		count250++;
	        	String s=String.valueOf(response);
	        	Log.i("sur",s);
	        }
	        if(count25>count100){
	        	if(count25>count250)
	        	tv.setText(String.format("25 concentration"));
	        	else
	        	tv.setText(String.format("250 concentration"));
	        }
	        else{
	        	if(count100>count250/2)
	        	tv.setText(String.format("100 concentration"));
	        	else
	        	tv.setText(String.format("250 concentration"));
	        }
	        }else{
	        	tv.setText(String.format("0 Concentration"));	
	        }
		}
	});
	
	
	/*------------------------------------select image file--------------------------------------*/

	selectimg = (Button)findViewById(R.id.button_selectimg);
	selectimg.setOnClickListener(new Button.OnClickListener(){

@Override
public void onClick(View arg0) {
showDialog(CUSTOM_DIALOG_ID1);
}});

    root1 = new File(Environment
      .getExternalStorageDirectory()
      .getAbsolutePath());
     
    curFolder1 = root1;
	}
	
	 @Override
	 protected Dialog onCreateDialog(int id) {
	 
	  Dialog dialog = null;
	   
	  switch(id) {
	     case CUSTOM_DIALOG_ID1:
	      dialog = new Dialog(ccsvmActivity.this);
	      dialog.setContentView(R.layout.image_select_dialog);
	      dialog.setTitle("Select JPG");
	       
	      dialog.setCancelable(true);
	      dialog.setCanceledOnTouchOutside(true);
	       
	      textFolder1 = (TextView)dialog.findViewById(R.id.folder);
	 
	      buttonUp1 = (Button)dialog.findViewById(R.id.up);
	      buttonUp1.setOnClickListener(new OnClickListener(){
	 
	    @Override
	    public void onClick(View v) {
	      
	     ListDir1(curFolder1.getParentFile());
	    }});
	 
	      //Prepare ListView in dialog
	      dialog_ListView1 = (ListView)dialog.findViewById(R.id.dialoglist);
	 
	      dialog_ListView1.setOnItemClickListener(new OnItemClickListener(){
	 
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view,
	      int position, long id) {
	      
	     selected1 = new File(fileList1.get(position));
	     if(selected1.isDirectory()){
	      ListDir1(selected1); 
	     }else {
	      Toast.makeText(ccsvmActivity.this,
	        selected1.toString() + " selected1",
	        Toast.LENGTH_LONG).show();
	      dismissDialog(CUSTOM_DIALOG_ID1);
	       
	      Bitmap bm = BitmapFactory.decodeFile(selected1.getAbsolutePath());
	            image.setImageBitmap(bm);
	            tv.setText(String.format("Image selected"));
	        }
	      
	    }});
	       
	         break;
	     }
	 
	  return dialog;
	 }
	 @Override
	 protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
	  // TODO Auto-generated method stub
	  super.onPrepareDialog(id, dialog, bundle);
	 
	  switch(id) {
	     case CUSTOM_DIALOG_ID1:
	      ListDir1(curFolder1);
	         break;
	     }
	   
	 }
	  
	 void ListDir1(File f){
	 
	  if(f.equals(root1)){
	   buttonUp1.setEnabled(false);
	  }else{
	   buttonUp1.setEnabled(true);
	  }
	   
	  curFolder1 = f;
	  textFolder1.setText(f.getPath());
	   
	  File[] files = f.listFiles();
	  fileList1.clear();
	  for (File file : files){
	 
	   if(file.isDirectory()){
	    fileList1.add(file.getPath());
	   }else{
	    Uri selectedUri = Uri.fromFile(file);
	    String fileExtension
	     = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
	    if(fileExtension.equalsIgnoreCase("jpg")){
	     fileList1.add(file.getPath());
	    }
	   }   
	  }
	        
	  ArrayAdapter<String> directoryList
	   = new ArrayAdapter<String>(this,
	     android.R.layout.simple_list_item_1, fileList1);
	  dialog_ListView1.setAdapter(directoryList);
	 }
	 //////////////////////////////////
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
	}
    private void selectImage() { 
    	Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                  testFile = getPath(selectedImageUri);
                  Bitmap bmp = BitmapFactory.decodeFile(testFile);
      			image.setImageBitmap(bmp);     
            }
        }
    }

    public String getPath(Uri uri) {

            if( uri == null ) {
                return null;
            }

            // this will only work for images selected from gallery
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            if( cursor != null ){
                int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }

            return uri.getPath();
    }
	
	
}
