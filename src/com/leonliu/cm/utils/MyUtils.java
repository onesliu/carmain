package com.leonliu.cm.utils;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

public class MyUtils {

	public static final String readNullString(byte []data, int maxlen) throws UnsupportedEncodingException
	{
		return readNullString(data, 0, maxlen);
	}
	
	public static final String readNullString(byte []data, int offset, int maxlen) throws UnsupportedEncodingException
	{
		int remain = data.length - offset;
		maxlen = (maxlen>remain)?remain:maxlen;
		for ( int i = 0; i < maxlen; i++)
		{
			if ( data[i] == 0 )
				return new String(data, 0, i, "UTF-8");
		}
		return new String(data, 0, maxlen, "UTF-8");
	}

	public static final int readNullString(DataInputStream data, byte []buf, int maxlen) throws IOException
	{
		byte c;
		
		int i = 0;
		c = data.readByte();
		while (c != 0 && i < maxlen && i < buf.length)
		{
			buf[i] = c;
			c = data.readByte();
			i++;
		}

		return i;
	}
	
	public static final void writeToFile(byte []data, int length, String fname)
	{
		FileOutputStream outStream;
		try {
			outStream = new FileOutputStream(fname);
			outStream.write(data, 0, length);
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final String hexToStr(byte []data)
	{
		String str = "";
		for(int i = 0; i < data.length; i++)
		{
			str += Integer.toString( ( data[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		
		return str;
	}
	
	 /** * 根据手机的分辨率从 dp 的单位 转成为 px(像素) */
	 
    public static int dip2px(Context context, float dpValue) {
    	final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /** * 根据手机的分辨率从 px(像素) 的单位 转成为 dp */
    public static int px2dip(Context context, float pxValue) {
    	final float scale = context.getResources().getDisplayMetrics().density;
    	return (int) (pxValue / scale + 0.5f);
    }
    
    public static boolean isPhoneNumber(String Phone) {
    	Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[^4,\\D]))\\d{8}$");
    	Matcher m = p.matcher(Phone);
    	return m.matches();
    }
    
    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
    
}
