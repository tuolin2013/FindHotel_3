package com.findhotel.activity;

import static com.findhotel.constant.Constant.WEB_SERVER_URL;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.trinea.android.common.service.impl.PreloadDataCache;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.findhotel.R;
import com.findhotel.adapter.ChoiceAdapter;
import com.findhotel.adapter.HotelAdapter;
import com.findhotel.util.CacheApplication;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.MyActionMenu;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HotelListActivity extends SherlockActivity {
	Context mContext = HotelListActivity.this;
	ActionBar bar;
	LayoutInflater mInflater;
	SlidingMenu menu;
	PullToRefreshListView mPullToRefreshListView;
	String category = "";
	HotelAdapter mAdapter;
	ExecutorService executorService = Executors.newCachedThreadPool();
	String action;
	private String testJson = "{pgCnt:10,pg:2,items:[{ghId:aa36-ec08c5bbb27e,ghName:留香居客栈,area:周庄,price:120,stars:828,orders:126,label:[{icon:icon01},{icon:icon02},{icon:icon03}],imgs:[{mUrl:www.zhaozhude.comimagedemon,srcId:xxx},{mUrl:www.zhaozhude.comimagedemon2,srcId:xxx},{mUrl:www.zhaozhude.comimagedemon3,srcId:xxx}],shares:[{mUrl:www.zhaozhude.comimagedemon,type:m,srcId:xxx},{mUrl:www.zhaozhude.comvediodemon,type:v,srcId:xxx},{mUrl:www.zhaozhude.comimagedemon3,type:m,srcId:xxx}],max:5,coupon:[{photoUrl:xxx,cnt:12},{photoUrl:xxx,cnt:8},{photoUrl:xxx,cnt:0}]},{ghId:aa36-ec08c5bbb27b,ghName:圆梦客栈,area:西塘,price:128,stars:900,orders:306,label:[{icon:icon01},{icon:icon02},{icon:icon03}],imgs:[{mUrl:www.zhaozhude.comimagedemon},{mUrl:www.zhaozhude.comimagedemon2},{mUrl:www.zhaozhude.comimagedemon3}],shares:[{mUrl:www.zhaozhude.comimagedemon,type:m},{mUrl:www.zhaozhude.comvediodemon,type:v},{mUrl:www.zhaozhude.comimagedemon3,type:m}],max:3,coupon:[{photoUrl:xxx,cnt:10},{photoUrl:xxx,cnt:5}]}]}";
	CacheApplication cacheApplication;
	JSONArray datasource;
	int pg_no = 1, pg_cnt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		ExitApplication.getInstance().addActivity(HotelListActivity.this);
		cacheApplication = (CacheApplication) getApplication();
		setContentView(R.layout.hotel_list);
		category = getIntent().getStringExtra("category");
		setActionbarTitle();
		menu = new MyActionMenu(HotelListActivity.this).initView();
		initView();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		action = getIntent().getStringExtra("action");
		if ("show_map".equals(action)) {
			menu.add("地图").setIcon(R.drawable.action_map)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		menu.add("更多").setIcon(R.drawable.ic_drawer_dark)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		String selected = (String) item.getTitle();
		if ("更多".equals(selected)) {
			menu.toggle();

		} else if ("地图".equals(selected)) {
			Intent intent = new Intent(HotelListActivity.this, MapViewActivity.class);
			startActivity(intent);

		} else {
			finish();
		}
		return false;
	}

	void initView() {
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview_hotel);
		loadTestData();
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
						| DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mPullToRefreshListView.onRefreshComplete();

				// Do work to refresh the list here.
				// new GetDataTask().execute();
			}
		});
		// Add an end-of-list listener
		mPullToRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				Toast.makeText(mContext, "End of List!", Toast.LENGTH_SHORT).show();

			}
		});
	}

	void loadTestData() {
		try {
			JSONObject json = new JSONObject(testJson);
			JSONArray datasource = json.getJSONArray("items");
			mAdapter = new HotelAdapter(mContext, datasource);
			mPullToRefreshListView.setAdapter(mAdapter);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// executorService.execute(new LoadRunnable());
		// if (cacheApplication.getCache(category) == null) {
		// executorService.execute(new LoadRunnable());
		// } else {
		// String cachedDatasource = cacheApplication.getCache(category);
		// try {
		// JSONObject jsObj = new JSONObject(cachedDatasource);
		// datasource = jsObj.getJSONArray("items");
		// mAdapter = new HotelAdapter(mContext, datasource);
		// mPullToRefreshListView.setAdapter(mAdapter);
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
	}

	private void setActionbarTitle() {

		String title = "";
		if ("want_go".equals(category)) {
			title = getString(R.string.title_activity_want_togo);
		} else if ("friend_go".equals(category)) {
			title = getString(R.string.title_activity_friends_go);
		} else if ("professional_go".equals(category)) {
			title = getString(R.string.title_activity_professional_go);
		} else if ("fans_camp".equals(category)) {
			title = getString(R.string.title_activity_fans_camp);
		} else if ("popularity".equals(category)) {
			title = getString(R.string.title_activity_popularity);
		} else {
			title = category;
		}

		bar = getSupportActionBar();
		bar.setTitle(title);
		// bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);

		bar.setIcon(R.drawable.all_return);
		bar.setDisplayShowTitleEnabled(false);
		mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_action_bar_title, null);
		TextView mTextView = (TextView) mCustomView.findViewById(R.id.tv_title);
		mTextView.setText(title);
		bar.setCustomView(mCustomView, params);
		bar.setDisplayShowCustomEnabled(true);

	}

	private String creatUrl() {
		String url = "";
		if ("want_go".equals(category)) {
		} else if ("friend_go".equals(category)) {
		} else if ("professional_go".equals(category)) {
			url = WEB_SERVER_URL + "/zzd/hotel/v1/popularList";
		} else if ("fans_camp".equals(category)) {
		} else if ("popularity".equals(category)) {
		} else {
		}
		return url;

	}

	private RequestParams createParams() {
		RequestParams params = new RequestParams();
		if ("want_go".equals(category)) {
		} else if ("friend_go".equals(category)) {
		} else if ("professional_go".equals(category)) {
			params.put("appId", "appid");
			params.put("ctg", getIntent().getStringExtra("ctg"));
			params.put("pg", pg_no + "");

		} else if ("fans_camp".equals(category)) {
		} else if ("popularity".equals(category)) {
		} else {
		}
		return params;

	}

	class LoadRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(HotelListActivity.this, creatUrl(), createParams(), new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					Toast.makeText(mContext, arg1, Toast.LENGTH_LONG).show();
				}

				@Override
				public void onStart() {
					super.onStart();
				}

				@Override
				public void onSuccess(final String arg0) {
					Toast.makeText(mContext, arg0, Toast.LENGTH_LONG).show();
					cacheApplication.setCache(category, arg0);
					JSONObject jsObj;
					try {
						jsObj = new JSONObject(arg0);
						// pg_cnt = jsObj.getInt("pgCnt");
						JSONArray array = jsObj.getJSONArray("items");
						myHandler.obtainMessage(0, -1, -1, array).sendToTarget();

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						myHandler.obtainMessage(1, -1, -1, e.getMessage()).sendToTarget();
					}
				}
			});
			Looper.loop();
		}
	}

	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				datasource = (JSONArray) msg.obj;
				mAdapter = new HotelAdapter(mContext, datasource);
				mPullToRefreshListView.setAdapter(mAdapter);
				break;

			case 1:
				Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_LONG).show();
				break;
			}
		}

	};
}
