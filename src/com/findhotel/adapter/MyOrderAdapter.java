package com.findhotel.adapter;

import static com.findhotel.constant.Constant.DEBUGGER;
import static com.findhotel.constant.Constant.WEB_SERVER_URL;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.findhotel.R;
import com.findhotel.activity.HaggleAnswerActivity;
import com.findhotel.activity.MyOrderActivity;
import com.findhotel.activity.OrderDetails_State_CheckedActivity;
import com.findhotel.activity.OrderDetails_State_ConfirmActivity;
import com.findhotel.activity.OrderDetails_State_ConfirmRoomActivity;
import com.findhotel.activity.OrderDetails_State_WaitCheckInActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyOrderAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private JSONArray list;
	String orderType;
	private ImageLoader mLoader;
	DisplayImageOptions options;
	ExecutorService executorService = Executors.newCachedThreadPool();

	public MyOrderAdapter(Context mContext, JSONArray list, String orderType) {
		this.mContext = mContext;
		mInflater = LayoutInflater.from(this.mContext);
		this.list = list;
		this.orderType = orderType;
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
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_item_my_order, null);
			holder.areaText = (TextView) convertView.findViewById(R.id.tv_area);
			holder.hotelText = (TextView) convertView.findViewById(R.id.tv_hotel_name);
			holder.dateText = (TextView) convertView.findViewById(R.id.tv_date);
			holder.iconImage = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.arrowImage = (ImageView) convertView.findViewById(R.id.iv_arrow);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		try {
			JSONObject jsonObject = list.getJSONObject(position);
			holder.areaText.setText("(" + jsonObject.getString("area") + ")");
			holder.hotelText.setText(jsonObject.getString("ghName"));
			SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf_2 = new SimpleDateFormat("EE,MM月dd日");
			String start = jsonObject.getString("startDate");
			String end = jsonObject.getString("endDate");
			Date startDate = sdf_1.parse(start);
			Date endDate = sdf_1.parse(end);
			String dayString = sdf_2.format(startDate) + "-" + sdf_2.format(endDate);
			holder.dateText.setText(dayString);
			String type = jsonObject.getString("ordType");
			final String orderId = jsonObject.getString("orderId");
			holder.arrowImage.setTag(jsonObject.getString("orderId"));
			if ("CG".equals(type)) {
				// mLoader.displayImage(jsonObject.getString("imgUrl"), holder.iconImage);
				holder.iconImage.setImageResource(R.drawable.ic_empty);
			} else {
				holder.iconImage.setImageResource(R.drawable.icon_bidding);
			}

			// 预订中
			if (orderType.equals("QRZ")) {
				if ("CG".equals(type)) {
					holder.arrowImage.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							RequestParams params = new RequestParams();
							params.put("appId", "appId");
							params.put("orderId", orderId);
							executorService.execute(new LoadOrderDetailsRunnable(params));
						}
					});
				} else {
					holder.arrowImage.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(mContext, HaggleAnswerActivity.class);
							intent.putExtra("orderId", orderId);
							((Activity) mContext).startActivityForResult(intent, MyOrderActivity.REQUST_ORDER_DETAILS);
						}
					});
				}

			}
			// 已入住
			if (orderType.equals("YRZ")) {
				holder.arrowImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext, OrderDetails_State_CheckedActivity.class);
						intent.putExtra("orderId", orderId);
						((Activity) mContext).startActivity(intent);
					}
				});

			}
			// 待入住
			if (orderType.equals("DRZ")) {
				holder.arrowImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext, OrderDetails_State_WaitCheckInActivity.class);
						intent.putExtra("orderId", orderId);
						((Activity) mContext).startActivityForResult(intent, MyOrderActivity.REQUST_ORDER_DETAILS);

					}
				});

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return convertView;
	}

	public final class ViewHolder {
		public TextView areaText;
		public TextView hotelText;
		public TextView dateText;
		public ImageView iconImage;
		public ImageView arrowImage;

	}

	class LoadOrderDetailsRunnable implements Runnable {
		RequestParams params;

		public LoadOrderDetailsRunnable(RequestParams params) {
			super();
			this.params = params;
		}

		@Override
		public void run() {

			Looper.prepare();
			String webUrl = WEB_SERVER_URL + "/zzd/book/v1/viewOrder1";
			AsyncHttpClient client = new AsyncHttpClient();
			client.addHeader("Authorization", "Basic MTM3OTgwNDAyMzk6ZWM4YTcxMWYtNGI0OS0xMWUzLTg3MTUtMDAxNjNlMDIxMzQz");
			client.post(mContext, webUrl, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					if (DEBUGGER) {
						Toast.makeText(mContext, arg1, Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onStart() {
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
					try {
						JSONObject json = new JSONObject(arg0);
						String status = json.getString("status");
						Intent intent = null;
						if ("DQR".equals(status)) {
							intent = new Intent(mContext, OrderDetails_State_ConfirmActivity.class);
						}
						if ("QRYF".equals(status)) {
							intent = new Intent(mContext, OrderDetails_State_ConfirmRoomActivity.class);
						}
						intent.putExtra("data", arg0);
						((Activity) mContext).startActivityForResult(intent, MyOrderActivity.REQUST_ORDER_DETAILS);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
			Looper.loop();

		}

	}

}
