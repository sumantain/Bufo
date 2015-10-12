/**
 * 
 */
package com.sbw.bufo.asynctask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.text.TextUtils;

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
	
//	private ProgressDialog pDialog;
	
	public AsyncRequestEnd(Context mContext, onRequestEnd monRequestEnd){
		this.mContext = mContext;
		this.monRequestEnd = monRequestEnd;
//		this.pDialog=new ProgressDialog(mContext);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
//		if (pDialog!=null) {
//			pDialog.setMessage("Loading... ");	
//			pDialog.setCancelable(false);
//			pDialog.show();
//		}
	}
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(String)
	 */
	@Override
	protected String doInBackground(String... params) {
		
		try {
			requestObject = new JSONObject();
			if (!TextUtils.isEmpty(params[0])) {
				requestObject.put(mContext.getResources().getString(R.string.api_card_id), params[0]);
			}
			if (!TextUtils.isEmpty(params[1])) {
				requestObject.put(mContext.getResources().getString(R.string.api_ticket_id), params[1]);
			}
			
			requestObject.put(mContext.getResources().getString(R.string.api_vendor_id), params[2]);
			
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
//		if (pDialog!=null) {			
//			pDialog.dismiss();
//		}
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
