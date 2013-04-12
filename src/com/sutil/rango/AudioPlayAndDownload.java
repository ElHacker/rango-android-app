package com.sutil.rango;

import java.io.IOException;
import java.net.Socket;

import org.holoeverywhere.app.Activity;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.AudioManager;
import android.os.Handler;

public class AudioPlayAndDownload {
	private static final String TAG = "AudioPlayAndDownload";
	
	private AudioTrack audioTrack;
	private int bufferSize;
	private boolean isPlaying;
	private Thread playingThread;
	private Socket socket;		// might be a better idea to make it a class object
	private Handler handler;
	private Context context;
	
	public AudioPlayAndDownload(Socket socket, Handler handler, Context context) {
		this.socket = socket;
		this.handler = handler;
		this.context = context;
		this.audioTrack = null;
		this.bufferSize = 0;
		this.isPlaying = false;
		this.playingThread = null;
	}
	
	// Runnable that executes the background processing
	private Runnable doBackgroundThreadProcessing = new Runnable() {
		public void run() {
			backgroundThreadProcessing();
		}
	};
	
	// Runnable that executes the updateGUI method.
	private Runnable doUpdateGUI = new Runnable() {
		public void run() {
			updateGUI();
		}
	};
	
	/*
	 * Create an instance of the AudioRecord class and starts the recording
	 * */
	public void startPlaying() {
		playingThread = new Thread(null, doBackgroundThreadProcessing, "PlayAndDownload");
		playingThread.start();
	}
	
	// Stops playing and releases system resources.
	public void stopPlaying() {
	    isPlaying = false;
	    if (audioTrack != null) {
		    audioTrack.stop();
		    audioTrack.release();
		    audioTrack = null;
	    }
	    playingThread = null;
	}
	
	
	// Method to download and play the data coming from server.
	private void backgroundThreadProcessing() {
		
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
				// Check for end of conversation
				String serverMessage = new String(data);
				if (serverMessage.contains("END\n")) {
					// END conversation
					stopPlaying();
					// Use the Handler to post the doUpdateGUI
					// runnable on the main UI thread.
					handler.post(doUpdateGUI);
				} else {
					audioTrack.write(data, 0, bufferSize);
				}
			} catch (IOException e) {
				Log.e(TAG, "Socket failed to read", e);
			} catch (Exception e) {
				Log.e(TAG, "Exception " + e.getMessage());
				e.printStackTrace();
			}
	    }
	}
	
	// This method must be called on the UI thread.
	private void updateGUI() {
		// [ ... Open a dialog or modify a GUI element ... ]
		ToggleButton pushToTalkButton;
		TextView callEndedText;
		RelativeLayout mainLayout;
		// The activity that instantiated this class
		Activity calleeActivity = (Activity) context;
		pushToTalkButton = (ToggleButton) calleeActivity.findViewById(R.id.pushToTalk);
		pushToTalkButton.setVisibility(View.GONE);
		
		callEndedText = (TextView) calleeActivity.findViewById(R.id.callEndedText);
		callEndedText.setVisibility(View.VISIBLE);
		
		mainLayout = (RelativeLayout) calleeActivity.findViewById(R.id.mainlayout);
		mainLayout.setBackgroundColor(calleeActivity.getResources().getColor(R.color.light_red));
	}
	
}
