package com.findhotel.activity;

import static com.findhotel.constant.Constant.DEBUGGER;
import static com.findhotel.constant.Constant.PHOTO_SAVE_PATH;
import static com.findhotel.constant.Constant.WEB_SERVER_URL;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.findhotel.R;
import com.findhotel.adapter.VideoThumbnailAdapter;
import com.findhotel.media.MyCamera;
import com.findhotel.util.ExitApplication;
import com.findhotel.util.MyActionMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PhotographActivity extends SherlockActivity {
	SlidingMenu menu;
	TextView areaText, hotelText, countText;
	EditText speechText;
	Button publishButton;
	String extra;
	ImageView currentImageView, currentVideoView;
	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 3023;
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	private static final int VIDEO_REQUST_CODE = 3024;
	/* 拍照的照片存储位置 */
	private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/" + PHOTO_SAVE_PATH);
	private File mCurrentPhotoFile;// 照相机拍照得到的图片

	List<ImageView> imageViews;
	List<ImageView> videoViews;

	ExecutorService executorService = Executors.newCachedThreadPool();
	ProgressDialog progressDialog;
	Context mContext = PhotographActivity.this;
	ImageLoader mLoader = ImageLoader.getInstance();
	DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory().cacheOnDisc()
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(PhotographActivity.this);
		setContentView(R.layout.activity_photograph);
		menu = new MyActionMenu(PhotographActivity.this).initView();
		extra = getIntent().getStringExtra("data");
		initView();
		executorService.execute(new LoadDataRunnable());
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
		String title = getResources().getString(R.string.title_activity_photograph);
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

	private void initView() {
		initActionBar();

		imageViews = new ArrayList<ImageView>();
		videoViews = new ArrayList<ImageView>();

		imageViews.add((ImageView) findViewById(R.id.iv_photo_1));
		imageViews.add((ImageView) findViewById(R.id.iv_photo_2));
		imageViews.add((ImageView) findViewById(R.id.iv_photo_3));

		videoViews.add((ImageView) findViewById(R.id.iv_video_1));
		videoViews.add((ImageView) findViewById(R.id.iv_video_2));
		videoViews.add((ImageView) findViewById(R.id.iv_video_3));

		for (ImageView v : imageViews) {
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					currentImageView = (ImageView) v;
					doPickPhotoAction();
				}
			});
		}

		for (ImageView v : videoViews) {
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					currentVideoView = (ImageView) v;
					doShootingVideoAction();
				}
			});

		}

		areaText = (TextView) findViewById(R.id.tv_area);
		hotelText = (TextView) findViewById(R.id.tv_name);
		try {
			JSONObject jsonObject = new JSONObject(extra);
			areaText.setText("[" + jsonObject.getString("area") + "]");
			hotelText.setText(jsonObject.getString("ghName"));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		countText = (TextView) findViewById(R.id.tv_count);
		speechText = (EditText) findViewById(R.id.etv_speech);
		speechText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				countText.setText((140 - speechText.getText().toString().length()) + "");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		publishButton = (Button) findViewById(R.id.btn_publish);
		publishButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String srcIds = getSrcId();
				String note = speechText.getText().toString();
				if (TextUtils.isEmpty(srcIds)) {
					showAlertMessage("没有内容发表！");

				} else {
					RequestParams params = new RequestParams();
					params.put("appId", "appId");
					params.put("srcId", srcIds);
					params.put("notes", note);
					executorService.execute(new PublishRunnable(params));
				}

			}
		});

	}

	private String getSrcId() {
		String ids = "";
		for (ImageView v : imageViews) {
			Object tag = v.getTag();
			if (tag != null) {
				String id = tag.toString();
				if (!TextUtils.isEmpty(id)) {
					ids += id + ",";
				}
			}

		}
		for (ImageView v : videoViews) {
			Object tag = v.getTag();
			if (tag != null) {
				String id = tag.toString();
				if (!TextUtils.isEmpty(id)) {
					ids += id + ",";
				}
			}
		}
		return ids;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的
			final Bitmap photo = data.getParcelableExtra("data");
			String fileName = PHOTO_DIR.getPath() + "/" + getPhotoFileName();
			MyCamera.saveBitmap(fileName, photo);

			// cust_ImageView.setImageBitmap(bitmap);
			// base64 = MyCamera.parseBase64(bitmap);
			// 下面就是显示照片了
			System.out.println(photo);
			currentImageView.setImageBitmap(photo);
			try {
				File myFile = new File(fileName);
				JSONObject jsonObject = new JSONObject(extra);
				String ghId = jsonObject.getString("ghId");
				RequestParams params = new RequestParams();
				params.put("appId", "appId");
				params.put("ghId", ghId);
				params.put("fileName", myFile);
				executorService.execute(new UploadImageRunnable(params, currentImageView));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				showAlertMessage(e.getLocalizedMessage());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				showAlertMessage(e.getLocalizedMessage());
			}

			break;
		}
		case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
			doCropPhoto(mCurrentPhotoFile);
			break;
		}
		case VIDEO_REQUST_CODE: {
			if (null != data) {
				Uri uri = data.getData();
				if (uri == null) {
					return;
				} else {
					Cursor c = getContentResolver().query(uri, new String[] { MediaStore.MediaColumns.DATA }, null, null, null);// 根据返回的URI，查找数据库，获取视频的路径
					if (c != null && c.moveToFirst()) {
						String filePath = c.getString(0);
						Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MICRO_KIND);
						Bitmap watermark = BitmapFactory.decodeResource(getResources(), R.drawable.icon_video);
						Bitmap scaleBitmap = MyCamera.lessenBitmap(watermark, 36, 36);
						Bitmap newBitmap = MyCamera.createWatermark(bitmap, scaleBitmap);
						currentVideoView.setImageBitmap(newBitmap);
						Log.d("test", filePath);
					}

				}
			}
		}

		}
	}

	private void doShootingVideoAction() {
		Context context = PhotographActivity.this;

		// Wrap our context to inflate list items using correct theme
		final Context dialogContext = new ContextThemeWrapper(context, R.style.Theme_Styled);
		String cancel = "返回";
		String[] choices;
		choices = new String[2];
		choices[0] = "录制视频  ";
		choices[1] = "从现有视频中选择  ";
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext, android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
		builder.setTitle("请选择");
		builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				case 0: {
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
						doShootingVideo();
					} else {
					}
					break;

				}
				case 1:
					doChooseVideo();
					break;
				}
			}

		});
		builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		});
		builder.create().show();
	}

	void doShootingVideo() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, VIDEO_REQUST_CODE);
		startActivityForResult(intent, VIDEO_REQUST_CODE);
	}

	void doChooseVideo() {
		Context context = PhotographActivity.this;
		LayoutInflater mInflater = LayoutInflater.from(context);
		Builder builder = new AlertDialog.Builder(context);
		View dialogView = mInflater.inflate(R.layout.dialog_video_thumbnail_list, null);
		GridView mListView = (GridView) dialogView.findViewById(R.id.gv_thumbnail);
		VideoThumbnailAdapter adapter = new VideoThumbnailAdapter(context);
		mListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		builder.setTitle("请选择").setView(dialogView).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		}).show();

	}

	private void doPickPhotoAction() {
		Context context = PhotographActivity.this;

		// Wrap our context to inflate list items using correct theme
		final Context dialogContext = new ContextThemeWrapper(context, R.style.Theme_Styled);
		String cancel = "返回";
		String[] choices;
		choices = new String[2];
		choices[0] = "拍照  "; // 拍照
		choices[1] = "从相册中选择  "; // 从相册中选择
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext, android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
		builder.setTitle("请选择");
		builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				case 0: {
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
						doTakePhoto();// 用户点击了从照相机获取
					} else {
					}
					break;

				}
				case 1:
					doPickPhotoFromGallery();// 从相册中去获取
					break;
				}
			}

		});
		builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		});
		builder.create().show();
	}

	private void doTakePhoto() {
		try {
			// Launch camera to take photo for selected contact
			PHOTO_DIR.mkdirs();// 创建照片的存储目录
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "找不到图片", Toast.LENGTH_LONG).show();
		}

	}

	private Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	private String getPhotoFileName() {
		String uuid = UUID.randomUUID().toString();
		return uuid + ".jpg";
	}

	private void doCropPhoto(File f) {
		try {
			// 启动gallery去剪辑这个照片
			final Intent intent = getCropImageIntent(Uri.fromFile(f));
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (Exception e) {
			Toast.makeText(this, "找不到图片", Toast.LENGTH_LONG).show();
		}

	}

	// 请求Gallery程序
	protected void doPickPhotoFromGallery() {
		try {
			// Launch picker to choose photo for selected contact
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		}
	}

	// 封装请求Gallery的intent
	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("return-data", true);
		return intent;
	}

	/**
	 * Constructs an intent for image cropping. 调用图片剪辑程序
	 */
	public static Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("return-data", true);
		return intent;
	}

	class LoadDataRunnable implements Runnable {
		RequestParams params = new RequestParams();

		private Handler loadHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				progressDialog.dismiss();
				switch (msg.what) {
				case 0:
					String result = (String) msg.obj;
					try {
						JSONObject json = new JSONObject(result);
						JSONArray images = json.getJSONArray("img");
						JSONArray videos = json.getJSONArray("video");

						for (ImageView iv : imageViews) {
							for (int i = 0; i < images.length(); i++) {
								JSONObject temp = images.getJSONObject(i);
								String src = temp.getString("mUrl");
								String srcId = temp.getString("srcId");
								iv.setTag(srcId);
								mLoader.displayImage(src, iv);
							}
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

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			String webUrl = WEB_SERVER_URL + "/zzd/view/v1/uploadPhotos";
			try {
				JSONObject jsonObject = new JSONObject(extra);
				String ghId = jsonObject.getString("ghId");
				params.put("appId", "appId");
				params.put("ghId", ghId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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

	class UploadImageRunnable implements Runnable {
		RequestParams params;
		ImageView targetView;

		public UploadImageRunnable(RequestParams params, ImageView targetView) {
			super();
			this.params = params;
			this.targetView = targetView;
		}

		private Handler uploadHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					String result = (String) msg.obj;
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						if ("200".equals(code)) {
							String mUrl = json.getString("mUrl");
							String srcId = json.getString("srcId");
							targetView.setTag(srcId);

						} else {
							showAlertMessage("上传失败");
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

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			String webUrl = WEB_SERVER_URL + "/zzd/view/v1/uploadMyPhoto";
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(1000 * 60);
			// client.setBasicAuth("Authorization", "Basic MTM3OTgwNDAyMzk6ZWM4YTcxMWYtNGI0OS0xMWUzLTg3MTUtMDAxNjNlMDIxMzQz");
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
					progressDialog = ProgressDialog.show(mContext, null, "正在上传，请稍候...", true, false);
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
					uploadHandler.obtainMessage(0, -1, -1, arg0).sendToTarget();

				}
			});
			Looper.loop();
		}

	}

	class UploadVideoRunnable implements Runnable {
		RequestParams params;
		ImageView targetView;

		public UploadVideoRunnable(RequestParams params, ImageView targetView) {
			super();
			this.params = params;
			this.targetView = targetView;
		}

		private Handler uploadHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					String result = (String) msg.obj;
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						if ("200".equals(code)) {
							String mUrl = json.getString("mUrl");
							String srcId = json.getString("srcId");
							targetView.setTag(srcId);

						} else {
							showAlertMessage("上传失败");
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

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			String webUrl = WEB_SERVER_URL + "/zzd/view/v1/uploadMyVideo";
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(1000 * 60);
			// client.setBasicAuth("Authorization", "Basic MTM3OTgwNDAyMzk6ZWM4YTcxMWYtNGI0OS0xMWUzLTg3MTUtMDAxNjNlMDIxMzQz");
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
					progressDialog = ProgressDialog.show(mContext, null, "正在上传，请稍候...", true, false);
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
					uploadHandler.obtainMessage(0, -1, -1, arg0).sendToTarget();

				}
			});
			Looper.loop();
		}

	}

	class PublishRunnable implements Runnable {
		RequestParams params;

		public PublishRunnable(RequestParams params) {
			super();
			this.params = params;
		}

		private Handler publishHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					String result = (String) msg.obj;
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						if ("200".equals(code)) {

						} else {
							showAlertMessage("发表失败");
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

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			String webUrl = WEB_SERVER_URL + "/zzd/view/v1/showMe";
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(1000 * 60);
			// client.setBasicAuth("Authorization", "Basic MTM3OTgwNDAyMzk6ZWM4YTcxMWYtNGI0OS0xMWUzLTg3MTUtMDAxNjNlMDIxMzQz");
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
					progressDialog = ProgressDialog.show(mContext, null, "正在处理，请稍候...", true, false);
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
					publishHandler.obtainMessage(0, -1, -1, arg0).sendToTarget();

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
