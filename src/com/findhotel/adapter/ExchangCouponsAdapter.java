package com.findhotel.adapter;

import static com.findhotel.constant.Constant.DEBUGGER;
import static com.findhotel.constant.Constant.WEB_SERVER_URL;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.findhotel.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ExchangCouponsAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private JSONArray list;
	View targetView;
	ImageLoader mLoader;
	DisplayImageOptions options;
	ExecutorService executorService = Executors.newCachedThreadPool();
	ProgressDialog progressDialog;
	HashMap<String, String> hashMap = new HashMap<String, String>();

	public ExchangCouponsAdapter(Context mContext, JSONArray list) {
		this.mContext = mContext;
		mInflater = LayoutInflater.from(this.mContext);
		this.list = list;
		mLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory().cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
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
		convertView = mInflater.inflate(R.layout.list_item_coupons, null);
		holder.couponsImage = (ImageView) convertView.findViewById(R.id.iv_coupons);
		holder.avatarImage = (ImageView) convertView.findViewById(R.id.iv_user_avatar);
		holder.mobileText = (TextView) convertView.findViewById(R.id.tv_mobile);
		holder.countText = (TextView) convertView.findViewById(R.id.tv_count);
		holder.exchageButton = (Button) convertView.findViewById(R.id.btn_exchange);

		try {
			JSONObject jsonObject = list.getJSONObject(position);
			holder.mobileText.setText(jsonObject.getString("phone"));
			holder.countText.setText(jsonObject.getString("cnt") + "��");

			mLoader.displayImage(jsonObject.getString("photoUrl"), holder.avatarImage, options);

			holder.exchageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					targetView = v;
					executorService.execute(new LoadMyCouponRunnable());
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

	class LoadMyCouponRunnable implements Runnable {

		@Override
		public void run() {
			Looper.prepare();
			String webUrl = WEB_SERVER_URL + "/zzd/coupon/v1/myCouponList";
			final RequestParams params = new RequestParams();
			params.put("appId", "appId");
			AsyncHttpClient client = new AsyncHttpClient();
			client.addHeader("Authorization", "Basic MTM3OTgwNDAyMzk6ZWM4YTcxMWYtNGI0OS0xMWUzLTg3MTUtMDAxNjNlMDIxMzQz");
			client.post(mContext, webUrl, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					if (DEBUGGER) {
						Toast.makeText(mContext, arg1, Toast.LENGTH_LONG).show();
					}
					progressDialog.dismiss();
				}

				@Override
				public void onStart() {
					progressDialog = ProgressDialog.show(mContext, null, "���ڼ��أ����Ժ�...", true, false);
					if (DEBUGGER) {
						Toast.makeText(mContext, params.toString(), Toast.LENGTH_LONG).show();
					}
					super.onStart();
				}

				@Override
				public void onSuccess(final String arg0) {
					if (DEBUGGER) {
						Toast.makeText(mContext, arg0, 1000 * 30).show();
					}
					showPopupWindowHandler.obtainMessage(0, -1, -1, arg0).sendToTarget();

				}
			});
			Looper.loop();
		}
	}

	private Handler showPopupWindowHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			progressDialog.dismiss();
			switch (msg.what) {
			case 0:
				String result = (String) msg.obj;
				View popupView = mInflater.inflate(R.layout.popup_window_my_coupons, null);
				ListView mListView = (ListView) popupView.findViewById(R.id.lv_my_coupons);
				Button exchangeButton = (Button) popupView.findViewById(R.id.btn_exchange);
				ImageView closeImage = (ImageView) popupView.findViewById(R.id.iv_close);

				final PopupWindow mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
				mPopupWindow.setTouchable(true);
				mPopupWindow.setOutsideTouchable(true);
				mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.card_border));
				try {
					JSONObject json = new JSONObject(result);
					JSONArray datasource = json.getJSONArray("coupon");
					PopupWindowCouponsAdapter adapter = new PopupWindowCouponsAdapter(mContext, datasource, exchangeButton);
					exchangeButton.setText("�һ�" + adapter.getTotalCoupons() + "��");
					hashMap = adapter.getExchageHashMap();
					mListView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					mPopupWindow.showAsDropDown(targetView);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				exchangeButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String ghId = "", cnt = "";
						for (String key : hashMap.keySet()) {
							ghId += key + ",";
							cnt += hashMap.get(key) + ",";

						}

						new AlertDialog.Builder(mContext).setTitle("ϵͳ��Ϣ").setMessage(ghId + " and " + cnt)
								.setNegativeButton("�ر�", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										Intent intent = new Intent();
										intent.putExtra("exra", hashMap);
										((Activity) mContext).setResult(Activity.RESULT_OK, intent);
										((Activity) mContext).finish();
										dialog.dismiss();

									}
								}).show();
					}
				});

				closeImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mPopupWindow.dismiss();

					}
				});

				break;

			default:
				break;
			}
		}

	};

	public final class ViewHolder {
		public ImageView couponsImage;
		public ImageView avatarImage;
		public TextView mobileText;
		public TextView countText;
		public Button exchageButton;

	}

}
