package com.findhotel.activity;

import static com.findhotel.constant.Constant.WEB_SERVER_URL;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.adapter.AvatarAdapter;
import com.findhotel.adapter.HotelAdapter;
import com.findhotel.adapter.HotelImageAdapter;
import com.findhotel.adapter.PriceAdapter;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.ListViewUtility;
import com.findhotel.util.MyActionMenu;
import com.findhotel.widget.FloatView;
import com.findhotel.widget.MyGridView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.origamilabs.library.loader.ImageLoader;
import com.readystatesoftware.viewbadger.BadgeView;

public class RoomListActivity extends SherlockActivity {
	boolean debugger = true;
	SlidingMenu menu;
	ListView mListView;
	TextView addressText, check_in_dateText, check_in_dayText, check_out_dateText, check_out_dayText;
	Button haggleButton;
	LinearLayout addressLayout;
	LinearLayout facilityIconView, facilityView;
	private PopupWindow mPopupWindow;
	View popupTarget;
	RoomAdapter mAdapter;
	Context mContext = RoomListActivity.this;
	String testJson = "{addr:西塘南山小学怀道里28号3A,endDate:2013-09-30,ghId:aa36-ec08c5bbb27b,label:[{icon:icon01},{icon:icon02},{icon:icon03}],lat:23.1234,lng:120.4534,rooms:[{price:160,rmId:xxx,rmName:双床房,size:床宽1.2米},{price:168,rmId:xxx,rmName:温馨情侣大床房,size:床宽1.8米}],startDate:2013-09-30}";
	ImageLoader loader;
	JSONObject hotel;
	HashMap<String, Integer> icon_lableHashMap, icon_facilityHashMap;
	String check_in_day, check_out_day, check_in_day_sdf4, check_out_day_sdf4, rmId;
	JSONObject datasource;
	String request_url = WEB_SERVER_URL + "/zzd/hotel/v1/viewHotel";
	ExecutorService executorService = Executors.newCachedThreadPool();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(RoomListActivity.this);
		setContentView(R.layout.activity_room_list);
		menu = new MyActionMenu(RoomListActivity.this).initView();
		loader = new ImageLoader(mContext);
		initView();
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

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (menu.isMenuShowing()) {
				menu.toggle();
				return true;
			} else {
				finish();
				return false;
			}

		}
		return false;
	}

	private void initIcon() {
		icon_facilityHashMap = new HashMap<String, Integer>();
		icon_facilityHashMap.put("icon_air_conditioner", R.drawable.icon_air_conditioner);
		icon_facilityHashMap.put("icon_breakfast", R.drawable.icon_breakfast);
		icon_facilityHashMap.put("icon_lift", R.drawable.icon_lift);
		icon_facilityHashMap.put("icon_p", R.drawable.icon_p);
		icon_facilityHashMap.put("icon_tv", R.drawable.icon_tv);
		icon_facilityHashMap.put("icon_wash", R.drawable.icon_wash);
		icon_facilityHashMap.put("icon_wifi", R.drawable.icon_wifi);

		icon_lableHashMap = new HashMap<String, Integer>();
		icon_lableHashMap.put("icon_v", R.drawable.icon_v);
		icon_lableHashMap.put("icon_camera", R.drawable.icon_camera);
	}

	void initActionBar() {
		String title = getResources().getString(R.string.title_activity_room_list);
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

	void initView() {
		initActionBar();
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
				mDatePickerDialog = new DatePickerDialog(RoomListActivity.this, new OnDateSetListener() {

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
				mDatePickerDialog = new DatePickerDialog(RoomListActivity.this, new OnDateSetListener() {

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
						executorService.execute(new RefreshRoomRunable(check_in_day_sdf4, check_out_day_sdf4));
					}
				}, year, monthOfYear, dayOfMonth);
				if (android.os.Build.VERSION.SDK_INT >= 14) {
					DatePicker dp = mDatePickerDialog.getDatePicker();
					dp.setMinDate(mCalendar.getTimeInMillis());
				}
				mDatePickerDialog.show();
			}
		});

		mListView = (ListView) findViewById(R.id.lv_room);
		facilityIconView = (LinearLayout) findViewById(R.id.ll_facility_icon);
		addressText = (TextView) findViewById(R.id.tv_address);
		addressLayout = (LinearLayout) findViewById(R.id.ll_address);

		addressLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, MapGoogleNavigateActivity.class);
				startActivity(intent);

			}
		});

		haggleButton = (Button) findViewById(R.id.btn_haggle);
		haggleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, HaggleActivity.class);
				intent.putExtra("hotel", hotel.toString());
				startActivity(intent);

			}
		});

		findViewById(R.id.ll_facility).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RoomListActivity.this, HotelDetailsActivity.class);
				intent.putExtra("hotel", hotel.toString());
				startActivity(intent);

			}
		});
		displayHotelDetails();
		loadData();
	}

	@SuppressLint("NewApi")
	void displayHotelDetails() {

		try {
			hotel = new JSONObject(getIntent().getStringExtra("json"));
			((TextView) findViewById(R.id.tv_area)).setText("[" + hotel.getString("area") + "]");
			((TextView) findViewById(R.id.tv_hotel_name)).setText(hotel.getString("ghName"));
			((TextView) findViewById(R.id.tv_price)).setText(hotel.getString("price"));
			((TextView) findViewById(R.id.tv_order_amount)).setText("订(" + hotel.getString("orders") + ")");
			((TextView) findViewById(R.id.tv_star_amount)).setText("赞(" + hotel.getString("stars") + ")");

			initIcon();
			JSONArray labels = hotel.getJSONArray("label");
			// int height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, mContext.getResources().getDisplayMetrics());
			LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 0, 10, 0);
			for (int i = 0; i < labels.length(); i++) {
				JSONObject temp = labels.getJSONObject(i);
				String icon = temp.getString("icon");
				for (String s : icon_lableHashMap.keySet()) {
					if (s.equals(icon)) {
						ImageView img = new ImageView(mContext);
						img.setImageResource(icon_lableHashMap.get(s));
						img.setLayoutParams(layoutParams);
						((LinearLayout) findViewById(R.id.ll_label)).addView(img);
					}
				}
			}

			JSONArray coupons = hotel.getJSONArray("coupon");
			AvatarAdapter avatarAdapter = new AvatarAdapter(mContext, coupons);
			MyGridView gridView = (MyGridView) findViewById(R.id.gv_avatar);
			gridView.setAdapter(avatarAdapter);

			JSONArray hotelImages = hotel.getJSONArray("imgs");
			List<String> bigImages = new ArrayList<String>();
			List<String> smallImages = new ArrayList<String>();
			for (int i = 0; i < 3; i++) {
				JSONObject temp = hotelImages.getJSONObject(i);
				bigImages.add(temp.getString("mUrl"));
			}
			for (int i = 2; i < hotelImages.length(); i++) {
				JSONObject temp = hotelImages.getJSONObject(i);
				smallImages.add(temp.getString("mUrl"));
			}
			smallImages.add("menu");
			String[] temp = new String[] {};
			String urls[] = bigImages.toArray(temp);
			String urls_2[] = smallImages.toArray(temp);

			HotelImageAdapter adapter = new HotelImageAdapter(RoomListActivity.this, R.id.imageView1, urls, hotel);
			((GridView) findViewById(R.id.gv_big_photo)).setAdapter(adapter);

			HotelImageAdapter adapter2 = new HotelImageAdapter(RoomListActivity.this, R.id.imageView1, urls_2, hotel);
			((GridView) findViewById(R.id.gv_small_photo)).setAdapter(adapter2);
			adapter2.notifyDataSetChanged();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void loadData() {
		executorService.execute(new LoadRunnable());
	}

	class LoadRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			final RequestParams requestParams = new RequestParams();
			requestParams.put("appId", "appId");
			try {
				requestParams.put("ghId", hotel.getString("ghId"));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(mContext, request_url, requestParams, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					Toast.makeText(mContext, arg1, Toast.LENGTH_LONG).show();
				}

				@Override
				public void onStart() {
					if (debugger) {
						Toast.makeText(mContext, requestParams.toString(), Toast.LENGTH_LONG).show();
					}
					super.onStart();
				}

				@Override
				public void onSuccess(final String arg0) {
					if (debugger) {
						Toast.makeText(mContext, arg0, Toast.LENGTH_LONG).show();
					}
					try {
						new JSONObject(arg0);
						// pg_cnt = jsObj.getInt("pgCnt");
						JSONObject object = new JSONObject(arg0);
						myHandler.obtainMessage(0, -1, -1, object).sendToTarget();

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
				datasource = (JSONObject) msg.obj;
				try {
					JSONArray rooms = datasource.getJSONArray("rooms");
					mAdapter = new RoomAdapter(mContext, rooms);
					mListView.setAdapter(mAdapter);
					ListViewUtility.setListViewHeightBasedOnChildren(mListView);
					addressText.setText(datasource.getString("addr"));
					JSONArray labels = datasource.getJSONArray("label");
					LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					layoutParams.setMargins(0, 0, 5, 0);
					for (String key : icon_facilityHashMap.keySet()) {
						for (int i = 0; i < labels.length(); i++) {
							JSONObject temp = labels.getJSONObject(i);
							String icon = temp.getString("icon");
							if (icon.equals(key)) {
								ImageView img = new ImageView(mContext);
								img.setImageResource(icon_facilityHashMap.get(key));
								img.setLayoutParams(layoutParams);
								facilityIconView.addView(img);
							}
						}

					}
					// init datetime
					check_in_day_sdf4 = datasource.getString("startDate");
					check_out_day_sdf4 = datasource.getString("endDate");
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

			case 1:
				Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_LONG).show();
				break;
			}
		}

	};

	class RefreshRoomRunable implements Runnable {
		String startDate, endDate;

		public RefreshRoomRunable(String startDate, String endDate) {
			super();
			this.startDate = startDate;
			this.endDate = endDate;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			final RequestParams requestParams = new RequestParams();
			requestParams.put("appId", "appId");
			requestParams.put("startDate", startDate);
			requestParams.put("endDate", endDate);
			try {
				requestParams.put("ghId", hotel.getString("ghId"));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(mContext, request_url, requestParams, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					Toast.makeText(mContext, arg1, Toast.LENGTH_LONG).show();
				}

				@Override
				public void onStart() {
					if (debugger) {
						Toast.makeText(mContext, requestParams.toString(), Toast.LENGTH_LONG).show();
					}
					super.onStart();
				}

				@Override
				public void onSuccess(final String arg0) {
					if (debugger) {
						Toast.makeText(mContext, arg0, Toast.LENGTH_LONG).show();
					}
					JSONObject object;
					try {
						object = new JSONObject(arg0);
						refreshHandler.obtainMessage(0, -1, -1, object).sendToTarget();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						refreshHandler.sendEmptyMessage(1);
					}

				}
			});
			Looper.loop();

		}

	}

	private Handler refreshHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				JSONObject object = (JSONObject) msg.obj;
				JSONArray rooms;
				try {
					rooms = object.getJSONArray("rooms");
					mAdapter = new RoomAdapter(mContext, rooms);
					mListView.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
					ListViewUtility.setListViewHeightBasedOnChildren(mListView);
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

	class LoadRoomPriceRunnable implements Runnable {
		String startDate;
		String endDate;

		public LoadRoomPriceRunnable(String startDate, String endDate) {
			super();
			this.startDate = startDate;
			this.endDate = endDate;
		}

		@Override
		public void run() {
			Looper.prepare();
			String webUrl = WEB_SERVER_URL + "/zzd/hotel/v1/rmPrice";
			AsyncHttpClient client = new AsyncHttpClient();
			final RequestParams requestParams = new RequestParams();
			requestParams.put("appId", "appId");
			try {
				requestParams.put("ghId", hotel.getString("ghId"));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			requestParams.put("rmId", rmId);
			requestParams.put("startDate", startDate);
			requestParams.put("endDate", endDate);
			client.post(mContext, webUrl, requestParams, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					if (debugger) {
						Toast.makeText(mContext, arg1, Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onStart() {
					if (debugger) {
						Toast.makeText(mContext, requestParams.toString(), Toast.LENGTH_LONG).show();
					}
					super.onStart();
				}

				@Override
				public void onSuccess(final String arg0) {
					if (debugger) {
						Toast.makeText(mContext, arg0, Toast.LENGTH_LONG).show();
					}
					try {
						JSONObject object = new JSONObject(arg0);
						loadRoomPriceHandler.obtainMessage(0, -1, -1, object).sendToTarget();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if (debugger) {
							Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
						}
						loadRoomPriceHandler.sendEmptyMessage(1);
					}

				}
			});
			Looper.loop();

		}

	}

	private Handler loadRoomPriceHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				View popupView = getLayoutInflater().inflate(R.layout.popup_window_room_price, null);
				ListView mListView = (ListView) popupView.findViewById(R.id.lv_price);
				ProgressBar mBar = (ProgressBar) popupView.findViewById(R.id.progressBar1);
				mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
				mPopupWindow.setTouchable(true);
				mPopupWindow.setOutsideTouchable(true);
				mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

				try {
					JSONObject jsonObject = (JSONObject) msg.obj;
					JSONArray datasource = jsonObject.getJSONArray("rooms");
					PriceAdapter adapter = new PriceAdapter(RoomListActivity.this, datasource);
					mListView.setAdapter(adapter);
					mPopupWindow.showAsDropDown(popupTarget);
					mBar.setVisibility(View.GONE);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			default:
				if (debugger) {
					Toast.makeText(mContext, "load price failure", Toast.LENGTH_LONG).show();
				}
				mPopupWindow.dismiss();
				break;
			}
		}
	};

	public class RoomAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private Context mContext;
		private JSONArray list;

		public RoomAdapter(Context mContext, JSONArray list) {
			this.mContext = mContext;
			mInflater = LayoutInflater.from(this.mContext);
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.list.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			try {
				return list.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, final ViewGroup parent) {
			ViewHolder holder = null;
			// if (convertView == null) {
			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.list_item_room, null);
			popupTarget = convertView;
			holder.nameText = (TextView) convertView.findViewById(R.id.tv_room_name);
			holder.sizeText = (TextView) convertView.findViewById(R.id.tv_room_size);
			holder.priceText = (TextView) convertView.findViewById(R.id.tv_room_price);
			holder.reservationsButton = (Button) convertView.findViewById(R.id.btn_reservations);
			convertView.findViewById(R.id.ll_content);

			try {
				final JSONObject item = list.getJSONObject(position);
				holder.nameText.setText(item.getString("rmName"));
				holder.sizeText.setText("(" + item.getString("size") + ")");
				holder.priceText.setText(item.getString("price"));
				holder.priceText.setTag(item.getString("rmId"));
				holder.priceText.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						rmId = v.getTag().toString();
						executorService.execute(new LoadRoomPriceRunnable(check_in_day_sdf4, check_out_day_sdf4));

					}
				});

				holder.reservationsButton.setTag(item);
				holder.reservationsButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mContext, CheckInInfoActivity.class);
						intent.putExtra("hotel", hotel.toString());
						intent.putExtra("room", v.getTag().toString());
						intent.putExtra("check_in_day", check_in_day_sdf4);
						intent.putExtra("check_out_day", check_out_day_sdf4);
						intent.putExtra("rmId", rmId);
						mContext.startActivity(intent);

					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// convertView.setTag(holder);
			// } else {
			// holder = (ViewHolder) convertView.getTag();
			// }
			return convertView;
		}

		public final class ViewHolder {
			public TextView nameText;
			public TextView sizeText;
			public TextView priceText;
			public Button reservationsButton;
		}

	}

}
