package com.leonliu.cm;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MyActivity extends Activity {

	
	//======================================================================
	// Pref config
	protected String PREF_CFG = "config";
	protected SharedPreferences cfgPref;
	
	protected void getCfg() {
		cfgPref = getSharedPreferences(PREF_CFG, 0);
		deviceName = cfgPref.getString("BluetoothDeviceName", "");
		deviceMac = cfgPref.getString("BluetoothDeviceMac", "");
	}
	
	protected void saveBtCfg() {
		cfgPref.edit().putString("BluetoothDeviceName", deviceName);
		cfgPref.edit().putString("BluetoothDeviceMac", deviceMac);
		cfgPref.edit().commit();
	}
	
	//======================================================================
	// Bluetooth service
	public int REQUEST_ENABLE_BT = 1;
	protected BluetoothAdapter mAdapter;
	protected BluetoothDevice mDevice;
	protected Set<BluetoothDevice> mDiscoveredDevice;
	protected BluetoothService btsrv;
	protected String deviceName;
	protected String deviceMac;
	
	public void connectBluetooth() {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mAdapter == null) {
			AlertToast.showAlert(this, getString(R.string.err_nobluetooth));
		}
		
		if (!mAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		else {
			if (searchPairedDevice()) {
				startConnectBt();
			}
			else {
				mAdapter.cancelDiscovery();
				mAdapter.startDiscovery();
			}
		}

	}
	
	protected boolean searchPairedDevice() {
		
		Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
	    return findSavedDevice(pairedDevices);
	}

	private boolean findSavedDevice(Set<BluetoothDevice> devices) {
		// If there are paired devices
		if (devices.size() > 0) {
		    // Loop through paired devices
			for (BluetoothDevice device : devices) {
			    // Add the name and address to an array adapter to show in a ListView
				if (deviceMac != null && device.getAddress().equals(deviceMac)) {
					if (deviceName != null && !device.getName().equals(deviceName))
						continue;
					mDevice = device;
					return true;
				}
			}
		}
		return false;
	}
	
	protected void ShowConnectProgressBar(boolean start) {
	}

	private void startConnectBt() {
		ShowConnectProgressBar(true);
		btsrv.connect(mDevice);
	}

	protected final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a ListView
	            mDiscoveredDevice.add(device);
	        }
	        else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
	        	ShowConnectProgressBar(true);
	        	mDiscoveredDevice.clear();
	        }
	        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	        	ShowConnectProgressBar(false);
	    		if (mDiscoveredDevice.size() > 0) {
	    		    // Loop through paired devices
	    			if (findSavedDevice(mDiscoveredDevice)) {
						startConnectBt();
	    			}
	    		}
	        }
	    }
	};
	
	protected final Handler bthandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String [] state_msg = getResources().getStringArray(R.array.connect_status);

			switch (msg.what) {
			case BluetoothService.MESSAGE_READ:
				break;
			case BluetoothService.MESSAGE_STATE_CHANGE:
				if (msg.arg1 == BluetoothService.STATE_CONNECTED) {
					retryBtStop = true;
					ShowConnectProgressBar(false);
				}
				else {
					AlertToast.showAlert(MyActivity.this, state_msg[msg.arg1]);
				}
				break;
			case BluetoothService.MESSAGE_CONNECTION_LOST:
			case BluetoothService.MESSAGE_CONNECTION_FAIL:
				retryBtStop = false;
				retryBt.start();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	protected boolean retryBtStop = false;
	Thread retryBt = new Thread() {
		public void run() {
			while (!retryBtStop) {
				startConnectBt();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					AlertToast.showAlert(MyActivity.this, getString(R.string.err_btdisconnect));
					break;
				}
			}
		}
	};
	
	protected Dialog CreateBtSearchedDialog() {
		
		String[] stringArr = new String[mDiscoveredDevice.size()];
		int i = 0;
		for (BluetoothDevice device : mDiscoveredDevice) {
			stringArr[i++] = device.getName() + " " + device.getAddress();
		}
		stringArr[i] = getString(R.string.title_btdeny);
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(R.string.title_btpopup)
	    		.setCancelable(true)
	    		.setItems(stringArr, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
            		   int i = 0;
            		   for (BluetoothDevice device : mDiscoveredDevice) {
            			   if (i == which) {
            				   mDevice = device;
							   startConnectBt();
            				   break;
            			   }
            		   }
	               }
	    });
	    return builder.create();
	}

	//======================================================================
	// Activity functions
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		btsrv = new BluetoothService(this, bthandler);
		mDiscoveredDevice = new HashSet<BluetoothDevice>();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPause() {
		unregisterReceiver(mReceiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
		
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		getCfg();
		
		super.onResume();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED) return;
		
		if (requestCode == REQUEST_ENABLE_BT) {
			if (searchPairedDevice()) {
				startConnectBt();
			}
			else {
				mAdapter.cancelDiscovery();
				mAdapter.startDiscovery();
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

}
