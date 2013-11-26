package com.findhotel.util;

import android.text.TextUtils;

public class MyTextUtils {
	public static String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}

	public static String convertMobile(String mobile) {
		String result = "";
		if (TextUtils.isDigitsOnly(mobile)) {
			if (mobile.length() == 11) {
				String preFix = mobile.substring(0, 3);
				String middle = "****";
				String end = mobile.substring(7, 11);
				result = preFix + middle + end;

			}
		}
		return result;
	}

	public static String spliteMobile(String mobile) {
		String result = "";
		if (TextUtils.isDigitsOnly(mobile)) {
			if (mobile.length() == 11) {
				String preFix = mobile.substring(0, 3);
				String middle = mobile.substring(3, 7);
				String end = mobile.substring(7, 11);
				result = preFix + "-" + middle + "-" + end;

			}
		}
		return result;
	}

}
