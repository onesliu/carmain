package com.leonliu.cm;

import java.util.HashMap;
import java.util.Map;
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
import android.os.Bundle;
import android.view.Menu;


public class MainActivity extends Activity {

	public int REQUEST_ENABLE_BT = 1;
	private Map<String,String> btmap;
	private BluetoothDevice selectedBtDev;
	private BluetoothAdapter mBluetoothAdapter;
	private Set<BluetoothDevice> pairedDevices;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btmap = new HashMap<String,String>();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
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
		IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		IntentFilter filter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
		registerReceiver(mReceiver, filter2);
		registerReceiver(mReceiver, filter3);
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onStart() {
		super.onStart();
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			AlertToast.showAlert(this, getString(R.string.err_nobluetooth));
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		else {
			showBtDialog(mBluetoothAdapter);
		}
	}

	public Dialog CreateBtPairedDialog() {
		
		String[] stringArr = new String[btmap.size()+1];
		btmap.values().toArray(stringArr);
		stringArr[stringArr.length-1] = getString(R.string.title_btsearch);
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(R.string.title_btpopup)
	    		.setCancelable(false)
	    		.setItems(stringArr, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
		               // The 'which' argument contains the index position
		               // of the selected item
	            	   if (btmap.size() == which+1) {
	            		   if (mBluetoothAdapter.isDiscovering() == false)
	            			   mBluetoothAdapter.startDiscovery();
	            	   }
	            	   else {
	            		   int i = 0;
	            		   for (BluetoothDevice device : pairedDevices) {
	            			   if (i == which) {
	            				   selectedBtDev = device;
	            				   AlertToast.showAlert(MainActivity.this, selectedBtDev.getName());
	            				   break;
	            			   }
	            		   }
	            	   }
	               }
	    });
	    return builder.create();
	}
	
	public Dialog CreateBtSearchedDialog() {
		
		String[] stringArr = new String[btmap.size()+1];
		btmap.values().toArray(stringArr);
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(R.string.title_btpopup)
	    		.setCancelable(false)
	    		.setItems(stringArr, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	               }
	    });
	    return builder.create();
	}

	private void showBtDialog(BluetoothAdapter mBluetoothAdapter) {
		
		pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
			btmap.clear();
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		    	btmap.put(device.getAddress(), device.getName());
		    }
		}
		
		Dialog dlg = CreateBtPairedDialog();
		dlg.show();
	}
	
	public void onActivityResult (int requestCode, int resultCode, Intent data) {
		
		if (resultCode == RESULT_CANCELED) return;
		
		if (requestCode == REQUEST_ENABLE_BT) {
			showBtDialog(mBluetoothAdapter);
		}
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a ListView
	            btmap.put(device.getAddress(), device.getName());
	        }
	        else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
	        	btmap.clear();
	        }
	        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	    		if (btmap.size() > 0) {
	    			Dialog dlg = CreateBtSearchedDialog();
	    			dlg.show();
	    		}
	    		else  {
	    			AlertToast.showAlert(MainActivity.this, getString(R.string.err_nobtadapter));
	    		}
	        }
	    }
	};
}
