package com.ui.android.ota;

import java.io.File;
import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

// Controller of OTA Activity
public class SystemUpdateActivity extends Activity {

	final String TAG = "SystemUpdateActivity";
	private String mUpdatePackageLocation= "/udisk/";
	private String mUpdatePackageFile= "update.zip";
	WakeLock mWakelock;
	private AlertDialog m_AlertDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate--------------");
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakelock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "OTA Wakelock");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume--------------");
		Message msg = mHandler1.obtainMessage(1);
		mHandler1.removeMessages(1);
		mHandler1.sendMessageDelayed(msg, 10);
	}

	private Handler mHandler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				m_AlertDialog=onCreateDialog();
				m_AlertDialog.show();
				Log.d(TAG, "m_AlertDialog.show()");
				break;
			default:
				break;
			}
		}
	};

	private AlertDialog onCreateDialog() {
		Log.d(TAG, "onCreateDialog--------------");
		CharSequence[] array = { "OS (update.zip)"};//, "MCU (mcu.zip)"
		return new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon)
				.setTitle(R.string.system_update_activity_mesg)
				.setCancelable(false)
				.setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked on a radio button do some stuff */
						if (whichButton == 1) {
							mUpdatePackageFile= "mcu.zip";
						} /*else {
							mUpdatePackageFile= "update.zip";
						}*/
					}
				}).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked Yes so do some stuff */
						Toast.makeText(getBaseContext(),"update:"+mUpdatePackageLocation+mUpdatePackageFile, Toast.LENGTH_SHORT).show();
						startInstallUpgradePackage();
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						/* User clicked No so do some stuff */
						dialog.dismiss();
						finish();
					}
				}).create();
	}

	private void startInstallUpgradePackage() {
		File recoveryFile = new File(mUpdatePackageLocation+mUpdatePackageFile);

		try {
			mWakelock.acquire();
			RecoverySystem.installPackage(this, recoveryFile);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			mWakelock.release();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			if (m_AlertDialog !=null && m_AlertDialog.isShowing()) {
				m_AlertDialog.dismiss();
			}
		} catch (Exception e) {
		}
		finish();
	}

}
