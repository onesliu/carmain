package com.leonliu.cm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class BluetoothThread extends Thread {

	private static final String TAG = "BluetoothThread";
	// Unique UUID for this application
	//private static final UUID SPP_UUID = UUID
	//		.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final UUID SPP_UUID = UUID
			.fromString("00001108-0000-1000-8000-00805F9B34FB");

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0;
	public static final int STATE_CONNECTING = 1;
	public static final int STATE_CONNECTED = 2;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_CONNECTION_FAIL = 4;
	public static final int MESSAGE_CONNECTION_LOST = 5;
	
	// lock object
	private int mState;
	private boolean stop = false;
	private byte []buffer = new byte[1024];
	private Handler mHandler;
	private final BluetoothAdapter mAdapter;
	private final BluetoothDevice mmDevice;
	private BluetoothSocket mmSocket;
	private InputStream mmInStream;
	private OutputStream mmOutStream;
	
	private final MyInterface.OnReadDataListner onReadDataListner;
	
	public BluetoothThread(BluetoothDevice device, MyInterface.OnReadDataListner listner) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mmDevice = device;
		onReadDataListner = listner;
	}
	
	public void setHandler(Handler handler) {
		mHandler = handler;
		Log.i(TAG, "Set activity's handler to " + mHandler);
	}
	
	private void sleep(int seconds) {
		try {
			Log.i(TAG, "sleep " + seconds + " seconds...");
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			Log.i(TAG, "sleep was interrupted by Exception.");
		}
	}

	private synchronized void setstate(int state) {
		if (mState != state) {
			mState = state;
			// Give the new state to the Handler so the UI Activity can update
			if (mHandler != null)
				mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
		}
	}

	/**
	 * Return the current connection state.
	 */
	public synchronized int getstate() {
		return mState;
	}
	
	public boolean write(byte[] out) {
		
		if (out.length <= 0) return true;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != STATE_CONNECTED)
				return false;
		}
		
		// Perform the write unsynchronized
		try {
			if (mmOutStream != null)
				mmOutStream.write(buffer);
			else
				return false;
		} catch (IOException e) {
			Log.e(TAG, "mmSocket write stream Exception.");
			return false;
		}

		return true;
	}
	
	public void cancel() {
		stop = true;
		close();
	}

	private synchronized void close() {
		try {
			if (mmInStream != null) {
				mmInStream.close();
			}
		}catch(IOException e) {
			Log.e(TAG, "mmSocket mmInStream close Exception.");
		}
		
		try {
			if (mmOutStream != null) {
				mmOutStream.close();
			}
		}catch(IOException e) {
			Log.e(TAG, "mmSocket mmOutStream close Exception.");
		}
		
		try {
			if (mmSocket != null) {
				mmSocket.close();
			}
		}catch(IOException e) {
			Log.e(TAG, "mmSocket close Exception.");
		}
		
		mmInStream = null;
		mmOutStream = null;
		mmSocket = null;
	}
	
	@Override
	public void run() {

		int waittime = 0;
		
		while (stop == false) {

			close();
			sleep(waittime);
			
			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			Log.i(TAG, "createRfcommSocketToServiceRecord to " + mmDevice.getName());
			setstate(STATE_CONNECTING);
			try {
				mmSocket = mmDevice.createRfcommSocketToServiceRecord(SPP_UUID);
			} catch (IOException e) {
				Log.e(TAG, "mmSocket gotten from device Exception.");
				setstate(STATE_NONE);
				waittime = 30;
				if (mHandler != null)
					mHandler.obtainMessage(MESSAGE_CONNECTION_FAIL).sendToTarget();
				continue;
			}

			Log.i(TAG, "Begin connect to " + mmDevice.getName());
			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();
			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();
			} catch (IOException e) {
				Log.e(TAG, "mmSocket connect Exception.");
				setstate(STATE_NONE);
				waittime = 30;
				if (mHandler != null)
					mHandler.obtainMessage(MESSAGE_CONNECTION_FAIL).sendToTarget();
				continue;
			}

			Log.i(TAG, "Connected to " + mmDevice.getName());
			// Get the BluetoothSocket input and output streams
			try {
				mmInStream = mmSocket.getInputStream();
				mmOutStream = mmSocket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "mmSocket get stream Exception.");
				setstate(STATE_NONE);
				waittime = 30;
				if (mHandler != null)
					mHandler.obtainMessage(MESSAGE_CONNECTION_LOST).sendToTarget();
				continue;
			}

			// Keep listening to the InputStream while connected
			while (stop == false) {
				try {
					// Read from the InputStream
					int bytes = mmInStream.read(buffer);

					// callback
					if (onReadDataListner != null) {
						onReadDataListner.onReading(buffer, bytes);
					}

					// Send the obtained bytes to the UI Activity
					if (mHandler != null)
						mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
				} catch (IOException e) {
					Log.e(TAG, "mmSocket read stream Exception.");
					setstate(STATE_NONE);
					waittime = 5;
					if (mHandler != null)
						mHandler.obtainMessage(MESSAGE_CONNECTION_LOST).sendToTarget();
					break;
				}
			}
		}
		
		close();
		setstate(STATE_NONE);
	}

}
