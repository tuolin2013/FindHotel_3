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
import com.origamilabs.library.loader.ImageLoader;

public class ChoiceAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private JSONArray list;
	ImageLoader loader;

	public ChoiceAdapter(Context mContext, JSONArray list) {
		this.mContext = mContext;
		mInflater = LayoutInflater.from(this.mContext);
		this.list = list;
		loader = new ImageLoader(mContext);
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
		convertView = mInflater.inflate(R.layout.list_item_choice, null);
		holder.iconImage = (ImageView) convertView.findViewById(R.id.iv_icon);
		holder.titleText = (TextView) convertView.findViewById(R.id.tv_title);
		holder.countText = (TextView) convertView.findViewById(R.id.tv_count);

		try {
			JSONObject item = list.getJSONObject(position);
			holder.titleText.setText(item.getString("title"));
			holder.countText.setText(item.getString("hotelCnt"));
			convertView.setTag(item.getString("ctg"));
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, HotelListActivity.class);
					intent.putExtra("category", "professional_go");
					intent.putExtra("ctg", v.getTag().toString());
					mContext.startActivity(intent);
				}
			});
			loader.DisplayImage(item.getString("imgUrl"), holder.iconImage);

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
		public ImageView iconImage;
		public TextView titleText;
		public TextView countText;

	}

}
