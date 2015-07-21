/**
 * 
 */
package com.sbw.bufo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * @author Sumanta
 *
 */
public class Preference {

	private Context mContext = null;
	
	public Preference(Context mContext){
		this.mContext = mContext;
	}

	
	private String getString(String key, String def) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		String s = prefs.getString(key, def);
		return s;
	}

	private void setString(String key, String val) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor e = prefs.edit();
		e.putString(key, val);
		e.commit();
	}
	
	public void clearPreference(){
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Editor e = prefs.edit();
		e.clear();
		e.commit();
	}
	
	public void setVendorId(String VendorId){
		setString("VendorId", VendorId);
	}
	
	public String getVendorId(){
		return getString("VendorId", "");
	}
	
	public void setVendorName(String VendorName){
		setString("VendorName", VendorName);
	}
	
	public String getVendorName(){
		return getString("VendorName", "");
	}
}
