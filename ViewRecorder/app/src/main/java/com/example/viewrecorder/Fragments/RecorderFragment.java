package com.example.viewrecorder.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.viewrecorder.R;

import java.io.File;
import java.io.IOException;

import static com.example.viewrecorder.Fragments.PlayListFragment.mRecordingAdapter;
import static com.example.viewrecorder.Fragments.PlayListFragment.refreshRecylerView;

public class RecorderFragment extends Fragment implements View.OnClickListener {

    private Toolbar toolbar;
    private Chronometer mChronometer;
    private ImageView mRecordView, mPlayRecordingView, mStopRecordingView;
    private SeekBar mSeekBar;
    private LinearLayout recorder, playLayout;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String fileName = null;
    private int lastProgress = 0;
    private Handler mHandler = new Handler();
    private int RECORD_AUDIO_REQUEST_CODE = 123;
    private boolean isPlaying = false;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.recorder_fragment, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToRecordAudio();
        }
        initViews(root.getContext(), root);
        return root;

    }

    private void initViews(Context context, View view) {

        recorder = view.findViewById(R.id.linearLayoutRecorder);
        mChronometer = view.findViewById(R.id.chronometerTimer);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mRecordView = view.findViewById(R.id.imageViewRecord);
        mStopRecordingView = view.findViewById(R.id.imageViewStop);
        mPlayRecordingView = view.findViewById(R.id.imageViewPlay);
        playLayout = view.findViewById(R.id.linearLayoutPlay);
        mSeekBar = view.findViewById(R.id.seekBarRecorder);
        mSeekBar.getProgressDrawable().setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.MULTIPLY);

        mRecordView.setOnClickListener(this);
        mStopRecordingView.setOnClickListener(this);
        mPlayRecordingView.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {

        if (view == mRecordView) {
            prepareforRecording();
            startRecording();
        } else if (view == mStopRecordingView) {
            prepareforStop();
            stopRecording();
            refreshRecylerView();
        } else if (view == mPlayRecordingView) {
            if (!isPlaying && fileName != null) {
                isPlaying = true;
                startPlaying();
            } else {
                isPlaying = false;
                stopPlaying();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void prepareforStop() {
        TransitionManager.beginDelayedTransition(recorder);
        mRecordView.setVisibility(View.VISIBLE);
        mStopRecordingView.setVisibility(View.GONE);
        playLayout.setVisibility(View.VISIBLE);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void prepareforRecording() {
        TransitionManager.beginDelayedTransition(recorder);
        mRecordView.setVisibility(View.GONE);
        mStopRecordingView.setVisibility(View.VISIBLE);
        playLayout.setVisibility(View.GONE);
    }

    private void stopPlaying() {
        try {
            mPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPlayer = null;
        //showing the play button
        mPlayRecordingView.setImageResource(R.drawable.recorder_play);
        mChronometer.stop();

    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        File root = android.os.Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/ViewRecorder/Audios");
        if (!file.exists()) {
            file.mkdirs();
        }

        fileName = root.getAbsolutePath() + "/ViewRecorder/Audios/" + String.valueOf(System.currentTimeMillis() + ".mp3");
        Log.e("filename", fileName);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        setFilename(fileName);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastProgress = 0;
        mSeekBar.setProgress(0);
        stopPlaying();
        // making the imageview a stop button
        //starting the chronometer
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }

    public static String fname;

    public static void setFilename(String name) {
        fname = name;
    }

    public static String getFilename() {
        return fname;
    }


    private void stopRecording() {

        try {
            mRecorder.stop();
            mRecorder.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRecorder = null;
        //starting the chronometer
        mChronometer.stop();
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mRecordingAdapter.notifyDataSetChanged();
        //showing the play button
        Toast.makeText(getContext(), "Recording saved successfully.", Toast.LENGTH_SHORT).show();
    }


    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
        //making the imageview pause button
        mPlayRecordingView.setImageResource(R.drawable.recorder_pause);

        mSeekBar.setProgress(lastProgress);
        mPlayer.seekTo(lastProgress);
        mSeekBar.setMax(mPlayer.getDuration());
        seekUpdation();
        mChronometer.start();


        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayRecordingView.setImageResource(R.drawable.recorder_play);
                isPlaying = false;
                mChronometer.stop();
            }
        });


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mPlayer != null && fromUser) {
                    mPlayer.seekTo(progress);
                    mChronometer.setBase(SystemClock.elapsedRealtime() - mPlayer.getCurrentPosition());
                    lastProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    private void seekUpdation() {
        if (mPlayer != null) {
            int mCurrentPosition = mPlayer.getCurrentPosition();
            mSeekBar.setProgress(mCurrentPosition);
            lastProgress = mCurrentPosition;
        }
        mHandler.postDelayed(runnable, 100);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToRecordAudio() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat
                .checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RECORD_AUDIO_REQUEST_CODE);

        }
    }

    // Callback with the request from calling requestPermissions(...)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.length == 3 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                //Toast.makeText(this, "Record Audio permission granted", Toast.LENGTH_SHORT).show();

            } else {
                // Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show();
                // finishAffinity();
            }
        }

    }
}
