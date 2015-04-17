package com.oocrr.findgo;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;

public class IMGHelper {
	private static Bitmap img;
	private static int imgWidth;
	private static int imgHeight;
	private static int[] imgPixels;
	
	private static void setImgInfo(Bitmap image) {
		img = image;
		imgWidth = img.getWidth();
		imgHeight = img.getHeight();
		imgPixels = new int[imgWidth * imgHeight];
		img.getPixels(imgPixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
	}

	/**
	 * ��ͼƬ���ɻҶ�ͼ
	 * 
	 * @param img
	 * @return
	 */
	public static Bitmap converyToGrayImg(Bitmap img) {

		setImgInfo(img);

		return getGrayImg();
	}
	
	private static Bitmap getGrayImg() {

		int alpha = 0xFF << 24;
		for (int i = 0; i < imgHeight; i++) {
			for (int j = 0; j < imgWidth; j++) {
				int grey = imgPixels[imgWidth * i + j];

				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);

				grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
				grey = alpha | (grey << 16) | (grey << 8) | grey;
				imgPixels[imgWidth * i + j] = grey;
			}
		}
		Bitmap result = Bitmap
				.createBitmap(imgWidth, imgHeight, Config.RGB_565);
		result.setPixels(imgPixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
		return result;
	}

	public static Bitmap doPretreatment(Bitmap img) {

		setImgInfo(img);

		Bitmap grayImg = getGrayImg();

		int[] p = new int[2];
		int maxGrayValue = 0, minGrayValue = 255;
		// ���������С�Ҷ�ֵ
		getMinMaxGrayValue(p);
		minGrayValue = p[0];
		maxGrayValue = p[1];
		// �����������ֵ
		int T1 = getIterationHresholdValue(minGrayValue, maxGrayValue);
		// // ��������ֵ
		// int T2 = getOtsuHresholdValue(minGrayValue, maxGrayValue);
		// // ��������ط���ֵ
		// int T3 = getMaxEntropytHresholdValue(minGrayValue, maxGrayValue);
		// int[] T = { T1, T2, T3 };
		//
		// Bitmap result = selectBinarization(T);
		Bitmap result = binarization(T1);

		return result;
	}
	
	// ��Ե�����ֵ��ֵ��ͼƬ
		private static Bitmap binarization(int T) {
			// ����ֵT1��ͼ����ж�ֵ��
			for (int i = 0; i < imgHeight; i++) {
				for (int j = 0; j < imgWidth; j++) {
					int gray = imgPixels[i * imgWidth + j];
					if (gray < T) {
						// С����ֵ��Ϊ��ɫ
						imgPixels[i * imgWidth + j] = Color.rgb(0, 0, 0);
					} else {
						// ������ֵ��Ϊ��ɫ
						imgPixels[i * imgWidth + j] = Color.rgb(255, 255, 255);
					}
				}
			}

			Bitmap result = Bitmap
					.createBitmap(imgWidth, imgHeight, Config.RGB_565);
			result.setPixels(imgPixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);

			return result;
		}

		// ���������С�Ҷ�,������������
		private static void getMinMaxGrayValue(int[] p) {
			int minGrayValue = 255;
			int maxGrayValue = 0;
			for (int i = 0; i < imgHeight - 1; i++) {
				for (int j = 0; j < imgWidth - 1; j++) {
					int gray = imgPixels[i * imgWidth + imgHeight];
					if (gray < minGrayValue)
						minGrayValue = gray;
					if (gray > maxGrayValue)
						maxGrayValue = gray;
				}
			}
			p[0] = minGrayValue;
			p[1] = maxGrayValue;
		}
		
		// ���õ�����������ֵ
		private static int getIterationHresholdValue(int minGrayValue,
				int maxGrayValue) {
			int T1;
			int T2 = (maxGrayValue + minGrayValue) / 2;
			do {
				T1 = T2;
				double s = 0, l = 0, cs = 0, cl = 0;
				for (int i = 0; i < imgHeight; i++) {
					for (int j = 0; j < imgWidth; j++) {
						int gray = imgPixels[imgWidth * i + j];
						if (gray < T1) {
							s += gray;
							cs++;
						}
						if (gray > T1) {
							l += gray;
							cl++;
						}
					}
				}
				T2 = (int) (s / cs + l / cl) / 2;
			} while (T1 != T2);
			return T1;
		}
}
