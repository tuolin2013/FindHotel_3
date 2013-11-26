package com.findhotel.activity;

import static com.findhotel.constant.Constant.WEB_SERVER_URL;
import static com.findhotel.constant.Constant.DEBUGGER;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDetails_State_ConfirmRoomActivity extends SherlockActivity {

	SlidingMenu menu;
	ImageView stateImage;
	TextView nameText, areaText, roomTypeText, checkDateText, roomNumText, guestText, contactText, mobileText, orderTotlalText,
			orderDiscountText, orderCashPayText, orderDepositText;
	Button cancelButton, payButton;
	ProgressDialog progressDialog;
	ExecutorService executorService = Executors.newCachedThreadPool();
	Context mContext = OrderDetails_State_ConfirmRoomActivity.this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);

		ExitApplication.getInstance().addActivity(OrderDetails_State_ConfirmRoomActivity.this);
		setContentView(R.layout.activity_order_details_confirm_room);
		menu = new MyActionMenu(OrderDetails_State_ConfirmRoomActivity.this).initView();
		initView();
		executorService.execute(new LoadOrderRunnable());
	}

	private void initView() {

		initActionBar();
		stateImage = (ImageView) findViewById(R.id.iv_state);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.watermark_wait);
		Bitmap rotate = rotate(bitmap, 15);
		stateImage.setImageBitmap(rotate);

		nameText = (TextView) findViewById(R.id.tv_hotel_name);
		areaText = (TextView) findViewById(R.id.tv_hotel_area);
		roomTypeText = (TextView) findViewById(R.id.tv_room_type);
		checkDateText = (TextView) findViewById(R.id.tv_check_date);
		roomNumText = (TextView) findViewById(R.id.tv_room_num);
		guestText = (TextView) findViewById(R.id.tv_guest);
		contactText = (TextView) findViewById(R.id.tv_contact);
		mobileText = (TextView) findViewById(R.id.tv_mobile);
		orderTotlalText = (TextView) findViewById(R.id.tv_order_total);
		orderDiscountText = (TextView) findViewById(R.id.tv_order_discount);
		orderCashPayText = (TextView) findViewById(R.id.tv_order_cash_pay);
		orderDepositText = (TextView) findViewById(R.id.tv_order_deposit);
		cancelButton = (Button) findViewById(R.id.btn_cancel);
		payButton = (Button) findViewById(R.id.btn_pay);

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
		});
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

	class LoadOrderRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			String webUrl = WEB_SERVER_URL + "/zzd/book/v1/viewOrder1";
			AsyncHttpClient client = new AsyncHttpClient();
			client.addHeader("Authorization", "Basic MTM3OTgwNDAyMzk6ZWM4YTcxMWYtNGI0OS0xMWUzLTg3MTUtMDAxNjNlMDIxMzQz");
			final RequestParams params = new RequestParams();
			params.put("appId", "appId");
			params.put("orderId", getIntent().getStringExtra("orderId"));
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
					loadOrderHandler.obtainMessage(0, -1, -1, arg0).sendToTarget();

				}
			});
			Looper.loop();

		}

	}

	private Handler loadOrderHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			switch (msg.what) {
			case 0:
				// {"orderId":"dcf9e5e2-5640-11e3-b392-00163e021343","ghName":"银子浜客栈 ","area":"周庄","rmName":"标准双人房","startDate":"2013-11-28","endDate":"2013-11-29","days":1,"rmCnt":1,"guest":"章子怡;","contact":"13556807578","contPhone":"汪峰","totalPrice":190,"discount":0,"actPrice":171,"deposit":19,"status":"DQR","needDeposit":1}
				String result = (String) msg.obj;
				try {
					JSONObject json = new JSONObject(result);
					nameText.setText(json.getString("ghName"));
					areaText.setText(json.getString("area"));
					roomTypeText.setText(json.getString("rmName"));
					String chech_in_date = json.getString("startDate");
					String check_out_date = json.getString("endDate");
					String days = json.getString("days");
					checkDateText.setText(chech_in_date + "至" + check_out_date + "共" + days + "晚");
					roomNumText.setText(json.getString("rmCnt"));
					guestText.setText(json.getString("guest"));
					contactText.setText(json.getString("contact"));
					mobileText.setText(json.getString("contPhone"));
					orderTotlalText.setText(json.getString("totalPrice"));
					orderCashPayText.setText(json.getString("actPrice"));
					orderDiscountText.setText(json.getString("discount"));
					orderDepositText.setText(json.getString("deposit"));

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					showAlertMessage(e.getLocalizedMessage());
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		}

	};

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
			params.put("orderId", getIntent().getStringExtra("orderId"));
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
