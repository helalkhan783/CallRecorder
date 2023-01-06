package com.example.callrecord;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class YourService extends Service implements MediaRecorder.OnInfoListener, MediaRecorder.OnErrorListener {
    private MediaRecorder rec;
    private static String file = null;
    boolean recordsStarted = false;

    AudioManager audioManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        TelephonyManager manager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        manager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);

                if (TelephonyManager.CALL_STATE_IDLE == state && recordsStarted == true) {
                    // rec.stop();
                    //  rec.reset();
                    // rec.release();
                    recordsStarted = false;
                    // stopSelf();
                } else if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                    ok();
                    recordsStarted = true;
                } else if (TelephonyManager.CALL_STATE_RINGING == state) {
                    MainActivity.showToaast("Call incoming...");
                }

            }
        }, PhoneStateListener.LISTEN_CALL_STATE);


        return START_STICKY;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, YourService.class);
        Log.i("serviceDestroy", "==============yes");
        this.sendBroadcast(broadcastIntent);
    }


    void ok() {

        try {

            rec = new MediaRecorder();
            file = Environment.getExternalStorageDirectory().getAbsolutePath();
            //    file = String.valueOf(new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))));

            file += "/AudioRecording.3gp";

            // file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
            Date date = new Date();
            CharSequence sdf = DateFormat.format("MM--dd--yy-hh-mm-ss", date.getTime());
            // rec.reset();
            rec.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
            rec.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            rec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            if (file != null) {
                rec.setOutputFile(file);
            }

            rec.prepare();
            rec.start();

            audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE); //record the audio of device
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(true); //enable the speaker for record the voices of the call

            MainActivity.showToaast("Call Started...");

        } catch (Exception e) {
            e.getMessage();
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public void onError(MediaRecorder mr, int what, int extra) {

    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {

    }


}
