package com.leonliu.cm;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;


public class MainActivity extends Activity {

	public static final int REQUEST_ENABLE_BT = 1;
	private BluetoothSearch btSearch;
	private ProgressBar connectProgress;
	private EditText inputText;
	private EditText outputText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findAllView();
	}
	
	private void findAllView() {
		connectProgress = (ProgressBar) findViewById(R.id.connectProgress);
		inputText = (EditText)findViewById(R.id.debugInput);
		outputText = (EditText)findViewById(R.id.debugOutput);
	}
	
	public void onConnectBt(View v) {
		btSearch.RefindBluetooth();
	}
	
	public void onClickSend(View v) {
		btSearch.btService.write(inputText.getText().toString().getBytes());
		inputText.setText("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onStart() {
		BluetoothAdapterEnable();
		super.onStart();
	}
	
	private void BluetoothAdapterEnable() {
		btSearch = BluetoothSearch.instance(this);
		if (BluetoothAdapter.getDefaultAdapter().enable() == false) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		else {
			StartBluetooth();
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED) return;
		
		if (requestCode == REQUEST_ENABLE_BT) {
			StartBluetooth();
		}
	}

	private void StartBluetooth() {
		btSearch.InitBluetooth();
		btSearch.setProgressBar(new MyInterface.OnProgressBarShow() {
			@Override
			public void ShowProgressBar(boolean show) {
				connectProgress.setVisibility((show)?View.VISIBLE:View.GONE);
			}
		});
		btSearch.setReadData(new MyInterface.OnReadDataListner() {
			@Override
			public void onReading(byte[] buffer, int len) {
				outputText.getText().append("\r\n" + new String(buffer, 0, len));
			}
		});
		btSearch.FindBtDevice();
	}

}
