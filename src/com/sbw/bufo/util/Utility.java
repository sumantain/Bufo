/**
 * 
 */
package com.sbw.bufo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Sumanta
 *
 */
public class Utility {

	
	
	/**
	 * Purpose: internet checking
	 */
	public static boolean isOnline(Context mContext) {
		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		// test for connection for WIFI
		if (info != null && info.isAvailable() && info.isConnected()) {
			return true;
		}
		info = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		// test for connection for Mobile
		if (info != null && info.isAvailable() && info.isConnected()) {
			return true;
		}
		return false;
	}
}
