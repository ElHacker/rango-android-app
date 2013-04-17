package com.sutil.rango;

import java.io.IOException;
import java.net.Socket;
import java.sql.Date;
import java.sql.Time;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.widget.ProfilePictureView;

import org.holoeverywhere.app.Activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class WalkieTalkieActivity extends Activity implements View.OnTouchListener{
	
	public AudioRecordAndUpload audioRecAndUp = null;
	public AudioPlayAndDownload audioPlayAndDown = null;
	
	private ProgressBar callingProgress;
	private TextView callingText;
	private ToggleButton pushToTalkButton;
	
	private final String TAG = "WalkieTalkieActivity";
	private Socket socket = null;
	// Using the handler class, you can post updates to the user interface from a background thread.
	private Handler handler = new Handler();
	private Context context;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.walkietalkie);
        
        context = this;
        
        // Create action bar
	    ActionBar bar = getSupportActionBar();
	    bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
	    bar.setTitle("Rango");
	    bar.setDisplayHomeAsUpEnabled(true);
	    bar.setIcon(R.drawable.rango_logo);
	    
	    // Get Friend data from bundler 
	    Bundle bundle = getIntent().getExtras();
	    String friend_fb_id = bundle.getString("target_id");
	    String friend_name = bundle.getString("target_name");
	    // And set it to UI
	    ProfilePictureView friend_pic= (ProfilePictureView) findViewById(R.id.chat_friend_icon);
	    TextView friend_name_text = (TextView) findViewById(R.id.chat_friend_name);
        
        // Register new call in calls log
        DatabaseHandler db = new DatabaseHandler(this);
        String [] full_name = friend_name.split(" ");
        Call call;
        // If the full name has a first and last names
        if (full_name.length == 2) {
	        call = new Call(friend_fb_id, full_name[0], full_name[1],
	        		new Date(System.currentTimeMillis()), new Time(System.currentTimeMillis()));
        } else {
        	// If the friend only has firstname
        	call = new Call(friend_fb_id, friend_name, "",
	        		new Date(System.currentTimeMillis()), new Time(System.currentTimeMillis()));
        }
        
        // Save call to database
        db.addCall(call);
        
        if (friend_pic != null) {
        	friend_pic.setProfileId(friend_fb_id);
        }
        if (friend_name_text != null) {
            friend_name_text.setText(friend_name);
        }
        
        callingProgress = (ProgressBar) findViewById(R.id.callingProgress);
		callingText = (TextView) findViewById(R.id.callingText);
        pushToTalkButton = (ToggleButton) findViewById(R.id.pushToTalk);
        pushToTalkButton.setOnTouchListener(this);
        
        // "Push to talk" can be a serious pain when the screen keeps turning off.
        // Let's prevent that.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    
    // Creates a valid tcp socket connection with the server
    private void initSocketConnection() {
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
    	// Wait for the server to give us permission to start
		new WaitForStartTask().execute();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	// Release system resources
    	if (audioRecAndUp != null) {
	    	audioRecAndUp.stopRecording();
	    	audioRecAndUp = null;
    	}
    	if (audioPlayAndDown != null) {
    		audioPlayAndDown.stopPlaying();
        	audioPlayAndDown = null;
    	}
    	try {
			socket.close();
			socket = null;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            NavUtils.navigateUpFromSameTask(this);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
    }
    
    // Internal class that executes an async task
    // waits for the start message from server
    private class WaitForStartTask extends AsyncTask<Void, Void, Boolean> {
    	
    	@Override
    	protected void onPreExecute() {
    		// Start a singleton socket
	        try {
				socket = new Socket("rangoapp.com", 8090);
				// Establish a valid connection with the server
				initSocketConnection();
			} catch (IOException e) {
		    	Log.d(TAG + "/thread/run", "Socket exception" , e);
			}
    	}
    	
		@Override
		protected Boolean doInBackground(Void... voids) {
			byte data[] = new byte[4096];
			try {
				// Read data from socket
				socket.getInputStream().read(data);
				String serverMessage = new String(data);
				if(serverMessage.contains("START\n")) {
					// START!
					return true;
				}
			} catch (Exception e) {
				Log.e(TAG, "" + e.getMessage());
				e.printStackTrace();
			}
			return false;
		}
		
		protected void onPostExecute(Boolean started) {
			if (started) {
				Log.d(TAG, "STARTED!");
				callingProgress.setVisibility(View.GONE);
				callingText.setVisibility(View.GONE);
				pushToTalkButton.setVisibility(View.VISIBLE);
				audioRecAndUp = new AudioRecordAndUpload(socket);
				if(audioPlayAndDown == null) {
					audioPlayAndDown = new AudioPlayAndDownload(socket, handler, context);
					audioPlayAndDown.startPlaying();
				}
			}
		}
    }
}