/**
 * 
 */
package com.sbw.bufo.asynctask;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.sbw.bufo.interactive.onGetClass;
import com.sbw.bufo.networkhelper.BufoRestClient;
import com.sbw.bufo.util.NetworkAPI;

/**
 * @author Sumanta
 *
 */
public class AsyncGetClass extends AsyncTask<String, Void, Void> {

	private String responseData;
	
	private onGetClass mOnGetClass;
	
	private ArrayList<String> product_id;
	private ArrayList<String> product_name;
	
	private Context mContext;
	private ProgressDialog pDialog;

	public AsyncGetClass(Context mContext,onGetClass onGetClass) {
		this.mContext=mContext;
		this.mOnGetClass =onGetClass;
		this.pDialog=new ProgressDialog(mContext);
	}

	@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pDialog!=null) {
				pDialog.setMessage("Loading... ");	
				pDialog.setCancelable(false);
				pDialog.show();
			}
		}
	
	@Override
	protected Void doInBackground(String... params) {

		try {
			responseData = BufoRestClient.getInstantData(NetworkAPI.BASE_URL+NetworkAPI.URL_PRODUCT+params[0]);
			responseData.replace("null", "");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if(responseData != null && responseData.toString().length() > 0){
				JSONObject jobj = new JSONObject(responseData);
				JSONArray jarr = jobj.getJSONArray("product_result");
				if(jarr.length() > 0){
					product_id = new ArrayList<String>();
					product_id.add("0");
					product_name = new ArrayList<String>();
					product_name.add("Select");
					for(int i = 0; i< jarr.length(); i++){
						product_id.add(jarr.getJSONObject(i).getString("product_id"));
						product_name.add(jarr.getJSONObject(i).getString("product_name"));
					}
				}
				
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (pDialog!=null) {			
			pDialog.dismiss();
		}
		mOnGetClass.onGetClassSucces(product_id, product_name);
	}
	
}
