package com.findhotel.activity;

import static com.findhotel.constant.Constant.DEBUGGER;
import static com.findhotel.constant.Constant.WEB_SERVER_URL;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class OrderDetails_State_CheckedActivity extends SherlockActivity {

	SlidingMenu menu;
	ImageView stateImage, addressImage, phoneImage;;
	TextView nameText, areaText, roomTypeText, checkInDateText, guestText, mobileText, contactText, addressText, phoneText, totalText,
			cashText, discountText, depositText;
	Context mContext = OrderDetails_State_CheckedActivity.this;
	ExecutorService executorService = Executors.newCachedThreadPool();
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);

		ExitApplication.getInstance().addActivity(OrderDetails_State_CheckedActivity.this);
		setContentView(R.layout.activity_order_details_checked);
		menu = new MyActionMenu(OrderDetails_State_CheckedActivity.this).initView();
		initView();
		RequestParams params = new RequestParams();
		params.put("appId", "appId");
		params.put("orderId", getIntent().getStringExtra("orderId"));
		executorService.execute(new LoadDetailsRunnable(params));
	}

	private void initView() {

		initActionBar();
		stateImage = (ImageView) findViewById(R.id.iv_state);
		addressImage = (ImageView) findViewById(R.id.iv_address);
		phoneImage = (ImageView) findViewById(R.id.iv_phone);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_occ);
		Bitmap rotate = rotate(bitmap, 15);
		stateImage.setImageBitmap(rotate);
		nameText = (TextView) findViewById(R.id.tv_hotel_name);
		areaText = (TextView) findViewById(R.id.tv_hotel_area);
		roomTypeText = (TextView) findViewById(R.id.tv_room_type);
		checkInDateText = (TextView) findViewById(R.id.tv_check_in_date);
		guestText = (TextView) findViewById(R.id.tv_guest);
		mobileText = (TextView) findViewById(R.id.tv_mobile);
		contactText = (TextView) findViewById(R.id.tv_contact);
		addressText = (TextView) findViewById(R.id.tv_address);
		phoneText = (TextView) findViewById(R.id.tv_hotel_phone);
		totalText = (TextView) findViewById(R.id.tv_order_total);
		cashText = (TextView) findViewById(R.id.tv_order_cash);
		discountText = (TextView) findViewById(R.id.tv_order_discount);
		depositText = (TextView) findViewById(R.id.tv_order_deposit);
	}

	Bitmap rotate(Bitmap src, float degree) {
		// create new matrix
		Matrix matrix = new Matrix();
		// setup rotation degree
		matrix.postRotate(degree);

		// return new bitmap rotated using matrix
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
	}

	void initActionBar() {
		String title = "订单详情";
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

	class LoadDetailsRunnable implements Runnable {
		RequestParams params;

		public LoadDetailsRunnable(RequestParams params) {
			super();
			this.params = params;
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
						nameText.setText(json.getString("ghName"));
						areaText.setText(json.getString("area"));
						roomTypeText.setText(json.getString("rmName"));
						String date = json.getString("startDate") + "至" + json.getString("endDate") + "共" + json.getString("days") + "晚";
						checkInDateText.setText(date);
						guestText.setText(json.getString("guest"));
						mobileText.setText(json.getString("contPhone"));
						contactText.setText(json.getString("contact"));
						addressText.setText(json.getString("addr"));
						phoneText.setText(json.getString("hPhone"));
						totalText.setText(json.getString("totalPrice"));
						cashText.setText(json.getString("actPrice"));
						discountText.setText(json.getString("discount"));
						depositText.setText(json.getString("deposit"));

						String location = json.getString("lat") + "," + json.getString("lng");

						addressImage.setTag(location);
						addressImage.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(mContext, MapGoogleNavigateActivity.class);
								intent.putExtra("location", v.getTag().toString());
								startActivity(intent);

							}
						});

						final String phone = "tel:" + json.getString("hPhone");
						phoneImage.setOnClickListener(new OnClickListener() {

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

		@Override
		public void run() {
			Looper.prepare();
			String webUrl = WEB_SERVER_URL + "/zzd/book/v1/viewOrder3";
			AsyncHttpClient client = new AsyncHttpClient();
			client.addHeader("Authorization", "Basic MTM3OTgwNDAyMzk6ZWM4YTcxMWYtNGI0OS0xMWUzLTg3MTUtMDAxNjNlMDIxMzQz");
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

	private void showAlertMessage(String message) {
		new AlertDialog.Builder(mContext).setTitle("系统消息").setMessage(message)
				.setNegativeButton("关闭", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();
	}
}
