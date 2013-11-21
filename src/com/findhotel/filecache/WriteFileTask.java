package com.findhotel.filecache;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.text.TextUtils;

public class WriteFileTask implements Runnable {
	String jsonCache;
	String _filename;

	public WriteFileTask(String jsonCache, String _filename) {
		super();
		this.jsonCache = jsonCache;
		this._filename = _filename;
	}

	@Override
	public void run() {
		File cacheDir = null;
		FileWriter writer = null;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "com.findhotel.cache");
		} else {

		}
		if (!cacheDir.exists())
			cacheDir.mkdirs();
		if (!TextUtils.isEmpty(jsonCache)) {
			String filename = cacheDir.getAbsolutePath() + File.separator + _filename;
			try {
				writer = new FileWriter(filename, true);
				writer.write(jsonCache);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

}
