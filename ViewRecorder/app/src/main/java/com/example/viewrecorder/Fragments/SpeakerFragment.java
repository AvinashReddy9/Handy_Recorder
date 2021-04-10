package com.example.viewrecorder.Fragments;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.viewrecorder.Dialog.HelpDialog;
import com.example.viewrecorder.MainActivity;
import com.example.viewrecorder.R;
import com.example.viewrecorder.Util.Bluetooth_Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.BLUETOOTH_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class SpeakerFragment extends Fragment {

    private TextView textViewStatus;
    private EditText editTextGainFactor;
    private FloatingActionButton mFloatingActionButton;

    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    private NoiseSuppressor mNoiseSupressor;
    private AcousticEchoCanceler mAcousticEchoCanceler;
    private Bluetooth_Util mBluetoothUtil;
    private BluetoothAdapter bAdapter;
    private AudioManager mAudioManager;
    private Handler mHandler = new Handler();
    private BluetoothManager mBluetoothManager;

    private int intBufferSize;
    private short[] shortAudioData;
    private Button mStartButton;
    private Button mStopButton;

    private boolean isActive = false;

    private Thread thread;
    private final static String TAG = "MIC_TO_SPEAKER";
    private BluetoothProfile.ServiceListener mServiceListener;
    private BluetoothHeadset mBluetoothHeadSet;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View root = inflater.inflate(R.layout.speaker_fragment,container,false);
        mBluetoothUtil = new Bluetooth_Util(getActivity());
        bAdapter = BluetoothAdapter.getDefaultAdapter();
        mAudioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
        mStartButton = root.findViewById(R.id.startbutton);
        mStopButton = root.findViewById(R.id.stopbutton);
        if (mAudioManager.isBluetoothA2dpOn()) {
            // Adjust output for Bluetooth.
            Log.e(TAG," BLUETOOTH DEVICES a2dp IS CONNECTED");

        } else if (mAudioManager.isBluetoothScoOn()) {
            // Adjust output for Bluetooth of sco.
            Log.e(TAG," BLUETOOTH DEVICE IS CONNECTED");

        } else if (mAudioManager.isWiredHeadsetOn()) {
            // Adjust output for headsets
            Log.e(TAG," HEADSET IS CONNECTED");

        } else if (mAudioManager.isSpeakerphoneOn()) {
            Log.e(TAG,"SPEAKER PHONE IS CONNECTED");
        } else {
            Log.e(TAG,"Nothing is connected");
        }
        mBluetoothManager = (BluetoothManager) getActivity().getSystemService(BLUETOOTH_SERVICE);
        // Adjust output for Speakerphone.
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO},
                PackageManager.PERMISSION_GRANTED);

        textViewStatus = root.findViewById(R.id.textViewStatus);
        editTextGainFactor = root.findViewById(R.id.editTextGainFactor);
        mFloatingActionButton = root.findViewById(R.id.floatingHelp);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpDialog helpDialog = new HelpDialog(root.getContext());
                helpDialog.show();
            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStart(v);
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStop(v);
            }
        });

        if (mBluetoothUtil.isBluetoothAdapterAvailable(bAdapter)) {
            if (mBluetoothUtil.isBluetoothEnabled(bAdapter)) {
                Log.e(TAG, "BLUETOOTH IS ENABLED");
            } else {
                Log.e(TAG, "BLUETOOTH IS NOT ENABLED BUT WE ARE ENABLING NOW");
                mBluetoothUtil.enableBluetooth(bAdapter);
            }
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                threadLoop();
            }
        });
        return  root;
    }


    @Override
    public void onResume() {
        super.onResume();
        Timer task = new Timer();
        task.schedule(new TimerTask() {
            @Override
            public void run() {
                mBluetoothUtil.getdeviceList(bAdapter);
            }
        }, 2000);
        List<BluetoothDevice> mDeviceList = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
        for(BluetoothDevice mDevice : mDeviceList) {
            Log.e(TAG, "BlUETOOTH DEVICE NAME " + mDevice.getName());
            Log.e(TAG, "BLUETOOTH MAC ADDRESS " + mDevice.getAddress());
        }

    }
    private void initBluetoothProfileListener() {
        mServiceListener = new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {

            }

            @Override
            public void onServiceDisconnected(int profile) {

            }
        };
    }



    public void buttonStart(View view) {
        isActive = true;
        textViewStatus.setText("Active");
        thread.start();
    }

    public void buttonStop(View view) {

        isActive = false;
        audioTrack.stop();
        audioRecord.stop();

        textViewStatus.setText("Stopped");
    }

    private void threadLoop() {
        int intRecordSampleRate = AudioTrack
                .getNativeOutputSampleRate(AudioManager.MODE_IN_COMMUNICATION);

        Log.e(TAG, "SAMPLE RATE MODIFIED " + intRecordSampleRate);

        int  intBufferSizes = AudioRecord
                .getMinBufferSize(intRecordSampleRate, AudioFormat.CHANNEL_OUT_STEREO
                        , AudioFormat.ENCODING_PCM_16BIT);
        intBufferSize =intBufferSizes*2;
        shortAudioData = new short[intBufferSize];

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION
                , intRecordSampleRate
                , AudioFormat.CHANNEL_IN_STEREO
                , AudioFormat.ENCODING_PCM_16BIT
                , intBufferSize);

        audioTrack = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION
                , intRecordSampleRate
                , AudioFormat.CHANNEL_OUT_STEREO
                , AudioFormat.ENCODING_PCM_16BIT
                , intBufferSize
                , AudioTrack.MODE_STREAM);

        audioTrack.setPlaybackRate(intRecordSampleRate);

        audioRecord.startRecording();

        if (NoiseSuppressor.isAvailable()) {
            mNoiseSupressor = NoiseSuppressor.create(audioRecord.getAudioSessionId());
            mNoiseSupressor.setEnabled(true);
            Log.e(TAG, "Noise supressor is  present");
        } else {
            Log.e(TAG, "Noise supressor is not present");
        }
        if (AcousticEchoCanceler.isAvailable()) {
            mAcousticEchoCanceler = AcousticEchoCanceler.create(audioRecord.getAudioSessionId());
            mAcousticEchoCanceler.setEnabled(true);
            Log.e(TAG, "Echo canceler is present");
        } else {
            Log.e(TAG, "Echo canceler is not present");
        }
        //        if (AutomaticGainControl.isAvailable()) {
        //            System.out.println("AGC IS AVAILIABLE");
        //            AutomaticGainControl agc = AutomaticGainControl.create(
        //                    audioRecord.getAudioSessionId()
        //            );
        //            agc.setEnabled(false);
        //        }
        //        else{
        //            System.out.println("AGC NOT AVAILIABLE");
        //        }

        audioTrack.play();
        while (isActive) {
            audioRecord.read(shortAudioData, 0, shortAudioData.length/2);

            for (int i = 0; i < shortAudioData.length; i++) {
                shortAudioData[i] = (short) Math.min(shortAudioData[i] / 5, Short.MAX_VALUE);
            }
            audioTrack.write(shortAudioData, 0, shortAudioData.length/2);
        }
    }
}
