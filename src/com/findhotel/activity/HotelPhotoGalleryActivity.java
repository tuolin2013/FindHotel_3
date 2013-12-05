package com.findhotel.activity;

import static com.findhotel.constant.Constant.DEBUGGER;
import static com.findhotel.constant.Constant.WEB_SERVER_URL;

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
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.origamilabs.library.views.ScaleImageView;

public class HotelPhotoGalleryActivity extends SherlockActivity {
	SlidingMenu menu;
	ListView photeListView;
	Button sortButton, bookButton;
	Spinner sortSpinner;
	TextView priceText;
	ExecutorService executorService = Executors.newCachedThreadPool();
	ProgressDialog progressDialog;
	Context mContext = HotelPhotoGalleryActivity.this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(HotelPhotoGalleryActivity.this);
		setContentView(R.layout.activity_hotel_photo_gallery);
		menu = new MyActionMenu(HotelPhotoGalleryActivity.this).initView();
		initView();
		try {
			JSONObject json_img = new JSONObject(getIntent().getStringExtra("image_data"));
			JSONObject json = new JSONObject(getIntent().getStringExtra("data"));
			RequestParams params = new RequestParams();
			params.put("appId", "appId");
			params.put("ghId", json.getString("ghId"));
			params.put("srcId", json_img.getString("srcId"));
			params.put("ord", "1");
			params.put("pg", "1");
			executorService.execute(new LoadDataRunnable(params));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initView() {
		initActionBar();
		sortButton = (Button) findViewById(R.id.btn_sort_by_praise);
		bookButton = (Button) findViewById(R.id.btn_book);
		photeListView = (ListView) findViewById(R.id.lv_photo);
		sortSpinner = (Spinner) findViewById(R.id.sp_sort);
		priceText = (TextView) findViewById(R.id.tv_price);

		String[] sort = { "官方推荐", "旅馆自拍", "驴友自拍" };
		ArrayAdapter<String> numAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, sort);
		numAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sortSpinner.setAdapter(numAdapter);
		sortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		bookButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, RoomListActivity.class);
				intent.putExtra("json", getIntent().getStringExtra("data"));
				startActivity(intent);

			}
		});
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
		String title = "随手拍";
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

	class LoadDataRunnable implements Runnable {
		RequestParams params;

		public LoadDataRunnable(RequestParams params) {
			super();
			this.params = params;
		}

		private Handler loadHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				progressDialog.dismiss();
				switch (msg.what) {
				case 0:

					String result = (String) msg.obj;
					try {
						JSONObject json = new JSONObject(result);
						JSONArray datasource = json.getJSONArray("views");
						String price = json.getString("price");
						String html = "<b>&#65509;" + price + "</b>起";
						priceText.setText(Html.fromHtml(html));
						PhotoAdapter adapter = new PhotoAdapter(mContext, datasource);
						photeListView.setAdapter(adapter);

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

		@Override
		public void run() {
			Looper.prepare();
			String webUrl = WEB_SERVER_URL + "/zzd/view/v1/viewHotelPhotos";
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(1000 * 60);
			client.addHeader("Authorization", "Basic MTM3OTgwNDAyMzk6ZWM4YTcxMWYtNGI0OS0xMWUzLTg3MTUtMDAxNjNlMDIxMzQz");
			client.post(mContext, webUrl, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					progressDialog.dismiss();
					if (DEBUGGER) {
						showAlertMessage(arg1);
					}

				}

				@Override
				public void onStart() {
					progressDialog = ProgressDialog.show(mContext, null, "正在加载，请稍候...", true, false);
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
					loadHandler.obtainMessage(0, -1, -1, arg0).sendToTarget();

				}
			});
			Looper.loop();

		}

	}

	class PhotoAdapter extends BaseAdapter {
		JSONArray datasource;
		private ImageLoader mLoader;
		DisplayImageOptions options;
		private LayoutInflater mInflater;

		public PhotoAdapter(Context mContext, JSONArray datasource) {
			mInflater = LayoutInflater.from(mContext);
			this.datasource = datasource;
			mLoader = ImageLoader.getInstance();
			options = new DisplayImageOptions.Builder().showStubImage(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty)
					.showImageOnFail(R.drawable.ic_error).cacheInMemory().cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datasource.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			try {
				return datasource.get(position);
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_item_my_photo, null);
				holder.countText = (TextView) convertView.findViewById(R.id.tv_count);
				holder.descText = (TextView) convertView.findViewById(R.id.tv_desc);
				holder.shareImage = (ImageView) convertView.findViewById(R.id.iv_share);
				holder.loveImage = (ImageView) convertView.findViewById(R.id.iv_love);
				holder.photeImage = (ScaleImageView) convertView.findViewById(R.id.siv_my_photo);
				holder.playerImage = (ImageView) convertView.findViewById(R.id.iv_video_play);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final LinearLayout shareLayout = (LinearLayout) convertView.findViewById(R.id.ll_share);
			// shareLayout.setAlpha(alpha);

			try {
				JSONObject jsonObject = datasource.getJSONObject(position);

				String type = jsonObject.getString("type");
				if ("m".equals(type)) {
					holder.playerImage.setVisibility(View.GONE);
				} else {
					holder.playerImage.setVisibility(View.VISIBLE);
				}
				holder.playerImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// player

					}
				});
				holder.descText.setText(jsonObject.getString("notes"));
				holder.countText.setText(jsonObject.getString("favors"));
				mLoader.displayImage(jsonObject.getString("mUrl"), holder.photeImage);

				holder.loveImage.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							shareLayout.setBackgroundResource(R.drawable.icon_background_left);
							break;

						case MotionEvent.ACTION_UP:
							shareLayout.setBackgroundResource(R.drawable.icon_background);
							break;
						}
						return true;
					}
				});

				holder.shareImage.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							shareLayout.setBackgroundResource(R.drawable.icon_background_right);
							break;

						case MotionEvent.ACTION_UP:
							shareLayout.setBackgroundResource(R.drawable.icon_background);
							break;
						}
						return true;
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return convertView;
		}

		class ViewHolder {
			public ScaleImageView photeImage;
			public ImageView playerImage;
			public ImageView loveImage;
			public ImageView shareImage;
			public TextView descText;
			public TextView countText;
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
