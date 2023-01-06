package com.example.callrecord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private MediaRecorder rec;
    private boolean recordsStarted;
    private File file;
    String path = "sdcard/alarms/";

    Button btn;
    ToggleButton startandoff;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private Intent mServiceIntent;
    private YourService mYourService;
  public   static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context =this;
        btn = findViewById(R.id.btn);
        startandoff = (ToggleButton) findViewById(R.id.toggleBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Player.class));
            }
        });
        startandoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    requestAudioPermissions();
                    if (mYourService == null) {
                        mYourService = new YourService();
                    }


                    mServiceIntent = new Intent(MainActivity.this, mYourService.getClass());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!isMyServiceRunning(mYourService.getClass())) {
                            startForegroundService(mServiceIntent);
                        }

                    } else {
                        if (!isMyServiceRunning(mYourService.getClass())) {
                            startService(mServiceIntent);
                        }
                    }



                } else {
               /*     Intent intent = new Intent(MainActivity.this, RecordingService.class);
                    startService(intent);
                    Toast.makeText(MainActivity.this, "Call recording STOPPED", Toast.LENGTH_SHORT).show();
              */  }
            }
        });

    }

    private void requestAudioPermissions() {
        //When permission is not granted by user, show them message why this permission is needed.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
            //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
          /*  Intent intent = new Intent(MainActivity.this, RecordingService.class);
            startService(intent);
            Toast.makeText(MainActivity.this, "Call Recording STARTED", Toast.LENGTH_SHORT).show();
*/
            //Go ahead with recording audio now

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }



/*

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    recordAudio();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
*/


    public  static void showToaast(String mes){
        Toast.makeText( context, ""+mes, Toast.LENGTH_SHORT).show();
    }

}