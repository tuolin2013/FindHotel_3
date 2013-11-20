package com.findhotel.activity;

import static com.findhotel.constant.Constant.PHOTO_SAVE_PATH;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.AlertDialog;
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
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.Editable;
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

public class PhotographActivity extends SherlockActivity {
	SlidingMenu menu;
	TextView areaText, hotelText, countText;
	EditText speechText;
	Button submitButton;
	String extra;
	ImageView currentImageView, currentVideoView;
	/* ������ʶ�������๦�ܵ�activity */
	private static final int CAMERA_WITH_DATA = 3023;
	/* ������ʶ����gallery��activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	private static final int VIDEO_REQUST_CODE = 3024;
	/* ���յ���Ƭ�洢λ�� */
	private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/" + PHOTO_SAVE_PATH);
	private File mCurrentPhotoFile;// ��������յõ���ͼƬ

	List<ImageView> imageViews;
	List<ImageView> videoViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Styled);
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(PhotographActivity.this);
		setContentView(R.layout.activity_photograph);
		menu = new MyActionMenu(PhotographActivity.this).initView();
		extra = getIntent().getStringExtra("data");
		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add("����").setIcon(R.drawable.ic_drawer_dark)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		String selected = (String) item.getTitle();
		if ("����".equals(selected)) {
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

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: {// ����Gallery���ص�
			final Bitmap photo = data.getParcelableExtra("data");
			MyCamera.saveBitmap(PHOTO_DIR.getPath() + "/" + getPhotoFileName(), photo);

			// cust_ImageView.setImageBitmap(bitmap);
			// base64 = MyCamera.parseBase64(bitmap);
			// ���������ʾ��Ƭ��
			System.out.println(photo);
			currentImageView.setImageBitmap(photo);

			break;
		}
		case CAMERA_WITH_DATA: {// ��������򷵻ص�,�ٴε���ͼƬ��������ȥ�޼�ͼƬ
			doCropPhoto(mCurrentPhotoFile);
			break;
		}
		case VIDEO_REQUST_CODE: {
			if (null != data) {
				Uri uri = data.getData();
				if (uri == null) {
					return;
				} else {
					Cursor c = getContentResolver().query(uri, new String[] { MediaStore.MediaColumns.DATA }, null, null, null);// ���ݷ��ص�URI���������ݿ⣬��ȡ��Ƶ��·��
					if (c != null && c.moveToFirst()) {
						String filPath = c.getString(0);
						Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filPath, Thumbnails.MICRO_KIND);
						Bitmap watermark = BitmapFactory.decodeResource(getResources(), R.drawable.icon_video);
						Bitmap scaleBitmap = MyCamera.lessenBitmap(watermark, 36, 36);
						Bitmap newBitmap = MyCamera.createWatermark(bitmap, scaleBitmap);
						currentVideoView.setImageBitmap(newBitmap);
						Log.d("test", filPath);
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
		String cancel = "����";
		String[] choices;
		choices = new String[2];
		choices[0] = "¼����Ƶ  ";
		choices[1] = "��������Ƶ��ѡ��  ";
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext, android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
		builder.setTitle("��ѡ��");
		builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				case 0: {
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED)) {// �ж��Ƿ���SD��
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
		builder.setTitle("��ѡ��").setView(dialogView).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

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
		String cancel = "����";
		String[] choices;
		choices = new String[2];
		choices[0] = "����  "; // ����
		choices[1] = "�������ѡ��  "; // �������ѡ��
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext, android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
		builder.setTitle("��ѡ��");
		builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				case 0: {
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED)) {// �ж��Ƿ���SD��
						doTakePhoto();// �û�����˴��������ȡ
					} else {
					}
					break;

				}
				case 1:
					doPickPhotoFromGallery();// �������ȥ��ȡ
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
			PHOTO_DIR.mkdirs();// ������Ƭ�Ĵ洢Ŀ¼
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// �����յ���Ƭ�ļ�����
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "�Ҳ���ͼƬ", Toast.LENGTH_LONG).show();
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
			// ����galleryȥ���������Ƭ
			final Intent intent = getCropImageIntent(Uri.fromFile(f));
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (Exception e) {
			Toast.makeText(this, "�Ҳ���ͼƬ", Toast.LENGTH_LONG).show();
		}

	}

	// ����Gallery����
	protected void doPickPhotoFromGallery() {
		try {
			// Launch picker to choose photo for selected contact
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		}
	}

	// ��װ����Gallery��intent
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
	 * Constructs an intent for image cropping. ����ͼƬ��������
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

}
