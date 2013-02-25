package com.sutil.rango;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ToggleButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class WalkieTalkieActivity extends Activity implements View.OnTouchListener{
	
	public AudioRecordAndUpload audioRecAndUp = null;
	public AudioPlayAndDownload audioPlayAndDown = null;
	
	private final String TAG = "RangoNativeActivity";
	private Socket socket = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walkietalkie);
        
        ToggleButton pushToTalkButton = (ToggleButton) findViewById(R.id.pushToTalk);
        pushToTalkButton.setOnTouchListener(this);
        
        // Start a singleton socket
        try {
			socket = new Socket("rangoapp.com", 8090);
			audioRecAndUp = new AudioRecordAndUpload(socket);
			if(audioPlayAndDown == null) {
				audioPlayAndDown = new AudioPlayAndDownload(socket);
				audioPlayAndDown.startPlaying();
			}
		} catch (IOException e) {
	    	Log.d(TAG + "/thread/run", "Socket exception" , e);
		}
        
        // "Push to talk" can be a serious pain when the screen keeps turning off.
        // Let's prevent that.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    
    public void initSocketConnection() {
    	Bundle bundle = getIntent().getExtras();
    	String my_id = bundle.getString("my_id");
    	String target_id = bundle.getString("target_id");
    }
    
    @Override
    public void onStart() {
        super.onStart();
        // When we get back from the preference setting Activity, assume
        // settings have changed, and re-login with new auth info.
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Updates whether or not the user's voice is muted, depending on whether the button is pressed.
     * @param v The View where the touch event is being fired.
     * @param event The motion to act on.
     * @return boolean Returns false to indicate that the parent view should handle the touch event
     * as it normally would.
     */
    @Override
	public boolean onTouch(View v, MotionEvent event) {
    	if (event.getAction() == MotionEvent.ACTION_DOWN) {
    		audioRecAndUp.startRecording();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            audioRecAndUp.stopRecording();
        }
        return false;
    }    
}