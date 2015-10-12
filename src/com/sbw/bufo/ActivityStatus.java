/**
 * 
 */
package com.sbw.bufo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sbw.bufo.asynctask.AsyncGetCardState;
import com.sbw.bufo.asynctask.AsyncGetClass;
import com.sbw.bufo.asynctask.AsyncRequestEnd;
import com.sbw.bufo.asynctask.AsyncRequestStart;
import com.sbw.bufo.interactive.onCardStatus;
import com.sbw.bufo.interactive.onGetClass;
import com.sbw.bufo.interactive.onRequestEnd;
import com.sbw.bufo.interactive.onRequestStart;
import com.sbw.bufo.util.Preference;
import com.sbw.bufo.util.Utility;

/**
 * @author Sumanta
 *
 */
public class ActivityStatus extends Activity implements OnClickListener {

	private String CardId = "";
	private String ticketID = "";
	private String type = "";

	private LinearLayout Loading;
	private RelativeLayout on_new, on_exist;

	private TextView cust_mob, unique_id, time;
	private EditText edit_mobile, edit_identity;
	private Spinner spinner_class;

	private RadioButton cls_i, cls_ii;

	private Button book_space_card, end_session;

	private AsyncGetCardState mAsyncGetCardState;
	private AsyncRequestStart mAsyncRequestStart;
	private AsyncRequestEnd mAsyncRequestEnd;

	private ArrayList<String> product_id;
	private ArrayList<String> product_name;
	private boolean status = false;
	private String selectedProductId = "";

	private Preference mPref = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);

		mPref = new Preference(getApplicationContext());
		type = getIntent().getExtras().getString("TYPE");
		if (type.equalsIgnoreCase("card_id")) {
			CardId = getIntent().getExtras().getString("ID");
		}
		if (type.equalsIgnoreCase("ticket_id")) {
			ticketID = getIntent().getExtras().getString("ID");
		}

		initalize();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_logout:
			actionLogout();
			break;

		default:
			break;
		}
		return true;
	}

	private void initalize() {
		setTitle(mPref.getVendorName());
		getActionBar().setIcon(
				new ColorDrawable(getResources().getColor(
						android.R.color.transparent)));
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#3F51B5")));

		Loading = (LinearLayout) findViewById(R.id.Loading);
		on_new = (RelativeLayout) findViewById(R.id.on_new);
		on_exist = (RelativeLayout) findViewById(R.id.on_exist);

		cust_mob = (TextView) findViewById(R.id.cust_mob);
		unique_id = (TextView) findViewById(R.id.unique_id);
		time = (TextView) findViewById(R.id.time);

		edit_mobile = (EditText) findViewById(R.id.edit_mobile);
		edit_identity = (EditText) findViewById(R.id.edit_identity);
		spinner_class = (Spinner) findViewById(R.id.spinner_class);

		book_space_card = (Button) findViewById(R.id.book_space_card);
		end_session = (Button) findViewById(R.id.end_session);
		if (Utility.isOnline(getApplicationContext())) {
			AsyncGetClass mAsyncGetClass = new AsyncGetClass(
					ActivityStatus.this, new onGetClass() {

						@Override
						public void onGetClassSucces(ArrayList<String> proId,
								ArrayList<String> proName) {

							product_id = proId;
							product_name = proName;

						}
					});
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				mAsyncGetClass.executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, mPref.getVendorId());
			else
				mAsyncGetClass.execute(mPref.getVendorId());
		} else {
			Toast.makeText(this,
					getResources().getString(R.string.msg_connect_network),
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (type.equalsIgnoreCase("card_id")) {
			getCardState();
		}
		if (type.equalsIgnoreCase("ticket_id")) {
			Loading.setVisibility(View.GONE);
			on_exist.setVisibility(View.VISIBLE);

			try {
				cust_mob.setText(getIntent().getExtras().getString("Mobile"));
				unique_id.setText(getIntent().getExtras().getString(
						"Identifier"));
				time.setText(getIntent().getExtras().getString("Starttime"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			end_session.setOnClickListener(this);
			end_session.setBackgroundColor(getResources().getColor(
					R.color.color_red_yello));
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent mIntent = new Intent(this, ActivityHome.class);
		startActivity(mIntent);
		finish();
	}

	private void getCardState() {
		if (Utility.isOnline(getApplicationContext())) {

			mAsyncGetCardState = new AsyncGetCardState(this, CardId,
					new onCardStatus() {

						@Override
						public void onCardStatusSuccess(JSONObject response) {

							if (response != null) {
								openNewCard(response);
							}

						}

						@Override
						public void onCardStatusFalse(JSONObject response) {
							if (response != null) {
								openExistingCard(response);
							}
						}
					});
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				mAsyncGetCardState.executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, CardId);
			else

				mAsyncGetCardState.execute(CardId);
		} else {
			Toast.makeText(this,
					getResources().getString(R.string.msg_connect_network),
					Toast.LENGTH_LONG).show();
		}
	}

	protected void openExistingCard(JSONObject response) {
		Loading.setVisibility(View.GONE);
		on_exist.setVisibility(View.VISIBLE);

		try {
			cust_mob.setText(response.getString("mobile"));
			unique_id.setText(response.getString("identifier"));
			time.setText(response.getString("start_time"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		end_session.setOnClickListener(this);
		end_session.setBackgroundColor(getResources().getColor(
				R.color.color_red_yello));
	}

	protected void openNewCard(JSONObject response) {

		Loading.setVisibility(View.GONE);
		on_new.setVisibility(View.VISIBLE);

		try {
			edit_mobile.setText(response.getString("mobile"));
			edit_identity.setText(response.getString("identifier"));
			// edit_class.setText(response.getString("vehicle_type"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, product_name);
		spinner_class.setAdapter(adapter);

		spinner_class.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				Log.v("position", "" + position);
				Log.v("position", ":" + product_id.get(position));

				if (status) {
					selectedProductId = product_id.get(position);
				} else {
					status = true;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		book_space_card.setOnClickListener(this);
		book_space_card.setBackgroundColor(getResources().getColor(
				R.color.btn_bg));

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.book_space_card:
			ConfirmBooking();

			break;
		case R.id.end_session:
			ConfirmEnding();

			break;
		default:
			break;
		}
	}

	private void ConfirmEnding() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ActivityStatus.this);
		// set title
		alertDialogBuilder.setTitle("");
		// set dialog message
		alertDialogBuilder
				.setMessage("Are you sure you want to END the session?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								erquestEndSession();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}

	private void ConfirmBooking() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ActivityStatus.this);
		// set title
		alertDialogBuilder.setTitle("");
		// set dialog message
		alertDialogBuilder
				.setMessage("Are you sure you want to book the space?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								requestStartSession();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}

	private void requestStartSession() {
		if (Utility.isOnline(getApplicationContext())) {
			if (edit_mobile.getText().toString() != null
					&& !(edit_mobile.getText().toString()).equalsIgnoreCase("")) {
				if (edit_identity.getText().toString() != null
						&& !(edit_identity.getText().toString())
								.equalsIgnoreCase("")) {
					if (selectedProductId.toString() != null
							&& !(selectedProductId.toString())
									.equalsIgnoreCase("")) {
						try {
							mAsyncRequestStart = new AsyncRequestStart(
									ActivityStatus.this, new onRequestStart() {

										@Override
										public void onRequestStartSuccess(
												JSONObject jsonObject) {
											Intent mIntent = new Intent(
													getApplicationContext(),
													ActivityBookingResult.class);
											mIntent.putExtra("result",
													jsonObject.toString());
											startActivity(mIntent);
											finish();
										}

										@Override
										public void onRequestStartFaild() {

										}
									});
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
								mAsyncRequestStart.executeOnExecutor(
										AsyncTask.THREAD_POOL_EXECUTOR, CardId,
										mPref.getVendorId(), edit_mobile
												.getText().toString(),
										edit_identity.getText().toString(),
										selectedProductId.toString());
							else
								mAsyncRequestStart.execute(CardId, mPref
										.getVendorId(), edit_mobile.getText()
										.toString(), edit_identity.getText()
										.toString(), selectedProductId
										.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"Class must be select", Toast.LENGTH_LONG)
								.show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Identity must not be blank", Toast.LENGTH_LONG)
							.show();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Mobile no must not be blank", Toast.LENGTH_LONG)
						.show();
			}
		} else {
			Toast.makeText(this,
					getResources().getString(R.string.msg_connect_network),
					Toast.LENGTH_LONG).show();
		}

	}

	private void erquestEndSession() {
		if (Utility.isOnline(getApplicationContext())) {
			try {
				mAsyncRequestEnd = new AsyncRequestEnd(getApplicationContext(),
						new onRequestEnd() {

							@Override
							public void onRequestEndSuccess(
									JSONObject jsonObject) {
								System.out.println(jsonObject.toString());
								Intent mIntent = new Intent(
										ActivityStatus.this,
										ActivitySessionComplete.class);
								mIntent.putExtra("result",
										jsonObject.toString());
								startActivity(mIntent);
								finish();
							}

							@Override
							public void onRequestEndFaild() {
								Toast.makeText(ActivityStatus.this,
										getResources().getString(R.string.something),
										Toast.LENGTH_LONG).show();
							}

						});
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					mAsyncRequestEnd.executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, CardId,ticketID,
							mPref.getVendorId());
				else
					mAsyncRequestEnd.execute(CardId,ticketID, mPref.getVendorId());
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		startActivity(new Intent(ActivityStatus.this, ActivityLogin.class));
		overridePendingTransition(0, 0);
		finish();
	}
}
