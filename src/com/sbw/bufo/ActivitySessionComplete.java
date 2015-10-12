/**
 * 
 */
package com.sbw.bufo;

import org.json.JSONException;
import org.json.JSONObject;

import com.sbw.bufo.util.Preference;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Sumanta
 *
 */
public class ActivitySessionComplete extends Activity implements
		OnClickListener {

	private TextView cust_mob_c, unique_id_c, start_time, end_time, cost,
			payment;
	private Button done;

	private JSONObject jsonObject;
	private Preference mPref = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_complete);

		mPref = new Preference(getApplicationContext());
		inisalize();
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

	private void inisalize() {

		setTitle(mPref.getVendorName());
		getActionBar().setIcon(
				new ColorDrawable(getResources().getColor(
						android.R.color.transparent)));
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#3F51B5")));

		cust_mob_c = (TextView) findViewById(R.id.cust_mob_c);
		unique_id_c = (TextView) findViewById(R.id.unique_id_c);
		start_time = (TextView) findViewById(R.id.start_time);
		end_time = (TextView) findViewById(R.id.end_time);
		cost = (TextView) findViewById(R.id.cost);
		payment = (TextView) findViewById(R.id.payment);

		done = (Button) findViewById(R.id.done);

		try {
			jsonObject = new JSONObject(getIntent().getExtras().getString(
					"result"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		done.setOnClickListener(this);
		try {
			cust_mob_c.setText(jsonObject.getString("mobile"));
			unique_id_c.setText(jsonObject.getString("identifier"));
			start_time.setText(jsonObject.getString("start_time"));
			end_time.setText(jsonObject.getString("end_time"));
			cost.setText(jsonObject.getString("cost"));
			payment.setText(jsonObject.getString("payment_mode"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent mIntent = new Intent(this, ActivityHome.class);
		startActivity(mIntent);
		finish();
	}

	@Override
	public void onClick(View v) {
		Intent mIntent = new Intent(this, ActivityHome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		switch (v.getId()) {
		case R.id.done:
			startActivity(mIntent);
			finish();
			break;

		default:
			break;
		}
	}
	
	/**
	 * User Logout operation
	 */
	private void actionLogout(){
		mPref.setVendorId("");
		mPref.setVendorName("");
		startActivity(new Intent(ActivitySessionComplete.this, ActivityLogin.class));
		overridePendingTransition(0, 0);
		finish();
	}
}
