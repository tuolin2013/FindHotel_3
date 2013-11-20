package com.findhotel.adapter;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.findhotel.R;
import com.findhotel.activity.RoomListActivity;
import com.findhotel.widget.MyGridView;
import com.origamilabs.library.loader.ImageLoader;
import com.readystatesoftware.viewbadger.BadgeView;

public class HotelAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private JSONArray list;
	ImageLoader loader;
	HashMap<String, Integer> icon_lableHashMap;

	public HotelAdapter(Context mContext, JSONArray list) {
		this.mContext = mContext;
		mInflater = LayoutInflater.from(this.mContext);
		this.list = list;
		loader = new ImageLoader(mContext);
		icon_lableHashMap = new HashMap<String, Integer>();
		icon_lableHashMap.put("icon_v", R.drawable.icon_v);
		icon_lableHashMap.put("icon_camera", R.drawable.icon_camera);
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
		convertView = mInflater.inflate(R.layout.list_item_hotel, null);
		holder.areaText = (TextView) convertView.findViewById(R.id.tv_area);
		holder.hotel_nameText = (TextView) convertView.findViewById(R.id.tv_hotel_name);
		holder.priceText = (TextView) convertView.findViewById(R.id.tv_price);
		holder.order_amountText = (TextView) convertView.findViewById(R.id.tv_order_amount);
		holder.star_amountText = (TextView) convertView.findViewById(R.id.tv_star_amount);

		holder.labelView = (LinearLayout) convertView.findViewById(R.id.ll_label);
		holder.bigView = (MyGridView) convertView.findViewById(R.id.gv_big_photo);
		holder.smallView = (MyGridView) convertView.findViewById(R.id.gv_small_photo);
		holder.avatarView = (MyGridView) convertView.findViewById(R.id.gv_avatar);
		holder.coupon_photoView = (LinearLayout) convertView.findViewById(R.id.ll_coupon_photo);
		holder.detailsImageView = (ImageView) convertView.findViewById(R.id.iv_details);
		try {
			final JSONObject item = list.getJSONObject(position);

			JSONArray labels = item.getJSONArray("label");

			// int height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, mContext.getResources().getDisplayMetrics());
			LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 0, 10, 0);
			for (String s : icon_lableHashMap.keySet()) {
				ImageView img = new ImageView(mContext);
				img.setImageResource(icon_lableHashMap.get(s));
				img.setLayoutParams(layoutParams);
				holder.labelView.addView(img);
			}
			// for (int i = 0; i < labels.length(); i++) {
			// ImageView img = new ImageView(mContext);
			// img.setImageResource(R.drawable.ic_launcher);
			//
			// holder.labelView.addView(img);
			//
			// }

			JSONArray hotelImgs = item.getJSONArray("imgs");
			for (int i = 0; i < hotelImgs.length(); i++) {

			}
			String urls[] = { "http://pic1a.nipic.com/20090312/550365_095010052_2.jpg",
					"http://www.veryeast.cn/cms/Files/%E9%85%92%E5%BA%97%E6%88%BF%E9%97%B4%E6%95%88%E6%9E%9C%E5%9B%BE.jpg",
					"http://upload.17u.net/uploadpicbase/2009/06/12/aa/2009061217264411163.jpg" };
			String urls_2[] = { "http://i0.sinaimg.cn/travel/ul/2009/1230/U3325P704DT20091230135003.jpg",
					"http://i3.sinaimg.cn/travel/ul/2009/1230/U3325P704DT20091230135037.jpg",
					"http://images.china.cn/attachement/jpg/site1000/20090317/0019b91ebfe20b294eb90e.jpg",
					"http://www.lvyou114.com/member/6082/hotelphoto/2008-1-9-13-35-18.jpg",
					"http://www.gdhotel.org/HotelFile/UP_20071117121448.jpg",
					"http://upload.17u.net/uploadpicbase/2009/06/12/aa/2009061217264411163.jpg", "menu" };

			HotelImageAdapter adapter = new HotelImageAdapter(mContext, R.id.imageView1, urls, item);
			holder.bigView.setAdapter(adapter);

			HotelImageAdapter adapter2 = new HotelImageAdapter(mContext, R.id.imageView1, urls_2, item);
			holder.smallView.setAdapter(adapter2);

			JSONArray coupons = item.getJSONArray("coupon");
			AvatarAdapter avatarAdapter = new AvatarAdapter(mContext, coupons);
			holder.avatarView.setAdapter(avatarAdapter);

			holder.areaText.setText("[" + item.getString("area") + "]");
			holder.hotel_nameText.setText(item.getString("ghName"));
			holder.priceText.setText(item.getString("price"));
			holder.order_amountText.setText("ЖЉ(" + item.getString("orders") + ")");
			holder.star_amountText.setText("до(" + item.getString("stars") + ")");

			holder.detailsImageView.setTag(item);
			holder.detailsImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, RoomListActivity.class);
					intent.putExtra("json", item.toString());
					mContext.startActivity(intent);
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
		public TextView areaText;
		public TextView hotel_nameText;
		public TextView priceText;
		public TextView order_amountText;
		public TextView star_amountText;
		public LinearLayout labelView;
		public LinearLayout coupon_photoView;
		public ImageView detailsImageView;
		public MyGridView bigView;
		public MyGridView smallView;
		public MyGridView avatarView;

	}

}
