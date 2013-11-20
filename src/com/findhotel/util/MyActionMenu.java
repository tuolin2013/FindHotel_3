package com.findhotel.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.findhotel.R;
import com.findhotel.activity.FeedbackActivity;
import com.findhotel.activity.MyCouponActivity;
import com.findhotel.activity.MyOrderActivity;
import com.findhotel.activity.MyPhotoActivity;
import com.findhotel.activity.MyProfileActivity;
import com.findhotel.activity.NoticeActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.readystatesoftware.viewbadger.BadgeView;

public class MyActionMenu {
	SlidingMenu menu;
	Context mContext;

	public MyActionMenu(Context mContext) {
		super();
		this.mContext = mContext;
		menu = new SlidingMenu(mContext);
		menu.setMode(SlidingMenu.RIGHT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity((Activity) mContext, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.action_menu);
	}

	public SlidingMenu initView() {
		int order_count = 10, notice_count = 8;
		ImageView orderImageView = (ImageView) menu.findViewById(R.id.iv_my_order);
//		BadgeView badge_order = new BadgeView(mContext, orderImageView);
//		badge_order.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//		badge_order.setText(order_count + "");
//		badge_order.show();

		TextView noticeTextView = (TextView) menu.findViewById(R.id.tv_notice);
//		BadgeView badge_notice = new BadgeView(mContext, noticeTextView);
//		badge_notice.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//		badge_notice.setText(notice_count + "");
//		badge_notice.show();
		menu.findViewById(R.id.iv_user_avatar).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menu.toggle();

				if (!isSameActivity(MyProfileActivity.class)) {
					Intent intent = new Intent(mContext, MyProfileActivity.class);
					mContext.startActivity(intent);
				}

			}
		});

		menu.findViewById(R.id.iv_more).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menu.toggle();
				if (!isSameActivity(MyPhotoActivity.class)) {
					Intent intent = new Intent(mContext, MyPhotoActivity.class);
					mContext.startActivity(intent);
				}
			}
		});

		menu.findViewById(R.id.ll_my_coupons).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menu.toggle();
				if (!isSameActivity(MyCouponActivity.class)) {
					Intent intent = new Intent(mContext, MyCouponActivity.class);
					mContext.startActivity(intent);
				}
			}
		});

		menu.findViewById(R.id.ll_my_notice).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menu.toggle();
				if (!isSameActivity(NoticeActivity.class)) {
					Intent intent = new Intent(mContext, NoticeActivity.class);
					mContext.startActivity(intent);
				}
			}
		});

		menu.findViewById(R.id.ll_feedback).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menu.toggle();
				if (!isSameActivity(FeedbackActivity.class)) {
					Intent intent = new Intent(mContext, FeedbackActivity.class);
					mContext.startActivity(intent);
				}
			}
		});

		menu.findViewById(R.id.iv_exit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							ExitApplication.getInstance().exit();
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							dialog.cancel();
							break;
						}
					}
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle("系统提示").setMessage("你确定要退出应用吗 ?").setPositiveButton("是", dialogClickListener)
						.setNegativeButton("否", dialogClickListener).show();
			}
		});

		menu.findViewById(R.id.ll_my_order).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menu.toggle();
				if (!isSameActivity(MyOrderActivity.class)) {
					Intent intent = new Intent(mContext, MyOrderActivity.class);
					mContext.startActivity(intent);
				}
			}
		});
		return menu;
	}

	private String getCurrentActivityClassName() {
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
		// String packageName = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
		String className = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
		return className;
	}

	private boolean isSameActivity(Class<?> cls) {
		return getCurrentActivityClassName().equals(cls.getName());
	}

}
