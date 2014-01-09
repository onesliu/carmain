package com.leonliu.cm;

import java.lang.reflect.Method;
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
import android.util.Log;

public class MyActivity extends Activity {

	
	//======================================================================
	// Pref config
	protected String PREF_CFG = "config";
	protected SharedPreferences cfgPref;
	
	protected final String keyBtDevName = "BluetoothDeviceName";
	protected final String keyBtDevMac = "BluetoothDeviceMac";
	
	protected void getCfg() {
		cfgPref = getSharedPreferences(PREF_CFG, 0);
		deviceName = cfgPref.getString(keyBtDevName, "");
		deviceMac = cfgPref.getString(keyBtDevMac, "");
	}
	
	protected void saveBtCfg() {
		cfgPref.edit().putString(keyBtDevName, deviceName).commit();
		cfgPref.edit().putString(keyBtDevMac, deviceMac).commit();
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

		if (mAdapter == null) {
			AlertToast.showAlert(this, getString(R.string.err_nobluetooth));
			return;
		}
		
		if (!mAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			//enableBtIntent.
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		else {
			findBtDev();
		}
	}
	
	public void reconnectBluetooth() {
		deviceName = "";
		deviceMac = "";
		saveBtCfg();
		connectBluetooth();
	}

	private void findBtDev() {
		if (searchPairedDevice()) {
			startConnectBt();
		}
		else {
			if (mAdapter.isDiscovering() == false)
				mAdapter.startDiscovery();
		}
	}
	
	private void startConnectBt() {
		ShowConnectProgressBar(true);
		deviceName = mDevice.getName();
		deviceMac = mDevice.getAddress();
		saveBtCfg();
		btsrv.connect(mDevice);
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
	        	ShowConnectProgressBar(true);
	        	mDiscoveredDevice.clear();
	        }
	        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	        	ShowConnectProgressBar(false);
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
    					AlertToast.showAlert(MyActivity.this, getString(R.string.err_nobtadapter));
    				}
    			}
	        }
	    }
	};

	protected void readFromBluetooth(byte[] buffer, int len) {
	}
	
	protected final Handler bthandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String [] state_msg = getResources().getStringArray(R.array.connect_status);

			switch (msg.what) {
			case BluetoothService.MESSAGE_READ:
				readFromBluetooth((byte[])msg.obj, msg.arg1);
				break;
			case BluetoothService.MESSAGE_STATE_CHANGE:
				if (msg.arg1 == BluetoothService.STATE_CONNECTED) {
					ShowConnectProgressBar(false);
					if (StopBtRetry == true) {
						StopBtRetry = false;
						retryBt.start();
					}
				}
				AlertToast.showAlert(MyActivity.this, state_msg[msg.arg1]);
				break;
			case BluetoothService.MESSAGE_CONNECTION_LOST:
			case BluetoothService.MESSAGE_CONNECTION_FAIL:
				ShowConnectProgressBar(false);
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	protected boolean StopBtRetry = true;
	Thread retryBt = new Thread() {
		public void run() {
			while (StopBtRetry == false) {
				if (btsrv.getState() == BluetoothService.STATE_NONE) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						AlertToast.showAlert(MyActivity.this, getString(R.string.err_btdisconnect));
						break;
					}

					startConnectBt();
				}
			}
		}
	};
	
	protected Dialog CreateBtSearchedDialog() {
		
		String[] stringArr = new String[mDiscoveredDevice.size()+1];
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
	// Activity functions
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		btsrv = new BluetoothService(this, bthandler);
		mDiscoveredDevice = new HashSet<BluetoothDevice>();
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		getCfg();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStop() {
		unregisterReceiver(mReceiver);
		StopBtRetry = true;
		super.onStop();
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
		StopBtRetry = false;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED) return;
		
		if (requestCode == REQUEST_ENABLE_BT) {
			findBtDev();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

}
