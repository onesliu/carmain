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

	// Unique UUID for this application
	private static final UUID SPP_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
/*
	private static final UUID SPP_UUID = UUID
			.fromString("00001108-0000-1000-8000-00805F9B34FB");
*/
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
	private Handler sHandler;
	private final BluetoothAdapter mAdapter;
	private final BluetoothDevice mmDevice;
	private BluetoothSocket mmSocket;
	private InputStream mmInStream;
	private OutputStream mmOutStream;
	
	private MyInterface.OnReadDataListner onReadDataListner;
	
	public BluetoothThread(BluetoothDevice device, Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		sHandler = handler;
		mmDevice = device;
		setName(this.getClass().getSimpleName());
	}
	
	public synchronized void setHandler(Handler handler, MyInterface.OnReadDataListner listner) {
		mHandler = handler;
		onReadDataListner = listner;
		Log.i(this.getClass().getSimpleName(), "Set activity's handler to " + mHandler);
		Log.i(this.getClass().getSimpleName(), "Set read data listner to " + listner);
	}
	
	private void sleep(int seconds) {
		try {
			Log.i(this.getClass().getSimpleName(), "sleep " + seconds + " seconds...");
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			Log.i(this.getClass().getSimpleName(), "sleep was interrupted by Exception.");
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

	private synchronized void handlerSendMsg(int what, int arg1, int arg2, Object obj) {
		if (mHandler != null)
			mHandler.obtainMessage(what, arg1, arg2, obj).sendToTarget();
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
				mmOutStream.write(out);
			else
				return false;
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), "mmSocket write stream Exception.");
			return false;
		}

		Log.d(this.getClass().getSimpleName(), "mmSocket write " + out.length + " bytes to BT device.");
		handlerSendMsg(MESSAGE_WRITE, out.length, -1, null);
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
			Log.e(this.getClass().getSimpleName(), "mmSocket mmInStream close Exception.");
		}
		
		try {
			if (mmOutStream != null) {
				mmOutStream.close();
			}
		}catch(IOException e) {
			Log.e(this.getClass().getSimpleName(), "mmSocket mmOutStream close Exception.");
		}
		
		try {
			if (mmSocket != null) {
				mmSocket.close();
			}
		}catch(IOException e) {
			Log.e(this.getClass().getSimpleName(), "mmSocket close Exception.");
		}
		
		mmInStream = null;
		mmOutStream = null;
		mmSocket = null;
	}
	
	static int waitcount = 0;
	static int lasterror = MESSAGE_CONNECTION_FAIL;
	private int getWaittime(int error) {
		int ret = 5;
		if (error == lasterror) {
			if (waitcount < 5) 
				waitcount++;
			else
				ret = 30;
		}
		else {
			waitcount = 0;
		}
		lasterror = error;
		return ret;
	}
	
	@Override
	public void run() {

		int waittime = 0;
		
		while (stop == false) {

			//如果蓝牙设备关掉了，线程就彻底停止
			if (mAdapter == null || mAdapter.isEnabled() == false) {
				sHandler.obtainMessage(BluetoothService.MSG_SERVICE_STOP).sendToTarget();
				break;
			}
			
			close();
			sleep(waittime);
			if (stop == true) {
				break;
			}
			
			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			Log.i(this.getClass().getSimpleName(), "createRfcommSocketToServiceRecord to " + mmDevice.getName());
			setstate(STATE_CONNECTING);
			try {
				mmSocket = mmDevice.createRfcommSocketToServiceRecord(SPP_UUID);
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), "mmSocket gotten from device Exception.");
				setstate(STATE_NONE);
				waittime = getWaittime(MESSAGE_CONNECTION_FAIL);
				handlerSendMsg(MESSAGE_CONNECTION_FAIL, 0, 0, null);
				continue;
			}

			Log.i(this.getClass().getSimpleName(), "Begin connect to " + mmDevice.getName());
			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();
			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), "mmSocket connect Exception.");
				setstate(STATE_NONE);
				waittime = getWaittime(MESSAGE_CONNECTION_FAIL);
				handlerSendMsg(MESSAGE_CONNECTION_FAIL, 0, 0, null);
				continue;
			}

			Log.i(this.getClass().getSimpleName(), "Connected to " + mmDevice.getName());
			// Get the BluetoothSocket input and output streams
			try {
				mmInStream = mmSocket.getInputStream();
				mmOutStream = mmSocket.getOutputStream();
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), "mmSocket get stream Exception.");
				setstate(STATE_NONE);
				waittime = getWaittime(MESSAGE_CONNECTION_LOST);
				handlerSendMsg(MESSAGE_CONNECTION_LOST, 0, 0, null);
				continue;
			}
			
			setstate(STATE_CONNECTED);

			// Keep listening to the InputStream while connected
			while (stop == false) {
				try {
					// Read from the InputStream
					int bytes = mmInStream.read(buffer);

					byte[] tbuf = new byte[bytes];
					System.arraycopy(buffer, 0, tbuf, 0, bytes);

					// callback
					synchronized (this) {
						if (onReadDataListner != null) {
							onReadDataListner.onReading(tbuf, bytes);
						}
					}

					// Send the obtained bytes to the UI Activity
					handlerSendMsg(MESSAGE_READ, bytes, -1, tbuf);
				} catch (Exception e) {
					Log.e(this.getClass().getSimpleName(), "mmSocket read stream Exception.");
					setstate(STATE_NONE);
					waittime = getWaittime(MESSAGE_CONNECTION_LOST);
					handlerSendMsg(MESSAGE_CONNECTION_LOST, 0, 0, null);
					break;
				}
			}
		}
		
		close();
		setstate(STATE_NONE);
		Log.i(this.getClass().getSimpleName(), "Bluetooth Thread stoped.");
	}

}
