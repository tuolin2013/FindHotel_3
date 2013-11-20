package com.findhotel.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
import com.findhotel.adapter.HotelImageAdapter;
import com.findhotel.adapter.PriceAdapter;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.ListViewUtility;
import com.findhotel.util.MyActionMenu;
import com.findhotel.widget.FloatView;
import com.findhotel.widget.MyGridView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.origamilabs.library.loader.ImageLoader;
import com.readystatesoftware.viewbadger.BadgeView;

public class RoomListActivity extends SherlockActivity {
	SlidingMenu menu;
	ListView mListView;
	TextView addressText, check_in_dateText, check_in_dayText, check_out_dateText, check_out_dayText;
	Button haggleButton;
	LinearLayout addressLayout;
	LinearLayout facilityIconView;
	private PopupWindow mPopupWindow;
	RoomAdapter mAdapter;
	Context mContext = RoomListActivity.this;
	String testJson = "{addr:西塘南山小学怀道里28号3A,endDate:2013-09-30,ghId:aa36-ec08c5bbb27b,label:[{icon:icon01},{icon:icon02},{icon:icon03}],lat:23.1234,lng:120.4534,rooms:[{price:160,rmId:xxx,rmName:双床房,size:床宽1.2米},{price:168,rmId:xxx,rmName:温馨情侣大床房,size:床宽1.8米}],startDate:2013-09-30}";
	ImageLoader loader;
	JSONObject hotel;
	HashMap<String, Integer> icon_lableHashMap, icon_facilityHashMap;
	String check_in_day, check_out_day;

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
				final int year = mCalendar.get(Calendar.YEAR);
				final int monthOfYear = mCalendar.get(Calendar.MONTH);
				final int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
				final int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
				mDatePickerDialog = new DatePickerDialog(RoomListActivity.this, new OnDateSetListener() {

					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						// TODO Auto-generated method stub
						SimpleDateFormat sdf_1 = new SimpleDateFormat("MM月dd日");
						SimpleDateFormat sdf_2 = new SimpleDateFormat("yy年，EE");
						SimpleDateFormat sdf_3 = new SimpleDateFormat("yyyy/MM/dd");
						mCalendar.set(year, monthOfYear, dayOfMonth);
						String formatedDate = sdf_1.format(mCalendar.getTime());
						String formatedDay = sdf_2.format(mCalendar.getTime());
						check_in_dateText.setText(formatedDate);
						check_in_dayText.setText(formatedDay);
						check_in_day = sdf_3.format(mCalendar.getTime());
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
				mCalendar.add(Calendar.DAY_OF_MONTH, 1);
				final int year = mCalendar.get(Calendar.YEAR);
				final int monthOfYear = mCalendar.get(Calendar.MONTH);
				final int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
				final int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
				mDatePickerDialog = new DatePickerDialog(RoomListActivity.this, new OnDateSetListener() {

					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						// TODO Auto-generated method stub
						SimpleDateFormat sdf_1 = new SimpleDateFormat("MM月dd日");
						SimpleDateFormat sdf_2 = new SimpleDateFormat("yy年，EE");
						SimpleDateFormat sdf_3 = new SimpleDateFormat("yyyy/MM/dd");
						mCalendar.set(year, monthOfYear, dayOfMonth);
						String formatedDate = sdf_1.format(mCalendar.getTime());
						String formatedDay = sdf_2.format(mCalendar.getTime());
						check_out_dateText.setText(formatedDate);
						check_out_dayText.setText(formatedDay);
						check_out_day = sdf_3.format(mCalendar.getTime());
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
		displayHotelDetails();
		loadTestData();
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

			// JSONArray labels = hotel.getJSONArray("label");
			// for (int i = 0; i < labels.length(); i++) {
			// ImageView img = new ImageView(mContext);
			// img.setImageResource(R.drawable.ic_launcher);
			// ((LinearLayout) findViewById(R.id.ll_label)).addView(img);
			//
			// for (String s : icon_lableHashMap.keySet()) {
			//
			// }
			//
			// }
			initIcon();
			LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 0, 10, 0);
			for (String s : icon_lableHashMap.keySet()) {
				ImageView img = new ImageView(mContext);
				img.setImageResource(icon_lableHashMap.get(s));
				img.setLayoutParams(layoutParams);
				((LinearLayout) findViewById(R.id.ll_label)).addView(img);
			}

			JSONArray coupons = hotel.getJSONArray("coupon");
			// LayoutParams layoutParams2 = new LayoutParams(100, 100);
			// layoutParams2.setMargins(0, 10, 0, 0);
			// for (int i = 0; i < coupons.length(); i++) {
			// ImageView img = new ImageView(mContext);
			// img.setImageResource(R.drawable.temp_avatar);
			// img.setLayoutParams(layoutParams2);
			// ((LinearLayout) findViewById(R.id.ll_coupon_photo)).addView(img);
			// BadgeView badge = new BadgeView(mContext, img);
			// badge.setText(i + "");
			// badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
			// badge.setBadgeMargin(-5, 0);
			// badge.setBackgroundResource(R.drawable.circle);
			// badge.show();
			// }
			AvatarAdapter avatarAdapter = new AvatarAdapter(mContext, coupons);
			MyGridView gridView = (MyGridView) findViewById(R.id.gv_avatar);
			gridView.setAdapter(avatarAdapter);

			String urls[] = { "http://pic1a.nipic.com/20090312/550365_095010052_2.jpg",
					"http://www.veryeast.cn/cms/Files/%E9%85%92%E5%BA%97%E6%88%BF%E9%97%B4%E6%95%88%E6%9E%9C%E5%9B%BE.jpg",
					"http://upload.17u.net/uploadpicbase/2009/06/12/aa/2009061217264411163.jpg" };
			String urls_2[] = { "http://i0.sinaimg.cn/travel/ul/2009/1230/U3325P704DT20091230135003.jpg",
					"http://i3.sinaimg.cn/travel/ul/2009/1230/U3325P704DT20091230135037.jpg",
					"http://images.china.cn/attachement/jpg/site1000/20090317/0019b91ebfe20b294eb90e.jpg",
					"http://www.lvyou114.com/member/6082/hotelphoto/2008-1-9-13-35-18.jpg",
					"http://www.gdhotel.org/HotelFile/UP_20071117121448.jpg",
					"http://upload.17u.net/uploadpicbase/2009/06/12/aa/2009061217264411163.jpg", "menu" };

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

	void loadTestData() {
		try {
			JSONObject json = new JSONObject(testJson);
			JSONArray datasource = json.getJSONArray("rooms");
			mAdapter = new RoomAdapter(mContext, datasource);
			mListView.setAdapter(mAdapter);
			ListViewUtility.setListViewHeightBasedOnChildren(mListView);
			addressText.setText(json.getString("addr"));
			JSONArray labels = json.getJSONArray("label");

			// for (int i = 0; i < labels.length(); i++) {
			// ImageView img = new ImageView(mContext);
			// img.setImageResource(R.drawable.ic_launcher);
			// img.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// Intent intent = new Intent(mContext, HotelDetailsActivity.class);
			// intent.putExtra("json", hotel.toString());
			// startActivity(intent);
			// }
			// });
			// labelView.addView(img);
			// }

			LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 0, 5, 0);
			for (String key : icon_facilityHashMap.keySet()) {
				ImageView img = new ImageView(mContext);
				img.setImageResource(icon_facilityHashMap.get(key));
				img.setLayoutParams(layoutParams);
				facilityIconView.addView(img);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
			holder.nameText = (TextView) convertView.findViewById(R.id.tv_room_name);
			holder.sizeText = (TextView) convertView.findViewById(R.id.tv_room_size);
			holder.priceText = (TextView) convertView.findViewById(R.id.tv_room_price);
			holder.reservationsButton = (Button) convertView.findViewById(R.id.btn_reservations);
			final View view = convertView.findViewById(R.id.ll_content);

			try {
				final JSONObject item = list.getJSONObject(position);
				holder.nameText.setText(item.getString("rmName"));
				holder.sizeText.setText("(" + item.getString("size") + ")");
				holder.priceText.setText(item.getString("price"));

				holder.priceText.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showPopupWindow();
						mPopupWindow.showAsDropDown(view);

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
						intent.putExtra("check_in_day", check_in_day);
						intent.putExtra("check_out_day", check_out_day);
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

	private void showPopupWindow() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		View popupView = getLayoutInflater().inflate(R.layout.popup_window_room_price, null);
		ListView mListView = (ListView) popupView.findViewById(R.id.lv_price);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

		String test = "{rmId:aa36-ec08c5bbb27b,rooms:[{price:168,sDate:2013-09-30},{price:188,sDate:2013-10-01},{price:188,sDate:2013-10-02}]}";

		try {
			JSONObject jsonObject = new JSONObject(test);
			JSONArray datasource = jsonObject.getJSONArray("rooms");
			PriceAdapter adapter = new PriceAdapter(RoomListActivity.this, datasource);
			mListView.setAdapter(adapter);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
