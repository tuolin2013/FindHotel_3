package com.findhotel.activity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.MyActionMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.app.ProgressDialog;
import android.content.Context;

public class HotelPhotoGalleryActivity extends SherlockActivity {
	SlidingMenu menu;
	ListView photeListView;
	Button sortButton, bookButton;
	Spinner sortSpinner;
	TextView priceText;
	ExecutorService executorService = Executors.newCachedThreadPool();
	ProgressDialog progressDialog;
	Context mContext = HotelPhotoGalleryActivity.this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(HotelPhotoGalleryActivity.this);
		setContentView(R.layout.activity_hotel_photo_gallery);
		menu = new MyActionMenu(HotelPhotoGalleryActivity.this).initView();
		initView();
	}

	private void initView() {
		initActionBar();
		sortButton = (Button) findViewById(R.id.btn_sort_by_praise);
		bookButton = (Button) findViewById(R.id.btn_book);
		photeListView = (ListView) findViewById(R.id.lv_photo);
		sortSpinner = (Spinner) findViewById(R.id.sp_sort);
		priceText = (TextView) findViewById(R.id.tv_price);

		String[] sort = { "官方推荐", "旅馆自拍", "驴友自拍" };
		ArrayAdapter<String> numAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, sort);
		numAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sortSpinner.setAdapter(numAdapter);
		sortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		bookButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
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

	void initActionBar() {
		String title = "随手拍";
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

	class LoadDataRunnable implements Runnable {
		String sort = "1";
		RequestParams params;

		public LoadDataRunnable(String sort) {
			super();
			this.sort = sort;
			params=new RequestParams();
			params.put("appId", "");
			params.put("ghId", "");
			params.put("srcId", "");
			params.put("ord", "");
			params.put("pg", "");
		}

		private Handler loadHandler = new Handler() {
		};

		@Override
		public void run() {
			// TODO Auto-generated method stub

		}

	}

}
