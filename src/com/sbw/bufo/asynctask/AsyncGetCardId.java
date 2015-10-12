package com.sbw.bufo.asynctask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sbw.bufo.networkhelper.BufoRestClient;
import com.sbw.bufo.util.NetworkAPI;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;

public class AsyncGetCardId extends AsyncTask<Void, Void, Void> {

	private String responseData;
	private String cardId;
	private String status;

	private String CARD_RESULT = "card_result";
	private String STATUS = "status";
	private String CARD_ID = "card_id";

	private ProgressDialog pDialog;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (pDialog != null) {
			pDialog.setMessage("Loading... ");	
			pDialog.setCancelable(false);
			pDialog.show();
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			responseData = BufoRestClient.getInstantData(NetworkAPI.BASE_URL
					+ NetworkAPI.URL_NEWCARD);
			responseData.replace("null", "");

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (responseData != null && responseData.toString().length() > 0) {
			try {
				JSONObject response = new JSONObject(responseData);
				JSONArray jsonArray = response.getJSONArray(CARD_RESULT);
				JSONObject jsonCardDetails = jsonArray.getJSONObject(0);
				if (jsonCardDetails.has(CARD_ID)) {
					cardId = jsonCardDetails.optString(CARD_ID);
				}
				if (jsonCardDetails.has(STATUS)) {
					status = jsonCardDetails.optString(STATUS);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (pDialog != null) {
			pDialog.dismiss();
		}
		try {
			if (status.equalsIgnoreCase("success") && !TextUtils.isEmpty(cardId)) {
				mGetCardid.onSuccess(cardId);
			} else {
				mGetCardid.onError();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private GetCardid mGetCardid;

	public GetCardid getmGetCardid() {
		return mGetCardid;
	}

	public void setmGetCardid(GetCardid mGetCardid) {
		this.mGetCardid = mGetCardid;
	}

	public interface GetCardid {
		public void onSuccess(String cardid);

		public void onError();

	}

}
