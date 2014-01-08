package com.leonliu.cm;

import java.util.Arrays;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends MyActivity {

	private ProgressBar connectProgress;
	private TextView inputText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findAllView();
	}
	
	private void findAllView() {
		connectProgress = (ProgressBar) findViewById(R.id.connectProgress);
		inputText = (TextView)findViewById(R.id.debugInput);
	}
	
	protected void ShowConnectProgressBar(boolean start) {
		super.ShowConnectProgressBar(start);
		connectProgress.setVisibility((start)?View.VISIBLE:View.GONE);
	}
	
	public void onConnectBt(View v) {
		reconnectBluetooth();
	}
	
	public void onClickSend(View v) {
		btsrv.write(inputText.getText().toString().getBytes());
		inputText.setText("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onStart() {
		super.onStart();
		connectBluetooth();
	}
	

}
