package com.leonliu.cm;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;


public class MainActivity extends Activity {

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
	
	protected void ShowConnectProgressBar(boolean start) {
		super.ShowConnectProgressBar(start);
		connectProgress.setVisibility((start)?View.VISIBLE:View.GONE);
	}

	protected void readFromBluetooth(byte[] buffer, int len) {
		//outputText.
	}

	public void onConnectBt(View v) {
		reconnectBluetooth();
	}
	
	public void onClickSend(View v) {
		btService.write(inputText.getText().toString().getBytes());
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
	}
	

}
