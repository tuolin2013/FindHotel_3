package com.findhotel.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.findhotel.R;
import com.origamilabs.library.views.ScaleImageView;

public class VideoThumbnailAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private String[] src;

	public VideoThumbnailAdapter(Context mContext) {
		this.mContext = mContext;
		mInflater = LayoutInflater.from(this.mContext);

		ContentResolver contentResolver = mContext.getContentResolver();
		String[] projection = new String[] { MediaStore.Video.Media.DATA };
		Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null,
				MediaStore.Video.Media.DEFAULT_SORT_ORDER);
		cursor.moveToFirst();
		int fileNum = cursor.getCount();
		src = new String[fileNum];
		for (int counter = 0; counter < fileNum; counter++) {
			src[counter] = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
			cursor.moveToNext();
		}
		cursor.close();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.src.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return src[position];
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
		convertView = mInflater.inflate(R.layout.list_item_video_thumbnail, null);
		holder.thumbnailImage = (ScaleImageView) convertView.findViewById(R.id.iv_thumbnail);
		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(src[position], Thumbnails.MICRO_KIND);
		holder.thumbnailImage.setImageBitmap(bitmap);
		// convertView.setTag(holder);
		// } else {
		// holder = (ViewHolder) convertView.getTag();
		// }
		return convertView;
	}

	public final class ViewHolder {
		public ScaleImageView thumbnailImage;

	}

}
