package com.oocrr.findgo;

import java.io.File;
import java.io.FileNotFoundException;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class main_1 extends Activity {
	
	private static final int PHOTO_CAPTURE = 0x11;// 拍照
	private static final int PHOTO_RESULT = 0x12;// 结果

	private static String LANGUAGE = "chi_sim";
	private static String IMG_PATH = getSDPath() + java.io.File.separator
			+ "findgocacheimg";
	
	private static String TESS_PATH = getSDPath() + java.io.File.separator
			+ "tessdata";
	
	private static final int SHOWRESULT = 10;
	private static final int SHOWTREATEDIMG = 11;
	private static Bitmap bitmapSelected;
	private static Bitmap bitmapTreated;
	
	private static ImageView imgview;
	static EditText et;
	static String strResult = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_1);
		imgview = (ImageView)findViewById(R.id.imageView1);
		et= (EditText)findViewById(R.id.editText1);
	}

	public static Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOWRESULT:
				//if (textResult.equals(""))
					//tvResult.setText("识别失败");
				//else
					//tvResult.setText(textResult);
				break;
			case SHOWTREATEDIMG:
				//tvResult.setText("识别中......");
				//showPicture(ivTreated, bitmapTreated);
				break;
			}
			super.handleMessage(msg);
		}

	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		
		if (resultCode == Activity.RESULT_CANCELED)
		{
			return;
		}
		
		if (requestCode == PHOTO_CAPTURE) {
			//tvResult.setText("abc");
			startPhotoCrop(Uri.fromFile(new File(IMG_PATH, "temp.jpg")));
		}
		
		if (requestCode == PHOTO_RESULT) 
		{
			bitmapSelected = decodeUriAsBitmap(Uri.fromFile(new File(IMG_PATH,
					"temp_cropped.jpg")));
			//if (chPreTreat.isChecked())
				//tvResult.setText("预处理中......");
			//else
				//tvResult.setText("识别中......");
			// 显示选择的图片
			

			//bitmapTreated = IMGHelper.converyToGrayImg(bitmapSelected);
			
			imgview.setImageBitmap(bitmapSelected);
			
			strResult = doOcr(bitmapSelected, LANGUAGE);
			
			et.setText(strResult);
			// 新线程来处理识别
						/*new Thread(new Runnable() {
							@Override
							public void run() {
								
									//bitmapTreated = IMGHelper
											//.converyToGrayImg(bitmapSelected);
									Message msg = new Message();
									msg.what = SHOWTREATEDIMG;
									myHandler.sendMessage(msg);
									strResult = doOcr(bitmapSelected, LANGUAGE);
								
								Message msg2 = new Message();
								msg2.what = SHOWRESULT;
								myHandler.sendMessage(msg2);
							}

						}).start();*/
		    }   
			
			super.onActivityResult(requestCode, resultCode, data);
	}
	
		public String doOcr(Bitmap bitmap, String language) {
			TessBaseAPI baseApi = new TessBaseAPI();

			baseApi.init(getSDPath(), language);

			// 必须加此行，tess-two要求BMP必须为此配置
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

			baseApi.setImage(bitmap);

			String text = baseApi.getUTF8Text();

			baseApi.clear();
			baseApi.end();

			return text;
		}

		
		private Bitmap decodeUriAsBitmap(Uri uri) {
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream(getContentResolver()
						.openInputStream(uri));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
			return bitmap;
		}
		
	public void startPhotoCrop(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(IMG_PATH, "temp_cropped.jpg")));
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, PHOTO_RESULT);
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
	
	public void OnPhoto(View arg0) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(IMG_PATH, "temp_cropped.jpg")));
		intent.putExtra("outputFormat",
				Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, PHOTO_RESULT);
	}
	
	public void OnCamera(View arg0) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(IMG_PATH, "temp.jpg")));
		startActivityForResult(intent, PHOTO_CAPTURE);
	}
}
