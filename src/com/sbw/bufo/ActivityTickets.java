package com.sbw.bufo;

import java.util.ArrayList;

import com.sbw.bufo.R.menu;
import com.sbw.bufo.adapter.TicketsAdapter;
import com.sbw.bufo.asynctask.AsyncGetTickets;
import com.sbw.bufo.asynctask.AsyncGetTickets.GetAllTickets;
import com.sbw.bufo.dataset.TicketsDataItem;
import com.sbw.bufo.util.Preference;
import com.sbw.bufo.util.Utility;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class ActivityTickets extends Activity {

	private Context contextTickets;

	private ListView listTickets;
	private EditText edt_SearchField;
	private Preference mPref = null;
	// private SearchView search;
	private TicketsAdapter mAdapter;
	private ArrayList<TicketsDataItem> mArrTickets=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tickets);

		contextTickets = ActivityTickets.this;
		mPref = new Preference(contextTickets);

		initView();
		initClickListener();
		apicall();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_logout:

			actionLogout();
			break;

		case R.id.action_search:

			if (edt_SearchField.isShown()) {
				edt_SearchField.setVisibility(View.GONE);
			} else {
				edt_SearchField.setVisibility(View.VISIBLE);
			}

			break;

		default:
			break;
		}
		return true;
	}

	private void initView() {
		setTitle(mPref.getVendorName());
		getActionBar().setIcon(
				new ColorDrawable(getResources().getColor(
						android.R.color.transparent)));
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#3F51B5")));
		edt_SearchField = (EditText) findViewById(R.id.edt_search_text_field);
		listTickets = (ListView) findViewById(R.id.list_ticket);
		mAdapter = new TicketsAdapter(contextTickets,
				new ArrayList<TicketsDataItem>());
		listTickets.setAdapter(mAdapter);

		edt_SearchField.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				mAdapter.getFilter().filter(s.toString());
			}
		});
	}
	
	
	private  void initClickListener(){
		listTickets.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent = new Intent(ActivityTickets.this,
						ActivityStatus.class);
				intent.putExtra("TYPE", "ticket_id");
				intent.putExtra("ID", mArrTickets.get(position).getTicket_ID());
				//Extra Data
				intent.putExtra("Mobile", mArrTickets.get(position).getMobile());
				intent.putExtra("Identifier", mArrTickets.get(position).getIdentifier());
				intent.putExtra("Starttime", mArrTickets.get(position).getStartTime());
				
				startActivity(intent);
				finish();
			}
		});
	}

	/**
	 * 
	 */
	private void apicall() {
		if (Utility.isOnline(contextTickets)) {

			AsyncGetTickets mAsyncGetTickets = new AsyncGetTickets(
					mPref.getVendorId(), ActivityTickets.this);
			// AsyncGetTickets mAsyncGetTickets = new AsyncGetTickets("2",
			// ActivityTickets.this);
			mAsyncGetTickets.setmGetAllTickets(new GetAllTickets() {

				@Override
				public void onSuccess(ArrayList<TicketsDataItem> arrTickets) {
					if (arrTickets != null && arrTickets.size() > 0) {
						mArrTickets=arrTickets;
						mAdapter.loadMore(arrTickets);
					}
				}

				@Override
				public void onError() {
					Toast.makeText(contextTickets,
							getResources().getString(R.string.something),
							Toast.LENGTH_LONG).show();
				}
			});

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				mAsyncGetTickets
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else

				mAsyncGetTickets.execute();

		} else {
			Toast.makeText(this,
					getResources().getString(R.string.msg_connect_network),
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * User Logout operation
	 */
	private void actionLogout() {
		mPref.setVendorId("");
		mPref.setVendorName("");
		startActivity(new Intent(contextTickets, ActivityLogin.class));
		overridePendingTransition(0, 0);
		finish();
	}

}
