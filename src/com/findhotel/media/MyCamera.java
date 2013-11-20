package com.findhotel.media;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;

public class MyCamera {
	public static final String SIGNATURE_PATH = "";
	String url;

	public MyCamera(String url) {
		this.url = url;
	}

	// �ж�SD���Ƿ����
	public static boolean getHaveSDcard() {
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	// �ж�SD���Ƿ����㹻�Ŀռ�
	public static boolean getAvailableSDcard(long minSizeSDcard) {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long sdCardSize = (availableBlocks * blockSize) / 1024;// KBֵ

		if (sdCardSize > minSizeSDcard) {
			return true;
		} else {
			return false;
		}
	}

	// ��SD���ϴ���һ���ļ���
	public static void createSDCardDir(String dirname) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			// ����һ���ļ��ж��󣬸�ֵΪ�ⲿ�洢����Ŀ¼
			File sdcardDir = Environment.getExternalStorageDirectory();
			// �õ�һ��·����������sdcard���ļ���·��������
			String path = sdcardDir.getPath() + "/" + dirname;
			File file = new File(path);
			if (!file.exists()) {
				// �������ڣ�����Ŀ¼��������Ӧ��������ʱ�򴴽�
				file.mkdirs();
			}
		} else {
			return;
		}
	}

	// �ж�SD�����ļ��Ƿ����
	public static boolean fileIsExists(String path) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			path = sdcardDir + "/" + path;
			File file = new File(path);
			if (file.exists()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static void saveBitmap(String path, Bitmap mBitmap) {
		File f = new File(path);
		try {
			f.createNewFile();
		} catch (IOException e) {
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Bitmap getSDBitmap(String filepath) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		opts.inSampleSize = computeSampleSize(opts, -1, 256 * 256);
		opts.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeFile(filepath, opts);
		return bm;
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * lessen the bitmap
	 * 
	 * @param src
	 *            bitmap
	 * @param destWidth
	 *            the dest bitmap width
	 * @param destHeigth
	 * @return new bitmap if successful ,oherwise null
	 */
	public static Bitmap lessenBitmap(Bitmap src, int destWidth, int destHeigth) {
		String tag = "lessenBitmap";
		if (src == null) {
			return null;
		}
		int w = src.getWidth();// Դ�ļ��Ĵ�С
		int h = src.getHeight();
		// calculate the scale - in this case = 0.4f
		float scaleWidth = ((float) destWidth) / w;// �����С����
		float scaleHeight = ((float) destHeigth) / h;// �߶���С����
		Matrix m = new Matrix();// ����
		m.postScale(scaleWidth, scaleHeight);// ���þ������
		Bitmap resizedBitmap = Bitmap.createBitmap(src, 0, 0, w, h, m, true);// ֱ�Ӱ��վ���ı�����Դ�ļ��������
		return resizedBitmap;
	}

	public static void calculate(int sleepSeconds) {
		try {
			Thread.sleep(sleepSeconds * 1000);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static String htmlEncode(String s) {
		StringBuilder sb = new StringBuilder();
		char c;
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			switch (c) {
			case '<':
				sb.append("&lt;"); //$NON-NLS-1$ 
				break;
			case '>':
				sb.append("&gt;"); //$NON-NLS-1$ 
				break;
			case '&':
				sb.append("&amp;"); //$NON-NLS-1$ 
				break;
			case '\'':
				sb.append("&apos;"); //$NON-NLS-1$ 
				break;
			case '"':
				sb.append(""); //$NON-NLS-1$ &quot;
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static boolean containsChinese(String s) {
		if (null == s || "".equals(s.trim()))
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (isChinese(s.charAt(i)))
				return true;
		}
		return false;
	}

	public static boolean isChinese(char a) {
		int v = (int) a;
		return (v >= 19968 && v <= 171941);
	}

	public static String parseBase64(Bitmap bm) {
		String base64 = "";
		try {
			// ���ͼƬ�Ŀ��
			int width = bm.getWidth();
			int height = bm.getHeight();
			// ������Ҫ�Ĵ�С

			int newWidth = 400;
			int newHeight = height * newWidth / width;
			// �������ű���
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			// ȡ����Ҫ���ŵ�matrix����
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			// �õ��µ�ͼƬ
			Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			newbm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is
																	// the
																	// bitmap
																	// object
			byte[] content = baos.toByteArray();

			// int length = inputStream.available();
			// byte[] content = new byte[length];
			// inputStream.read(content, 0, length);
			base64 = Base64.encodeToString(content, Base64.DEFAULT);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			base64 = e.getLocalizedMessage();
			e.printStackTrace();
		}
		return base64;
	}

	public static Bitmap createWatermark(Bitmap src, Bitmap watermark) {
		Bitmap newb = null;
		int w = src.getWidth();
		int h = src.getHeight();
		int ww = watermark.getWidth();
		int wh = watermark.getHeight();
		newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(src, 0, 0, null);
		cv.drawBitmap(watermark, (w - ww) / 2, (h - wh) / 2, null);// ��src�л���ˮӡ
		cv.save();// ����
		cv.restore();// �洢
		return newb;// ���ش�ˮӡ��λͼ
	}

}
