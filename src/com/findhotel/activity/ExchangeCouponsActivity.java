package com.findhotel.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.adapter.ExchangCouponsAdapter;
import com.findhotel.util.ExitApplication;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ExchangeCouponsActivity extends SherlockActivity {
	SlidingMenu menu;
	TextView areaText, hotelText;
	ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(ExchangeCouponsActivity.this);
		setContentView(R.layout.activity_exchange_coupons);
		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		// menu.add("更多").setIcon(R.drawable.ic_drawer_dark)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
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

	private void initView() {
		initActionBar();
		String testData = "{area:西塘,coupon:[{ photoUrl:xxx,cnt:9,phone:131xxxxx,userId:xxxxx},{ photoUrl:xxx,cnt:8,phone:135xxxxx,userId:xxxxx},{ photoUrl:xxx,cnt:7,phone:138xxxxx,userId:xxxxx}],ghId:xxx,ghName:xx客栈}";

		areaText = (TextView) findViewById(R.id.tv_area);
		hotelText = (TextView) findViewById(R.id.tv_hotel);
		mListView = (ListView) findViewById(R.id.lv_coupons);

		try {
			JSONObject object = new JSONObject(testData);
			areaText.setText(object.getString("area"));
			hotelText.setText(object.getString("ghName"));
			JSONArray datasource = object.getJSONArray("coupon");
			ExchangCouponsAdapter adapter = new ExchangCouponsAdapter(ExchangeCouponsActivity.this, datasource);
			mListView.setAdapter(adapter);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void initActionBar() {
		String title = getResources().getString(R.string.title_activity_exchange_coupons);
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
