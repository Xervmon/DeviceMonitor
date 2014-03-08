package com.demo.setting;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class FirstActivity extends Activity implements OnClickListener {
	String str = "http://devmaas.xervmon.com/operation-kriya/index.php/api/SystemMonitor/Authenticate?X-API-KEY=0e5979d2fe0906c97b24659a1acb821a346457f7&username=admin";
	String strUrlGet;
	EditText editText_url, editText_api, editText_username;
	Spinner spinner;
	Button btnSave, btnLogin;
	String refreshRate;
	SampleAlarmReceiver alarm = new SampleAlarmReceiver();
	SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		sharedpreferences = getSharedPreferences("MyPREFERENCES", MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		editor.putInt("Interval", 1);
		editor.commit();
		editText_url = (EditText) findViewById(R.id.editText_Url);
		editText_api = (EditText) findViewById(R.id.editText_Api);
		editText_username = (EditText) findViewById(R.id.editText_UserName);
		btnLogin = (Button) findViewById(R.id.button_login);
		spinner = (Spinner) findViewById(R.id.spinner1);
		btnLogin.setOnClickListener(this);
		btnSave = (Button) findViewById(R.id.button_save);
		btnSave.setOnClickListener(this);
		refreshRate = spinner.getSelectedItem().toString();
		strUrlGet = editText_url.getText().toString()
				+ editText_api.getText().toString() + "&username="
				+ editText_username.getText().toString();
		Log.e("ur", "" + sharedpreferences.getInt("Interval", 1));
		if (sharedpreferences.getInt("Interval", 1) == 1) {
			spinner.setSelection(1);
		} else {
			spinner.setSelection(2);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_login:
			AsnktaskLogin asnktaskLogin = new AsnktaskLogin();
			asnktaskLogin.execute(strUrlGet);
			// Log.e("cpu", getCPUinfo());
			break;
		case R.id.button_save:
			Editor editor = sharedpreferences.edit();
			editor.putInt("Interval",
					Integer.parseInt(spinner.getSelectedItem().toString()));
			editor.commit();
			alarm.setAlarm(getApplicationContext());

			Toast.makeText(
					FirstActivity.this,
					"You have set reminder after "
							+ spinner.getSelectedItem().toString() + " hours",
					3000).show();
			finish();
			break;

		default:
			break;
		}

	}

	private String getCPUinfo() {
		ProcessBuilder cmd;
		String result = "";

		try {
			String[] args = { "/system/bin/cat", "/proc/cpuinfo" };
			cmd = new ProcessBuilder(args);

			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[1024];
			while (in.read(re) != -1) {
				// System.out.println(new String(re));
				result = result + new String(re);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (result.lastIndexOf("\n") > 0) {
			return result.substring(0, result.lastIndexOf("\n"));
		} else {
			return result;
		}
	}

	public class AsnktaskLogin extends AsyncTask<String, Void, String> {
		ProgressDialog pd;
		String response;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(FirstActivity.this);
			pd.setMessage("Please wait authentication going on.......");
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {

			return WebAPIRequest.performGet(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pd.dismiss();
			Log.e("result", result);
			try {
				JSONObject json = new JSONObject(result);
				if (json.getString("response").equalsIgnoreCase("OK")) {
					btnLogin.setEnabled(false);
					btnLogin.setVisibility(View.GONE);
					btnSave.setEnabled(true);
					spinner.setClickable(true);
					Toast.makeText(FirstActivity.this,
							"You have succsessfully login..", 3000).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
