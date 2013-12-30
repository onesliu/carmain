package com.leonliu.cm;

import java.util.ArrayList;
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
	private ArrayList<String> btlist;
	private BluetoothDevice selectedBtDev;
	private BluetoothAdapter mBluetoothAdapter;
	private Set<BluetoothDevice> pairedDevices;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btlist = new ArrayList<String>();
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		IntentFilter filter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
		registerReceiver(mReceiver, filter2);
		registerReceiver(mReceiver, filter3);
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public Dialog onCreateDialog() {
		
		String[] stringArr = new String[btlist.size()];
		btlist.toArray(stringArr);
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(R.string.title_btpopup)
	           .setItems(stringArr, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
		               // The 'which' argument contains the index position
		               // of the selected item
	            	   if (btlist.size() == which+1) {
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

	private void showBtDialog(BluetoothAdapter mBluetoothAdapter) {
		
		pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
			btlist.clear();
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		    	btlist.add(device.getName());
		    }
		}
		
		if (btlist.size() > 0) {
			btlist.add(getString(R.string.title_btsearch));
			Dialog dlg = onCreateDialog();
			dlg.show();
		}
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
	            btlist.add(device.getName());
	        }
	        else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
	        	btlist.clear();
	        }
	        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	    		if (btlist.size() > 0) {
	    			Dialog dlg = onCreateDialog();
	    			dlg.show();
	    		}
	    		else  {
	    			AlertToast.showAlert(MainActivity.this, getString(R.string.err_nobtadapter));
	    		}
	        }
	    }
	};
}
