package com.findhotel.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.util.ExitApplication;
import com.findhotel.widget.SquareImageView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HaggleAnswerActivity extends SherlockActivity {
	SlidingMenu menu;
	ListView mListView;
	String selectId = "";
	Context mContext = HaggleAnswerActivity.this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(HaggleAnswerActivity.this);
		setContentView(R.layout.activity_haggle_answer);
		initView();
	}

	void initView() {
		// TODO Auto-generated method stub
		initActionBar();
		mListView = (ListView) findViewById(R.id.lv_answer);
		String testJson = "{orderId:2013405005-33433,area:西塘,price:130,startDate:2013-10-10,endDate:2013-10-11,rmType:DC,rmCnt:1,resp:[{ordId:2012020334,ghName:留香居客栈,url:www.zhaozhude.comimage,addService:增值服务},{ordId:2012020332,ghName:小桥流水旅馆,url:www.zhaozhude.comimage,addService:增值服务2},{ordId:2012020330,ghName:月圆人家,url:www.zhaozhude.comimage,addService:增值服务3}]}";
		try {
			JSONObject object = new JSONObject(testJson);
			JSONArray datasource = object.getJSONArray("resp");
			HanggleAnswerAdaper adapter = new HanggleAnswerAdaper(datasource);
			mListView.setAdapter(adapter);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		// menu.add("更多").setIcon(R.drawable.ic_drawer_dark)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
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

	class HanggleAnswerAdaper extends BaseAdapter {
		JSONArray data;
		ImageLoader mLoader;
		DisplayImageOptions options;

		public HanggleAnswerAdaper(JSONArray data) {
			this.data = data;
			mLoader = ImageLoader.getInstance();
			options = new DisplayImageOptions.Builder().showStubImage(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty)
					.showImageOnFail(R.drawable.ic_error).cacheInMemory().cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			try {
				return data.get(position);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, final ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater layoutInflator = LayoutInflater.from(mContext);
				convertView = layoutInflator.inflate(R.layout.list_item_hotel_radio, null);
				holder = new ViewHolder();
				holder.imageView = (SquareImageView) convertView.findViewById(R.id.siv_hotel);
				holder.radioButton = (RadioButton) convertView.findViewById(R.id.radio_select);
				holder.nameText = (TextView) convertView.findViewById(R.id.tv_name);
				holder.priceText = (TextView) convertView.findViewById(R.id.tv_price);
				holder.discountText = (TextView) convertView.findViewById(R.id.tv_discount);
				convertView.setTag(holder);
			}
			holder = (ViewHolder) convertView.getTag();

			try {
				JSONObject item = data.getJSONObject(position);
				String orderId = item.getString("ordId");
				holder.radioButton.setTag(orderId);
				if (position == 0) {
					holder.radioButton.setChecked(true);
					selectId = orderId;
				}

				holder.radioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							for (int i = 0; i < parent.getChildCount(); i++) {
								RadioButton temp = (RadioButton) parent.getChildAt(i).findViewById(R.id.radio_select);
								temp.setChecked(false);
							}
							selectId = buttonView.getTag().toString();
							buttonView.setChecked(true);
						}

					}
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return convertView;
		}

		class ViewHolder {
			SquareImageView imageView;
			RadioButton radioButton;
			TextView nameText;
			TextView priceText;
			TextView discountText;
		}

	}

}
