package com.leonliu.cm;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;

public class BluetoothService extends Service{

	// Debugging
	private static final String TAG = "BluetoothService";
	
	private BluetoothThread bthread = null;
	private Messenger mMessenger;
	private Messenger rMessenger;

	//public methods
	public void connect(BluetoothDevice device) {
		if (bthread != null) {
			bthread.cancel();
			bthread = null;
		}
		
		bthread = new BluetoothThread(device, handler, )
	}
	
	//message handler
	Handler handler = new Handler() {
		
	};
	
	//Service support
	@Override
	public void onCreate() {
		mMessenger = new Messenger(handler);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new MsgBinder();
	}
	
	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	public class MsgBinder extends Binder {
		public BluetoothService getService() {
			return BluetoothService.this;
		}
	}

}
