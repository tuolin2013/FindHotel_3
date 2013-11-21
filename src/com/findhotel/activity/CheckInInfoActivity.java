package com.findhotel.activity;

import static com.findhotel.constant.Constant.WEB_SERVER_URL;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.util.DateUtil;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.MyActionMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

public class CheckInInfoActivity extends SherlockActivity {
	SlidingMenu menu;
	Button nextButton, exchangeButton;
	Spinner numSpinner;
	TextView hotelNameText, hotelAreaText, roomTypeText, dateText;// hotel information
	TextView orderTotalText, discountText, cashpayText, depositText, noteText;// order
	EditText couponText;
	Context mContext = CheckInInfoActivity.this;
	List<EditText> editTexts;
	TableLayout tableLayout;
	JSONObject hotel, room;
	String check_in_day, check_out_day;
	String response = "";
	int rmCnt = 0;
	ExecutorService executorService = Executors.newCachedThreadPool();
	boolean debugger = true;

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

	void initView() {
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

		initActionBar();
		exchangeButton = (Button) findViewById(R.id.btn_exchange);
		exchangeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CheckInInfoActivity.this, ExchangeCouponsActivity.class);
				startActivity(intent);

			}
		});

		tableLayout = (TableLayout) findViewById(R.id.tab_check_in);
		nextButton = (Button) findViewById(R.id.btn_next);

		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CheckInInfoActivity.this, OrderDetails_State_ConfirmActivity.class);
				startActivity(intent);

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
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.put("rmId", getIntent().getStringExtra("rmId"));
		params.put("startDate", check_in_day);
		params.put("endDate", check_out_day);
		params.put("rmCnt", rmCnt + "");
		params.put("useCoupons", "0");
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
			client.post(mContext, webUrl, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					if (debugger) {
						Toast.makeText(mContext, arg1, Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onStart() {
					if (debugger) {
						Toast.makeText(mContext, params.toString(), Toast.LENGTH_LONG).show();
					}
					super.onStart();
				}

				@Override
				public void onSuccess(final String arg0) {
					if (debugger) {
						Toast.makeText(mContext, arg0, Toast.LENGTH_LONG).show();
					}
					try {
						response = arg0;
						JSONObject object = new JSONObject(arg0);
						calculateHandler.obtainMessage(0, -1, -1, object).sendToTarget();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if (debugger) {
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
				JSONObject json = (JSONObject) msg.obj;
				try {
					orderTotalText.setText(json.getString("totalPrice"));
					discountText.setText(json.getString("discount"));
					cashpayText.setText(json.getString("actPrice"));
					depositText.setText(json.getString("deposit"));
					noteText.setText(json.getString("notes"));

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
}
