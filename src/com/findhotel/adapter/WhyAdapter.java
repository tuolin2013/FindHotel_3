package com.findhotel.adapter;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.findhotel.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class WhyAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private JSONArray list;
	DisplayImageOptions options;
	ImageLoader mLoader;
	HashMap<String, Integer> icon_lableHashMap;

	public WhyAdapter(Context mContext, JSONArray list) {
		this.mContext = mContext;
		mInflater = LayoutInflater.from(this.mContext);
		this.list = list;
		mLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory().cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
		icon_lableHashMap = new HashMap<String, Integer>();
		icon_lableHashMap.put("about_50", R.drawable.about_50);
		icon_lableHashMap.put("about_60", R.drawable.about_60);
		icon_lableHashMap.put("about_70", R.drawable.about_70);
		icon_lableHashMap.put("about_80", R.drawable.about_80);

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
		convertView = mInflater.inflate(R.layout.list_item_why, null);
		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_icon);
		holder.countText = (TextView) convertView.findViewById(R.id.tv_text);

		try {
			JSONObject obj = list.getJSONObject(position);
			String photeUrl = obj.getString("icon");
			String count = obj.getString("text");
			// mLoader.displayImage(photeUrl, holder.imageView);
			if (icon_lableHashMap.containsKey(photeUrl)) {
				holder.imageView.setImageResource(icon_lableHashMap.get(photeUrl));
			}

			holder.countText.setText(count);
		} catch (JSONException e) {
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
		ImageView imageView;
		TextView countText;

	}

}
