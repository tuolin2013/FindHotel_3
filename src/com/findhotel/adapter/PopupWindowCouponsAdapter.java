package com.findhotel.adapter;

import static com.findhotel.constant.Constant.DEBUGGER;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.findhotel.R;

public class PopupWindowCouponsAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private JSONArray list;
	private Button exchangeButton;
	private HashMap<String, String> exchageHashMap;
	private boolean hasChange = false;

	public PopupWindowCouponsAdapter(Context mContext, JSONArray list, Button button) {
		this.mContext = mContext;
		mInflater = LayoutInflater.from(this.mContext);
		this.list = list;
		this.exchangeButton = button;
		exchageHashMap = new HashMap<String, String>();

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
		convertView = mInflater.inflate(R.layout.list_item_my_coupons_popup, null);
		holder.areaText = (TextView) convertView.findViewById(R.id.tv_area);
		holder.couponNumText = (TextView) convertView.findViewById(R.id.tv_coupon_num);
		holder.nameText = (TextView) convertView.findViewById(R.id.tv_hotel_name);
		holder.plusImage = (ImageView) convertView.findViewById(R.id.iv_plus);
		holder.minusImage = (ImageView) convertView.findViewById(R.id.iv_minus);
		holder.couponsEditText = (EditText) convertView.findViewById(R.id.etv_coupon);

		try {
			JSONObject jsonObject = list.getJSONObject(position);
			holder.areaText.setText("[" + jsonObject.getString("area") + "]");
			holder.couponNumText.setText("(" + jsonObject.getString("cnt") + "’≈)");
			holder.nameText.setText(jsonObject.getString("ghName"));
			holder.couponsEditText.setText(jsonObject.getString("cnt"));
			// holder.couponsEditText.setText("0");
			holder.couponsEditText.setTag(jsonObject.getString("ghId"));
			final EditText couponsEditText = holder.couponsEditText;
			final int max = jsonObject.getInt("cnt");

			holder.plusImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int current = Integer.parseInt(couponsEditText.getText().toString());
					if (current < max) {
						++current;
						couponsEditText.setText(current + "");

					}

				}
			});
			holder.minusImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int current = Integer.parseInt(couponsEditText.getText().toString());
					if (current > 0) {
						--current;
						couponsEditText.setText(current + "");
					}
				}
			});
			holder.couponsEditText.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					hasChange = true;
					int usageCnt = 0;
					for (int i = 0; i < parent.getChildCount(); i++) {
						EditText tempEditText = (EditText) parent.getChildAt(i).findViewById(R.id.etv_coupon);
						int temp = Integer.parseInt(tempEditText.getText().toString());
						usageCnt += temp;
						setExchageValue(tempEditText.getTag().toString(), tempEditText.getText().toString());
					}
					exchangeButton.setText("∂“ªª" + usageCnt + "’≈");

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub

				}
			});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (DEBUGGER) {
				Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			}
		}
		// convertView.setTag(holder);
		// } else {
		// holder = (ViewHolder) convertView.getTag();
		// }
		return convertView;
	}

	public int getTotalCoupons() {
		int total = 0;
		for (int i = 0; i < list.length(); i++) {
			try {
				JSONObject temp = list.getJSONObject(i);
				int cnt = temp.getInt("cnt");
				total += cnt;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return total;

	}

	public HashMap<String, String> getExchageHashMap() {
		if (hasChange == false) {
			for (int i = 0; i < list.length(); i++) {
				try {
					JSONObject temp = list.getJSONObject(i);
					exchageHashMap.put(temp.getString("ghId"), temp.getString("cnt"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return exchageHashMap;
	}

	private void setExchageValue(String ghId, String couponNum) {
		exchageHashMap.put(ghId, couponNum);

	}

	public final class ViewHolder {
		public TextView areaText;
		public TextView couponNumText;
		public TextView nameText;
		public ImageView plusImage;
		public ImageView minusImage;
		public EditText couponsEditText;
	}

}
