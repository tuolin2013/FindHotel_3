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

public class MyCouponsNestedAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private JSONArray list;

	public MyCouponsNestedAdapter(Context mContext, JSONArray list) {
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
		convertView = mInflater.inflate(R.layout.list_item_my_coupons_nested, null);
		holder.couponsImage = (ImageView) convertView.findViewById(R.id.iv_coupons);
		holder.lockOpenImage = (ImageView) convertView.findViewById(R.id.iv_lock_open);
		holder.giftImage = (ImageView) convertView.findViewById(R.id.iv_gift);
		holder.expiredTextView = (TextView) convertView.findViewById(R.id.tv_expired);

		try {
			JSONObject obj = list.getJSONObject(position);
			holder.expiredTextView.setText(obj.getString("expried") + "¹ýÆÚ");
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
		public ImageView giftImage;
		public ImageView lockOpenImage;
		public TextView expiredTextView;

	}

}
