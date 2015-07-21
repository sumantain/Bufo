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
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

	private String CardId;

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
		CardId = getIntent().getExtras().getString("card_id");
		initalize();
	}

	private void initalize() {
		setTitle(mPref.getVendorName());
		getActionBar().setIcon(
				new ColorDrawable(getResources().getColor(
						android.R.color.transparent)));
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
		AsyncGetClass mAsyncGetClass = new AsyncGetClass(new onGetClass() {

			@Override
			public void onGetClassSucces(ArrayList<String> proId,
					ArrayList<String> proName) {

				product_id = proId;
				product_name = proName;

			}
		});
		mAsyncGetClass.execute(mPref.getVendorId());

	}

	@Override
	protected void onStart() {
		super.onStart();

		if (Utility.isOnline(getApplicationContext())) {

			mAsyncGetCardState = new AsyncGetCardState(this,
					new onCardStatus() {

						@Override
						public void onCardStatusSuccess(JSONObject response) {

							openNewCard(response);
						}

						@Override
						public void onCardStatusFalse(JSONObject response) {
							openExistingCard(response);
						}
					});
			mAsyncGetCardState.execute(CardId);
		} else {
			Intent mIntent = new Intent(this, ActivityHome.class);
			startActivity(mIntent);
			finish();

		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent mIntent = new Intent(this, ActivityHome.class);
		startActivity(mIntent);
		finish();
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
									getApplicationContext(),
									new onRequestStart() {

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
							mAsyncRequestStart.execute(CardId, mPref
									.getVendorId(), edit_mobile.getText()
									.toString(), edit_identity.getText()
									.toString(), selectedProductId.toString());
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
										getApplicationContext(),
										ActivitySessionComplete.class);
								mIntent.putExtra("result",
										jsonObject.toString());
								startActivity(mIntent);
								finish();
							}

							@Override
							public void onRequestEndFaild() {

							}

						});
				mAsyncRequestEnd.execute(CardId, mPref.getVendorId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this,
					getResources().getString(R.string.msg_connect_network),
					Toast.LENGTH_LONG).show();
		}

	}
}
