package com.findhotel.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.findhotel.R;
import com.findhotel.activity.HotelListActivity;
import com.findhotel.widget.FloatView;

public class ExchangCouponsAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private JSONArray list;

	public ExchangCouponsAdapter(Context mContext, JSONArray list) {
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
		convertView = mInflater.inflate(R.layout.list_item_coupons, null);
		holder.couponsImage = (ImageView) convertView.findViewById(R.id.iv_coupons);
		holder.avatarImage = (ImageView) convertView.findViewById(R.id.iv_user_avatar);
		holder.mobileText = (TextView) convertView.findViewById(R.id.tv_mobile);
		holder.countText = (TextView) convertView.findViewById(R.id.tv_count);
		holder.exchageButton = (Button) convertView.findViewById(R.id.btn_exchange);

		try {
			JSONObject jsonObject = list.getJSONObject(position);
			holder.mobileText.setText(jsonObject.getString("phone"));
			holder.exchageButton.setTag(jsonObject.getString(""));
			holder.countText.setText(jsonObject.getString("cnt"));
			holder.exchageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

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
		public ImageView couponsImage;
		public ImageView avatarImage;
		public TextView mobileText;
		public TextView countText;
		public Button exchageButton;

	}

}
