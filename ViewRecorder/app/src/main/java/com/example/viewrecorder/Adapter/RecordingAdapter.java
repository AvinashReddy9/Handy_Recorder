package com.example.viewrecorder.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
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

import static com.example.viewrecorder.Fragments.RecorderFragment.getFilename;

public class RecordingAdapter  extends RecyclerView.Adapter<RecordingAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Recording> recordingArrayList;
    private MediaPlayer mPlayer;
    private boolean isPlaying = false;
    private int last_index = -1;
    private static final String TAG = "RecordingAdapter";
    public RecordingAdapter(Context context, ArrayList<Recording> recordingArrayList){
        this.context = context;
        this.recordingArrayList = recordingArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater
                .from(context).inflate(R.layout.recording_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setUpData(holder,position,"");
        Log.e(TAG, "Position of the current item " + position);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               Log.e("INFO","Recording list is clicked");
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setUpData(@NonNull ViewHolder holder, int position, String name) {

        Recording recording = recordingArrayList.get(position);
        if (!name.isEmpty()) {
            holder.textViewName.setText(name);
        } else {
            holder.textViewName.setText(recording.getFileName());
        }

        if (recording.isPlaying()) {
            holder.imageViewPlay.setImageResource(R.drawable.ic_pause);
            TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
            holder.seekBar.setVisibility(View.VISIBLE);
            holder.seekUpdation(holder);
        } else {
//            if(holder != null) {
//                holder.imageViewPlay.setImageResource(R.drawable.ic_play);
//                TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
//                holder.seekBar.setVisibility(View.GONE);
//            }
        }

        holder.manageSeekBar(holder);

    }

    @Override
    public int getItemCount() {
        return recordingArrayList.size();
    }

    public String getRecordingName(int position) {
        if( recordingArrayList == null || position >= recordingArrayList.size()) {
            Log.e(TAG, "Size of Array:ist " + recordingArrayList.size() + "Requested position to delete " + position);
            Log.e(TAG, "Invalid position on recycler view is requested");
            return "";
        }
        return recordingArrayList.get(position).getFileName();
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
        public  ViewHolder holder;
        View view;

        public void updateTextView(){
            holder.textViewName.setText(EditName.updatedName());
        }

        public ViewHolder(View itemView) {
            super(itemView);
            view =itemView;
            final View mView = itemView;

        //    imageViewPlay = itemView.findViewById(R.id.imageViewPlay);
            seekBar = itemView.findViewById(R.id.seekBar);
            textViewName = itemView.findViewById(R.id.textViewRecordingname);
            shareButton = itemView.findViewById(R.id.share_Button);
            mCardView = itemView.findViewById(R.id.cardview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Log.e("POSITION", "Position in View Holder " + position);
                }
            });

            textViewName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    EditName name = new EditName(mView.getContext());
//                    name.show();
//                    int position = getAdapterPosition();
                }
            });


            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("audio/*");
                    String shareBody = getFilename();
                    Uri uri = Uri.parse(shareBody);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                 //   sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    mView.getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            });

//            imageViewPlay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) mCardView.getLayoutParams();
//                    params.height = params.height + 20;
//                    mCardView.setLayoutParams(params);
//                    int position = getAdapterPosition();
//                    Recording recording = recordingArrayList.get(position);
//                    recordingUri = recording.getUri();
//
//                    if( isPlaying ){
//                        stopPlaying();
//                        if( position == last_index ){
//                            recording.setPlaying(false);
//                            stopPlaying();
//                            notifyItemChanged(position);
//                        }else{
//                            markAllPaused();
//                            recording.setPlaying(true);
//                            notifyItemChanged(position);
//                            startPlaying(recording,position);
//                            last_index = position;
//                        }
//
//                    }else {
//                        if( recording.isPlaying() ){
//                            recording.setPlaying(false);
//                            stopPlaying();
//                            seekBar.setVisibility(View.GONE);
//                            params.height = params.height - 20;
//                            mCardView.setLayoutParams(params);
//                            Log.d("isPlayin","True");
//                        }else {
//                            startPlaying(recording,position);
//                            recording.setPlaying(true);
//                            seekBar.setMax(mPlayer.getDuration());
//                            params.height = params.height - 20;
//                            mCardView.setLayoutParams(params);
//                            Log.d("isPlayin","False");
//                        }
//                        notifyItemChanged(position);
//                        last_index = position;
//                    }
//
//                }
//
//            });
        }
        public void manageSeekBar(ViewHolder holder){
            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if( mPlayer!=null && fromUser ){
                        mPlayer.seekTo(progress);
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
            for( int i=0; i < recordingArrayList.size(); i++ ){
                recordingArrayList.get(i).setPlaying(false);
                recordingArrayList.set(i,recordingArrayList.get(i));
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
            if(mPlayer != null){
                int mCurrentPosition = mPlayer.getCurrentPosition() ;
                holder.seekBar.setMax(mPlayer.getDuration());
                holder.seekBar.setProgress(mCurrentPosition);
                lastProgress = mCurrentPosition;
            }
            mHandler.postDelayed(runnable, 100);
        }


        private void stopPlaying() {
            try{
                mPlayer.release();
            }catch (Exception e){
                e.printStackTrace();
            }
            mPlayer = null;
            isPlaying = false;
        }

        private void startPlaying(final Recording audio, final int position) {
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(recordingUri);
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Log.e("LOG_TAG", "prepare() failed");
            }
            //showing the pause button
            seekBar.setMax(mPlayer.getDuration());
            isPlaying = true;

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    audio.setPlaying(false);
                    notifyItemChanged(position);
                }
            });
        }

    }
}
