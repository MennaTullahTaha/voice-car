package com.android.team.ble;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import com.microsoft.cognitiveservices.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.cognitiveservices.speechrecognition.MicrophoneRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionResult;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionStatus;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionMode;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionServiceFactory;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ISpeechRecognitionServerEvents {


    private MicrophoneRecognitionClient microphoneRecognitionClient;


    Switch voiceSwitch;
    boolean listenFlag;
    ConstraintLayout btns_layout;
    ImageButton right;
    ImageButton left;
    ImageButton forward;
    ImageButton backward;

    ArrayList<Command> newCommands;
    public final static String ACTION_DATA_AVAILABLE =//receive on this action
            "com.arduino.team.ble.ACTION_DATA_AVAILABLE";
    public final static String ACTION_DATA_SEND =
            "com.arduino.team.ble.ACTION_DATA_SEND"; //send on this action
    public final static String EXTRA_DATA =
            "com.arduino.team.ble.EXTRA_DATA";
    public final static int REQUEST_ENABLE_RECORD = 1;

    String lastDirection="";
    String lastDistance="";
    boolean buttonDown=false;
    int buttonNumber=-1;//0 up  1 right 2 down 3 left
    View.OnClickListener snakeBarBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            newCommands.add(new Command(lastDirection,lastDistance));

        }
    };

    final private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_DATA_AVAILABLE.equals(action)) {
                //ex: receive distance of last command

                lastDistance=  intent.getStringExtra(EXTRA_DATA);
                Snackbar snackbar =Snackbar
                        .make(btns_layout, "Add "+lastDirection+" :"+lastDistance+" CM to firebase ?", Snackbar.LENGTH_LONG)
                        .setAction("Send", snakeBarBtn);
                snackbar.show();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(broadcastReceiver, new IntentFilter(ACTION_DATA_AVAILABLE));
        newCommands=new ArrayList<>();
        right = (ImageButton) findViewById(R.id.Btn_right);
        left = (ImageButton) findViewById(R.id.Btn_left);
        forward = (ImageButton) findViewById(R.id.Btn_forward);
        backward = (ImageButton) findViewById(R.id.Btn_backward);

        forward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (!buttonDown&&event.getAction() == MotionEvent.ACTION_DOWN) {
                    buttonDown=true;
                    buttonNumber=0;
                    ((ImageButton) v).setColorFilter(new PorterDuffColorFilter(0x50509000, PorterDuff.Mode.SRC_IN));
                    Intent data = new Intent(ACTION_DATA_SEND);
                    data.putExtra(ACTION_DATA_SEND, "go");
                    sendBroadcast(data);
                    lastDirection="go";

                } else if (buttonNumber==0&&event.getAction() == MotionEvent.ACTION_UP) {
                    ((ImageButton) v).clearColorFilter();

                    Intent data = new Intent(ACTION_DATA_SEND);
                    data.putExtra(ACTION_DATA_SEND, "stop");
                    sendBroadcast(data);

                    buttonDown=false;
                    buttonNumber=-1;

                }
                return true;
            }
        });
        backward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (!buttonDown&&event.getAction() == MotionEvent.ACTION_DOWN) {
                    buttonDown=true;
                    buttonNumber=2;
                    ((ImageButton) v).setColorFilter(new PorterDuffColorFilter(0x50509000, PorterDuff.Mode.SRC_IN));

                    Intent data = new Intent(ACTION_DATA_SEND);
                    data.putExtra(ACTION_DATA_SEND, "back");
                    sendBroadcast(data);
                    lastDirection="back";
                } else if (buttonNumber==2&&event.getAction() == MotionEvent.ACTION_UP) {
                    ((ImageButton) v).clearColorFilter();
                    Intent data = new Intent(ACTION_DATA_SEND);
                    data.putExtra(ACTION_DATA_SEND, "stop");
                    sendBroadcast(data);
                    buttonDown=false;
                    buttonNumber=-1;
                }
                return true;
            }
        });
        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (!buttonDown&&event.getAction() == MotionEvent.ACTION_DOWN) {
                    buttonDown=true;
                    buttonNumber=1;
                    ((ImageButton) v).setColorFilter(new PorterDuffColorFilter(0x50509000, PorterDuff.Mode.SRC_IN));

                    Intent data = new Intent(ACTION_DATA_SEND);
                    data.putExtra(ACTION_DATA_SEND, "rightSticky");
                    sendBroadcast(data);
                    lastDirection="right";
                } else if (buttonNumber==1&&event.getAction() == MotionEvent.ACTION_UP) {
                    ((ImageButton) v).clearColorFilter();
                    Intent data = new Intent(ACTION_DATA_SEND);
                    data.putExtra(ACTION_DATA_SEND, "stop");
                    sendBroadcast(data);
                    buttonDown=false;
                    buttonNumber=-1;
                }
                return true;
            }
        });
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (!buttonDown&&event.getAction() == MotionEvent.ACTION_DOWN) {
                    buttonDown=true;
                    buttonNumber=3;
                    ((ImageButton) v).setColorFilter(new PorterDuffColorFilter(0x50509000, PorterDuff.Mode.SRC_IN));

                    Intent data = new Intent(ACTION_DATA_SEND);
                    data.putExtra(ACTION_DATA_SEND, "leftSticky");
                    sendBroadcast(data);
                    lastDirection="left";
                } else if (buttonNumber==3&&event.getAction() == MotionEvent.ACTION_UP) {
                    ((ImageButton) v).clearColorFilter();
                    Intent data = new Intent(ACTION_DATA_SEND);
                    data.putExtra(ACTION_DATA_SEND, "stop");
                    sendBroadcast(data);
                    buttonDown=false;
                    buttonNumber=-1;
                }
                return true;
            }
        });
        initializeRecClient();
        voiceSwitch = (Switch) findViewById(R.id.voice_switch);
        btns_layout = (ConstraintLayout) findViewById(R.id.btns_layout);
        voiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listenFlag = isChecked;
                if (isChecked) {
                    if (isNetworkAvailable()) {
                        btns_layout.setVisibility(View.INVISIBLE);
                        microphoneRecognitionClient.startMicAndRecognition();
                    } else {
                        Toast.makeText(MainActivity.this, "Enable Network Connection.", Toast.LENGTH_LONG).show();
                        listenFlag = false;
                        voiceSwitch.setChecked(false);

                    }
                } else {

                    btns_layout.setVisibility(View.VISIBLE);
                    microphoneRecognitionClient.endMicAndRecognition();
                }
            }
        });


    }

    void initializeRecClient() {
        String language = "en-us";
        String subscriptionKey = getString(R.string.subscriptionKey);
        microphoneRecognitionClient = SpeechRecognitionServiceFactory.createMicrophoneClient(this, SpeechRecognitionMode.LongDictation, language, this, subscriptionKey);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        newCommands.clear();
        if(requestCode==1&&resultCode== Activity.RESULT_OK)
        {
            Command c= (Command) data.getExtras().get("command");
            Intent data2 = new Intent(ACTION_DATA_SEND);
            data2.putExtra(ACTION_DATA_SEND, c.getCommandDirection()+" "+c.getCommandDistance());
            sendBroadcast(data2);
            Toast.makeText(this,c.getCommandDirection()+" "+c.getCommandDistance()+" CM",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.commandsItem) {
            Intent intent=new Intent(this,CommandsActivity.class);
            intent.putExtra("newCommands",newCommands);

            startActivityForResult(intent,1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    //Returns Partial Result
    public void onPartialResponseReceived(String s) {
        Intent data=new Intent(ACTION_DATA_SEND);
        switch (s) {
            case "go":
                Log.d("Debug", "go called");
                Toast.makeText(this, "go called", Toast.LENGTH_LONG).show();

                data.putExtra(ACTION_DATA_SEND,"go");
                sendBroadcast(data);
                lastDirection="go";
                break;
            case "stop":
                Log.d("Debug", "stop called");
                Toast.makeText(this, "stop called", Toast.LENGTH_LONG).show();

                data.putExtra(ACTION_DATA_SEND,"stop");
                sendBroadcast(data);
                break;
            case "right":
                Log.d("Debug", "right called");
                Toast.makeText(this, "right called", Toast.LENGTH_LONG).show();

                data.putExtra(ACTION_DATA_SEND,"right");
                sendBroadcast(data);
                lastDirection="right";
                break;
            case "left":
                Log.d("Debug", "left called");
                Toast.makeText(this, "left called", Toast.LENGTH_LONG).show();

                data.putExtra(ACTION_DATA_SEND,"left");
                sendBroadcast(data);
                lastDirection="left";
                break;
            case "back":
                Log.d("Debug", "back called");
                Toast.makeText(this, "back called", Toast.LENGTH_LONG).show();

                data.putExtra(ACTION_DATA_SEND,"back");
                sendBroadcast(data);
                lastDirection="back";
                break;
        }

    }

    @Override
    public void onFinalResponseReceived(RecognitionResult recognitionResult) {
        Log.d("Debug", recognitionResult.RecognitionStatus.name());

        if (listenFlag && (recognitionResult.RecognitionStatus == RecognitionStatus.InitialSilenceTimeout||recognitionResult.RecognitionStatus == RecognitionStatus.DictationEndSilenceTimeout||recognitionResult.RecognitionStatus == RecognitionStatus.EndOfDictation  || recognitionResult.RecognitionStatus == RecognitionStatus.None))
            {
                      microphoneRecognitionClient.endMicAndRecognition();
                      microphoneRecognitionClient.startMicAndRecognition();
                return;
            }
        // Log.d("Debug","final : "+recognitionResult.Results[0].DisplayText);

        if (recognitionResult.Results.length != 0) {
            if (recognitionResult.Results[0].DisplayText.equals("Run for one meter.")) {
                Log.d("Debug", recognitionResult.Results[0].DisplayText);
                Toast.makeText(this, "Run for one meter.", Toast.LENGTH_LONG).show();
                Intent data=new Intent(ACTION_DATA_SEND);
                data.putExtra(ACTION_DATA_SEND,"Run for one meter.");
                sendBroadcast(data);
                lastDirection="go";
            }
        }
    }

    @Override
    public void onIntentReceived(String s) {

    }

    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void onAudioEvent(boolean b) {

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isNetworkAvailable()) {
            voiceSwitch.setChecked(false);
            btns_layout.setVisibility(View.VISIBLE);
            microphoneRecognitionClient.endMicAndRecognition();
        }
        try {
            String mPermission = Manifest.permission.RECORD_AUDIO;
            if (ContextCompat.checkSelfPermission(this, mPermission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{mPermission}, REQUEST_ENABLE_RECORD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ENABLE_RECORD && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
