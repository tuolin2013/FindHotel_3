package com.findhotel.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.findhotel.R;
import com.findhotel.util.ListViewUtility;

public class MyCouponsAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private JSONArray list;

	public MyCouponsAdapter(Context mContext, JSONArray list) {
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
		convertView = mInflater.inflate(R.layout.list_item_my_coupons, null);
		holder.areaText = (TextView) convertView.findViewById(R.id.tv_area);
		holder.hotelText = (TextView) convertView.findViewById(R.id.tv_hotel);
		holder.reservationButton = (Button) convertView.findViewById(R.id.btn_reservation);
		holder.nestedListView = (ListView) convertView.findViewById(R.id.lv_nested);

		try {
			JSONObject jsonObject = list.getJSONObject(position);
			holder.areaText.setText(jsonObject.getString("area"));
			holder.hotelText.setText(jsonObject.getString("ghName"));
			JSONArray coupons = jsonObject.getJSONArray("coupon");
			MyCouponsNestedAdapter adapter = new MyCouponsNestedAdapter(mContext, coupons);
			holder.nestedListView.setAdapter(adapter);
			ListViewUtility.setListViewHeightBasedOnChildren(holder.nestedListView);

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
		public TextView areaText;
		public TextView hotelText;
		public Button reservationButton;
		public ListView nestedListView;

	}

}
