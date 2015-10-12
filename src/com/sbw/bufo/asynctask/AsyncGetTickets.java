package com.sbw.bufo.asynctask;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sbw.bufo.dataset.TicketsDataItem;
import com.sbw.bufo.networkhelper.BufoRestClient;
import com.sbw.bufo.util.NetworkAPI;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

public class AsyncGetTickets extends AsyncTask<Void, Void, Void> {

	private String responseData = "";
	private String vendorID = "";
	private ArrayList<TicketsDataItem> arrTickets = new ArrayList<TicketsDataItem>();
	private GetAllTickets mGetAllTickets;
	/**
	 * Json Data key
	 */
	private String TICKETS = "tickets";
	private String TICKET_ID = "ticket_id";
	private String MOBILE = "mobile";
	private String IDENTIFIER = "identifier";
	private String STRAT_TIME = "start_time";
	
	private ProgressDialog pDialog;
	private Context mContext;

	public AsyncGetTickets(String vendorID,Context mContext) {
		this.mContext=mContext;
		this.vendorID = vendorID;
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
	protected Void doInBackground(Void... params) {
		String res="";
		try {
			res = BufoRestClient.getInstantData(NetworkAPI.BASE_URL
					+ NetworkAPI.URL_TICKETS + vendorID);
			responseData=res.replace("null", "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!TextUtils.isEmpty(responseData)) {
			try {
				JSONObject jsonobj = new JSONObject(responseData);
				if (jsonobj.has(TICKETS)) {
					JSONArray jsonArr = jsonobj.getJSONArray(TICKETS);
					JSONObject jsonsubObject;
					for (int i = 0; i < jsonArr.length(); i++) {
						TicketsDataItem mItem = new TicketsDataItem();
						jsonsubObject = jsonArr.getJSONObject(i);
						if (jsonsubObject.has(TICKET_ID)) {
							mItem.setTicket_ID(jsonsubObject
									.optString(TICKET_ID));
						}
						if (jsonsubObject.has(IDENTIFIER)) {
							mItem.setIdentifier(jsonsubObject
									.optString(IDENTIFIER));
						}
						if (jsonsubObject.has(MOBILE)) {
							mItem.setMobile(jsonsubObject.optString(MOBILE));
						}
						if (jsonsubObject.has(STRAT_TIME)) {
							mItem.setStartTime(jsonsubObject
									.optString(STRAT_TIME));
						}
						arrTickets.add(mItem);
					}
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
		if (pDialog!=null) {			
			pDialog.dismiss();
		}
		if (mGetAllTickets != null && arrTickets != null
				&& arrTickets.size() > 0) {
			mGetAllTickets.onSuccess(arrTickets);
		} else {
			mGetAllTickets.onError();
		}
	}

	public GetAllTickets getmGetAllTickets() {
		return mGetAllTickets;
	}

	public void setmGetAllTickets(GetAllTickets mGetAllTickets) {
		this.mGetAllTickets = mGetAllTickets;
	}

	public interface GetAllTickets {
		public void onSuccess(ArrayList<TicketsDataItem> arrTickets);

		public void onError();
	}

}
