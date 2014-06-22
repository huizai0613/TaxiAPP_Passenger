package com.wutong.taxiapp.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Base64;

public class AudioRecorder {
	private static final int MAX_VU_SIZE = 14;

	private static int SAMPLE_RATE_IN_HZ = 16000; // ������

	private MediaRecorder mMediaRecorder;
	private String mPath;

	public byte[] getWAVDate() {
		File fi = new File(mPath);
		if (!fi.exists()){
			return null;
		}
		byte[] soure=null;
		try {
			InputStream is = new FileInputStream(fi);
			int available = is.available();
			
			 soure = new byte[available];
			
			 is.read(soure);

			is.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return soure;
	}

	public void getWAV() {
		try {
			// byte[] data = Base64.decode(getWAV(), Base64.DEFAULT);
			/*
			 * for(int i=0 ;i<data.length;i++){ if (data[i] < 0) { data[i] +=
			 * 256; } }
			 */
			// OutputStream out = new FileOutputStream(Environment
			// .getExternalStorageDirectory().getAbsolutePath()
			// + "/WifiChat/voiceRecord/123.amr");
			// out.write(data);
			// out.flush();
			// out.close();
		} catch (Exception ex) {

		}
	}

	public AudioRecorder(String path) {
		if (mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorder();
		}
		this.mPath = sanitizePath(path);
	}

	private String sanitizePath(String path) {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/WifiChat/voiceRecord/" + path + ".amr";
	}

	public void start() throws IOException {
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			throw new IOException("SD Card is not mounted,It is  " + state
					+ ".");
		}
		File directory = new File(mPath).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Path to file could not be created");
		}
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		// mMediaRecorder.setAudioChannels(AudioFormat.CHANNEL_CONFIGURATION_MONO);
		mMediaRecorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
		mMediaRecorder.setOutputFile(mPath);
		mMediaRecorder.prepare();
		mMediaRecorder.start();
	}

	public void stop() throws IOException {
		mMediaRecorder.stop();
		mMediaRecorder.release();
	}

	public double getAmplitude() {
		if (mMediaRecorder != null) {
			int maxAmplitude = MAX_VU_SIZE * mMediaRecorder.getMaxAmplitude()
					/ 32768;
			return (maxAmplitude);
		} else
			return 0;
	}
}