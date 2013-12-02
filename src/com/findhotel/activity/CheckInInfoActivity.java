package com.findhotel.activity;

import static com.findhotel.constant.Constant.WEB_SERVER_URL;
import static com.findhotel.constant.Constant.DEBUGGER;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.entity.ExchangeCouponPostParameter;
import com.findhotel.util.DateUtil;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.MyActionMenu;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class CheckInInfoActivity extends SherlockActivity {
	SlidingMenu menu;
	Button nextButton, exchangeButton;
	ImageView plusImage, minusImage;
	Spinner numSpinner;
	TextView hotelNameText, hotelAreaText, roomTypeText, dateText;// hotel information
	TextView orderTotalText, discountText, cashpayText, depositText, noteText;// order
	EditText couponText, claim_moreText, contactText, mobileText;
	Context mContext = CheckInInfoActivity.this;
	List<EditText> editTexts;
	TableLayout tableLayout;
	JSONObject hotel, room;
	String check_in_day, check_out_day;
	String response = "", orderId = "";
	int rmCnt = 0;
	int availableCoupon, usedCoupon;
	ExecutorService executorService = Executors.newCachedThreadPool();
	ProgressDialog progressDialog;
	public static final int REQUEST_COUPON_CODE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(CheckInInfoActivity.this);
		setContentView(R.layout.activity_check_in_info);
		menu = new MyActionMenu(CheckInInfoActivity.this).initView();
		initView();
		displayHotelInfo();
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
		case REQUEST_COUPON_CODE:
			String json = data.getStringExtra("extra");
			Gson gson = new Gson();
			ExchangeCouponPostParameter parameter = gson.fromJson(json, ExchangeCouponPostParameter.class);
			try {
				JSONObject obj = new JSONObject(getIntent().getStringExtra("hotel"));
				parameter.setExchGhId(obj.getString("ghId"));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			executorService.execute(new ExchangeCouponRunnable(parameter));

			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	void initView() {
		plusImage = (ImageView) findViewById(R.id.iv_plus);
		minusImage = (ImageView) findViewById(R.id.iv_minus);

		hotelNameText = (TextView) findViewById(R.id.tv_hotel_name);
		hotelAreaText = (TextView) findViewById(R.id.tv_hotel_area);
		roomTypeText = (TextView) findViewById(R.id.tv_room_type);
		dateText = (TextView) findViewById(R.id.tv_in_date);

		orderTotalText = (TextView) findViewById(R.id.tv_order_total);
		discountText = (TextView) findViewById(R.id.tv_order_discount);
		cashpayText = (TextView) findViewById(R.id.tv_order_cash_pay);
		depositText = (TextView) findViewById(R.id.tv_order_deposit);
		noteText = (TextView) findViewById(R.id.tv_note);

		couponText = (EditText) findViewById(R.id.etv_coupon);
		claim_moreText = (EditText) findViewById(R.id.etv_claim_more);
		contactText = (EditText) findViewById(R.id.etv_contact);
		mobileText = (EditText) findViewById(R.id.etv_mobile);

		initActionBar();
		exchangeButton = (Button) findViewById(R.id.btn_exchange);
		exchangeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CheckInInfoActivity.this, ExchangeCouponsActivity.class);
				JSONObject obj;
				try {
					obj = new JSONObject(getIntent().getStringExtra("hotel"));
					intent.putExtra("ghId", obj.getString("ghId"));
					startActivityForResult(intent, REQUEST_COUPON_CODE);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		tableLayout = (TableLayout) findViewById(R.id.tab_check_in);
		nextButton = (Button) findViewById(R.id.btn_next);

		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RequestParams params = new RequestParams();
				try {
					JSONObject obj = new JSONObject(getIntent().getStringExtra("hotel"));
					JSONObject room = new JSONObject(getIntent().getStringExtra("room"));
					params.put("appId", "appid");
					params.put("ghId", obj.getString("ghId"));
					params.put("ghName", obj.getString("ghName"));
					params.put("area", obj.getString("area"));
					params.put("rmId", getIntent().getStringExtra("rmId"));
					params.put("rmName", room.getString("rmName"));
					params.put("startDate", getIntent().getStringExtra("check_in_day"));
					params.put("endDate", getIntent().getStringExtra("check_out_day"));
					params.put("rmCnt", rmCnt + "");
					params.put("guest", getGuest());
					params.put("contact", contactText.getText().toString());
					params.put("contPhone", mobileText.getText().toString());
					params.put("reqMore", claim_moreText.getText().toString());
					// useCoupons
					params.put("useCoupons", usedCoupon + "");
					params.put("totalPrice", orderTotalText.getText().toString());
					params.put("discount", discountText.getText().toString());
					params.put("actPrice", cashpayText.getText().toString());
					params.put("deposit", depositText.getText().toString());
					String p = params.toString();

					executorService.execute(new SaveOrderRunnable(params));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		addRow(1);
		numSpinner = (Spinner) findViewById(R.id.sp_room_num);
		String[] nums = { "1", "2", "3", "4", "5" };
		ArrayAdapter<String> numAdapter = new ArrayAdapter<String>(CheckInInfoActivity.this, android.R.layout.simple_spinner_item, nums);
		numAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		numSpinner.setAdapter(numAdapter);
		numSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				addRow(arg2 + 1);
				rmCnt = arg2 + 1;
				refreshOrder(rmCnt);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		// load default data.
		RequestParams params = new RequestParams();

		params.put("appId", "appId");
		try {
			hotel = new JSONObject(getIntent().getStringExtra("hotel"));
			params.put("ghId", hotel.getString("ghId"));
			params.put("rmId", getIntent().getStringExtra("rmId"));
			params.put("startDate", getIntent().getStringExtra("check_in_day"));
			params.put("endDate", getIntent().getStringExtra("check_out_day"));
			params.put("rmCnt", rmCnt + "");
			params.put("useCoupons", "0");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		executorService.execute(new CalculateOrderRunnable(params));

	}

	void initActionBar() {
		String title = getResources().getString(R.string.title_activity_check_in_info);
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

	void displayHotelInfo() {
		try {
			hotel = new JSONObject(getIntent().getStringExtra("hotel"));
			room = new JSONObject(getIntent().getStringExtra("room"));
			hotelNameText.setText(hotel.getString("ghName"));
			hotelAreaText.setText("(" + hotel.getString("area") + ")");
			roomTypeText.setText(room.getString("rmName"));
			check_in_day = getIntent().getStringExtra("check_in_day");
			check_out_day = getIntent().getStringExtra("check_out_day");

			try {
				SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf_2 = new SimpleDateFormat("MM月dd日");

				Date start_date = sdf_1.parse(check_in_day);
				Date end_date = sdf_1.parse(check_out_day);

				String start = sdf_2.format(start_date);
				String end = sdf_2.format(end_date);

				long distance = DateUtil.getDistanceDays(check_in_day, check_out_day);

				dateText.setText(start + "-" + end + "共" + distance + "晚");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void addRow(int rows) {
		editTexts = new ArrayList<EditText>();
		tableLayout.removeAllViews();
		TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 0, 5, 0);
		for (int i = 0; i < rows; i++) {
			TextView lableText = new TextView(mContext);
			EditText nameText = new EditText(mContext);
			View lineView = new View(mContext);
			lineView.setBackgroundResource(R.drawable.dotted_horizontal_line);
			lineView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			lineView.setLayoutParams(params);
			lableText.setText("房间" + (i + 1) + "住人");
			lableText.setTextColor(Color.parseColor("#343434"));
			lableText.setTextSize(18);
			nameText.setBackground(null);
			nameText.setTextSize(18);
			TableRow row = new TableRow(mContext);
			row.addView(lableText);
			row.addView(nameText);
			tableLayout.addView(row);
			tableLayout.addView(lineView);
			editTexts.add(nameText);

		}
	}

	String getGuest() {
		String guest = "";
		for (TextView tv : editTexts) {
			guest += tv.getText().toString() + ";";
		}
		return guest;

	}

	void initCoupon() {
		JSONObject obj;
		try {
			obj = new JSONObject(response);
			int coupon = obj.getInt("coupons");
			// int coupon = 20;
			int limits = obj.getInt("limits");
			final double depositRatio = obj.getDouble("depositRatio");
			availableCoupon = coupon > limits ? limits : coupon;
			usedCoupon = availableCoupon;
			couponText.setText(availableCoupon + "张");
			discountText.setText(coupon * 10 + "");
			plusImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (usedCoupon < availableCoupon) {
						++usedCoupon;
						couponText.setText(usedCoupon + "张");
						int discount = usedCoupon * 10;
						discountText.setText(discount + "");
						int total = Integer.parseInt(orderTotalText.getText().toString());
						int deposit = (int) ((total - discount) * depositRatio * 0.01);
						int cash = total - discount - deposit;
						cashpayText.setText(cash + "");
						depositText.setText(deposit + "");

					}

				}
			});

			minusImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (usedCoupon > 0) {
						--usedCoupon;
						couponText.setText(usedCoupon + "张");
						couponText.setText(usedCoupon + "张");
						int discount = usedCoupon * 10;
						discountText.setText(discount + "");
						int total = Integer.parseInt(orderTotalText.getText().toString());
						int deposit = (int) ((total - discount) * depositRatio * 0.01);
						int cash = total - discount - deposit;
						depositText.setText(deposit + "");
						cashpayText.setText(cash + "");
					}

				}
			});

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void refreshOrder(int rmCnt) {
		if (!TextUtils.isEmpty(response)) {
			JSONObject object;
			try {
				object = new JSONObject(response);
				int total = object.getInt("totalPrice");
				int needDeposit = object.getInt("needDeposit");
				int discount = Integer.parseInt(discountText.getText().toString());
				double depositRatio = object.getDouble("depositRatio");
				int deposit = 0, cash;
				int refresh = total * rmCnt;
				if (needDeposit == 0) {
					deposit = 0;
				} else {
					deposit = (int) ((refresh - discount) * depositRatio / 100);
					depositText.setText(deposit + "");

				}
				cash = refresh - discount - deposit;
				cashpayText.setText(cash + "");

				orderTotalText.setText(refresh + "");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	class CalculateOrderRunnable implements Runnable {
		RequestParams params;

		public CalculateOrderRunnable(RequestParams params) {
			super();
			this.params = params;
		}

		@Override
		public void run() {
			Looper.prepare();
			String webUrl = WEB_SERVER_URL + "/zzd/book/v1/createOrder";
			AsyncHttpClient client = new AsyncHttpClient();
			client.addHeader("Authorization", "Basic MTM3OTgwNDAyMzk6ZWM4YTcxMWYtNGI0OS0xMWUzLTg3MTUtMDAxNjNlMDIxMzQz");
			client.post(mContext, webUrl, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					if (DEBUGGER) {
						Toast.makeText(mContext, arg1, Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onStart() {
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
					try {
						response = arg0;
						JSONObject object = new JSONObject(arg0);
						calculateHandler.obtainMessage(0, -1, -1, object).sendToTarget();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if (DEBUGGER) {
							Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
						}
						calculateHandler.sendEmptyMessage(1);
					}

				}
			});
			Looper.loop();
		}
	}

	private Handler calculateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				// {"limits":70,"rmCnt":1,"startDate":"2013-11-27","deposit":13,"depositRatio":10,"endDate":"2013-11-28","code":"200","rmName":"标准双人房","ghName":"银子浜客栈 ","contact":"小蔚子","discount":60,"useCoupons":6,"coupons":6,"ghId":"SPBH-20130728130","actPrice":117,"rmId":"5bc0811b-f737-11e2-9609-00163e020d45","area":"周庄","needDeposit":1,"days":1,"contPhone":"13798040239","notes":"本订单最多能用70张优惠劵，总优惠700元","totalPrice":190}
				JSONObject json = (JSONObject) msg.obj;
				try {
					orderTotalText.setText(json.getString("totalPrice"));
					discountText.setText(json.getString("discount"));
					cashpayText.setText(json.getString("actPrice"));
					depositText.setText(json.getString("deposit"));
					noteText.setText(json.getString("notes"));
					initCoupon();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;

			default:
				break;
			}
		}

	};

	class SaveOrderRunnable implements Runnable {
		RequestParams params;

		public SaveOrderRunnable(RequestParams params) {
			super();
			this.params = params;
		}

		@Override
		public void run() {
			Looper.prepare();

			String webUrl = WEB_SERVER_URL + "/zzd/book/v1/saveOrder";
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
					progressDialog = ProgressDialog.show(CheckInInfoActivity.this, null, "正在处理，请稍候...", true, false);
					super.onStart();
				}

				@Override
				public void onSuccess(final String arg0) {
					if (DEBUGGER) {
						Toast.makeText(mContext, arg0, Toast.LENGTH_LONG).show();
					}
					saveOrderHandler.obtainMessage(0, -1, -1, arg0).sendToTarget();

				}
			});
			Looper.loop();

		}

	}

	private String getExtraString() {
		JSONObject json = new JSONObject();
		return json.toString();

	}

	private Handler saveOrderHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			switch (msg.what) {
			case 0:
				String response = (String) msg.obj;
				try {
					JSONObject json = new JSONObject(response);
					String code = json.getString("code");
					// String code = "200";
					orderId = json.getString("orderId");
					String message = "";
					if ("200".equals(code)) {
						message = "请求成功";
						Intent intent = new Intent(CheckInInfoActivity.this, OrderDetails_State_ConfirmRoomActivity.class);
						intent.putExtra("orderId", orderId);
						startActivity(intent);

					} else {
						if ("600".equals(code)) {
							message = "请求失败，无房";
						} else if ("610".equals(code)) {
							message = "请求失败，请求使用优惠劵的数量与实际拥有可用数量不符";
						} else if ("620".equals(code)) {
							message = "请求失败，请求使用优惠劵的数量与旅馆允许使用数量不符";
						}
						showAlertMessage(message);
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

	class ExchangeCouponRunnable implements Runnable {
		ExchangeCouponPostParameter parameter;
		RequestParams params;

		public ExchangeCouponRunnable(ExchangeCouponPostParameter parameter) {
			super();
			this.parameter = parameter;
			params = new RequestParams();
			params.put("appId", parameter.getAppId());
			params.put("ghId", parameter.getGhId());
			params.put("cnt", parameter.getCnt());
			params.put("exchUserId", parameter.getExchUserId());
			params.put("exchCnt", parameter.getExchCnt());
			params.put("exchGhId", parameter.getExchGhId());

		}

		@Override
		public void run() {
			Looper.prepare();

			String webUrl = WEB_SERVER_URL + "/zzd/coupon/v1/exchangeMyCoupon";
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
					progressDialog = ProgressDialog.show(CheckInInfoActivity.this, null, "正在处理，请稍候...", true, false);
					super.onStart();
				}

				@Override
				public void onSuccess(final String arg0) {
					progressDialog.dismiss();
					if (DEBUGGER) {
						Toast.makeText(mContext, arg0, Toast.LENGTH_LONG).show();
					}

					try {
						JSONObject obj = new JSONObject(arg0);
						String code = obj.getString("code");

						if ("200".equals(code)) {
							couponText.setText(parameter.getExchCnt() + "张");

						} else {
							showAlertMessage("兑换失败！");
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

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
