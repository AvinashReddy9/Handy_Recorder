package com.example.viewrecorder.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewrecorder.Dialog.EditName;
import com.example.viewrecorder.R;
import com.example.viewrecorder.model.Recording;

import java.io.IOException;
import java.util.ArrayList;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Recording> playListRecordings;
    private MediaPlayer mMediaPlayer;
    private boolean isRecordingPlaying = false;
    private int last_index = -1;
    private static final String TAG = "RecordingAdapter";
    private boolean isARecordingPlaying = false;

    public RecordingAdapter(Context mContext, ArrayList<Recording> playListRecordings) {
        this.mContext = mContext;
        this.playListRecordings = playListRecordings;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater
                .from(mContext).inflate(R.layout.recording_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setUpData(holder, position, "");
        Log.e(TAG, "Position of the current item " + position);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setUpData(@NonNull ViewHolder holder, int position, String name) {

        Recording recording = playListRecordings.get(position);
        if (!name.isEmpty()) {
            holder.textViewName.setText(name);
        } else {
            holder.textViewName.setText(recording.getFileName());
        }

        if (recording.isPlaying()) {
            holder.imageViewPlay.setImageResource(R.drawable.recorder_pause);
            TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
            holder.seekBar.setVisibility(View.VISIBLE);
            holder.seekUpdation(holder);
        } else {
            holder.imageViewPlay.setImageResource(R.drawable.recorder_play);
            TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
            holder.seekBar.setVisibility(View.GONE);
        }

        holder.manageSeekBar(holder);

    }

    @Override
    public int getItemCount() {
        return playListRecordings.size();
    }

    public String getRecordingName(int position) {
        if (playListRecordings == null || position >= playListRecordings.size()) {
            Log.e(TAG, "Size of Array:ist " + playListRecordings.size() + "Requested position to delete " + position);
            Log.e(TAG, "Invalid position on recycler view is requested");
            return "";
        }
        return playListRecordings.get(position).getFileName();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewPlay;
        SeekBar seekBar;
        TextView textViewName;
        ImageView shareButton;
        private String recordingUri;
        private CardView mCardView;
        private int lastProgress = 0;
        private Handler mHandler = new Handler();
        public ViewHolder holder;
        View view;

        public void updateTextView() {
            holder.textViewName.setText(EditName.updatedName());
        }

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            final View mView = itemView;

            imageViewPlay = itemView.findViewById(R.id.imageViewPlay);
            seekBar = itemView.findViewById(R.id.seekBarRecorder);
            seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.MULTIPLY);
            textViewName = itemView.findViewById(R.id.textViewRecordingname);
            shareButton = itemView.findViewById(R.id.share_Button);
            mCardView = itemView.findViewById(R.id.cardview);

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("CLICKED", "Clicked share button" + getAdapterPosition());
                    Log.e("CLICKED", "Clicked share button file name " + getRecordingName(getAdapterPosition()));
                    String filePath = new String(Environment.getExternalStorageDirectory() + "/ViewRecorder/Audios/" + getRecordingName(getAdapterPosition()));
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("audio/*");
                    Uri uri = Uri.parse(filePath);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    mView.getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            });

            imageViewPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) mCardView.getLayoutParams();
                    params.height = params.height + 20;
                    mCardView.setLayoutParams(params);
                    int position = getAdapterPosition();
                    Recording recording = playListRecordings.get(position);
                    recordingUri = recording.getUri();

                    if (isRecordingPlaying) {
                        stopPlaying();
                        if (position == last_index) {
                            recording.setPlaying(false);
                            stopPlaying();
                            notifyItemChanged(position);
                        } else {
                            Log.e("INFO", "Is Playing");
                            markAllPaused();
                            recording.setPlaying(true);
                            notifyItemChanged(position);
                            startPlaying(recording, position);
                            last_index = position;
                        }

                    } else {
                        if (recording.isPlaying()) {
                            recording.setPlaying(false);
                            stopPlaying();
                            seekBar.setVisibility(View.GONE);
                            params.height = params.height - 20;
                            mCardView.setLayoutParams(params);
                            Log.d("isPlayin", "True");
                        } else {
                            Log.e("INFO", "Is Playing");
                            startPlaying(recording, position);
                            recording.setPlaying(true);
                            seekBar.setMax(mMediaPlayer.getDuration());
                            params.height = params.height - 20;
                            mCardView.setLayoutParams(params);
                            Log.d("isPlayin", "False");
                        }
                        notifyItemChanged(position);
                        last_index = position;
                    }

                }

            });
        }

        public void manageSeekBar(ViewHolder holder) {
            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (mMediaPlayer != null && fromUser) {
                        mMediaPlayer.seekTo(progress);
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


        private void markAllPaused() {
            for (int i = 0; i < playListRecordings.size(); i++) {
                playListRecordings.get(i).setPlaying(false);
                playListRecordings.set(i, playListRecordings.get(i));
            }
            notifyDataSetChanged();
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                seekUpdation(holder);
            }
        };

        private void seekUpdation(ViewHolder holder) {
            this.holder = holder;
            if (mMediaPlayer != null) {
                int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                holder.seekBar.setMax(mMediaPlayer.getDuration());
                holder.seekBar.setProgress(mCurrentPosition);
                lastProgress = mCurrentPosition;
            }
            mHandler.postDelayed(runnable, 100);
        }


        private void stopPlaying() {
            //   isARecordingPlaying = false;
            try {
                mMediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mMediaPlayer = null;
            isRecordingPlaying = false;
        }

        private void startPlaying(final Recording audio, final int position) {
            mMediaPlayer = new MediaPlayer();
            //  isARecordingPlaying = true;
            try {
                mMediaPlayer.setDataSource(recordingUri);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                Log.e("LOG_TAG", "prepare() failed");
            }
            //showing the pause button
            seekBar.setMax(mMediaPlayer.getDuration());
            isRecordingPlaying = true;

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    audio.setPlaying(false);
                    notifyItemChanged(position);
                }
            });
        }

    }
}
