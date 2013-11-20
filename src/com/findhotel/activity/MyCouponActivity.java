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
import com.findhotel.adapter.MyCouponsAdapter;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.MyActionMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MyCouponActivity extends SherlockActivity {
	SlidingMenu menu;
	TextView areaText, hotelText;
	ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_coupon);
		ExitApplication.getInstance().addActivity(MyCouponActivity.this);
		menu = new MyActionMenu(MyCouponActivity.this).initView();
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
		} else {
			finish();
		}
		return false;
	}

	void initView() {
		initActionBar();
		String testDatasource = "{items:[{area:周庄,coupon:[{expried:2013-10-12,cpId:xxx,status:0},{expried:2013-10-12,cpId:xxx,status:0}],ghId:aa36-ec08c5bbb27e,ghName:留香居客栈},{area:丽江,coupon:[{expried:2013-10-28,cpId:xxx,status:1}],ghId:aa36-ec08c5bbb272,ghName:山水人家客栈},{area:丽江,coupon:[{expried:2013-10-12,cpId:xxx,status:1},{expried:2013-10-12,cpId:xxx,status:1}],ghId:aa36-ec08c5bbb273,ghName:悠闲客栈}],pg:2,pgCnt:10}";
		areaText = (TextView) findViewById(R.id.tv_area);
		hotelText = (TextView) findViewById(R.id.tv_hotel);
		mListView = (ListView) findViewById(R.id.lv_my_coupons);
		try {
			JSONObject o = new JSONObject(testDatasource);
			JSONArray array = o.getJSONArray("items");
			MyCouponsAdapter adapter = new MyCouponsAdapter(MyCouponActivity.this, array);
			mListView.setAdapter(adapter);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void initActionBar() {
		String title = getResources().getString(R.string.title_activity_my_coupon);
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
