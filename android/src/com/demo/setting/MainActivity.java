package com.demo.setting;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView textView1, textView2, textView3;
	String health;
	protected String status;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textView1 = (TextView) findViewById(R.id.textView1);
		textView2 = (TextView) findViewById(R.id.textView2);
		textView3 = (TextView) findViewById(R.id.textView3);

		String str_manufacture = "manufacture--"
				+ android.os.Build.MANUFACTURER + "\n"; // manufacture
		String str_device = "device--" + android.os.Build.DEVICE + "\n"; // device
		String str_product = "product--" + android.os.Build.PRODUCT + "\n"; // product
		String str_brand = "brand--" + android.os.Build.BRAND + "\n"; // brand
		String str_cpu_abi = "cpu_abi--" + android.os.Build.CPU_ABI + "\n"; // cpu_abi
		String str_cpu_abi2 = "cpu_abi2--" + android.os.Build.CPU_ABI2 + "\n"; // cpu_abi2
		String str_Kernel_version = "Kernel_version--"
				+ System.getProperty("os.version") + "\n"; // Kernel version
		String str_build_number = "os.version--" + android.os.Build.FINGERPRINT
				+ "\n"; // str_build_number
		String str_release = "relese--" + android.os.Build.VERSION.RELEASE
				+ "\n"; // release
		String str_sdk = "sdk--" + android.os.Build.VERSION.SDK_INT + "\n"; // sdk
		String str_display = "display-" + android.os.Build.DISPLAY + "\n"; // display

		// get screen dpi
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		String str_screen_size = "width--" + dm.widthPixels + "height--"
				+ dm.heightPixels + "\n"; // screen size -in pixel
		String str_densityDpi = "densityDpi--" + dm.densityDpi + "\n"; // densityDpi
		String str_logincal_density = "logincal_density--" + dm.density + "\n"; // logincal_density
		String str_font_scaling_factor = "font_scaling_factor--"
				+ dm.scaledDensity + "\n"; // font_scaling_factor
		String str_pixel_per_inch = "pixel_per_inch--x" + dm.xdpi
				+ "pixel_per_inch--y" + dm.ydpi + "\n";
		String str_health = "health--" + health + "\n";
		String str_status = "status--" + status + "\n";
		String str_wireless_ssid = "str_wirelss_ssid--"
				+ getCurrentSsid(getApplicationContext()) + "\n";

		// memory information
		String str_external = "External Memory Total--"
				+ getTotalExternalMemorySize() + "\n"
				+ "External Memory Free--" + getAvailableExternalMemorySize()
				+ "\n";
		String str_internal = "Internal Memory Total--"
				+ getTotalInternalMemorySize() + "\n"
				+ "Internal Memory Free--" + getAvailableInternalMemorySize()
				+ "\n";
		String memory;
		if (externalMemoryAvailable()) {
			memory = str_external + str_internal;
		} else {
			memory = str_internal;
		}

		String str_intalledAppSize = TotalApplicationIntalled();
		String str_callCount = "Total Call Count--"
				+ getCallDetails(3600000 * 1) + "\n";
		String count_contacts = "Contact List Size--" + fetchContacts() + "\n";
		String count_images = "Images List Size--" + fetchImages() + "\n";
		String count_videos = "Video List Size--" + fetchVideos() + "\n";
		String cellId = "Cell Id::" + getCID();

		textView1.setText(str_manufacture + str_device + str_product
				+ str_brand + str_cpu_abi + str_cpu_abi2 + str_Kernel_version
				+ str_build_number + str_release + str_sdk + str_display
				+ str_screen_size + str_densityDpi + str_logincal_density
				+ str_font_scaling_factor + str_pixel_per_inch + str_health
				+ str_status + str_wireless_ssid + memory + str_intalledAppSize
				+ str_callCount + count_contacts + count_images + count_videos
				+ cellId);

		// textView2.setText(ReadCPUinfo());

	}

	// total application installed
	private String TotalApplicationIntalled() {
		final PackageManager pm = getPackageManager();
		// get a list of installed apps.
		List<ApplicationInfo> packages = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);
		String str_intalledAppSize = "IntalledAppSize" + packages.size() + "\n";

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
	private String ReadCPUinfo() {
		ProcessBuilder cmd;
		String result = "";

		try {
			String[] args = { "/system/bin/cat", "/proc/cpuinfo" };
			cmd = new ProcessBuilder(args);

			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[1024];
			while (in.read(re) != -1) {
				System.out.println(new String(re));
				result = result + new String(re);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
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
		Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
				null, null, null);
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

		ContentResolver contentResolver = getContentResolver();

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
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(
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
		Cursor c = getContentResolver()
				.query(uri, projection, null, null, null);
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
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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

}
