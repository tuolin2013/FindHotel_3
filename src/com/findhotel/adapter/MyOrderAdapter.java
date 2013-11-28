package com.findhotel.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.findhotel.R;
import com.findhotel.util.ListViewUtility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyOrderAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private JSONArray list;
	String orderType;
	private ImageLoader mLoader;
	DisplayImageOptions options;

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
			holder.areaText.setText(jsonObject.getString("area"));
			holder.hotelText.setText(jsonObject.getString("ghName"));
			SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf_2 = new SimpleDateFormat("EE,MM‘¬dd»’");
			String start = jsonObject.getString("startDate");
			String end = jsonObject.getString("endDate");
			Date startDate = sdf_1.parse(start);
			Date endDate = sdf_1.parse(end);
			String dayString = sdf_2.format(startDate) + "-" + sdf_2.format(endDate);
			holder.dateText.setText(dayString);
			String type = jsonObject.getString("ordType");
			holder.arrowImage.setTag(jsonObject.getString("orderId"));
			if ("CG".equals(type)) {
				mLoader.displayImage(jsonObject.getString("imgUrl"), holder.iconImage);
			} else if ("FB".equals(type)) {

				holder.arrowImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String orderId = v.getTag().toString();

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

}
