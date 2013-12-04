package com.findhotel.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.findhotel.R;
import com.findhotel.activity.HotelPhotoGalleryActivity;
import com.findhotel.activity.PhotographActivity;
import com.findhotel.widget.SquareImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

public class HotelImageAdapter extends BaseAdapter {
	private ImageLoader mLoader;
	Context context;
	DisplayImageOptions options;
	JSONObject extraData;
	JSONArray images;

	public HotelImageAdapter(Context context, JSONArray images, JSONObject json) {
		this.images = images;
		extraData = json;
		mLoader = ImageLoader.getInstance();
		this.context = context;
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory().cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.images.length();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub

		try {
			return images.get(position);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater layoutInflator = LayoutInflater.from(context);
			convertView = layoutInflator.inflate(R.layout.row_staggered, null);
			holder = new ViewHolder();
			holder.imageView = (SquareImageView) convertView.findViewById(R.id.imageView1);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		try {
			final JSONObject obj = this.images.getJSONObject(position);
			String url = obj.getString("mUrl");
			if (url.equals("menu")) {
				holder.imageView.setImageResource(R.drawable.icon_big_camera);
				FrameLayout f = (FrameLayout) convertView.findViewById(R.id.fl_container);
				f.setBackgroundResource(R.drawable.card_border_dashed);

				holder.imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, PhotographActivity.class);
						intent.putExtra("data", extraData.toString());
						context.startActivity(intent);
					}
				});
			} else {
				mLoader.displayImage(url, holder.imageView, options);
				holder.imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, HotelPhotoGalleryActivity.class);
						intent.putExtra("data", extraData.toString());
						intent.putExtra("image_data", obj.toString());

						context.startActivity(intent);
					}
				});
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return convertView;
	}

	static class ViewHolder {
		SquareImageView imageView;
	}

}
