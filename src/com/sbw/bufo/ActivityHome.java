/**
 * 
 */
package com.sbw.bufo;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sbw.bufo.asynctask.AsyncGetAvailable;
import com.sbw.bufo.asynctask.AsyncGetCardId;
import com.sbw.bufo.asynctask.AsyncGetCardId.GetCardid;
import com.sbw.bufo.interactive.onAvailableResponse;
import com.sbw.bufo.util.Preference;
import com.sbw.bufo.util.Utility;

/**
 * @author Sumanta
 *
 */
public class ActivityHome extends Activity implements OnClickListener {

	private TextView tv_home_place;
	private Button button1, button2;

	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;

	private NdefMessage mNdefPushMessage;

	private String scanData;
	private String[] scanDataArr;

	private AsyncGetAvailable mAsyncGetAvailable;

	private Preference mPref = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mPref = new Preference(getApplicationContext());
		inisalize();
		getPlaceData();
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

		// btn_home_book = (Button) findViewById(R.id.book_space_card);
		// // btn_home_book.setClickable(false);
		// btn_home_book.setBackgroundColor(getResources().getColor(
		// R.color.btn_disable));

		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);

		tv_home_place = (TextView) findViewById(R.id.place);

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
	}

	private void getPlaceData() {

		if (Utility.isOnline(ActivityHome.this)) {
			mAsyncGetAvailable = new AsyncGetAvailable(this,
					new onAvailableResponse() {

						@Override
						public void onBackGroundResponseFalse(boolean data) {

						}

						@Override
						public void onBackGroundResponseSuccess(boolean data,
								int place) {
							tv_home_place.setText(String.valueOf(place));
						}
					});
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				mAsyncGetAvailable
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else

				mAsyncGetAvailable.execute();

		} else {
			Toast.makeText(this,
					getResources().getString(R.string.msg_connect_network),
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void onStart() {
		super.onStart();

		if (mNfcAdapter == null) {
			// finish();
		} else {
			mPendingIntent = PendingIntent.getActivity(this, 0,
					new Intent(this, getClass())
							.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

			mNdefPushMessage = new NdefMessage(
					new NdefRecord[] { newTextRecord(
							"Message from NFC Reader :-)", Locale.ENGLISH, true) });
		}

		setAction();
	}

	private void setAction() {
		// btn_home_book.setOnClickListener(this);
	}

	private NdefRecord newTextRecord(String text, Locale locale,
			boolean encodeInUtf8) {
		byte[] langBytes = locale.getLanguage().getBytes(
				Charset.forName("US-ASCII"));

		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset
				.forName("UTF-16");
		byte[] textBytes = text.getBytes(utfEncoding);

		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char) (utfBit + langBytes.length);

		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
				textBytes.length);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
				new byte[0], data);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		if (mNfcAdapter != null) {
			if (!mNfcAdapter.isEnabled()) {
				// showWirelessSettingsDialog();
			}
			mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null,
					null);
			mNfcAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		if (mNfcAdapter != null) {
			mNfcAdapter.disableForegroundDispatch(this);
			mNfcAdapter.disableForegroundNdefPush(this);
		}
	}

	private void resolveIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msgs;
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[0];
				byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
				Parcelable tag = intent
						.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				byte[] payload = dumpTagData(tag).getBytes();
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, id, payload);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
			// Setup the views
			// buildTagViews(msgs);
			showData(msgs);
		}

	}

	private void showData(NdefMessage[] msgs) {
		if (msgs == null || msgs.length == 0) {
			return;
		}

		try {
			scanData = new String(msgs[0].getRecords()[0].getPayload(), "UTF-8");
			if (!scanData.equalsIgnoreCase("") && scanData != null) {

				if (Utility.isOnline(getApplicationContext())) {
					try {
						try {
							Intent intent = new Intent(this,
									ActivityStatus.class);
							intent.putExtra("card_id", scanData);
							startActivity(intent);
							finish();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (NotFoundException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(
							this,
							getResources().getString(
									R.string.msg_connect_network),
							Toast.LENGTH_LONG).show();
				}

			} else {
				Toast.makeText(this,
						getResources().getString(R.string.msg_scan_card_fist),
						Toast.LENGTH_LONG).show();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private String dumpTagData(Parcelable p) {
		StringBuilder sb = new StringBuilder();
		Tag tag = (Tag) p;
		byte[] id = tag.getId();
		sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
		sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
		sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

		String prefix = "android.nfc.tech.";
		sb.append("Technologies: ");
		for (String tech : tag.getTechList()) {
			sb.append(tech.substring(prefix.length()));
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		for (String tech : tag.getTechList()) {
			if (tech.equals(MifareClassic.class.getName())) {
				sb.append('\n');
				MifareClassic mifareTag = MifareClassic.get(tag);
				String type = "Unknown";
				switch (mifareTag.getType()) {
				case MifareClassic.TYPE_CLASSIC:
					type = "Classic";
					break;
				case MifareClassic.TYPE_PLUS:
					type = "Plus";
					break;
				case MifareClassic.TYPE_PRO:
					type = "Pro";
					break;
				}
				sb.append("Mifare Classic type: ");
				sb.append(type);
				sb.append('\n');

				sb.append("Mifare size: ");
				sb.append(mifareTag.getSize() + " bytes");
				sb.append('\n');

				sb.append("Mifare sectors: ");
				sb.append(mifareTag.getSectorCount());
				sb.append('\n');

				sb.append("Mifare blocks: ");
				sb.append(mifareTag.getBlockCount());
			}

			if (tech.equals(MifareUltralight.class.getName())) {
				sb.append('\n');
				MifareUltralight mifareUlTag = MifareUltralight.get(tag);
				String type = "Unknown";
				switch (mifareUlTag.getType()) {
				case MifareUltralight.TYPE_ULTRALIGHT:
					type = "Ultralight";
					break;
				case MifareUltralight.TYPE_ULTRALIGHT_C:
					type = "Ultralight C";
					break;
				}
				sb.append("Mifare Ultralight type: ");
				sb.append(type);
			}
		}

		return sb.toString();
	}

	private String getHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = bytes.length - 1; i >= 0; --i) {
			int b = bytes[i] & 0xff;
			if (b < 0x10)
				sb.append('0');
			sb.append(Integer.toHexString(b));
			if (i > 0) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	private long getDec(byte[] bytes) {
		long result = 0;
		long factor = 1;
		for (int i = 0; i < bytes.length; ++i) {
			long value = bytes[i] & 0xffl;
			result += value * factor;
			factor *= 256l;
		}
		return result;
	}

	private long getReversed(byte[] bytes) {
		long result = 0;
		long factor = 1;
		for (int i = bytes.length - 1; i >= 0; --i) {
			long value = bytes[i] & 0xffl;
			result += value * factor;
			factor *= 256l;
		}
		return result;
	}

	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
		resolveIntent(intent);

		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
		// mText.setText("Discovered tag NDEF " + ++mCount + " with intent: " +
		// intent);

		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

			if (rawMsgs != null) {
				NdefMessage[] msgs = new NdefMessage[rawMsgs.length];

				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}

				@SuppressWarnings("unused")
				NdefMessage msg = msgs[0];

				try {
					// nfc_code.setText(new
					// String(msg.getRecords()[0].getPayload(), "UTF-8"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button1:
			getCardId();
			break;

		case R.id.button2:
			startActivity(new Intent(ActivityHome.this, ActivityTickets.class));
			overridePendingTransition(0, 0);

			break;

		case R.id.book_space_card:
			// System.out.println(scanDataArr[1]);
			if (Utility.isOnline(getApplicationContext())) {
				try {
					if (scanDataArr != null && scanDataArr.length > 1) {
						try {
							if ((scanDataArr[1]) != null
									&& (scanDataArr[1]).length() > 0) {
								Intent intent = new Intent(this,
										ActivityStatus.class);
								intent.putExtra("card_id", scanDataArr[1]);
								startActivity(intent);
								finish();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(
								this,
								getResources().getString(
										R.string.msg_scan_card_fist),
								Toast.LENGTH_LONG).show();
					}
				} catch (NotFoundException e) {
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

	/**
	 * Call Asynctask for get Card id.
	 */
	private void getCardId() {

		if (Utility.isOnline(ActivityHome.this)) {
			AsyncGetCardId mAsyncGetCardId = new AsyncGetCardId();
			mAsyncGetCardId.setmGetCardid(new GetCardid() {

				@Override
				public void onSuccess(String cardid) {
					Intent intent = new Intent(ActivityHome.this,
							ActivityStatus.class);
					intent.putExtra("TYPE", "card_id");
					intent.putExtra("ID", cardid);
					startActivity(intent);
					finish();
				}

				@Override
				public void onError() {
					Toast.makeText(ActivityHome.this,
							getResources().getString(R.string.something),
							Toast.LENGTH_LONG).show();
				}
			});
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				mAsyncGetCardId
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else

				mAsyncGetCardId.execute();
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
		startActivity(new Intent(ActivityHome.this, ActivityLogin.class));
		overridePendingTransition(0, 0);
		finish();
	}

}
