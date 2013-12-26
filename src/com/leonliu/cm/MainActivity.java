package com.leonliu.cm;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.example.carmaintances.R;

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
	            	   //selectedBtDev = pairedDevices.toArray()[which]
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
}
