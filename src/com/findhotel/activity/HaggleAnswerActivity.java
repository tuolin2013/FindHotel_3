package com.findhotel.activity;

import static com.findhotel.constant.Constant.DEBUGGER;
import static com.findhotel.constant.Constant.WEB_SERVER_URL;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.findhotel.util.MyActionMenu;
import com.findhotel.widget.SquareImageView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HaggleAnswerActivity extends SherlockActivity {
	SlidingMenu menu;
	ListView mListView;
	TextView topDescText, areaText, priceText, check_in_dateText, check_in_dayText, check_out_dateText, check_out_dayText, roomTypeText,
			roomNumText;
	Button payButton, refuseButton;
	String selectId = "";
	Context mContext = HaggleAnswerActivity.this;
	ExecutorService executorService = Executors.newCachedThreadPool();
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(HaggleAnswerActivity.this);
		setContentView(R.layout.activity_haggle_answer);
		menu = new MyActionMenu(HaggleAnswerActivity.this).initView();
		initView();
		RequestParams params = new RequestParams();
		params.put("appId", "appId");
		params.put("orderId", getIntent().getStringExtra("orderId"));
		executorService.execute(new LoadAnswerDetailsRunnable(params));

	}

	void initView() {
		// TODO Auto-generated method stub
		initActionBar();
		mListView = (ListView) findViewById(R.id.lv_answer);
		areaText = (TextView) findViewById(R.id.tv_area);
		priceText = (TextView) findViewById(R.id.tv_price);
		check_in_dateText = (TextView) findViewById(R.id.tv_check_in_date);
		check_in_dayText = (TextView) findViewById(R.id.tv_check_in_day);
		check_out_dateText = (TextView) findViewById(R.id.tv_check_out_date);
		check_out_dayText = (TextView) findViewById(R.id.tv_check_out_day);
		roomTypeText = (TextView) findViewById(R.id.tv_room_type);
		roomNumText = (TextView) findViewById(R.id.tv_room_num);
		topDescText = (TextView) findViewById(R.id.tv_top_desc);

		refuseButton = (Button) findViewById(R.id.btn_refuse);
		refuseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RequestParams params = new RequestParams();
				params.put("appId", "appId");
				params.put("orderId", getIntent().getStringExtra("orderId"));
				executorService.execute(new RefuseRunnable(params));

			}
		});

		// String testJson =
		// "{orderId:2013405005-33433,area:西塘,price:130,startDate:2013-10-10,endDate:2013-10-11,rmType:DC,rmCnt:1,resp:[{ordId:2012020334,ghName:留香居客栈,url:www.zhaozhude.comimage,addService:增值服务},{ordId:2012020332,ghName:小桥流水旅馆,url:www.zhaozhude.comimage,addService:增值服务2},{ordId:2012020330,ghName:月圆人家,url:www.zhaozhude.comimage,addService:增值服务3}]}";
		// try {
		// JSONObject object = new JSONObject(testJson);
		// JSONArray datasource = object.getJSONArray("resp");
		// HanggleAnswerAdaper adapter = new HanggleAnswerAdaper(datasource);
		// mListView.setAdapter(adapter);
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
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

	void initActionBar() {
		String title = "旅馆接飙了";
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

	class LoadAnswerDetailsRunnable implements Runnable {
		RequestParams params;

		public LoadAnswerDetailsRunnable(RequestParams params) {
			super();
			this.params = params;
		}

		@Override
		public void run() {
			Looper.prepare();

			String webUrl = WEB_SERVER_URL + "/zzd/book/v1/viewBidding";

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
					loadHandler.obtainMessage(0, -1, -1, arg0).sendToTarget();

				}
			});
			Looper.loop();

		}

	}

	private Handler loadHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();

			switch (msg.what) {
			case 0:
				String result = (String) msg.obj;
				try {
					JSONObject json = new JSONObject(result);
					JSONArray datasource = json.getJSONArray("resp");
					areaText.setText(json.getString("area"));
					priceText.setText(json.getString("price"));
					SimpleDateFormat sdf_1 = new SimpleDateFormat("MM月dd日");
					SimpleDateFormat sdf_2 = new SimpleDateFormat("yy年，EE");
					SimpleDateFormat sdf_3 = new SimpleDateFormat("yyyy/MM/dd");
					SimpleDateFormat sdf_4 = new SimpleDateFormat("yyyy-MM-dd");
					String start = json.getString("startDate");
					String end = json.getString("endDate");
					try {
						Date startDate = sdf_4.parse(start);
						Date endDate = sdf_4.parse(end);
						check_in_dateText.setText(sdf_1.format(startDate));
						check_in_dayText.setText(sdf_2.format(startDate));
						check_out_dateText.setText(sdf_1.format(endDate));
						check_out_dayText.setText(sdf_2.format(endDate));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String roomType = json.getString("rmType");
					roomTypeText.setText(roomType);
					roomNumText.setText(json.getString("rmCnt"));

					HanggleAnswerAdaper adapter = new HanggleAnswerAdaper(datasource);
					mListView.setAdapter(adapter);
					adapter.notifyDataSetChanged();

					topDescText.setText("有" + datasource.length() + "个旅馆很想邀你入住，请尽快选择，以免预订的房间被售空。");

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

	class RefuseRunnable implements Runnable {
		RequestParams params;

		public RefuseRunnable(RequestParams params) {
			super();
			this.params = params;
		}

		private Handler refuseHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				switch (msg.what) {
				case 0:
					String result = (String) msg.obj;
					try {
						JSONObject obj = new JSONObject(result);
						String code = obj.getString("code");
						if ("200".equals(code)) {
							showAlertMessage("回绝成功！");
						} else {
							showAlertMessage("操作失败！");
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

		@Override
		public void run() {
			Looper.prepare();

			String webUrl = WEB_SERVER_URL + "/zzd/book/v1/cancelBidding";

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
					refuseHandler.obtainMessage(0, -1, -1, arg0).sendToTarget();

				}
			});
			Looper.loop();

		}

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
				holder.nameText.setText(item.getString("ghName"));
				mLoader.displayImage(item.getString("url"), holder.imageView);
				// holder.priceText.setText(item.getString(""));
				// holder.discountText.setText(item.getString(""));
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
