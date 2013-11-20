package com.findhotel.gps;

import java.util.Iterator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSService extends Service {
	private static final String TAG = "BOOMBOOMTESTGPS";
	private LocationManager mLocationManager = null;
	private static final int LOCATION_INTERVAL = 1000;
	private static final float LOCATION_DISTANCE = 10f;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Log.e(TAG, "onCreate");

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e(TAG, "onDestroy");
		super.onDestroy();
		if (mLocationManager != null) {
			mLocationManager.removeGpsStatusListener(gpsListener);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onStartCommand");
		super.onStartCommand(intent, flags, startId);
		MyLocationListener locationListener = new MyLocationListener();
		initializeLocationManager();
		Criteria criteria = new Criteria();
		String best;
		best = mLocationManager.getBestProvider(criteria, true);
		Location location = mLocationManager.getLastKnownLocation(best);
		broadcastLocation(location);
		mLocationManager.requestLocationUpdates(best, LOCATION_INTERVAL,
				LOCATION_DISTANCE, locationListener);
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mLocationManager.addGpsStatusListener(gpsListener);
		}

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void initializeLocationManager() {
		Log.e(TAG, "initializeLocationManager");
		if (mLocationManager == null) {
			mLocationManager = (LocationManager) getApplicationContext()
					.getSystemService(Context.LOCATION_SERVICE);
		}

	}

	private void broadcastLocation(Location location) {
		//mLatitude=22.5373167,mLongitude=114.0046014

		String bestLocation = "";
		if (location != null) {
			bestLocation = location.getLatitude() + ","
					+ location.getLongitude();
		}
		Intent intent = new Intent();
		intent.putExtra("location", bestLocation);
		intent.setAction("com.hc.gps");
		sendBroadcast(intent);

	}

	private Location findBestLocation(Location location) {
		Location networkLocation, gpsLocation, bestLocation;
		networkLocation = mLocationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		gpsLocation = mLocationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location == null && gpsLocation == null) {
			bestLocation = networkLocation;

		} else {

		}
		return null;

	}

	private GpsStatus.Listener gpsListener = new GpsStatus.Listener() {

		public void onGpsStatusChanged(int event) {
			switch (event) {
			case GpsStatus.GPS_EVENT_STARTED:
				Log.i(TAG, "定位启动");
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.i(TAG, "定位结束");
				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				// 获取当前状态
				GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
				// 获取卫星颗数的默认最大值
				int maxSatellites = gpsStatus.getMaxSatellites();
				// 创建一个迭代器保存所有卫星
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
						.iterator();
				int count = 0;
				while (iters.hasNext() && count <= maxSatellites) {
					GpsSatellite s = iters.next();
					count++;
				}
				Log.i(TAG, "搜索到：" + count + "颗卫星");
				if (count > 1) {
					Location gpsLocation = mLocationManager
							.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (gpsLocation != null) {
						broadcastLocation(gpsLocation);
					}
				}

				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Log.i(TAG, "第一次定位");
				break;
			}

		}

	};

	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onLocationChanged: " + location);
			broadcastLocation(location);
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onProviderDisabled: " + provider);
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onProviderEnabled: " + provider);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			case LocationProvider.AVAILABLE:
				Log.i(TAG, "当前:" + provider + " 状态为可见状态");
				break;
			case LocationProvider.OUT_OF_SERVICE:
				Log.i(TAG, "当前" + provider + "状态为服务区外状态");
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.i(TAG, "当前" + provider + "状态为暂停服务状态");
			}

		}

	}

}
