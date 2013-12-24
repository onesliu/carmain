package com.leonliu.cm;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class AlertToast {

	private static Handler handler = new Handler(Looper.getMainLooper());
	private static Toast toast = null;
	private static Object synObj = new Object();

	public static void showAlert(final Context c, final String msg) {

		new Thread(new Runnable() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						synchronized (synObj) {
							if (toast != null) {
								toast.cancel();
								toast.setText(msg);
								toast.setDuration(Toast.LENGTH_SHORT);
							} else {
								toast = Toast.makeText(c, msg,
										Toast.LENGTH_SHORT);
							}
							toast.show();
						}
					}
				});
			}
		}).start();
	}
}
