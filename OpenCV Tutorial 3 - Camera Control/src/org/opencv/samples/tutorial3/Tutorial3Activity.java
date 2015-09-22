package org.opencv.samples.tutorial3;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Tutorial3Activity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    private Tutorial3View mOpenCvCameraView;
    private List<Size> mResolutionList;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;
    private Button button, button1, button2,button3;
    private String m_chosenDir = "";
    private boolean m_newFolderEnabled = true;

    
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    Toast.makeText(getApplicationContext(), "OpenCV loaded successfully", Toast.LENGTH_LONG).show();
                    mOpenCvCameraView.enableView();
                   // mOpenCvCameraView.setOnTouchListener((OnTouchListener) Tutorial3Activity.this);
                    Toast.makeText(getApplicationContext(), "Ready to Capture images", Toast.LENGTH_LONG).show();
                   // mOpenCvCameraView.setOnClickListener(OnClickListener);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public Tutorial3Activity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
      
    	Log.i(TAG, "called onCreate");
        Toast.makeText(getApplicationContext(), "called onCreate", 
                Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.tutorial3_surface_view);

        mOpenCvCameraView = (Tutorial3View) findViewById(R.id.tutorial3_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
        ///////*********************Capturing images**************************/////////////////
        button = (Button) findViewById(R.id.capture_btn);

 		button.setOnClickListener(new OnClickListener() {

 			@Override
 			public void onClick(View view) {
 				//Toast.makeText(Tutorial3Activity.this, "Button Clicked",	Toast.LENGTH_SHORT).show();
 				 Log.i(TAG,"onTouch event");
                 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                 String currentDateandTime = sdf.format(new Date());
              
				//String fileName = Environment.getExternalStorageDirectory().getPath() +"/img" + currentDateandTime + ".jpg";
                 
		                          
              
                 
                 
                 String fileName = m_chosenDir +"/img" + currentDateandTime + ".jpg";

                 mOpenCvCameraView.takePicture(fileName);
                 Toast.makeText(Tutorial3Activity.this, fileName + " saved", Toast.LENGTH_SHORT).show();
 			}

 		});
 		
 		 ///////*********************Menu**************************/////////////////
      button1 = (Button) findViewById(R.id.menu_btn);
       
 		button1.setOnClickListener(new OnClickListener() {

 			@Override
 			public void onClick(View view) {
 				//Toast.makeText(Tutorial3Activity.this, "Button Clicked",	Toast.LENGTH_SHORT).show();
 				Intent intObj1 = new Intent(Tutorial3Activity.this,MenuActivity.class);
 				startActivity(intObj1); 
 				//finish();
 			}

 		});
 		 ///////*********************Menu**************************/////////////////
 	      button3 = (Button) findViewById(R.id.ccsvm);
 	       
 	 		button3.setOnClickListener(new OnClickListener() {

 	 			@Override
 	 			public void onClick(View view) {
 	 				//Toast.makeText(Tutorial3Activity.this, "Button Clicked",	Toast.LENGTH_SHORT).show();
 	 				Intent ccsvmintent = new Intent(Tutorial3Activity.this,ccsvmActivity.class);
 	 				startActivity(ccsvmintent); 
 	 				//finish();
 	 			}

 	 		});
 		
////////////////***********************Set Location to store captured images********************/////////////
 	      button2 = (Button) findViewById(R.id.loc);
 	     
 	 		//button2.setOnClickListener(new OnClickListener() {

 	 			//@Override
 	 			//public void onClick(View view) {
 	 				//Toast.makeText(Tutorial3Activity.this, "Button Clicked",	Toast.LENGTH_SHORT).show();
 	 				//Intent intObj2 = new Intent(Tutorial3Activity.this,DirectoryChooserActivity.class);
 	 				//startActivity(intObj2); 
 	 				//finish();
 	 				
 	 				/////////////////
 	     button2.setOnClickListener(new OnClickListener() 
 	 		        {
 	 		          //  private String m_chosenDir = "";
 	 		          //  private boolean m_newFolderEnabled = true;

 	 		            @Override
 	 		            public void onClick(View v) 
 	 		            {
 	 		                // Create DirectoryChooserDialog and register a callback 
 	 		                DirectoryChooserDialog directoryChooserDialog = new DirectoryChooserDialog(Tutorial3Activity.this,  new DirectoryChooserDialog.ChosenDirectoryListener() 
 	 		                {
 	 		                    @Override
 	 		                    public void onChosenDir(String chosenDir) 
 	 		                    {
 	 		                        m_chosenDir = chosenDir;
 	 		                        Toast.makeText(
 	 		                        		Tutorial3Activity.this, "Chosen directory: " + 
 	 		                          chosenDir, Toast.LENGTH_LONG).show();
 	 		                    }
 	 		                }); 
 	 		                // Toggle new folder button enabling
 	 		                directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
 	 		                // Load directory chooser dialog for initial 'm_chosenDir' directory.
 	 		                // The registered callback will be called upon final directory selection.
 	 		                directoryChooserDialog.chooseDirectory(m_chosenDir);
 	 		                m_newFolderEnabled = ! m_newFolderEnabled;
 	 		            }
 	 		        });
 	 			

 	 		//});
 	 		//////////////////////////////////////
        
      /*  Button mSaveButton = (Button) findViewById(R.id.capture_btn);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG,"onTouch event");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                String currentDateandTime = sdf.format(new Date());
                String fileName = Environment.getExternalStorageDirectory().getPath() +
                        "/sample_picture_" + currentDateandTime + ".jpg";
                mOpenCvCameraView.takePicture(fileName);
                Toast.makeText(Tutorial3Activity.this, fileName + " saved", Toast.LENGTH_SHORT).show();

            }
        });*/
        
   
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
       // OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);

    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	/*
    	
        List<String> effects = mOpenCvCameraView.getEffectList();

        if (effects == null) {
            Log.e(TAG, "Color effects are not supported by device!");
            
            return true;
        }

        mColorEffectsMenu = menu.addSubMenu("Color Effect");
        mEffectMenuItems = new MenuItem[effects.size()];

        int idx = 0;
        ListIterator<String> effectItr = effects.listIterator();
        while(effectItr.hasNext()) {
           String element = effectItr.next();
           mEffectMenuItems[idx] = mColorEffectsMenu.add(1, idx, Menu.NONE, element);
           idx++;
        }

        mResolutionMenu = menu.addSubMenu("Resolution");
        mResolutionList = mOpenCvCameraView.getResolutionList();
        mResolutionMenuItems = new MenuItem[mResolutionList.size()];

        ListIterator<Size> resolutionItr = mResolutionList.listIterator();
        idx = 0;
        while(resolutionItr.hasNext()) {
            Size element = resolutionItr.next();
            mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE,
                    Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
            idx++;
         }*/
        

	////////////***********************select folder to save*********************///////
     /*   button1 = (Button) findViewById(R.id.select_folder_btn);

		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				//Toast.makeText(Tutorial3Activity.this, "Button Clicked",	Toast.LENGTH_SHORT).show();
				Intent myIntent = new Intent(Tutorial3Activity.this, Select_Folder.class);
				//myIntent.putExtra("key", value); //Optional parameters
				startActivity(myIntent);
                //Toast.makeText(Tutorial3Activity.this, fileName + " saved", Toast.LENGTH_SHORT).show();

			}

		});
		
		*/
		//return true;

        return true;
    }

    /*public boolean onOptionsItemSelected(MenuItem item) {
       Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        
        if (item.getGroupId() == 1)
        {
            mOpenCvCameraView.setEffect((String) item.getTitle());
            Toast.makeText(this, mOpenCvCameraView.getEffect(), Toast.LENGTH_SHORT).show();
        }
        else if (item.getGroupId() == 2)
        {
            int id = item.getItemId();
            Size resolution = mResolutionList.get(id);
            mOpenCvCameraView.setResolution(resolution);
            resolution = mOpenCvCameraView.getResolution();
            String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
            Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
        }

        return true;
    }*/

    /* @SuppressLint("SimpleDateFormat")
     @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG,"onTouch event");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                               "/sample_picture_" + currentDateandTime + ".jpg";
        mOpenCvCameraView.takePicture(fileName);
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        return false;
    }*/
    
    
    
}
