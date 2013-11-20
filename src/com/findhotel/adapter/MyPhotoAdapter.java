package com.findhotel.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import com.origamilabs.library.views.ScaleImageView;

public class MyPhotoAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private JSONArray list;

	public MyPhotoAdapter(Context mContext, JSONArray list) {
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
		convertView = mInflater.inflate(R.layout.list_item_my_photo, null);
		holder.countText = (TextView) convertView.findViewById(R.id.tv_count);
		holder.descText = (TextView) convertView.findViewById(R.id.tv_desc);
		holder.shareImage = (ImageView) convertView.findViewById(R.id.iv_share);
		holder.loveImage = (ImageView) convertView.findViewById(R.id.iv_love);
		holder.photeImage = (ScaleImageView) convertView.findViewById(R.id.siv_my_photo);
		final LinearLayout shareLayout = (LinearLayout) convertView.findViewById(R.id.ll_share);
		;
		try {
			JSONObject jsonObject = list.getJSONObject(position);
			holder.descText.setText(jsonObject.getString("notes"));
			holder.countText.setText(jsonObject.getString("favors"));
			ImageLoader loader = new ImageLoader(mContext);
			loader.DisplayImage("http://www.365zhuti.com/wall/UploadPic/2012-8/365zhuti_20120831095616_1.jpg", holder.photeImage);

			holder.loveImage.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub

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
		// convertView.setTag(holder);
		// } else {
		// holder = (ViewHolder) convertView.getTag();
		// }
		return convertView;
	}

	public final class ViewHolder {
		public ScaleImageView photeImage;
		public ImageView loveImage;
		public ImageView shareImage;
		public TextView descText;
		public TextView countText;

	}

}
