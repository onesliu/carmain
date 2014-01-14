package com.leonliu.cm;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class BluetoothService extends Service{

	// Debugging
	private static final String TAG = "BluetoothService";
	
	public static final int MSG_REPLY = 1;
	private BluetoothThread bthread = null;
	private Messenger mMessenger;
	private Messenger rMessenger;

	//public methods
	public void connect(BluetoothDevice device, MyInterface.OnReadDataListner listner) {
		if (bthread != null) {
			bthread.cancel();
			bthread = null;
		}
		
		bthread = new BluetoothThread(device, mHandler, listner);
		bthread.start();
	}
	
	//message handler
	Handler rHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REPLY:
				rMessenger = msg.replyTo;
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (rMessenger != null) {
				try {
					rMessenger.send(msg);
				}catch(RemoteException e) {
					Log.e(TAG, "rMessenger send msg to Activity Exception.");
				}
			}
			super.handleMessage(msg);
		}
	};
	
	//Service support
	@Override
	public void onCreate() {
		mMessenger = new Messenger(rHandler);
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
