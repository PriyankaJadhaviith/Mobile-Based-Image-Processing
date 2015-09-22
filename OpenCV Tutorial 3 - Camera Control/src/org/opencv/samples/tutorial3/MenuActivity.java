package org.opencv.samples.tutorial3;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ToggleButton;
 
public class MenuActivity extends Activity {
	private static final String  TAG  = "OCVSample::Activity";
	
	public static final int      VIEW_MODE_RGBA      = 0;
	public static final int      VIEW_MODE_GRAY      = 1;
    public static final int      VIEW_MODE_PREP      = 2;
    
    public static int           viewMode = VIEW_MODE_RGBA;
    
    private MenuItem             mItemPreviewRGBA;
    private MenuItem             mItemPreviewGRAY;
    private MenuItem             mItemPreviewPrep;
 
    private static final int SELECT_PICTURE = 1;
 
    private String selectedImagePath;
    
    Button buttonOpenDialog;
    Button buttonUp;
  
    TextView textFolder;
    ImageView image;
     
    String KEY_TEXTPSS = "TEXTPSS";
    static final int CUSTOM_DIALOG_ID = 0;
    
    ListView dialog_ListView;
     
    File root;
    File curFolder;
    
    

    
    private List<String> fileList = new ArrayList<String>();
    
    
	 @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		 Log.i(TAG, "called onCreateOptionsMenu");
		 mItemPreviewRGBA=menu.add("Input Image");
		 mItemPreviewGRAY=menu.add("gray image");
		 mItemPreviewPrep=menu.add("Preprocessing");
		 
		return true;
	}
	 
	 



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		 Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
		 if (item == mItemPreviewRGBA)
	            viewMode = VIEW_MODE_RGBA;
		 else if (item == mItemPreviewGRAY)
	            viewMode = VIEW_MODE_GRAY;
		 else if (item == mItemPreviewPrep)
	            viewMode = VIEW_MODE_PREP;
		return true;
	}

    
    
    
    

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_display);
 
        image = (ImageView)findViewById(R.id.ImageView01);
     //   toggle_btn = (ToggleButton)findViewById(R.id.togglebutton);
        		
        ((Button) findViewById(R.id.pic_btn))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                    	
         				
         				  showDialog(CUSTOM_DIALOG_ID);

                    }
                    
                });
   
        
	        root = new File(Environment
	          .getExternalStorageDirectory()
	          .getAbsolutePath());
	         
	        curFolder = root;
	        
    }
    
    
    



	@Override
	 protected Dialog onCreateDialog(int id) {
	        
	  Dialog dialog = null;
	          
	  switch(id) {
	  	case CUSTOM_DIALOG_ID:
	             dialog = new Dialog(MenuActivity.this);
	             dialog.setContentView(R.layout.image_select_dialog);
	             dialog.setTitle("Select JPG File");
	              
	             dialog.setCancelable(true);
	             dialog.setCanceledOnTouchOutside(true);
	              
	             textFolder = (TextView)dialog.findViewById(R.id.folder);
	        
	             buttonUp = (Button)dialog.findViewById(R.id.up);
	             buttonUp.setOnClickListener(new OnClickListener(){
	        
	    @Override
	      public void onClick(View v) {
	             
	            ListDir(curFolder.getParentFile());
	           }});
	        
	             //Prepare ListView in dialog
	             dialog_ListView = (ListView)dialog.findViewById(R.id.dialoglist);
	        
	             dialog_ListView.setOnItemClickListener(new OnItemClickListener(){
	        
	      @Override
	      public void onItemClick(AdapterView<?> parent, View view,
	       int position, long id) {
	             
	            File selected = new File(fileList.get(position));
	            if(selected.isDirectory()){
	             ListDir(selected); 
	            }else {
	             Toast.makeText(MenuActivity.this,
	               selected.toString() + " selected",
	               Toast.LENGTH_LONG).show();
	             dismissDialog(CUSTOM_DIALOG_ID);
	            Bitmap bmp = BitmapFactory.decodeFile(selected.getAbsolutePath());
	            switch(MenuActivity.viewMode){
	            case MenuActivity.VIEW_MODE_RGBA:
	           	 image.setImageBitmap(bmp);
	           	 break;
	            case MenuActivity.VIEW_MODE_GRAY:
	            	Mat tmp0 = new Mat ();
		           	  Mat tmp1 =new Mat();
		 	         Utils.bitmapToMat(bmp,tmp0);
		 	        Imgproc.cvtColor(tmp0, tmp1, Imgproc.COLOR_RGB2GRAY);
		 	         Utils.matToBitmap(tmp1, bmp);
		 	         tmp0.release();
		 	         tmp1.release();
		              image.setImageBitmap(bmp);
		           	 break;
	            case MenuActivity.VIEW_MODE_PREP:
	            	Mat tmp2 = new Mat ();
	           	  Mat tmp3 =new Mat();
	 	         Utils.bitmapToMat(bmp,tmp2);
	 	   List<Mat> channels = new ArrayList<Mat>();
	 	   Core.split(tmp2,channels);
	 	   Mat[] rgb=new Mat[]{channels.get(0),channels.get(1),channels.get(2)};
	 	   Core.merge(Arrays.asList(rgb), tmp2);
	 	   tmp3=TSH.preprocessing(tmp2);
 	        //Imgproc.cvtColor(tmp, tmp1, Imgproc.COLOR_RGB2GRAY);
	 	   Bitmap newBitmap = Bitmap.createBitmap(tmp3.cols(),tmp3.rows(),Bitmap.Config.ARGB_8888);
	 	        Utils.matToBitmap(tmp3,newBitmap);
	 	        tmp2.release();
	 	        tmp3.release();
	              image.setImageBitmap(newBitmap);
	              break;
	            
	            }
	            
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
	       case CUSTOM_DIALOG_ID:
	             ListDir(curFolder);
	                break;
	            }
	          
	   }
	         
	  void ListDir(File f){
	        
	     if(f.equals(root)){
	          buttonUp.setEnabled(false);
	         }else{
	          buttonUp.setEnabled(true);
	         }
	          
	         curFolder = f;
	         textFolder.setText(f.getPath());
	          
	         File[] files = f.listFiles();
	         fileList.clear();
	         for (File file : files){
	        
	          if(file.isDirectory()){
	           fileList.add(file.getPath());
	          }else{
	           Uri selectedUri = Uri.fromFile(file);
	           String fileExtension
	            = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
	           if(fileExtension.equalsIgnoreCase("jpg")){
	            fileList.add(file.getPath());
	           }
	          }   
	         }
	               
	         ArrayAdapter<String> directoryList
	          = new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1, fileList);
	         dialog_ListView.setAdapter(directoryList);
	        }
	  
	

	       }
 
