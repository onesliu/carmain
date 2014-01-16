package com.leonliu.cm;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class BluetoothService extends Service{

	public static final int MSG_SERVICE_STOP = 1;
	
	private BluetoothThread bthread = null;

	//public methods
	public BluetoothThread connect(BluetoothDevice device, Handler btHandler, MyInterface.OnReadDataListner listner) {
		if (bthread != null) {
			bthread.cancel();
			bthread = null;
		}
		
		bthread = new BluetoothThread(device, threadHandler, listner);
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
	
	//Handler
	Handler threadHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SERVICE_STOP:
				Log.i(this.getClass().getSimpleName(), "Bluetooth adapter closed, service will stop.");
				if (bthread != null)
					bthread.cancel();
				stopSelf();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	//Service support
	@Override
	public void onCreate() {
		Log.i(this.getClass().getSimpleName(), "Bluetooth Service onCreate");
		
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.i(this.getClass().getSimpleName(), "Bluetooth Service onDestroy");
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(this.getClass().getSimpleName(), "Bluetooth Service onBind, bthread = " + bthread);
		return new MsgBinder();
	}
	
	@Override
	public void onRebind(Intent intent) {
		Log.i(this.getClass().getSimpleName(), "Bluetooth Service onRebind, bthread = " + bthread);
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(this.getClass().getSimpleName(), "Bluetooth Service onUnbind, bthread = " + bthread);
		if (bthread != null)
			bthread.setHandler(null);
		super.onUnbind(intent);
		return false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(this.getClass().getSimpleName(), "Bluetooth Service onStartCommand, bthread = " + bthread);
		return super.onStartCommand(intent, flags, startId);
	}

	//Binder
	public class MsgBinder extends Binder {
		public BluetoothService getService() {
			return BluetoothService.this;
		}
	}
	
	//Broadcaster
	protected final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
				if (state == BluetoothAdapter.STATE_OFF) {
					Log.i(this.getClass().getSimpleName(), "Bluetooth adapter closed, service will stop.");
					if (bthread != null)
						bthread.cancel();
					stopSelf();
				}
			}
		}
	};

}
