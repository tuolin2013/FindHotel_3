package com.findhotel.activity;

import static com.findhotel.constant.Constant.DEBUGGER;
import static com.findhotel.constant.Constant.WEB_SERVER_URL;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import com.findhotel.util.MyActionMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ExchangeCouponsActivity extends SherlockActivity {
	SlidingMenu menu;
	TextView areaText, hotelText;
	ListView mListView;
	Context mContext = ExchangeCouponsActivity.this;
	ExecutorService executorService = Executors.newCachedThreadPool();
	ProgressDialog progressDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(ExchangeCouponsActivity.this);
		setContentView(R.layout.activity_exchange_coupons);
		initView();
		menu = new MyActionMenu(ExchangeCouponsActivity.this).initView();
		executorService.execute(new LoadAllCouponsRunnable());
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

	private void initView() {
		initActionBar();
		// String testData =
		// "{area:西塘,coupon:[{ photoUrl:xxx,cnt:9,phone:131xxxxx,userId:xxxxx},{ photoUrl:xxx,cnt:8,phone:135xxxxx,userId:xxxxx},{ photoUrl:xxx,cnt:7,phone:138xxxxx,userId:xxxxx}],ghId:xxx,ghName:xx客栈}";
		areaText = (TextView) findViewById(R.id.tv_area);
		hotelText = (TextView) findViewById(R.id.tv_hotel);
		mListView = (ListView) findViewById(R.id.lv_coupons);

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

	class LoadAllCouponsRunnable implements Runnable {

		@Override
		public void run() {
			Looper.prepare();

			String webUrl = WEB_SERVER_URL + "/zzd/coupon/v1/hotelCouponList";
			final RequestParams params = new RequestParams();
			String ghId = getIntent().getStringExtra("ghId");
			params.put("appId", "appId");
			params.put("ghId", ghId);

			AsyncHttpClient client = new AsyncHttpClient();
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
					if (DEBUGGER) {
						Toast.makeText(mContext, params.toString(), Toast.LENGTH_LONG).show();
					}
					progressDialog = ProgressDialog.show(mContext, null, "正在处理，请稍候...", true, false);
					super.onStart();
				}

				@Override
				public void onSuccess(final String arg0) {
					if (DEBUGGER) {
						Toast.makeText(mContext, arg0, Toast.LENGTH_LONG).show();
					}
					loadAllHandler.obtainMessage(0, -1, -1, arg0).sendToTarget();

				}
			});
			Looper.loop();
		}

	}

	private Handler loadAllHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			switch (msg.what) {
			case 0:
				String result = (String) msg.obj;
				try {
					JSONObject object = new JSONObject(result);
					areaText.setText(object.getString("area"));
					hotelText.setText(object.getString("ghName"));
					JSONArray datasource = object.getJSONArray("coupon");
					if (datasource.length() > 0) {
						ExchangCouponsAdapter adapter = new ExchangCouponsAdapter(ExchangeCouponsActivity.this, datasource);
						mListView.setAdapter(adapter);
					} else {
						showAlertMessage("没有数据!");
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					showAlertMessage(e.getLocalizedMessage());
				}
				break;

			default:
				break;
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
						finish();

					}
				}).show();
	}
}
