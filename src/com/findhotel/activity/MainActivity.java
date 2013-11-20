package com.findhotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.MyActionMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends SherlockActivity {
	SlidingMenu menu;
	String category = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(MainActivity.this);
		setContentView(R.layout.activity_main);
		menu = new MyActionMenu(MainActivity.this).initView();
		initView();
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
		}
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (menu.isMenuShowing()) {
				menu.toggle();
				return true;
			} else {
				finish();
				return false;
			}

		}
		return false;
	}

	void initView() {

		findViewById(R.id.btn_want_go).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				category = "want_go";
				Intent intent = new Intent(MainActivity.this, WantGoActivity.class);
				intent.putExtra("category", category);
				startActivity(intent);
			}
		});

		findViewById(R.id.btn_friend_go).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				category = "friend_go";
				Intent intent = new Intent(MainActivity.this, HotelListActivity.class);
				intent.putExtra("category", category);
				startActivity(intent);
			}
		});

		findViewById(R.id.btn_professional_go).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				category = "professional_go";
				Intent intent = new Intent(MainActivity.this, RecommendActivity.class);
				intent.putExtra("category", category);
				startActivity(intent);
			}
		});

		findViewById(R.id.btn_fans_camp).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				category = "fans_camp";
				Intent intent = new Intent(MainActivity.this, HotelListActivity.class);
				intent.putExtra("category", category);
				startActivity(intent);
			}
		});

		findViewById(R.id.btn_popularity).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				category = "popularity";
				Intent intent = new Intent(MainActivity.this, HotelListActivity.class);
				intent.putExtra("category", category);
				startActivity(intent);
			}
		});
	}

}
