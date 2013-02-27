package com.sutil.rango;

import java.io.IOException;
import java.net.Socket;
import com.actionbarsherlock.app.ActionBar;
import com.facebook.widget.ProfilePictureView;

import org.holoeverywhere.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.walkietalkie);
        
        // Create action bar
	    ActionBar bar = getSupportActionBar();
	    bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
	    bar.setTitle("Rango");
	    
	    // Get Friend data from bundler 
	    Bundle bundle = getIntent().getExtras();
	    String friend_fb_id = bundle.getString("target_id");
	    String friend_name = bundle.getString("target_name");
	    String friend_desc = bundle.getString("target_desc");
	    // And set it to UI
	    ProfilePictureView friend_pic= (ProfilePictureView) findViewById(R.id.chat_friend_icon);
	    TextView friend_name_text = (TextView) findViewById(R.id.chat_friend_name);
        TextView friend_desc_text = (TextView) findViewById(R.id.chat_friend_desc);
        
        if (friend_pic != null) {
        	friend_pic.setProfileId(friend_fb_id);
        }
        if (friend_name_text != null) {
            friend_name_text.setText(friend_name);
        }
        if (friend_desc_text != null) {
            friend_desc_text.setText(friend_desc);
        }
	    
        
        ToggleButton pushToTalkButton = (ToggleButton) findViewById(R.id.pushToTalk);
        pushToTalkButton.setOnTouchListener(this);
        
        // Start a singleton socket
        try {
			socket = new Socket("rangoapp.com", 8090);
			// Establish a valid connection with the server
			initSocketConnection();
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
    
    // Creates a valid tcp socket connection with the server
    public void initSocketConnection() {
    	Bundle bundle = getIntent().getExtras();
    	String my_id = bundle.getString("my_id");
    	String target_id = bundle.getString("target_id");
    	// The id_msg will be send to the server and must
    	// have the form: "my_id,target_id\n"
    	String id_msg = String.format("%s,%s\n", my_id, target_id);
    	try {
    		// Send it to server
			socket.getOutputStream().write(id_msg.getBytes());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
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