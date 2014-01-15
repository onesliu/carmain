package com.leonliu.cm;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class BluetoothService extends Service{

	// Debugging
	private static final String TAG = "BluetoothService";
	
	private BluetoothThread bthread = null;

	//public methods
	public BluetoothThread connect(BluetoothDevice device, Handler btHandler, MyInterface.OnReadDataListner listner) {
		if (bthread != null) {
			bthread.cancel();
			bthread = null;
		}
		
		bthread = new BluetoothThread(device, listner);
		bthread.setHandler(btHandler);
		bthread.start();
		
		return bthread;
	}
	
	public boolean write(byte[] out) {
		if (bthread != null) {
			return bthread.write(out);
		}
		return false;
	}
	
	public BluetoothThread getBthread() {
		return bthread;
	}
	
	//Service support
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "Bluetooth Service onBind, bthread = " + bthread);
		return new MsgBinder();
	}
	
	@Override
	public void onRebind(Intent intent) {
		Log.i(TAG, "Bluetooth Service onRebind, bthread = " + bthread);
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "Bluetooth Service onUnbind, bthread = " + bthread);
		if (bthread != null)
			bthread.setHandler(null);
		return super.onUnbind(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Bluetooth Service onStartCommand, bthread = " + bthread);
		return super.onStartCommand(intent, flags, startId);
	}

	public class MsgBinder extends Binder {
		public BluetoothService getService() {
			return BluetoothService.this;
		}
	}

}
