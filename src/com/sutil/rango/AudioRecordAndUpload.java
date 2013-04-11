package com.sutil.rango;

import java.io.IOException;
import java.net.Socket;

import android.util.Log;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;


public class AudioRecordAndUpload {
	
	private static final String TAG = "AudioRecordAndUpload";
	
	private AudioRecord audioRecord;
	private int bufferSize;
	private boolean isRecording;
	private Thread recordingThread;
	private Socket socket;		// might be a better idea to make it a class object
	
	public AudioRecordAndUpload(Socket socket) {
		this.socket = socket;
		this.audioRecord = null;
		this.bufferSize = 0;
		this.isRecording = false;
		this.recordingThread = null;
	}
	
	/*
	 * Create an instance of the AudioRecord class and starts the recording
	 * */
	public void startRecording() {
		
		recordingThread = new Thread(new Runnable(){

			public void run() {
				android.os.Process.setThreadPriority
		        (android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				
				// Set up the audio recording
				audioRecord = setUpAudioRecord();
				if(audioRecord == null) {
					Log.d(TAG + "/thread/run", "AudioRecord object not initialized");
				}
			    audioRecord.startRecording();
			    
			    byte data[] = new byte[bufferSize];
			    
			    isRecording = true;
			    
		    	while(isRecording) {
			    	audioRecord.read(data, 0, bufferSize);
			    	try {
						socket.getOutputStream().write(data);
					} catch (IOException e) {
						Log.e(TAG, "Socket failed to write", e);
					}
			    }
			}
			
		});
		recordingThread.start();
	}
	
	public void stopRecording() {
	    isRecording = false;
	    if (audioRecord != null) {
		    audioRecord.stop();
		    audioRecord.release();
		    audioRecord = null;
	    }
	    recordingThread = null;
	}
	
	public AudioRecord setUpAudioRecord() {
		int rate = 8000;
		short audioFormat = AudioFormat.ENCODING_PCM_16BIT;
		short channelConfig = AudioFormat.CHANNEL_IN_MONO;
        try {
        	bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);
            Log.d(TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                    + channelConfig + " , buffer: " + bufferSize);

            if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                // check if we can instantiate and have a success
                AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, rate, channelConfig, audioFormat, bufferSize);

                if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                	Log.d(TAG, "INITIALIZED AUDIO RECORD");
                }
                return recorder;
            }
        } catch (Exception e) {
            Log.e(TAG, rate + "Exception, keep trying.", e);
        }
	    return null;
	}
}