package com.leonliu.cm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Alarmreceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("leonliu.alarm.action")) {
			Intent i = new Intent();
			i.setClass(context, BluetoothService.class);
			// 启动service
			// 多次调用startService并不会启动多个service 而是会多次调用onStart
			context.startService(i);
		}
	}

}
