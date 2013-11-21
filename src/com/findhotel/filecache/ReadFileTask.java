package com.findhotel.filecache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.Callable;

public class ReadFileTask implements Callable<String> {
	String _fileName;

	public ReadFileTask(String fileName) {
		super();
		this._fileName = fileName;
	}

	@Override
	public String call() throws Exception {
		String filename = android.os.Environment.getExternalStorageDirectory() + File.separator + "com.findhotel.cache" + File.separator + _fileName;
		FileReader reader = new FileReader(filename);
		BufferedReader br = new BufferedReader(reader);
		StringBuffer sBuffer = new StringBuffer();
		String s;
		while ((s = br.readLine()) != null) {
			sBuffer.append(s);
		}
		return sBuffer.toString();
	}

}
