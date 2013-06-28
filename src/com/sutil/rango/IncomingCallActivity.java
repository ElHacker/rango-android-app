package com.sutil.rango;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.holoeverywhere.app.Activity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import com.actionbarsherlock.app.ActionBar;
import com.facebook.widget.ProfilePictureView;
import com.sutil.rango.libs.RestClient;

/**
 * @author syred
 * 
 * Wakes the device when there's an incoming call
 * and plays a ring tone.
 * 
 */
public class IncomingCallActivity extends Activity {
	private final String TAG = "IncominCallActivity";
	
    private MediaPlayer mMediaPlayer;
    private PowerManager.WakeLock wl;
    private Context context;
    
    private Bundle walkieTalkieBundle;
    private JSONObject friend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Rango");
            wl.acquire();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_incoming_call);
         
        context = getApplicationContext();
        
        // Create action bar
	    ActionBar bar = getSupportActionBar();
	    bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
	    bar.setTitle("Rango");
	    bar.setIcon(R.drawable.rango_logo);
	    
	    // Get the bundle information
	    Bundle extras = getIntent().getExtras();
	    // Get the friend information from server
	    String friendFbId = extras.getString("from_fb_id");
	    friend = RestClient.get_user(friendFbId);
	    
	    // Load the friend information into the layout
	    ProfilePictureView contactPicture = (ProfilePictureView) findViewById(R.id.contact_profile_pic);
	    TextView contactName = (TextView) findViewById(R.id.contact_name);
	    
	    try {
	    	contactPicture.setProfileId(friendFbId);
			contactName.setText(
					friend.getString("first_name") + " " + friend.getString("last_name"));
		} catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
			ex.printStackTrace();
		}
	    
	    // Configure button to stop the alarm sound
        Button rejectCall = (Button) findViewById(R.id.reject);
        rejectCall.setOnTouchListener(new View.OnTouchListener() {
        	@Override
            public boolean onTouch(View v, MotionEvent event) {
        		try {
	                if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
		        		mMediaPlayer.stop();
		                mMediaPlayer.release();
	                }
        		} catch (Exception e) {
        			Log.e(TAG, e.getMessage());
        		}
        		finish();
        		return false;
            }
        });
        
        // Configure answer button to go to other walkie talkie activity
        Button answerCall = (Button) findViewById(R.id.answer);
        answerCall.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				try {
	                if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
		        		mMediaPlayer.stop();
		                mMediaPlayer.release();
	                }
				    // Get current user's fb id
				    SharedPreferences prefs = getSharedPreferences("MyUserInfo", 0);
				    String currentUserFbId = prefs.getString("my_fb_id", "");
			    	// Create a new Bundle for the walkie talkie activity
				    walkieTalkieBundle = new Bundle();
				    walkieTalkieBundle.putString("my_id", currentUserFbId);
					walkieTalkieBundle.putString("target_id", friend.getString("fb_id"));
					walkieTalkieBundle.putString("target_name", 
							friend.getString("first_name") + " " + friend.getString("last_name"));
					// Start the walkie talkie activity
					Intent walkieTalkieIntent = new Intent(context, WalkieTalkieActivity.class);
					walkieTalkieIntent.putExtras(walkieTalkieBundle);
					startActivity(walkieTalkieIntent);
				} catch (JSONException ex) {
					Log.e(TAG, ex.getMessage());
					ex.printStackTrace();
				} catch (Exception ex) {
					Log.e(TAG, ex.getMessage());
					ex.printStackTrace();
				}
				
				return false;
			}
		});

        playSound(this, getRingtoneUri());
    }

    @Override
    protected void onStop() {
        super.onStop();
        wl.release();
    }

    private void playSound(Context context, Uri ringtone) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, ringtone);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    //Get an alarm sound. Try for ringtone. If none set, try alarm,
    //Otherwise, notification.
    private Uri getRingtoneUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        return alert;
    }
}