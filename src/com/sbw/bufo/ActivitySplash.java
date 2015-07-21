package com.sbw.bufo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ActivitySplash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	goToNextScreen(3000);
    }

	private void goToNextScreen(int delay) {
		Handler handler = new Handler(){
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				Intent intent = new Intent(ActivitySplash.this, ActivityLogin.class);
				startActivity(intent);
				finish();
			}
		};
		handler.sendEmptyMessageDelayed(0, delay);
	}
}
