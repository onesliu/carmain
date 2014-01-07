package com.leonliu.cm;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ProgressBar;


public class MainActivity extends MyActivity {

	private ProgressBar connectProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findAllView();
	}
	
	private void findAllView() {
		connectProgress = (ProgressBar) findViewById(R.id.connectProgress);
	}
	
	protected void ShowConnectProgressBar(boolean start) {
		super.ShowConnectProgressBar(start);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		//
		super.onPause();
	}

	@Override
	protected void onResume() {
		//
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
		connectBluetooth();
	}
	

}
