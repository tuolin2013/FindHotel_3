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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.activity.OrderDetails_State_ConfirmRoomActivity.CancelOrderRunnable;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.MyActionMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class OrderDetails_State_ConfirmActivity extends SherlockActivity {

	SlidingMenu menu;
	ImageView stateImage;
	TextView nameText, areaText, dateText, roomTypeText, roomNumText, guestText, contactText, mobileText, totalText, discountText,
			cashText, depositText;
	Button cancelButton;
	Context mContext = OrderDetails_State_ConfirmActivity.this;
	ProgressDialog progressDialog;
	ExecutorService executorService = Executors.newCachedThreadPool();
	String orderId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);

		ExitApplication.getInstance().addActivity(OrderDetails_State_ConfirmActivity.this);
		setContentView(R.layout.activity_order_details_confirm);
		menu = new MyActionMenu(OrderDetails_State_ConfirmActivity.this).initView();
		initView();
		loadData();
	}

	private void initView() {

		initActionBar();
		stateImage = (ImageView) findViewById(R.id.iv_state);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_ing);
		Bitmap rotate = rotate(bitmap, 15);
		stateImage.setImageBitmap(rotate);

		nameText = (TextView) findViewById(R.id.tv_hotel_name);
		areaText = (TextView) findViewById(R.id.tv_hotel_area);
		dateText = (TextView) findViewById(R.id.tv_check_date);
		roomTypeText = (TextView) findViewById(R.id.tv_room_type);
		roomNumText = (TextView) findViewById(R.id.tv_room_num);
		guestText = (TextView) findViewById(R.id.tv_guest);
		contactText = (TextView) findViewById(R.id.tv_contact);
		mobileText = (TextView) findViewById(R.id.tv_mobile);
		totalText = (TextView) findViewById(R.id.tv_total);
		discountText = (TextView) findViewById(R.id.tv_discount);
		cashText = (TextView) findViewById(R.id.tv_cash_pay);
		depositText = (TextView) findViewById(R.id.tv_deposit);

		cancelButton = (Button) findViewById(R.id.btn_cancel);

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

	void loadData() {
		String extra = getIntent().getStringExtra("data");
		if (!TextUtils.isEmpty(extra)) {
			try {
				JSONObject json = new JSONObject(extra);
				orderId = json.getString("orderId");
				nameText.setText(json.getString("ghName"));
				areaText.setText("(" + json.getString("area") + ")");
				dateText.setText(json.getString("startDate") + "至" + json.getString("endDate") + "共" + json.getString("days"));
				roomNumText.setText(json.getString("rmCnt"));
				roomTypeText.setText(json.getString("rmName"));
				guestText.setText(json.getString("guest"));
				contactText.setText(json.getString("contact"));
				mobileText.setText(json.getString("contPhone"));
				totalText.setText(json.getString("totalPrice"));
				discountText.setText(json.getString("discount"));
				cashText.setText(json.getString("actPrice"));
				depositText.setText(json.getString("deposit"));
				cancelButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!TextUtils.isEmpty(orderId)) {

							new AlertDialog.Builder(mContext).setTitle("系统提示").setMessage("您确定要取消订单吗？")
									.setNegativeButton("取消", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											dialog.dismiss();

										}
									}).setPositiveButton("确定", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											executorService.execute(new CancelOrderRunnable());
										}
									}).show();
						}

					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

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

	class CancelOrderRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			String webUrl = WEB_SERVER_URL + "/zzd/book/v1/cancelOrder";
			AsyncHttpClient client = new AsyncHttpClient();
			client.addHeader("Authorization", "Basic MTM3OTgwNDAyMzk6ZWM4YTcxMWYtNGI0OS0xMWUzLTg3MTUtMDAxNjNlMDIxMzQz");
			final RequestParams params = new RequestParams();
			params.put("appId", "appId");
			params.put("orderId", orderId);
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
					progressDialog = ProgressDialog.show(mContext, null, "正在处理，请稍候...", true, false);
					super.onStart();
				}

				@Override
				public void onSuccess(final String arg0) {
					if (DEBUGGER) {
						Toast.makeText(mContext, arg0, Toast.LENGTH_LONG).show();
					}
					cancelOrderHandler.obtainMessage(0, -1, -1, arg0).sendToTarget();

				}
			});
			Looper.loop();

		}

	}

	private Handler cancelOrderHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			progressDialog.dismiss();
			String message = "";
			switch (msg.what) {
			case 0:
				String result = (String) msg.obj;
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString("code");
					if ("200".equals(code)) {
						message = "取消订单操作成功！";
					} else {
						message = "取消订单操作失败！";
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					message = e.getLocalizedMessage();
				}
				break;

			default:
				break;
			}

			showAlertMessage(message);
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
