package com.leonliu.cm;

import java.util.Set;

import com.leonliu.cm.obd.ObdInterface;
import com.leonliu.cm.obd.ObdModule;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class BluetoothService extends Service{

	public static final int MSG_SERVICE_STOP = 1;
	
	private BluetoothThread bthread = null;
	private BluetoothDevice mDevice = null;
	private Handler mbtHandler = null;
	private MyInterface.OnReadDataListner mListener = null;
	private PrefConfig config = null;
	ObdInterface.FlowDataInteface obdRealtime;

	//public methods
	public BluetoothThread connect(BluetoothDevice device, Handler btHandler, MyInterface.OnReadDataListner listner) {
		
		if (bthread != null) {
			if (bthread.getstate() == BluetoothThread.STATE_CONNECTED) {
				bthread.setHandler(btHandler, listner);
				return bthread;
			}
		}
		
		closeBthread();
		
		if (device == null) return null;
		mDevice = device;
		mbtHandler = btHandler;
		mListener = listner;
		bthread = new BluetoothThread(device, threadHandler);
		bthread.setHandler(btHandler, listner);
		bthread.start();
		
		return bthread;
	}
	
	public boolean isConnected() {
		if (bthread != null) {
			if (bthread.getstate() == BluetoothThread.STATE_CONNECTED) {
				return true;
			}
		}
		return false;
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
	
	public void close() {
		Log.i(this.getClass().getSimpleName(), "User is closing BT thread.");
		closeBthread();
	}

	private void closeBthread() {
		if (bthread != null) {
			bthread.cancel();
			bthread.interrupt();
			bthread = null;
		}
	}
	
	//Handler
	Handler threadHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SERVICE_STOP:
				Log.i(this.getClass().getSimpleName(), "Bluetooth adapter closed, BT thread will stop.");
				closeBthread();
				break;
			case BluetoothThread.MESSAGE_READ:
				obdRealtime.OnDataListener((byte[])msg.obj, msg.arg1);
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

		config = PrefConfig.instance(this);
		config.getCfg();
		mDevice = findSavedDevice(config);
		
		obdRealtime = ObdModule


		if (mDevice != null)
			connect(mDevice, mbtHandler, mListener);

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
			bthread.setHandler(null, null);
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
					Log.i(this.getClass().getSimpleName(), "Bluetooth adapter closed, BT thread will stop.");
					closeBthread();
				}
				else if (state == BluetoothAdapter.STATE_ON) {
					Log.i(this.getClass().getSimpleName(), "Bluetooth adapter turned on, BT thread will start.");
					if (mDevice != null)
						connect(mDevice, mbtHandler, mListener);
				}
			}
		}
	};
	
	private BluetoothDevice findSavedDevice(PrefConfig config) {
		
		BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mAdapter == null) return null;
		
		if (!mAdapter.isEnabled()) return null;
		
		Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (config.getDeviceMac() != null && device.getAddress().equals(config.getDeviceMac())) {
					if (config.getDeviceName() != null && !device.getName().equals(config.getDeviceName()))
						continue;
					return device;
				}
			}
		}
		return null;
	}

}
