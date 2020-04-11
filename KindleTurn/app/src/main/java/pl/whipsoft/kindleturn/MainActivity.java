package pl.whipsoft.kindleturn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		View v = getLayoutInflater().inflate(R.layout.activity_main, null);
		v.setKeepScreenOn(true);
		setContentView(v);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(MainActivity.this);
		String ip = prefs.getString("kindle_ip", "");
		Log.d("KindleTurn", "Kindle IP  = " + ip);
		String username = prefs.getString("username", "");
		Log.d("KindleTurn", "Kindle username  = " + username);
		String password = prefs.getString("password", "");
		Log.d("KindleTurn", "Kindle password  = " + password);
//		JSch jsch = new JSch();
//		try{
//			session = jsch.getSession(username, ip, 22);
//			java.util.Properties config = new java.util.Properties();
//			config.put("StrictHostKeyChecking", "no");
//			session.setConfig(config);
//
//			// If two machines have SSH passwordless logins setup, the following
//			// line is not needed:
//			session.setPassword(password);
//			session.connect();
//		} catch (Exception e) {
//			System.out.println(e);
//		}
		serviceInt = new Intent(this, KindleTurnService.class);
		serviceInt.putExtra("ip", ip);
		serviceInt.putExtra("username", username);
        serviceInt.putExtra("password", password);
        startService(serviceInt);

		if (ip.length() > 0 && username.length() > 0 && password.length() > 0) {
			SendCommandTask.setPrefs(ip, username, password);
		} else{
			// Launch settings activity
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
		}

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();
		Log.d("SWITCH", "keycode= " + keyCode);

		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (action == KeyEvent.ACTION_UP) {
                Log.i("KindleTurn", "KEYCODE_VOLUME_UP");
                AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
//				Log.i("KindleTurn", "NEXT");
//				try {
//
//					Channel channel = session.openChannel("exec");
//					((ChannelExec) channel).setCommand(COMMAND.NEXT_PAGE.getLinux_command());
//
//					// channel.setInputStream(System.in);
//					//channel.setInputStream(null);
//
//					((ChannelExec) channel).setErrStream(System.err);
//
//					InputStream in = channel.getInputStream();
//
//					channel.connect();
//					byte[] tmp = new byte[1024];
//					while (true) {
//						while (in.available() > 0) {
//							int i = in.read(tmp, 0, 1024);
//							if (i < 0)
//								break;
//							System.out.print(new String(tmp, 0, i));
//						}
//						if (channel.isClosed()) {
//							System.out.println("exit-status: "
//									+ channel.getExitStatus());
//							break;
//						}
//						try {
//							Thread.sleep(500);
//						} catch (Exception ee) {
//							ee.printStackTrace();
//						}
//					}
//					channel.disconnect();
//				} catch (Exception e) {
//					System.out.println(e);
//				}
			}
			return true;
		case KeyEvent.KEYCODE_ENTER:
			if (action == KeyEvent.ACTION_UP) {
				Log.i("KindleTurn", "KEYCODE_ENTER");
                AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
            }
			return true;
		default:
			return super.dispatchKeyEvent(event);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			// Launch settings activity
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			break;
		}

		return true;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		stopService(serviceInt);
	}

	private Session session;
	private Intent serviceInt;

}
