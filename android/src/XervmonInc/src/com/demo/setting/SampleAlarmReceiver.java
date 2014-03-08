package com.demo.setting;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast
 * Intent and then starts the IntentService {@code SampleSchedulingService} to
 * do some work.
 */
public class SampleAlarmReceiver extends BroadcastReceiver {
	// The app's AlarmManager, which provides access to the system alarm
	// services.
	private AlarmManager alarmMgr;
	// The pending intent that is triggered when the alarm fires.
	private PendingIntent alarmIntent;
	SharedPreferences sharedpreferences;
	String Url = "http://devmaas.xervmon.com/operation-kriya/index.php/api/SystemMonitor/Datastream?X-API-KEY=0e5979d2fe0906c97b24659a1acb821a346457f7&username=admin&deviceData=";
	Context context;
	DisplayMetrics dm;
	String battery_level, battery_status;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;

		dm = Resources.getSystem().getDisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(dm);
		AsnktaskLogin asnktaskLogin = new AsnktaskLogin();
		asnktaskLogin.execute(Url);

	}

	// BEGIN_INCLUDE(set_alarm)
	/**
	 * Sets a repeating alarm that runs once a day at approximately 8:30 a.m.
	 * When the alarm fires, the app broadcasts an Intent to this
	 * WakefulBroadcastReceiver.
	 * 
	 * @param context
	 */
	public void setAlarm(Context context) {
		sharedpreferences = context.getSharedPreferences("MyPREFERENCES",
				context.MODE_PRIVATE);
		alarmMgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, SampleAlarmReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		// Set the alarm's trigger time to 8:30 a.m.
		// calendar.set(Calendar.HOUR_OF_DAY, 8);
		// calendar.set(Calendar.MINUTE, 30);

		/*
		 * If you don't have precise time requirements, use an inexact repeating
		 * alarm the minimize the drain on the device battery.
		 * 
		 * The call below specifies the alarm type, the trigger time, the
		 * interval at which the alarm is fired, and the alarm's associated
		 * PendingIntent. It uses the alarm type RTC_WAKEUP ("Real Time Clock"
		 * wake up), which wakes up the device and triggers the alarm according
		 * to the time of the device's clock.
		 * 
		 * Alternatively, you can use the alarm type ELAPSED_REALTIME_WAKEUP to
		 * trigger an alarm based on how much time has elapsed since the device
		 * was booted. This is the preferred choice if your alarm is based on
		 * elapsed time--for example, if you simply want your alarm to fire
		 * every 60 minutes. You only need to use RTC_WAKEUP if you want your
		 * alarm to fire at a particular date/time. Remember that clock-based
		 * time may not translate well to other locales, and that your app's
		 * behavior could be affected by the user changing the device's time
		 * setting.
		 * 
		 * Here are some examples of ELAPSED_REALTIME_WAKEUP:
		 * 
		 * // Wake up the device to fire a one-time alarm in one minute.
		 * alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		 * SystemClock.elapsedRealtime() + 60*1000, alarmIntent);
		 * 
		 * // Wake up the device to fire the alarm in 30 minutes, and every 30
		 * minutes // after that.
		 * alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		 * AlarmManager.INTERVAL_HALF_HOUR, AlarmManager.INTERVAL_HALF_HOUR,
		 * alarmIntent);
		 */

		// Set the alarm to fire at approximately 8:30 a.m., according to the
		// device's
		// clock, and to repeat once a day
		int interval = sharedpreferences.getInt("Interval", 1);
		Log.e("interval", "" + interval);
		Log.e("interval", "" + sharedpreferences.getInt("Interval", 0));
		alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), 360000 * interval, alarmIntent);

		// Enable {@code SampleBootReceiver} to automatically restart the alarm
		// when the
		// device is rebooted.
		ComponentName receiver = new ComponentName(context,
				SampleBootReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}

	// END_INCLUDE(set_alarm)
	public class AsnktaskLogin extends AsyncTask<String, Void, String> {
		ProgressDialog pd;
		String response;

		@Override
		protected String doInBackground(String... params) {
			batteryLevel();
			String str_url = null;
			try {
				str_url = params[0] + URLEncoder.encode(Getdata(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			Log.e("data", Getdata());
			Log.e("Url::", str_url);

			return WebAPIRequest.performGet(str_url);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// pd.dismiss();
			Log.e("result", result);
			try {
				JSONObject json = new JSONObject(result);
				if (json.getString("status").equalsIgnoreCase("OK")) {

				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * Cancels the alarm.
	 * 
	 * @param context
	 */
	// BEGIN_INCLUDE(cancel_alarm)
	public void cancelAlarm(Context context) {
		// If the alarm has been set, cancel it.
		if (alarmMgr != null) {
			alarmMgr.cancel(alarmIntent);
		}

		// Disable {@code SampleBootReceiver} so that it doesn't automatically
		// restart the
		// alarm when the device is rebooted.
		ComponentName receiver = new ComponentName(context,
				SampleBootReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
	}

	// END_INCLUDE(cancel_alarm)
	private String Getdata() {
		String str_manufacture = android.os.Build.MANUFACTURER; // manufacture
		String str_device = android.os.Build.DEVICE; // device
		String str_product = android.os.Build.PRODUCT;
		// product
		String str_brand = android.os.Build.BRAND; // brand
		String str_cpu_abi = android.os.Build.CPU_ABI;
		// cpu_abi
		String str_cpu_abi2 = android.os.Build.CPU_ABI2; // cpu_abi2
		String str_Kernel_version = System.getProperty("os.version"); // Kernel
																		// version
		String str_build_number = android.os.Build.FINGERPRINT; // str_build_number
		String str_release = android.os.Build.VERSION.RELEASE; // release
		String str_sdk = "" + android.os.Build.VERSION.SDK_INT;
		// sdk
		String str_display = android.os.Build.DISPLAY; // display

		// get screen dpi
		String str_widthPixels = "" + dm.widthPixels;
		String str_heightPixels = "" + dm.heightPixels; // screen size -in pixel
		String densityDpi = "" + dm.densityDpi; // densityDpi
		String str_logincal_density = "" + dm.density; // logincal_density
		String str_font_scaling_factor = "" + dm.scaledDensity; // font_scaling_factor
		String str_pixel_per_inch_x = "" + dm.xdpi;
		String str_pixel_per_inch_y = "" + dm.ydpi;
		// String str_health = "health--" + "";
		// String str_status = "status--" + "";
		String str_wireless_ssid = getCurrentSsid(context);
		String str_cpu_info = getCPUinfo();
		// memory information
		String str_external_total = getTotalExternalMemorySize();
		String str_external_free = getAvailableExternalMemorySize();

		String str_internal_total = getTotalInternalMemorySize();
		String str_internal_free = getAvailableInternalMemorySize();

		String str_intalledAppSize = TotalApplicationIntalled();
		String str_callCount = getCallDetails(3600000 * 1);
		String count_contacts = fetchContacts();
		String count_images = fetchImages();
		String count_videos = fetchVideos();
		String cellId = getCID();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject();
			jsonObject.put("manufacture", str_manufacture);
			jsonObject.put("device", str_device);
			jsonObject.put("product", str_product);
			jsonObject.put("brand", str_brand);
			jsonObject.put("cpu_abi", str_cpu_abi);
			jsonObject.put("cpu_abi2", str_cpu_abi2);
			jsonObject.put("kernel_version", str_Kernel_version);
			jsonObject.put("build_number", str_build_number);
			jsonObject.put("release", str_release);
			jsonObject.put("sdk", str_sdk);
			jsonObject.put("display", str_display);
			jsonObject.put("build_number", str_build_number);
			jsonObject.put("release", str_release);
			jsonObject.put("sdk", str_sdk);
			jsonObject.put("display", str_display);

			JSONArray jsonArraywidthheightPixel = new JSONArray();
			jsonArraywidthheightPixel.put(new JSONObject().put("widthPixels",
					str_widthPixels));
			jsonArraywidthheightPixel.put(new JSONObject().put("heightPixels",
					str_heightPixels));
			jsonObject.put("width_height_pixel", jsonArraywidthheightPixel);

			jsonObject.put("densityDpi", densityDpi);
			jsonObject.put("logincal_density", str_logincal_density);
			jsonObject.put("font_scaling_factor", str_font_scaling_factor);

			JSONArray jsonArraypixelPerInch = new JSONArray();
			jsonArraypixelPerInch.put(new JSONObject().put("pixel_per_inch_x",
					str_pixel_per_inch_x));
			jsonArraypixelPerInch.put(new JSONObject().put("pixel_per_inch_y",
					str_pixel_per_inch_y));
			jsonObject.put("pixel_per_inch", jsonArraypixelPerInch);

			jsonObject.put("wireless_ssid", str_wireless_ssid);

			JSONArray jsonArrayExternalMemory = new JSONArray();
			jsonArrayExternalMemory.put(new JSONObject().put("external_total",
					str_external_total));
			jsonArrayExternalMemory.put(new JSONObject().put("external_free",
					str_external_free));
			jsonObject.put("external_memory", jsonArrayExternalMemory);

			JSONArray jsonArrayInternalMemory = new JSONArray();
			jsonArrayInternalMemory.put(new JSONObject().put("internal_total",
					str_internal_total));
			jsonArrayInternalMemory.put(new JSONObject().put("internal_free",
					str_internal_free));
			jsonObject.put("internal_memory", jsonArrayInternalMemory);

			jsonObject.put("intalledAppSize", str_intalledAppSize);
			jsonObject.put("callCount", str_callCount);
			jsonObject.put("contactsCount", count_contacts);
			jsonObject.put("imagesCount", count_images);
			jsonObject.put("videosCount", count_videos);
			jsonObject.put("cellId", cellId);
			jsonObject.put("cpu_info", str_cpu_info);

			jsonObject.put("battery_health", battery_level);
			jsonObject.put("battery_status", battery_status);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// textView1.setText(str_manufacture + str_device + str_product
		// + str_brand + str_cpu_abi + str_cpu_abi2 + str_Kernel_version
		// + str_build_number + str_release + str_sdk + str_display
		// + str_screen_size + str_densityDpi + str_logincal_density
		// + str_font_scaling_factor + str_pixel_per_inch + str_health
		// + str_status + str_wireless_ssid + memory + str_intalledAppSize
		// + str_callCount + count_contacts + count_images + count_videos
		// + cellId);

		// textView2.setText(ReadCPUinfo());
		return jsonObject.toString();
	}

	// total application installed
	private String TotalApplicationIntalled() {
		final PackageManager pm = context.getPackageManager();
		// get a list of installed apps.
		List<ApplicationInfo> packages = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);
		String str_intalledAppSize = "IntalledAppSize" + packages.size();

		for (ApplicationInfo packageInfo : packages) {
			String strpackageName = packageInfo.packageName;
			String strSourcedir = packageInfo.sourceDir;
			String fileName = strSourcedir.substring(
					strSourcedir.lastIndexOf('/') + 1, strSourcedir.length());
			String fileNameWithoutExtn = fileName.substring(0,
					fileName.lastIndexOf('.'));
		}
		return str_intalledAppSize;
	}

	// cpu information
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

	// currner ssid
	public static String getCurrentSsid(Context context) {
		String ssid = null;
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo.isConnected()) {
			final WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
			if (connectionInfo != null
					&& !TextUtils.isEmpty(connectionInfo.getSSID())) {
				ssid = connectionInfo.getSSID();
			}
		}
		return ssid;
	}

	// calculate internal storage
	public static boolean externalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	public static String getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return formatSize(availableBlocks * blockSize);
	}

	public static String getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return formatSize(totalBlocks * blockSize);
	}

	public static String getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return formatSize(availableBlocks * blockSize);
		} else {
			return "";
		}
	}

	public static String getTotalExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return formatSize(totalBlocks * blockSize);
		} else {
			return "";
		}
	}

	public static String formatSize(long size) {
		String suffix = null;

		if (size >= 1024) {
			suffix = "KB";
			size /= 1024;
			if (size >= 1024) {
				suffix = "MB";
				size /= 1024;
			}
		}

		StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

		int commaOffset = resultBuffer.length() - 3;
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, ',');
			commaOffset -= 3;
		}

		if (suffix != null)
			resultBuffer.append(suffix);
		return resultBuffer.toString();
	}

	// total call count
	private String getCallDetails(long time) {

		StringBuffer sb = new StringBuffer();
		@SuppressWarnings("deprecation")
		Cursor managedCursor = context.getContentResolver().query(
				CallLog.Calls.CONTENT_URI, null, null, null, null);
		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
		sb.append("Call Details :");
		int counte = 0;
		Calendar cal = Calendar.getInstance();
		Date current_date, calldate;
		current_date = new Date(cal.getTimeInMillis() - time);
		// Log.e("currentdate", "" + current_date);

		while (managedCursor.moveToNext()) {
			String phNumber = managedCursor.getString(number);
			String callType = managedCursor.getString(type);
			String callDate = managedCursor.getString(date);
			Date callDayTime = new Date(Long.valueOf(callDate));
			String callDuration = managedCursor.getString(duration);

			calldate = new Date(callDayTime.getTime());

			String dir = null;
			int dircode = Integer.parseInt(callType);
			switch (dircode) {
			case CallLog.Calls.OUTGOING_TYPE:
				dir = "OUTGOING";
				break;

			case CallLog.Calls.INCOMING_TYPE:
				dir = "INCOMING";
				break;

			case CallLog.Calls.MISSED_TYPE:
				dir = "MISSED";
				break;
			}
			if (calldate.after(current_date)) {
				counte++;
				// Log.e("this hour", "" + callDayTime);
			} else {
				// Log.e("before this hours", "" + callDayTime);
			}
			sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
					+ dir + " \nCall Date:--- " + callDayTime
					+ " \nCall duration in sec :--- " + callDuration);
			sb.append("\n----------------------------------");
		}
		// managedCursor.close();
		// textView2.setText(sb);
		return "" + counte;

		// call.setText(sb);
	}

	// get all contact
	public String fetchContacts() {
		String count_contacts;
		String phoneNumber = null;
		String email = null;

		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

		Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;

		StringBuffer output = new StringBuffer();

		ContentResolver contentResolver = context.getContentResolver();

		Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
				null);

		// Loop for every contact in the phone
		count_contacts = "" + cursor.getCount();
		if (cursor.getCount() > 0) {

			while (cursor.moveToNext()) {

				String contact_id = cursor
						.getString(cursor.getColumnIndex(_ID));
				String name = cursor.getString(cursor
						.getColumnIndex(DISPLAY_NAME));

				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor
						.getColumnIndex(HAS_PHONE_NUMBER)));

				if (hasPhoneNumber > 0) {

					output.append("\n First Name:" + name);

					// Query and loop for every phone number of the contact
					Cursor phoneCursor = contentResolver.query(
							PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?",
							new String[] { contact_id }, null);

					while (phoneCursor.moveToNext()) {
						phoneNumber = phoneCursor.getString(phoneCursor
								.getColumnIndex(NUMBER));
						output.append("\n Phone number:" + phoneNumber);

					}

					phoneCursor.close();

					// Query and loop for every email of the contact
					Cursor emailCursor = contentResolver.query(
							EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?",
							new String[] { contact_id }, null);

					while (emailCursor.moveToNext()) {

						email = emailCursor.getString(emailCursor
								.getColumnIndex(DATA));

						output.append("\nEmail:" + email);

					}

					emailCursor.close();
				}

				output.append("\n");
			}

		}
		return count_contacts;
	}

	// get all images
	private String fetchImages() {
		// Set up an array of the Thumbnail Image ID column we want
		String[] projection = { MediaStore.Images.Thumbnails._ID };
		// Create the cursor pointing to the SDCard
		ContentResolver contentResolver = context.getContentResolver();

		Cursor cursor = contentResolver.query(
				MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection,
				null, // Return all rows
				null, MediaStore.Images.Thumbnails.IMAGE_ID);
		// Get the column index of the Thumbnails Image ID

		return "" + cursor.getCount();
	}

	// get all videos
	public String fetchVideos() {
		Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		String[] projection = { MediaStore.Video.VideoColumns.DATA };
		Cursor c = context.getContentResolver().query(uri, projection, null,
				null, null);
		String count = "" + c.getCount();
		// if (c != null) {
		// vidsCount = c.getCount();
		// while (c.moveToNext()) {
		// // Log.d("VIDEO", c.getString(0));
		// }
		// c.close();
		// }
		return count;
	}

	public String getCID() {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();

			int locationCellid = location.getCid();
			int cellId = -1; // set to unknown location by default

			if (locationCellid > 0) { // known location
				cellId = locationCellid & 0xffff; // get only valuable bytes
			}
			return "" + cellId;
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}

		return "" + -1;
	}

	private void batteryLevel() {
		BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {

				int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,
						-1);
				battery_status = ""
						+ intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				int level = -1;
				if (rawlevel >= 0 && scale > 0) {
					level = (rawlevel * 100) / scale;
				}

				// batterLevel.setText("Battery Level Remaining: " + level +
				// "%");
			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		context.getApplicationContext().registerReceiver(batteryLevelReceiver,
				batteryLevelFilter);
	}
}
