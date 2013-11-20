package com.findhotel.util;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import cn.trinea.android.common.entity.CacheObject;
import cn.trinea.android.common.service.impl.PreloadDataCache;
import cn.trinea.android.common.service.impl.PreloadDataCache.OnGetDataListener;
import cn.trinea.android.common.service.impl.RemoveTypeEnterTimeFirst;

import com.findhotel.constant.Constant.Config;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class CacheApplication extends Application {
	private static CacheApplication instance;
	private PreloadDataCache<String, String> cache;
	String filePath;

	public CacheApplication() {
		cache = new PreloadDataCache<String, String>();
		instance = this;

	}

	public static Context getContext() {
		return instance;
	}

	public void setCache(String key, final String value) {
		cache.setValidTime(1000 * 60 * 60 * 60 * 24);
		cache.setOnGetDataListener(new OnGetDataListener<String, String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public CacheObject<String> onGetData(String key) {
				// TODO Auto-generated method stub
				CacheObject<String> o = new CacheObject<String>();
				o.setData(value);
				cache.put(key, o);
				return o;
			}
		});
		cache.setCacheFullRemoveType(new RemoveTypeEnterTimeFirst<String>() {
			private static final long serialVersionUID = 1L;

		});

	}

	public String getCache(String key) {
		if (cache.get(key) != null) {
			return cache.get(key).getData();
		}
		return null;

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
