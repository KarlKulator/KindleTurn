package pl.whipsoft.kindleturn;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;

public class KindleTurnService extends Service {
    private MediaSessionCompat mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();

        JSch jsch = new JSch();
        try{
            session = jsch.getSession("root", "192.168.0.48", 22);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            // If two machines have SSH passwordless logins setup, the following
            // line is not needed:
            session.setPassword("qwerasdf1");
            session.connect();
        } catch (Exception e) {
            System.out.println(e);
        }

        mediaSession = new MediaSessionCompat(this, "KindleTurnService");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0) //you simulate a player which plays something.
                .build());

        //this will only work on Lollipop and up, see https://code.google.com/p/android/issues/detail?id=224134
        VolumeProviderCompat myVolumeProvider =
                new VolumeProviderCompat(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, /*max volume*/100, /*initial volume level*/50) {
                    @Override
                    public void onAdjustVolume(int direction) {
                        if(direction == 1){
                            Log.i("KindleTurn", "NEXT");
                            try {

                                Channel channel = session.openChannel("exec");
                                ((ChannelExec) channel).setCommand(COMMAND.NEXT_PAGE.getLinux_command());

                                // channel.setInputStream(System.in);
                                //channel.setInputStream(null);

                                ((ChannelExec) channel).setErrStream(System.err);

                                InputStream in = channel.getInputStream();

                                channel.connect();
                                byte[] tmp = new byte[1024];
                                while (true) {
                                    while (in.available() > 0) {
                                        int i = in.read(tmp, 0, 1024);
                                        if (i < 0)
                                            break;
                                        System.out.print(new String(tmp, 0, i));
                                    }
                                    if (channel.isClosed()) {
                                        System.out.println("exit-status: "
                                                + channel.getExitStatus());
                                        break;
                                    }
                                    try {
                                        Thread.sleep(500);
                                    } catch (Exception ee) {
                                        ee.printStackTrace();
                                    }
                                }
                                channel.disconnect();
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        } else if (direction == -1){
                            Log.i("KindleTurn", "PREVIOUS");
                            try {

                                Channel channel = session.openChannel("exec");
                                ((ChannelExec) channel).setCommand(COMMAND.PREVIOUS_PAGE.getLinux_command());

                                // channel.setInputStream(System.in);
                                //channel.setInputStream(null);

                                ((ChannelExec) channel).setErrStream(System.err);

                                InputStream in = channel.getInputStream();

                                channel.connect();
                                byte[] tmp = new byte[1024];
                                while (true) {
                                    while (in.available() > 0) {
                                        int i = in.read(tmp, 0, 1024);
                                        if (i < 0)
                                            break;
                                        System.out.print(new String(tmp, 0, i));
                                    }
                                    if (channel.isClosed()) {
                                        System.out.println("exit-status: "
                                                + channel.getExitStatus());
                                        break;
                                    }
                                    try {
                                        Thread.sleep(500);
                                    } catch (Exception ee) {
                                        ee.printStackTrace();
                                    }
                                }
                                channel.disconnect();
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }
                /*
                -1 -- volume down
                1 -- volume up
                0 -- volume button released
                 */
                    }
                };

        mediaSession.setPlaybackToRemote(myVolumeProvider);
        mediaSession.setActive(true);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSession.release();
    }

    private Session session;
}