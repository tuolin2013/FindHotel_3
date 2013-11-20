package com.findhotel.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.MyActionMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MyOrderActivity extends SherlockActivity {
	SlidingMenu menu;
	Button bookingBtn, waitingBtn, stayedBtn;
	ListView mlListView;

	List<Button> buttons = new ArrayList<Button>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(MyOrderActivity.this);
		setContentView(R.layout.activity_my_order);
		menu = new MyActionMenu(MyOrderActivity.this).initView();
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

	void initView() {
		initActionBar();
		bookingBtn = (Button) findViewById(R.id.btn_booking);
		waitingBtn = (Button) findViewById(R.id.btn_waiting);
		stayedBtn = (Button) findViewById(R.id.btn_stayed);
		buttons.add(bookingBtn);
		buttons.add(waitingBtn);
		buttons.add(stayedBtn);
		bookingBtn.setOnClickListener(buttonOnClickListener);
		waitingBtn.setOnClickListener(buttonOnClickListener);
		stayedBtn.setOnClickListener(buttonOnClickListener);

	}

	void initActionBar() {
		String title = getResources().getString(R.string.title_activity_my_order);
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

	OnClickListener buttonOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String tag = v.getTag().toString();
			Toast.makeText(MyOrderActivity.this, v.getTag().toString(), 5000).show();
			resetButtonBackground();
			v.setBackgroundResource(R.drawable.btn_bg_line);
			((Button) v).setTextColor(Color.parseColor("#FF7400"));

			// 等待入住
			if ("DRZ".equals(tag)) {
				Intent intent = new Intent(MyOrderActivity.this, OrderDetails_State_WaitCheckInActivity.class);
				startActivity(intent);

				// 已入住
			} else if ("YRZ".equals(tag)) {
				Intent intent = new Intent(MyOrderActivity.this, OrderDetails_State_CheckedActivity.class);
				startActivity(intent);

				// 预订中
			} else if ("YDZ".equals(tag)) {
				Intent intent = new Intent(MyOrderActivity.this, OrderDetails_State_ConfirmRoomActivity.class);
				startActivity(intent);

			}

		}
	};

	private void resetButtonBackground() {
		for (Button b : buttons) {
			b.setBackgroundResource(R.drawable.btn_bg);
			b.setTextColor(Color.parseColor("#000000"));
		}
	}

}
