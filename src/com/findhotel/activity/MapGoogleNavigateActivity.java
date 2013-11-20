package com.findhotel.activity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.Window;
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

public class MapGoogleNavigateActivity extends SherlockActivity {

	WebView webView;
	String destination = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(MapGoogleNavigateActivity.this);
		destination = getIntent().getStringExtra("destination");
		setContentView(R.layout.webview);
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

	@SuppressLint("JavascriptInterface")
	private void openBrowser() {
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		JSInterface jsInterface = new JSInterface();
		webView.addJavascriptInterface(jsInterface, "android");
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				Toast.makeText(MapGoogleNavigateActivity.this, message, Toast.LENGTH_LONG).show();
				return true;
			}
		});
		webView.loadUrl("file:///android_asset/html/map_directions.html");
	}

	class JSInterface {

		public String getOrigin() {
			GpsApi api = new GpsApi(getApplicationContext());
			Location location = api.getLocation();
			return location.getLatitude() + "," + location.getLongitude();

		}

		public String getDestination() {
			return "22.5460700000,113.9602900000";

		}

	}

}
