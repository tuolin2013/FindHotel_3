package com.findhotel.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.gps.GpsApi;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.MyActionMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MapGoogleNavigateActivity extends SherlockActivity {
	SlidingMenu menu;
	WebView webView;
	String hotel;
	Context mContext = MapGoogleNavigateActivity.this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		hotel = getIntent().getStringExtra("hotel");
		ExitApplication.getInstance().addActivity(MapGoogleNavigateActivity.this);
		setContentView(R.layout.webview);
		menu = new MyActionMenu(MapGoogleNavigateActivity.this).initView();
		initActionBar();
		openBrowser();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add("更多").setIcon(R.drawable.ic_drawer_dark)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		String selected = (String) item.getTitle();
		if ("更多".equals(selected)) {
			menu.toggle();

		} else {
			finish();
		}
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
				Toast.makeText(MapGoogleNavigateActivity.this, message, Toast.LENGTH_LONG).show();
				return true;
			}
		});
		webView.loadUrl("file:///android_asset/html/map_directions.html");
	}

	void initActionBar() {

		String title = getIntent().getStringExtra("name");
		ActionBar bar = getSupportActionBar();
		// actionBar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		bar.setIcon(R.drawable.all_return);
		bar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_action_bar_title, null);
		TextView mTextView = (TextView) mCustomView.findViewById(R.id.tv_title);
		mTextView.setText(title);
		bar.setCustomView(mCustomView, params);
		bar.setDisplayShowCustomEnabled(true);

	}

	class JSInterface {

		public String getOrigin() {
			GpsApi api = new GpsApi(getApplicationContext());
			Location location = api.getLocation();
			return location.getLatitude() + "," + location.getLongitude();

		}

		public String getDestination() {
			String location = getIntent().getStringExtra("location");
			return "22.53722881322112,113.96992027759552,";

		}

	}

}
