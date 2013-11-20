package com.findhotel.adapter;

import org.json.JSONObject;

import com.findhotel.R;
import com.findhotel.activity.PhotographActivity;
import com.findhotel.activity.StartActivity;
import com.findhotel.widget.SquareImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.origamilabs.library.views.ScaleImageView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

public class HotelImageAdapter extends ArrayAdapter<String> {
	private ImageLoader mLoader;
	Context context;
	DisplayImageOptions options;
	JSONObject extraData;

	public HotelImageAdapter(Context context, int textViewResourceId, String[] objects, JSONObject json) {
		super(context, textViewResourceId, objects);
		extraData = json;
		mLoader = ImageLoader.getInstance();
		this.context = context;
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory().cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater layoutInflator = LayoutInflater.from(getContext());
			convertView = layoutInflator.inflate(R.layout.row_staggered, null);
			holder = new ViewHolder();
			holder.imageView = (SquareImageView) convertView.findViewById(R.id.imageView1);
			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();

		if (getItem(position).equals("menu")) {
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
			mLoader.displayImage(getItem(position), holder.imageView, options);
			holder.imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(context, getItem(position), Toast.LENGTH_LONG).show();
				}
			});
		}

		return convertView;
	}

	static class ViewHolder {
		SquareImageView imageView;
	}
}
