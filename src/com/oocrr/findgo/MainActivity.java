package com.oocrr.findgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	
	private static final int PHOTO_CAPTURE = 0x11;// 拍照
	private static final int PHOTO_RESULT = 0x12;// 结果

	private static String LANGUAGE = "eng";
	private static String IMG_PATH = getSDPath() + java.io.File.separator
			+ "findgocacheimg";
	
	private static String TESS_PATH = getSDPath() + java.io.File.separator
			+ "tessdata";
	
	private static final int SHOWRESULT = 10;
	private static final int SHOWTREATEDIMG = 11;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		/*Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.);
		//获取这个图片的宽和高
		int width = bitmap .getWidth();
		int height = bitmap .getHeight();
		//计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) screenWidth) / width;
		float scaleHeight = ((float) screenHeight) / height;

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		//旋转图片 动作
		matrix.postRotate(45);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
		width, height,matrix,true);

		BitmapDrawable bd = new BitmapDrawable(resizedBitmap);
		bd.setTileModeXY(TileMode.CLAMP , TileMode.CLAMP );
		bd.setDither(true);

		imageView.setBackgroundDrawable(bd); */
		
		
		//临时目录
		File path = new File(IMG_PATH);
		if (!path.exists()) {
			path.mkdirs();
		}
		//ocr文件
		File path_tess = new File(TESS_PATH);
		if (!path_tess.exists()) 
		{
			if(path_tess.mkdirs())
			{
				CopyToSD(TESS_PATH + "/chi_sim.traineddata",R.raw.chi_sim);
				CopyToSD(TESS_PATH + "/eng.traineddata",R.raw.eng);
			}			
		}
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		new Handler().postDelayed(new Runnable() {
            public void run() {
                /* Create an Intent that will start the Main WordPress Activity. */
                Intent mainIntent = new Intent(MainActivity.this, main_1.class);
                MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();
            	
            }
        }, 1000); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取外存目录
		}
		return sdDir.toString();
	}
	
	void CopyToSD(String file,int id)
	{
		InputStream is = getResources().openRawResource(id);
        FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        byte[] buffer = new byte[8192];
        int count = 0;
      
        try {
			while ((count = is.read(buffer)) > 0)
			{
			    fos.write(buffer, 0, count);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
