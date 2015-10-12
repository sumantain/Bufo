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
public class ActivityBookingResult extends Activity implements OnClickListener {

	private TextView cust_mob, unique_id, time;
	private Button ent_done, ent_cancel;

	private JSONObject jsonObject;
	private Preference mPref = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking_result);

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
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));

		cust_mob = (TextView) findViewById(R.id.cust_mob);
		unique_id = (TextView) findViewById(R.id.unique_id);
		time = (TextView) findViewById(R.id.time);

		ent_done = (Button) findViewById(R.id.ent_done);
		ent_cancel = (Button) findViewById(R.id.ent_cancel);

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
		ent_done.setOnClickListener(this);
		ent_cancel.setOnClickListener(this);
		try {
			cust_mob.setText(jsonObject.getString("mobile"));
			unique_id.setText(jsonObject.getString("identifier"));
			time.setText(jsonObject.getString("start_time"));
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
		Intent mIntent = new Intent(this, ActivityHome.class);
		switch (v.getId()) {
		case R.id.ent_done:
			startActivity(mIntent);
			finish();
			break;

		case R.id.ent_cancel:
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
		startActivity(new Intent(ActivityBookingResult.this, ActivityLogin.class));
		overridePendingTransition(0, 0);
		finish();
	}

}
