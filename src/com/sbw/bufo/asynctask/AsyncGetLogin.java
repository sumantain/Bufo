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
import com.sbw.bufo.interactive.onGetLogin;
import com.sbw.bufo.networkhelper.BufoRestClient;
import com.sbw.bufo.util.NetworkAPI;

/**
 * @author Sumanta
 *
 */
public class AsyncGetLogin extends AsyncTask<String, Void, String>{
	private Context mContext;
	onGetLogin monGetLogin;
	
	private JSONObject requestObject;
	
	private String responseData;
	
	public AsyncGetLogin(Context mContext, onGetLogin monGetLogin) {
		this.mContext = mContext;
		this.monGetLogin = monGetLogin;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... params) {
		try {
			requestObject = new JSONObject();
			requestObject.put(mContext.getResources().getString(R.string.api_login_id), params[0]);
			requestObject.put(mContext.getResources().getString(R.string.api_Password), params[1]);
			
			
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		responseData = BufoRestClient.postData(NetworkAPI.BASE_URL+NetworkAPI.URL_LOGIN, requestObject.toString());
		System.out.println(responseData);
		return responseData;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		try {
			if((new JSONObject(result).getJSONArray("login_result").getJSONObject(0).getString("status")).equalsIgnoreCase("success")){
				monGetLogin.onGetLoginSuccess(new JSONObject(result).getJSONArray("login_result").getJSONObject(0));
			}else if((new JSONObject(result).getJSONArray("login_result").getJSONObject(0).getString("status")).equalsIgnoreCase("fail")){
				monGetLogin.onGetLoginFaild();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}
