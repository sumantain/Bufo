/**
 * 
 */
package com.sbw.bufo.asynctask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.sbw.bufo.interactive.onCardStatus;
import com.sbw.bufo.networkhelper.BufoRestClient;
import com.sbw.bufo.util.NetworkAPI;

/**
 * @author Sumanta
 *
 */
public class AsyncGetCardState extends AsyncTask<String, Void, String> {
	@SuppressWarnings("unused")
	private Context mContext;
	private onCardStatus monCardStatus;
	
	private String responseData;
	
	public AsyncGetCardState(Context mContext, onCardStatus monCardStatus){
		this.mContext = mContext;
		this.monCardStatus = monCardStatus;
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
			responseData = BufoRestClient.getInstantData(NetworkAPI.BASE_URL+NetworkAPI.URL_STATE+params[0]);
			responseData.replace("null", "");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseData.replace("null", "");
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		if(result != null && !result.equalsIgnoreCase("")){
			try {
				if((new JSONObject(result).getJSONArray("card_info").getJSONObject(0).getString("status")).equalsIgnoreCase("100")){
					monCardStatus.onCardStatusSuccess(new JSONObject(result).getJSONArray("card_info").getJSONObject(0));
				}else if ((new JSONObject(result).getJSONArray("card_info").getJSONObject(0).getString("status")).equalsIgnoreCase("200")) {
					monCardStatus.onCardStatusFalse(new JSONObject(result).getJSONArray("card_info").getJSONObject(0));
				} 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			
		}
	}

}
