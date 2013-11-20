package com.findhotel.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.MyActionMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class HotelDetailsActivity extends SherlockActivity {
	SlidingMenu menu;
	JSONObject hotel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(HotelDetailsActivity.this);
		setContentView(R.layout.activity_hotel_details);

		initView();
		menu = new MyActionMenu(HotelDetailsActivity.this).initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add("����").setIcon(R.drawable.ic_drawer_dark)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		String selected = (String) item.getTitle();
		if ("����".equals(selected)) {
			menu.toggle();

		} else {
			finish();
		}
		return false;
	}

	void initView() {
		initActionBar();
	}

	void initActionBar() {
		try {
			hotel = new JSONObject(getIntent().getStringExtra("hotel"));
			String title = hotel.getString("ghName");
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
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
