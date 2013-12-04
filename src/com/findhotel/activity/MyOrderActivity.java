package com.findhotel.activity;

import static com.findhotel.constant.Constant.DEBUGGER;
import static com.findhotel.constant.Constant.WEB_SERVER_URL;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateUtils;
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
import com.findhotel.activity.CheckInInfoActivity.ExchangeCouponRunnable;
import com.findhotel.adapter.MyOrderAdapter;
import com.findhotel.entity.ExchangeCouponPostParameter;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.MyActionMenu;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MyOrderActivity extends SherlockActivity {
	SlidingMenu menu;
	Button bookingBtn, waitingBtn, stayedBtn;
	PullToRefreshListView mPullToRefreshListView;

	List<Button> buttons = new ArrayList<Button>();
	Context mContext = MyOrderActivity.this;
	ExecutorService executorService = Executors.newCachedThreadPool();
	ProgressDialog progressDialog;
	String orderType = "QRZ";
	int page_no = 1;
	int page_count = 0;
	public final static int REQUST_ORDER_DETAILS = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(MyOrderActivity.this);
		setContentView(R.layout.activity_my_order);
		menu = new MyActionMenu(MyOrderActivity.this).initView();
		initView();
		executorService.execute(new LoadOrderRunnable(orderType, "1"));

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		case REQUST_ORDER_DETAILS:
			executorService.execute(new LoadOrderRunnable(orderType, "1"));
			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	void initView() {
		initActionBar();
		bookingBtn = (Button) findViewById(R.id.btn_booking);
		waitingBtn = (Button) findViewById(R.id.btn_waiting);
		stayedBtn = (Button) findViewById(R.id.btn_stayed);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview_order);
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
						| DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				new LoadMoreDataTask().execute(orderType, "1");
			}
		});
		// Add an end-of-list listener
		mPullToRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				++page_no;
				if (page_count <= page_count) {
					new LoadMoreDataTask().execute(orderType, page_no + "");
				} else {
					Toast.makeText(mContext, "已经是最后一页了...", Toast.LENGTH_SHORT).show();
				}

			}
		});

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
			orderType = tag;
			Toast.makeText(MyOrderActivity.this, v.getTag().toString(), 5000).show();
			resetButtonBackground();
			v.setBackgroundResource(R.drawable.btn_bg_line);
			((Button) v).setTextColor(Color.parseColor("#FF7400"));

			executorService.execute(new LoadOrderRunnable(orderType, "1"));
			//
			// // 等待入住
			// if ("DRZ".equals(tag)) {
			// Intent intent = new Intent(MyOrderActivity.this, OrderDetails_State_WaitCheckInActivity.class);
			// startActivity(intent);
			//
			// // 已入住
			// } else if ("YRZ".equals(tag)) {
			// Intent intent = new Intent(MyOrderActivity.this, OrderDetails_State_CheckedActivity.class);
			// startActivity(intent);
			//
			// // 预订中
			// } else if ("YDZ".equals(tag)) {
			// Intent intent = new Intent(MyOrderActivity.this, OrderDetails_State_ConfirmRoomActivity.class);
			// startActivity(intent);
			//
			// }

		}
	};

	private void resetButtonBackground() {
		for (Button b : buttons) {
			b.setBackgroundResource(R.drawable.btn_bg);
			b.setTextColor(Color.parseColor("#000000"));
		}
	}

	class LoadMoreDataTask extends AsyncTask<String, String, String> {
		String result = "";

		@Override
		protected String doInBackground(String... params) {
			String type = params[0];
			String pg = params[1];
			AsyncHttpClient client = new AsyncHttpClient();
			String webUrl = WEB_SERVER_URL + "/zzd/book/v1/myOrder";
			final RequestParams params1 = new RequestParams();
			params1.put("appId", "appId");
			params1.put("status", type);
			params1.put("pg", pg);
			client.addHeader("Authorization", "Basic MTM3OTgwNDAyMzk6ZWM4YTcxMWYtNGI0OS0xMWUzLTg3MTUtMDAxNjNlMDIxMzQz");
			client.post(mContext, webUrl, params1, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					if (DEBUGGER) {
						Toast.makeText(mContext, arg1, Toast.LENGTH_LONG).show();
					}
					showAlertMessage(arg1);
				}

				@Override
				public void onStart() {
					if (DEBUGGER) {
						Toast.makeText(mContext, params1.toString(), Toast.LENGTH_LONG).show();
					}
					super.onStart();
				}

				@Override
				public void onSuccess(final String arg0) {
					if (DEBUGGER) {
						Toast.makeText(mContext, arg0, Toast.LENGTH_LONG).show();
					}
					result = arg0;

				}
			});
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				JSONObject obj = new JSONObject(result);
				JSONArray datasource = obj.getJSONArray("order");
				MyOrderAdapter adapter = new MyOrderAdapter(mContext, datasource, orderType);
				mPullToRefreshListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				mPullToRefreshListView.onRefreshComplete();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				showAlertMessage(e.getLocalizedMessage());
			}

			super.onPostExecute(result);
		}

	}

	class LoadOrderRunnable implements Runnable {
		String status;
		String pg;

		public LoadOrderRunnable(String status, String pg) {
			super();
			this.status = status;
			this.pg = pg;

		}

		@Override
		public void run() {
			Looper.prepare();
			String webUrl = WEB_SERVER_URL + "/zzd/book/v1/myOrder";
			AsyncHttpClient client = new AsyncHttpClient();
			final RequestParams params = new RequestParams();
			params.put("appId", "appId");
			params.put("status", status);
			params.put("pg", pg);
			client.addHeader("Authorization", "Basic MTM3OTgwNDAyMzk6ZWM4YTcxMWYtNGI0OS0xMWUzLTg3MTUtMDAxNjNlMDIxMzQz");
			client.post(mContext, webUrl, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					progressDialog.dismiss();
					if (DEBUGGER) {
						Toast.makeText(mContext, arg1, Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onStart() {
					progressDialog = ProgressDialog.show(mContext, null, "正在加载，请稍候...", true, false);
					if (DEBUGGER) {
						Toast.makeText(mContext, params.toString(), Toast.LENGTH_LONG).show();
					}
					super.onStart();
				}

				@Override
				public void onSuccess(final String arg0) {
					if (DEBUGGER) {
						Toast.makeText(mContext, arg0, Toast.LENGTH_LONG).show();
					}
					loadHandler.obtainMessage(0, -1, -1, arg0).sendToTarget();

				}
			});
			Looper.loop();

		}

	}

	private Handler loadHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			String result = (String) msg.obj;
			try {
				JSONObject obj = new JSONObject(result);
				page_count = obj.getInt("pgCnt");
				JSONArray datasource = obj.getJSONArray("order");
				MyOrderAdapter adapter = new MyOrderAdapter(mContext, datasource, orderType);
				mPullToRefreshListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				showAlertMessage(e.getLocalizedMessage());
			}
		}

	};

	private void showAlertMessage(String message) {
		new AlertDialog.Builder(mContext).setTitle("系统消息").setMessage(message)
				.setNegativeButton("关闭", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						// finish();

					}
				}).show();
	}
}
