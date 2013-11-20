package com.findhotel.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.adapter.MyPhotoAdapter;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.MyActionMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MyPhotoActivity extends SherlockActivity {
	SlidingMenu menu;
	ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(MyPhotoActivity.this);
		setContentView(R.layout.activity_my_photo);
		menu = new MyActionMenu(MyPhotoActivity.this).initView();
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
		mListView = (ListView) findViewById(R.id.lv_my_photo);
		loadTestData();
	}

	void loadTestData() {
		String testJson = "{views:[{srcId:xxx,mUrl:xxx,ghId:aa36-ec08c5bbb27e,notes:环境非常棒,favors:218,type:m},{srcId:xxx,mUrl:xxx,ghId:aa36-ec08c5bbb27e,notes:环境非常棒,favors:218,type:m},{srcId:xxx,mUrl:xxx,ghId:aa36-ec08c5bbb27e,notes:环境非常棒,favors:218,type:m}]}";

		try {
			JSONObject object = new JSONObject(testJson);
			JSONArray datasource = object.getJSONArray("views");
			MyPhotoAdapter adapter = new MyPhotoAdapter(MyPhotoActivity.this, datasource);
			mListView.setAdapter(adapter);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void initActionBar() {
		String title = getResources().getString(R.string.title_activity_my_photo);
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

}
