package com.findhotel.activity;

import static com.findhotel.constant.Constant.WEB_SERVER_URL;
import static com.findhotel.constant.Constant.DEBUGGER;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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
import com.findhotel.util.MyTextUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class HaggleActivity extends SherlockActivity {
	SlidingMenu menu;
	JSONObject hotel;
	TextView titleText, mobileText, locationText;
	TextView check_in_dateText, check_in_dayText, check_out_dateText, check_out_dayText;
	EditText rmCntText, priceText;
	ImageView plusImage, minusImage;
	String[] roomType = { "双床房", "大床房" };
	ArrayAdapter<String> typeAdapter;
	Spinner typeSpinner;
	Context mContext = HaggleActivity.this;
	ExecutorService executorService = Executors.newCachedThreadPool();
	ProgressDialog progressDialog;
	int rmCnt = 1;
	String check_in_day, check_out_day, check_in_day_sdf4, check_out_day_sdf4;
	String rmType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(HaggleActivity.this);
		setContentView(R.layout.activity_haggle);
		initActionBar();
		menu = new MyActionMenu(HaggleActivity.this).initView();
		initView();
		executorService.execute(new CreateHaggleRunnable());
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
		titleText = (TextView) findViewById(R.id.tv_titel);
		mobileText = (TextView) findViewById(R.id.tv_mobie);
		locationText = (TextView) findViewById(R.id.tv_location);
		typeSpinner = (Spinner) findViewById(R.id.sp_type);
		rmCntText = (EditText) findViewById(R.id.etv_room_num);
		priceText = (EditText) findViewById(R.id.etv_price);
		plusImage = (ImageView) findViewById(R.id.iv_setplus);
		minusImage = (ImageView) findViewById(R.id.iv_setminus);
		check_in_dateText = (TextView) findViewById(R.id.tv_check_in_date);
		check_in_dayText = (TextView) findViewById(R.id.tv_check_in_day);
		check_out_dateText = (TextView) findViewById(R.id.tv_check_out_date);
		check_out_dayText = (TextView) findViewById(R.id.tv_check_out_day);
		findViewById(R.id.ll_check_in_date).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final DatePickerDialog mDatePickerDialog;
				final Calendar mCalendar = Calendar.getInstance(Locale.CHINA);
				Date nowDate = new Date();
				mCalendar.setTime(nowDate);
				mCalendar.add(Calendar.DAY_OF_MONTH, 2);
				final int year = mCalendar.get(Calendar.YEAR);
				final int monthOfYear = mCalendar.get(Calendar.MONTH);
				final int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
				mCalendar.get(Calendar.DAY_OF_WEEK);
				mDatePickerDialog = new DatePickerDialog(mContext, new OnDateSetListener() {

					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						// TODO Auto-generated method stub
						SimpleDateFormat sdf_1 = new SimpleDateFormat("MM月dd日");
						SimpleDateFormat sdf_2 = new SimpleDateFormat("yy年，EE");
						SimpleDateFormat sdf_3 = new SimpleDateFormat("yyyy/MM/dd");
						SimpleDateFormat sdf_4 = new SimpleDateFormat("yyyy-MM-dd");
						mCalendar.set(year, monthOfYear, dayOfMonth);
						String formatedDate = sdf_1.format(mCalendar.getTime());
						String formatedDay = sdf_2.format(mCalendar.getTime());
						check_in_dateText.setText(formatedDate);
						check_in_dayText.setText(formatedDay);
						check_in_day = sdf_3.format(mCalendar.getTime());
						check_in_day_sdf4 = sdf_4.format(mCalendar.getTime());
					}
				}, year, monthOfYear, dayOfMonth);
				if (android.os.Build.VERSION.SDK_INT >= 14) {
					DatePicker dp = mDatePickerDialog.getDatePicker();
					dp.setMinDate(mCalendar.getTimeInMillis());
				}
				mDatePickerDialog.show();
			}
		});

		findViewById(R.id.ll_check_out_date).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final DatePickerDialog mDatePickerDialog;
				final Calendar mCalendar = Calendar.getInstance(Locale.CHINA);
				Date nowDate = new Date();
				mCalendar.setTime(nowDate);
				mCalendar.add(Calendar.DAY_OF_MONTH, 3);
				final int year = mCalendar.get(Calendar.YEAR);
				final int monthOfYear = mCalendar.get(Calendar.MONTH);
				final int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
				mCalendar.get(Calendar.DAY_OF_WEEK);
				mDatePickerDialog = new DatePickerDialog(mContext, new OnDateSetListener() {

					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						// TODO Auto-generated method stub
						SimpleDateFormat sdf_1 = new SimpleDateFormat("MM月dd日");
						SimpleDateFormat sdf_2 = new SimpleDateFormat("yy年，EE");
						SimpleDateFormat sdf_3 = new SimpleDateFormat("yyyy/MM/dd");
						SimpleDateFormat sdf_4 = new SimpleDateFormat("yyyy-MM-dd");
						mCalendar.set(year, monthOfYear, dayOfMonth);
						String formatedDate = sdf_1.format(mCalendar.getTime());
						String formatedDay = sdf_2.format(mCalendar.getTime());
						check_out_dateText.setText(formatedDate);
						check_out_dayText.setText(formatedDay);
						check_out_day = sdf_3.format(mCalendar.getTime());
						check_out_day_sdf4 = sdf_4.format(mCalendar.getTime());
					}
				}, year, monthOfYear, dayOfMonth);
				if (android.os.Build.VERSION.SDK_INT >= 14) {
					DatePicker dp = mDatePickerDialog.getDatePicker();
					dp.setMinDate(mCalendar.getTimeInMillis());
				}
				mDatePickerDialog.show();
			}
		});
		plusImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (rmCnt < 5) {
					++rmCnt;
					rmCntText.setText(rmCnt + "");
				}

			}
		});

		minusImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (rmCnt > 1) {
					--rmCnt;
					rmCntText.setText(rmCnt + "");
				}

			}
		});

		typeAdapter = new ArrayAdapter<String>(HaggleActivity.this, android.R.layout.simple_spinner_item, roomType);
		typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinner.setAdapter(typeAdapter);
		typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0:
					rmType = "FC";
					break;

				default:
					rmType = "DC";
					break;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		try {
			hotel = new JSONObject(getIntent().getStringExtra("hotel"));

			titleText.setText("订房你订价！你可以把你想要入住的价格给给" + hotel.getString("ghName") + "同区域、同档次的旅馆，旅馆将在10分钟内回复。");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// test
		((Button) findViewById(R.id.btn_haggle)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent(HaggleActivity.this, HaggleAnswerActivity.class);
				// startActivity(intent);
				executorService.execute(new SubmitHaggleRunnable());

			}
		});

	}

	void initActionBar() {
		String title = getResources().getString(R.string.title_activity_haggle);
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

	class CreateHaggleRunnable implements Runnable {

		@Override
		public void run() {
			Looper.prepare();

			String webUrl = WEB_SERVER_URL + "/zzd/book/v1/createBidding";
			final RequestParams params = new RequestParams();
			String ghId;
			try {
				ghId = hotel.getString("ghId");
				params.put("appId", "appId");
				params.put("ghId", ghId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
					createHandler.obtainMessage(0, -1, -1, arg0).sendToTarget();

				}
			});
			Looper.loop();

		}

	}

	private Handler createHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			switch (msg.what) {
			case 0:
				String result = (String) msg.obj;
				try {
					JSONObject json = new JSONObject(result);
					String mobile = json.getString("contPhone");
					mobileText.setText(MyTextUtils.convertMobile(mobile));
					locationText.setText(json.getString("area"));
					check_in_day_sdf4 = json.getString("startDate");
					check_out_day_sdf4 = json.getString("endDate");
					SimpleDateFormat sdf_1 = new SimpleDateFormat("MM月dd日");
					SimpleDateFormat sdf_2 = new SimpleDateFormat("yy年,EE");
					new SimpleDateFormat("yyyy/MM/dd");
					SimpleDateFormat sdf_4 = new SimpleDateFormat("yyyy-MM-dd");
					try {
						Date start = sdf_4.parse(check_in_day_sdf4);
						Date end = sdf_4.parse(check_out_day_sdf4);
						check_in_dateText.setText(sdf_1.format(start));
						check_in_dayText.setText(sdf_2.format(start));
						check_out_dateText.setText(sdf_1.format(end));
						check_out_dayText.setText(sdf_2.format(end));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

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

	class SubmitHaggleRunnable implements Runnable {

		@Override
		public void run() {
			Looper.prepare();

			String webUrl = WEB_SERVER_URL + "/zzd/book/v1/saveBidding";
			final RequestParams params = new RequestParams();
			String ghId;
			try {
				ghId = hotel.getString("ghId");
				params.put("appId", "appId");
				params.put("ghId", ghId);
				params.put("rmType", rmType);
				params.put("startDate", check_in_day_sdf4);
				params.put("endDate", check_out_day_sdf4);
				params.put("rmCnt", rmCntText.getText().toString());
				params.put("totalPrice", priceText.getText().toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
					saveHandler.obtainMessage(0, -1, -1, arg0).sendToTarget();

				}
			});
			Looper.loop();

		}

	}

	private Handler saveHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			switch (msg.what) {
			case 0:
				String result = (String) msg.obj;
				String message = "";
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString("code");
					if ("200".equals(code)) {
						message = "发飙成功！";
					} else {
						message = "发飙失败！";

					}
					showAlertMessage(message);

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

					}
				}).show();
	}
}
