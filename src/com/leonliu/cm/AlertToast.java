package com.leonliu.cm;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class AlertToast {

	private static Toast toast = null;

	public static void showAlert(Context c, String msg) {
        if (toast == null) {
            toast = Toast.makeText(c , msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
}
