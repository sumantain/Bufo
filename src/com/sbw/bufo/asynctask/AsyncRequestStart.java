/**
 * 
 */
package com.sbw.bufo.asynctask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;

import com.sbw.bufo.R;
import com.sbw.bufo.interactive.onRequestStart;
import com.sbw.bufo.networkhelper.BufoRestClient;
import com.sbw.bufo.util.NetworkAPI;

/**
 * @author Sumanta
 *
 */
public class AsyncRequestStart extends AsyncTask<String, Void, String> {
	private Context mContext;
	private onRequestStart monRequestStart;

	private JSONObject requestObject;
	private String responseData;

	public AsyncRequestStart(Context mContext, onRequestStart monRequestStart) {
		this.mContext = mContext;
		this.monRequestStart = monRequestStart;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(String)
	 */
	@Override
	protected String doInBackground(String... params) {
		try {
			requestObject = new JSONObject();
			requestObject.put(mContext.getResources().getString(R.string.api_card_id), params[0]);
			requestObject.put(mContext.getResources().getString(R.string.api_vendor_id), params[1]);
			requestObject.put(mContext.getResources().getString(R.string.api_mobile), params[2]);
			requestObject.put(mContext.getResources().getString(R.string.api_identifier), params[3]);
			requestObject.put(mContext.getResources().getString(R.string.api_class), params[4]);
			
			responseData = BufoRestClient.postData(NetworkAPI.BASE_URL+NetworkAPI.URL_REQ_START, requestObject.toString());
			
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		
		return responseData;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		try {
			if(((new JSONObject(result).getJSONArray("card_info")).getJSONObject(0).get("status")).equals("200")){
				monRequestStart.onRequestStartSuccess((new JSONObject(result).getJSONArray("card_info")).getJSONObject(0));
			}else{
				monRequestStart.onRequestStartFaild();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
