package com.sutil.rango;

import java.io.IOException;
import java.net.Socket;

import android.util.Log;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.AudioManager;

public class AudioPlayAndDownload {
	private static final String TAG = "AudioPlayAndDownload";
	
	private AudioTrack audioTrack;
	private int bufferSize;
	private boolean isPlaying;
	private Thread playingThread;
	private Socket socket;		// might be a better idea to make it a class object
	
	public AudioPlayAndDownload(Socket socket) {
		this.socket = socket;
		this.audioTrack = null;
		this.bufferSize = 0;
		this.isPlaying = false;
		this.playingThread = null;
	}
	
	/*
	 * Create an instance of the AudioRecord class and starts the recording
	 * */
	public void startPlaying() {
		
		playingThread = new Thread(new Runnable(){

			public void run() {
				android.os.Process.setThreadPriority
		        (android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				
				bufferSize = 4096;	// Check why it doesn't want the getMinBuffer value
				
				// Set up the audio recording
				audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 
						8000,
    					AudioFormat.CHANNEL_CONFIGURATION_DEFAULT,
    					AudioFormat.ENCODING_PCM_16BIT,
    					bufferSize,
    					AudioTrack.MODE_STREAM);
				if(audioTrack == null) {
					Log.d(TAG + "/thread/run", "AudioTrack object not initialized");
				}
				audioTrack.setPlaybackRate(audioTrack.getSampleRate());
			    audioTrack.play();
			    
			    byte data[] = new byte[bufferSize];
			    
			    isPlaying = true;
			    
		    	while(isPlaying) {
			    	try {
						socket.getInputStream().read(data);
						audioTrack.write(data, 0, bufferSize);
					} catch (IOException e) {
						Log.e(TAG, "Socket failed to write", e);
					}
			    }
			}
			
		});
		playingThread.start();
	}
	
	public void stopPlaying() {
	    isPlaying = false;
	    audioTrack.stop();
	    audioTrack.release();
	    audioTrack = null;
	    playingThread = null;
	}
	
}
