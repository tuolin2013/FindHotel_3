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
import com.findhotel.adapter.HotelExperienceAdapter;
import com.findhotel.adapter.WhyAdapter;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.ListViewUtility;
import com.findhotel.util.MyActionMenu;
import com.findhotel.widget.MyGridView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HotelDetailsActivity extends SherlockActivity {
	SlidingMenu menu;
	JSONObject hotel;
	TextView introductText, serviceText, trafficText;
	Button telButton;
	MyGridView experienceGridView;
	ListView whyListView;
	Context mContext = HotelDetailsActivity.this;
	ExecutorService executorService = Executors.newCachedThreadPool();
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(HotelDetailsActivity.this);
		setContentView(R.layout.activity_hotel_details);
		initView();
		menu = new MyActionMenu(HotelDetailsActivity.this).initView();
		executorService.execute(new loadDataRunnableClass());
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
		introductText = (TextView) findViewById(R.id.tv_introduction);
		serviceText = (TextView) findViewById(R.id.tv_add_service);
		trafficText = (TextView) findViewById(R.id.tv_traffic);
		experienceGridView = (MyGridView) findViewById(R.id.gv_experience);
		whyListView = (ListView) findViewById(R.id.lv_why);
		telButton = (Button) findViewById(R.id.btn_tel);
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

	class loadDataRunnableClass implements Runnable {

		@Override
		public void run() {
			Looper.prepare();
			String webUrl = WEB_SERVER_URL + "/zzd/hotel/v1/moreInfo";
			AsyncHttpClient client = new AsyncHttpClient();
			client.addHeader("Authorization", "Basic MTM3OTgwNDAyMzk6ZWM4YTcxMWYtNGI0OS0xMWUzLTg3MTUtMDAxNjNlMDIxMzQz");
			final RequestParams params = new RequestParams();
			params.put("appId", "appId");
			if (hotel != null) {
				try {
					String ghId = hotel.getString("ghId");
					params.put("ghId", ghId);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			client.post(mContext, webUrl, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					progressDialog.dismiss();

					if (DEBUGGER) {
						Toast.makeText(mContext, arg1, Toast.LENGTH_LONG).show();
					}
					showAlertMessage(arg1);
				}

				@Override
				public void onStart() {
					if (DEBUGGER) {
						Toast.makeText(mContext, params.toString(), Toast.LENGTH_LONG).show();
					}
					progressDialog = ProgressDialog.show(mContext, null, "正在加载，请稍候...", true, false);
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
			progressDialog.dismiss();
			switch (msg.what) {
			case 0:
				String result = (String) msg.obj;
				try {
					JSONObject json = new JSONObject(result);
					introductText.setText(json.getString("introd"));
					serviceText.setText(json.getString("moreService"));
					trafficText.setText(json.getString("trans"));

					JSONArray experience = json.getJSONArray("label");
					HotelExperienceAdapter experienceAdapter = new HotelExperienceAdapter(mContext, experience);
					experienceGridView.setAdapter(experienceAdapter);

					JSONArray why = json.getJSONArray("who");
					WhyAdapter whyAdapter = new WhyAdapter(mContext, why);
					whyListView.setAdapter(whyAdapter);
					ListViewUtility.setListViewHeightBasedOnChildren(whyListView);

					final String phone = "tel:" + json.getString("custPhone");
					telButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(Intent.ACTION_DIAL);
							intent.setData(Uri.parse(phone));
							startActivity(intent);

						}
					});

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
