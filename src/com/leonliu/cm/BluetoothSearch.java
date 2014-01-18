package com.leonliu.cm;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class BluetoothSearch {
	
	//======================================================================
	// Pref config
	protected PrefConfig config;
	
	//======================================================================
	// Bluetooth service
	public int REQUEST_ENABLE_BT = 1;
	protected BluetoothAdapter mAdapter;
	protected BluetoothDevice mDevice;
	protected Set<BluetoothDevice> mDiscoveredDevice;
	protected BluetoothService btService = null;
	private boolean mBound = false;
	private boolean mRegisted = false;
	private final Activity a;
	private MyInterface.OnProgressBarShow progressBar;
	private MyInterface.OnReadDataListner readData;
	private static BluetoothSearch self = null;

	// singleton
	private BluetoothSearch(Activity a) {
		this.a = a;
	}
	
	public static BluetoothSearch instance(Activity a) {
		if (a == null) return null;
		if (self == null) {
			self = new BluetoothSearch(a);
		}
		return self;
	}
	
	// public methods
	public void setProgressBar(MyInterface.OnProgressBarShow bar) {
		progressBar = bar;
	}
	
	public void setReadData(MyInterface.OnReadDataListner read) {
		readData = read;
	}

	public void UnbindBtService() {
		if (mBound) {
			a.unbindService(mConnection);
			mBound = false;
		}
		if (mRegisted) {
			a.unregisterReceiver(mReceiver);
			mRegisted = false;
		}
	}

	// call this method after enable bluetooth adapter, and start bluetooth service
	public void StartBtService() {

		mAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mAdapter == null) {
			AlertToast.showAlert(a, a.getString(R.string.err_nobluetooth));
			return;
		}
		
		if (btService == null) {
			InitBluetooth();
			return;
		}

		if (isBtConnected() == true) {
			btService.connect(mDevice, btHandler, null);
			return;
		}

		findBtDev();
	}
	
	public void ReStartBtService() {
		if (mAdapter == null) {
			AlertToast.showAlert(a, a.getString(R.string.err_nobluetooth));
			return;
		}

		if (btService == null) {
			AlertToast.showAlert(a, a.getString(R.string.err_btservicestop));
			return;
		}
		btService.close();
		
		config.setDeviceMac("");
		config.setDeviceName("");
		if (mAdapter.isDiscovering() == false)
			mAdapter.startDiscovery();
	}
	
	// private methods
	private void InitBluetooth() {
		mDiscoveredDevice = new HashSet<BluetoothDevice>();
		config = PrefConfig.instance(a);
		config.getCfg();
		
		if (btHandler == null)
			btHandler = new BtHandler(this);

		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		a.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
		mRegisted = true;

		Intent srvIntent = new Intent(a, BluetoothService.class);
		a.startService(srvIntent);
		a.bindService(srvIntent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	private boolean isBtConnected() {
		if (btService == null) return false;
		return btService.isConnected();
	}
	
	private void findBtDev() {
		Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
		if (findSavedDevice(pairedDevices)) {
			startConnectBt();
		}
		else {
			if (mAdapter.isDiscovering() == false)
				mAdapter.startDiscovery();
		}
	}
	
	private void startConnectBt() {
		if (progressBar != null)
			progressBar.ShowProgressBar(true);
		config.setDeviceName(mDevice.getName());
		config.setDeviceMac(mDevice.getAddress());
		config.saveBtCfg();

		if (btService != null)
			btService.connect(mDevice, btHandler, null);
		else {
			AlertToast.showAlert(a, a.getString(R.string.err_btservicestop));
			if (progressBar != null)
				progressBar.ShowProgressBar(false);
		}
	}

	private boolean findSavedDevice(Set<BluetoothDevice> devices) {
		// If there are paired devices
		if (devices.size() > 0) {
			for (BluetoothDevice device : devices) {
				if (config.getDeviceMac() != null && device.getAddress().equals(config.getDeviceMac())) {
					if (config.getDeviceName() != null && !device.getName().equals(config.getDeviceName()))
						continue;
					mDevice = device;
					return true;
				}
			}
		}
		return false;
	}
	
	protected final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            mDiscoveredDevice.add(device);
	        }
	        else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
	        	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                case BluetoothDevice.BOND_BONDING:
                    Log.d("MyActivity", "Bluetooth pairing......");
                    break;
                case BluetoothDevice.BOND_BONDED:
                    Log.d("MyActivity", "Bluetooth paired.");
					mDevice = device;
                    startConnectBt();
                    break;
                case BluetoothDevice.BOND_NONE:  
                    Log.d("MyActivity", "Bluetooth pair caceled.");  
                    break;  
                }
	        }
	        else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
	        	if (progressBar != null)
	        		progressBar.ShowProgressBar(true);
	        	mDiscoveredDevice.clear();
	        }
	        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	        	if (progressBar != null)
	        		progressBar.ShowProgressBar(false);
    		    // Loop through paired devices
    			if (findSavedDevice(mDiscoveredDevice)) {
					startConnectBt();
    			}
    			else {
    				if (mDiscoveredDevice.size() > 0) {
    					Dialog dlg = CreateBtSearchedDialog();
    					dlg.show();
    				}
    				else {
    					AlertToast.showAlert(a, a.getString(R.string.err_nobtadapter));
    				}
    			}
	        }
	    }
	};

	protected BtHandler btHandler = null;
	
	protected static class BtHandler extends Handler {
		private final WeakReference<BluetoothSearch> w;
		public BtHandler(BluetoothSearch s) {
			w = new WeakReference<BluetoothSearch>(s);
		}
		@Override
		public void handleMessage(Message msg) {
			String [] state_msg = w.get().a.getResources().getStringArray(R.array.connect_status);

			switch (msg.what) {
			case BluetoothThread.MESSAGE_READ:
				if (w.get().readData != null)
					w.get().readData.onReading((byte[])msg.obj, msg.arg1);
				break;
			case BluetoothThread.MESSAGE_STATE_CHANGE:
				if (msg.arg1 == BluetoothThread.STATE_CONNECTED) {
					if (w.get().progressBar != null)
						w.get().progressBar.ShowProgressBar(false);
				}
				AlertToast.showAlert(w.get().a, state_msg[msg.arg1]);
				break;
			case BluetoothThread.MESSAGE_CONNECTION_LOST:
			case BluetoothThread.MESSAGE_CONNECTION_FAIL:
				if (w.get().progressBar != null)
					w.get().progressBar.ShowProgressBar(false);
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	protected Dialog CreateBtSearchedDialog() {
		
		String[] stringArr = new String[mDiscoveredDevice.size()+1];
		int i = 0;
		for (BluetoothDevice device : mDiscoveredDevice) {
			stringArr[i++] = device.getName() + " " + device.getAddress();
		}
		stringArr[i] = a.getString(R.string.title_btdeny);
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(a);
	    builder.setTitle(R.string.title_btpopup)
	    		.setCancelable(true)
	    		.setItems(stringArr, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
            		   int i = 0;
            		   for (BluetoothDevice device : mDiscoveredDevice) {
            			   if (i == which) {
            				   mDevice = device;
            				   //蓝牙设备配对
            				   if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
            					 	//利用反射方法调用BluetoothDevice.createBond(BluetoothDevice remoteDevice);  
            	                    Method createBondMethod;
									try {
										createBondMethod = BluetoothDevice.class.getMethod("createBond");
	            	                    createBondMethod.invoke(mDevice);  
									} catch (Exception e) {
										e.printStackTrace();
									}
            				   }
            				   else
            					   startConnectBt();
            				   break;
            			   }
            		   }
	               }
	    });
	    return builder.create();
	}

	//======================================================================
	// service control
	ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			btService = ((BluetoothService.MsgBinder)service).getService();
			mBound = true;

			findBtDev();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			btService = null;
			mBound = false;
		}
		
	};
	
}
