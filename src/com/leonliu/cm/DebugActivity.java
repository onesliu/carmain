package com.leonliu.cm;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leonliu.cm.obd.ObdDao;
import com.leonliu.cm.utils.MyUtils;


public class DebugActivity extends Activity {

	private BluetoothSearch btSearch;
	private ProgressBar connectProgress;
	private EditText inputText;
	private TextView outputText;
	private TextView binOutput;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		findAllView();
		Log.d(this.getClass().getSimpleName(), "Activity onCreate.");
	}
	
	private void findAllView() {
		connectProgress = (ProgressBar) findViewById(R.id.connectProgress);
		inputText = (EditText)findViewById(R.id.debugInput);
		outputText = (TextView)findViewById(R.id.debugOutput);
		binOutput = (TextView)findViewById(R.id.binOutput);
	}
	
	public void onConnectBt(View v) {
		btSearch.ReStartBtService();
	}
	
	public void onClickSend(View v) {
		/*String s = inputText.getText().toString();
		if (s.length() > 0 && s.charAt(0) == '0') {
			btSearch.btService.write(MyUtils.strToHex(s));
			btSearch.btService.write("\n".getBytes());
		}
		else {*/
			btSearch.btService.write((inputText.getText().toString()+ "\r").getBytes());
		//}
		inputText.setText("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onStart() {
		btSearch = BluetoothSearch.instance(this);
		if (btSearch != null) {
			btSearch.setProgressBar(onProgress);
			btSearch.setReadData(onReadData);
		}
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		if (btSearch != null) {
			btSearch.setProgressBar(null);
			btSearch.setReadData(null);
		}
		super.onStop();
	}

	private MyInterface.OnProgressBarShow onProgress = new MyInterface.OnProgressBarShow() {
		@Override
		public void ShowProgressBar(boolean show) {
			connectProgress.setVisibility((show)?View.VISIBLE:View.GONE);
		}
	};
	
	private MyInterface.OnReadDataListner onReadData = new MyInterface.OnReadDataListner() {
		@Override
		public void onReading(byte[] buf, int len) {
			StringBuffer sbuf = new StringBuffer(binOutput.getText());
			sbuf = new StringBuffer(binOutput.getText());
			sbuf.insert(0, new String(buf) + "\n");
			binOutput.setText(sbuf);
		}

		@Override
		public void onReading(ObdDao data) {
			StringBuffer sbuf = new StringBuffer();
			sbuf.append(String.format("电瓶电压：%.2fv", data.getBat()) +
					System.getProperty("line.separator"));
			sbuf.append(String.format("发动机转速：%d", data.getRpm()) +
					System.getProperty("line.separator"));
			sbuf.append(String.format("行驶时速：%d", data.getVss()) +
					System.getProperty("line.separator"));
			sbuf.append(String.format("节气门开度：%.1f%%", data.getTp()) +
					System.getProperty("line.separator"));
			sbuf.append(String.format("发动机负荷：%.1f%%", data.getLod()) +
					System.getProperty("line.separator"));
			sbuf.append(String.format("冷却液温度：%dC", data.getEct()) +
					System.getProperty("line.separator"));
			sbuf.append(String.format("瞬时油耗：%.2fL", data.getMpg()) +
					System.getProperty("line.separator"));
			sbuf.append(String.format("平均油耗：%.2fL/100km", data.getAvm()) +
					System.getProperty("line.separator"));
			sbuf.append(String.format("本次行驶里程：%.2fkm", data.getDst()) +
					System.getProperty("line.separator"));
			sbuf.append(String.format("总里程：%.2fkm", data.getTDst()) +
					System.getProperty("line.separator"));
			sbuf.append(String.format("本次耗油量：%.2fL", data.getFue()) +
					System.getProperty("line.separator"));
			sbuf.append(String.format("累计耗油量：%.2fL", data.getTFue()) +
					System.getProperty("line.separator"));
			
			outputText.setText(sbuf);
		}
	};
}
