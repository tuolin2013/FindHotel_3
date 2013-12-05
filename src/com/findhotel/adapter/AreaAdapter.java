package com.findhotel.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.findhotel.R;
import com.findhotel.activity.HotelListActivity;
import com.findhotel.widget.FloatView;

public class AreaAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private JSONArray list;

	public AreaAdapter(Context mContext, JSONArray list) {
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
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_item_area, null);
			holder.themeText = (TextView) convertView.findViewById(R.id.tv_theme);
			holder.floatView = (FloatView) convertView.findViewById(R.id.floatView1);
			// holder.detailLayout = (LinearLayout) convertView.findViewById(R.id.ll_details);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			JSONObject jsonObject = list.getJSONObject(position);
			holder.themeText.setText(jsonObject.getString("theme"));
			JSONArray jsonAreas = jsonObject.getJSONArray("data");
			// showArea(jsonAreas, holder.detailLayout);
			holder.floatView.removeAllViews();
			for (int i = 0; i < jsonAreas.length(); i++) {
				JSONObject obj = jsonAreas.getJSONObject(i);
				Button btn = new Button(mContext);
				btn.setText(obj.getString("name"));
				btn.setTag(obj.get("name"));
				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mContext, HotelListActivity.class);
						intent.putExtra("category", v.getTag().toString());
						intent.putExtra("action", "show_map");
						mContext.startActivity(intent);
					}
				});
				holder.floatView.addView(btn);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return convertView;
	}

	public final class ViewHolder {
		public ImageView arrowImage;
		// public LinearLayout detailLayout;
		public FloatView floatView;
		public TextView themeText;

	}

	void showArea(JSONArray areas, LinearLayout target) {
		target.removeAllViews();
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		int text_size = 18, text_width, text_heigth, wrap_height;
		int margin_left, margin_right, margin_top, margin_bottom;

		text_size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, text_size, dm);
		text_heigth = (int) mContext.getResources().getDimension(R.dimen.show_area_text_height);
		margin_left = (int) mContext.getResources().getDimension(R.dimen.show_area_margin_left);
		margin_right = (int) mContext.getResources().getDimension(R.dimen.show_area_margin_right);
		margin_top = (int) mContext.getResources().getDimension(R.dimen.show_area_margin_top);
		margin_bottom = (int) mContext.getResources().getDimension(R.dimen.show_area_margin_bottom);
		wrap_height = (int) mContext.getResources().getDimension(R.dimen.show_area_wrap_height);

		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;

		// tab.setStretchAllColumns(true);
		List<TextView> textViews = new ArrayList<TextView>();
		int sum = areas.length();
		int column = 3;
		int row = 0;
		int otiose = sum % column;
		row = sum / column;

		LinearLayout.LayoutParams params_container = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params_container.gravity = Gravity.CENTER;
		target.setLayoutParams(params_container);

		LinearLayout.LayoutParams params_normal = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, wrap_height);
		params_normal.gravity = Gravity.CENTER;
		params_normal.setMargins(2, 10, 3, 20);

		LinearLayout.LayoutParams params_otiose = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, wrap_height);
		params_otiose.gravity = Gravity.CENTER;
		params_otiose.setMargins(0, 10, 3, 20);

		// for (int i = 0; i < row; i++) {
		// LinearLayout tr = new LinearLayout(mContext);
		// tr.setOrientation(LinearLayout.HORIZONTAL);
		// tr.setBackgroundResource(R.drawable.square_white);
		// // tr.setLayoutParams(params_normal);
		// container.addView(tr, params_normal);
		// }

		for (int i = 0; i < areas.length(); i++) {
			TextView mTextView = new TextView(mContext);
			try {
				JSONObject obj = areas.getJSONObject(i);
				mTextView.setText(obj.getString("name"));
				mTextView.setTextColor(Color.parseColor("#343434"));
				mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				mTextView.setHeight(text_heigth);
				mTextView.setWidth(width / 3 - 20);
				mTextView.setGravity(Gravity.CENTER);
				mTextView.setTag(obj.getString("name"));
				mTextView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mContext, HotelListActivity.class);
						intent.putExtra("category", v.getTag().toString());
						intent.putExtra("action", "show_map");
						mContext.startActivity(intent);
					}
				});
				textViews.add(mTextView);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (int i = 0; i < row; i++) {
			LinearLayout tr = new LinearLayout(mContext);
			tr.setOrientation(LinearLayout.HORIZONTAL);
			tr.setBackgroundResource(R.drawable.square_white);
			tr.setLayoutParams(params_normal);
			for (int j = i; j < (i + 1) * column; j++) {
				TextView temp = textViews.get(j);
				if ((j + 1) % 3 != 0) {
					Resources res = mContext.getResources();
					Drawable myImage = res.getDrawable(R.drawable.divider);
					temp.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
				}
				tr.addView(temp);

			}
			target.addView(tr, params_normal);
		}

		if (otiose != 0) {
			LinearLayout tr = new LinearLayout(mContext);
			tr.setOrientation(LinearLayout.HORIZONTAL);
			tr.setBackgroundResource(R.drawable.square_white);
			tr.setLayoutParams(params_otiose);
			int start = areas.length() - otiose;
			for (int i = start; i < areas.length(); i++) {
				TextView temp = textViews.get(i);
				if (start + 1 != areas.length()) {
					Resources res = mContext.getResources();
					Drawable myImage = res.getDrawable(R.drawable.divider);
					temp.setCompoundDrawablesWithIntrinsicBounds(null, null, myImage, null);
				}
				tr.addView(temp);

			}
			target.addView(tr);
		}

	}
}
