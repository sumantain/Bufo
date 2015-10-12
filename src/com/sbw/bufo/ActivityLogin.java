package com.sbw.bufo;

import org.json.JSONException;
import org.json.JSONObject;

import com.sbw.bufo.asynctask.AsyncGetLogin;
import com.sbw.bufo.interactive.onGetLogin;
import com.sbw.bufo.util.Preference;
import com.sbw.bufo.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityLogin extends Activity implements OnClickListener {

	private EditText edittext_email, edittext_password;
	private Button button_login;

	private AsyncGetLogin mAsyncGetLogin;
	private Preference mPref;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mPref = new Preference(getApplicationContext());
		inisalize();
		
		
		
		
		
		if(!TextUtils.isEmpty(mPref.getVendorId()) && !TextUtils.isEmpty(mPref.getVendorName())){
			Intent intent = new Intent(ActivityLogin.this,
					ActivityHome.class);
			startActivity(intent);
			finish();
		}
		
	}

	private void inisalize() {
		button_login = (Button) findViewById(R.id.button_login);

		edittext_email = (EditText) findViewById(R.id.edittext_email);
		edittext_password = (EditText) findViewById(R.id.edittext_password);

		button_login.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_login:

			// Intent intent = new Intent(ActivityLogin.this,
			// ActivityHome.class);
			// startActivity(intent);
			// finish();
			if (Utility.isOnline(getApplicationContext())) {
				try {
					mAsyncGetLogin = new AsyncGetLogin(this, new onGetLogin() {

						@Override
						public void onGetLoginSuccess(JSONObject jsonObject) {
							try {
								mPref.setVendorId(jsonObject
										.getString("vendor_id"));
								mPref.setVendorName(jsonObject
										.getString("vendor_name"));

								Intent intent = new Intent(ActivityLogin.this,
										ActivityHome.class);
								startActivity(intent);
								finish();
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void onGetLoginFaild() {
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.msg_login_error),
									Toast.LENGTH_LONG).show();
						}

					});
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						mAsyncGetLogin.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,edittext_email.getText().toString(),edittext_password.getText().toString());
				     else
				    	 mAsyncGetLogin.execute(edittext_email.getText().toString(),
									edittext_password.getText().toString());
					
				} catch (Exception e) {
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(
									R.string.msg_login_error),
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			} else {
				Toast.makeText(this,
						getResources().getString(R.string.msg_connect_network),
						Toast.LENGTH_LONG).show();
			}
			break;

		default:
			break;
		}
	}
}
