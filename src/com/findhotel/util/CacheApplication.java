package com.findhotel.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import com.findhotel.cache.MyCache;
import com.findhotel.constant.Constant.Config;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class CacheApplication extends Application {
	private static CacheApplication instance;
	private MyCache myCache;
	String filePath;
	List<String> keyset = new ArrayList<String>();

	public CacheApplication() {
		File cacheFile = new File(android.os.Environment.getExternalStorageDirectory(), "com.findhotel.cache");
		myCache = MyCache.get(cacheFile);
		instance = this;

	}

	public static Context getContext() {
		return instance;
	}

	public JSONObject getCache(String key) {
		return myCache.getAsJSONObject(key);

	}

	public void saveCahce(String key, JSONObject value) {
		keyset.add(key);
		myCache.put(key, value, MyCache.TIME_HOUR);

	}

	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}

		super.onCreate();

		initImageLoader(getApplicationContext());
	}

	// @Override
	// public void onTerminate() {
	// // TODO Auto-generated method stub
	// super.onTerminate();
	// myCache.clear();
	// for (String key : keyset) {
	// myCache.file(key).delete();
	// }
	//
	// }

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging() // Not necessary in common
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}
