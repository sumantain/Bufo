/**
 * 
 */
package com.sbw.bufo.asynctask;

import org.json.JSONException;
import org.json.JSONObject;

import com.sbw.bufo.interactive.onAvailableResponse;
import com.sbw.bufo.networkhelper.BufoRestClient;
import com.sbw.bufo.util.NetworkAPI;
import com.sbw.bufo.util.Preference;

import android.content.Context;
import android.os.AsyncTask;

/**
 * @author Sumanta
 *
 */
public class AsyncGetAvailable extends AsyncTask<String, Void, String> {
	private Context mContext;
	private onAvailableResponse monBackGroundResponse;
	private Preference mPref = null;
	
	private String responseData;
	
	public AsyncGetAvailable(Context mContext, onAvailableResponse monBackGroundResponse) {
		this.mContext = mContext;
		this.monBackGroundResponse = monBackGroundResponse;
		this.mPref = new Preference(this.mContext);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... params) {
		
		try {
			responseData = BufoRestClient.getInstantData(NetworkAPI.BASE_URL+NetworkAPI.URL_AVAILABLE+mPref.getVendorId());
			responseData.replace("null", "");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return responseData.replace("null", "");
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if(result != null && !result.equalsIgnoreCase("")){
			try {
				monBackGroundResponse.onBackGroundResponseSuccess(true, Integer.parseInt(new JSONObject(result).getJSONArray("availability").getJSONObject(0).getString("available")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			monBackGroundResponse.onBackGroundResponseFalse(false);
		}
		
	}
}
