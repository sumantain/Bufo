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
import com.sbw.bufo.interactive.onRequestEnd;
import com.sbw.bufo.networkhelper.BufoRestClient;
import com.sbw.bufo.util.NetworkAPI;

/**
 * @author Sumanta
 *
 */
public class AsyncRequestEnd extends AsyncTask<String, Void, String> {
	private Context mContext;
	public onRequestEnd monRequestEnd;
	
	private JSONObject requestObject;
	private String responseData;
	
	public AsyncRequestEnd(Context mContext, onRequestEnd monRequestEnd){
		this.mContext = mContext;
		this.monRequestEnd = monRequestEnd;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(String)
	 */
	@Override
	protected String doInBackground(String... params) {
		
		try {
			requestObject = new JSONObject();
			requestObject.put(mContext.getResources().getString(R.string.api_card_id), params[0]);
			requestObject.put(mContext.getResources().getString(R.string.api_vendor_id), params[1]);
			
			responseData = BufoRestClient.postData(NetworkAPI.BASE_URL+NetworkAPI.URL_REQ_END, requestObject.toString());
			
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
		if(result != null && result.length() > 0){
			try {
				if(new JSONObject(result).getJSONArray("receipt").getJSONObject(0).has("end_time")){
					monRequestEnd.onRequestEndSuccess(new JSONObject(result).getJSONArray("receipt").getJSONObject(0));
				}else{
					monRequestEnd.onRequestEndFaild();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			monRequestEnd.onRequestEndFaild();
		}
	}

}
