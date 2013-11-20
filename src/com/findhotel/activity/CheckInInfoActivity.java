package com.findhotel.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.util.DateUtil;
import com.findhotel.util.DisplayUtil;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.MyActionMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

public class CheckInInfoActivity extends SherlockActivity {
	SlidingMenu menu;
	Button nextButton, exchangeButton;
	Spinner numSpinner;
	TextView hotelNameText, hotelAreaText, roomTypeText, dateText;
	Context mContext = CheckInInfoActivity.this;
	List<EditText> editTexts;
	TableLayout tableLayout;
	JSONObject hotel, room;
	String check_in_day, check_out_day;

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
		// room_numText = (EditText) findViewById(R.id.etv_room_num);
		// room_numText.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before, int count) {
		// // TODO Auto-generated method stub
		// int num = Integer.parseInt(room_numText.getText().toString());
		// addRow(num);
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// // TODO Auto-generated method stub
		//
		// }
		// });

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

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

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
				SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy/MM/dd");
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
}
