package com.findhotel.activity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.gps.GpsApi;
import com.findhotel.util.ExitApplication;

public class MapViewActivity extends SherlockActivity {

	WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(MapViewActivity.this);
		setContentView(R.layout.activity_map_view);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		openBrowser();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		// menu.add("¸ü¶à").setIcon(R.drawable.ic_drawer_dark)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		finish();
		return false;
	}

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void openBrowser() {
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		JSInterface jsInterface = new JSInterface();
		webView.addJavascriptInterface(jsInterface, "android");
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				Toast.makeText(MapViewActivity.this, message, Toast.LENGTH_LONG).show();
				return true;
			}
		});
		webView.loadUrl("file:///android_asset/html/map_location.html");
	}

	class JSInterface {

		public String getCurrentLocation() {
			GpsApi api = new GpsApi(getApplicationContext());
			Location location = api.getLocation();
			return location.getLatitude() + "," + location.getLongitude();

		}

	}
}
