package com.example.viewrecorder.Fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viewrecorder.Adapter.RecordingAdapter;
import com.example.viewrecorder.R;
import com.example.viewrecorder.model.Recording;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import javax.net.ssl.SNIHostName;

public class PlayListFragment extends Fragment {

    private Toolbar toolbar;
    public static RecyclerView recyclerViewRecordings;
    private ArrayList<Recording> recordingArraylist;
    public static RecordingAdapter recordingAdapter;
    private TextView textViewNoRecordings;
    private HashMap<String, Integer>  mHashMap =new HashMap<>();
    private static final String TAG = "PlayListFragment";
    Handler handler = new Handler();
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  fetchRecordings();
        Log.e("INFO", "OnCreate  in playlist  called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.playlist_fragment,container,false);
        Log.e("INFO", "OnCreateView in playlist  called");
        initViews(root);
        recordingArraylist = new ArrayList<Recording>();
       // init();
     //   fetchRecordings();
       Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                // Do something here on the main thread
                Log.d("Handlers", "Called on main thread");
                fetchRecordings();
                // Repeat this the same runnable code block again another 2 seconds
                // 'this' is referencing the Runnable object
                handler.postDelayed(this, 2000);
            }
        };

        handler.post(runnableCode);


        deleteRecordingsOnSwipe();
        return  root;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("INFO", "OnResume in playlist  called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("INFO", "OnPause in playlist  called");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void updateName(String name) {

    }


    private void deleteRecordingsOnSwipe() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.e("INFO", "Swiped Recordings to Delete it");
                String currentRecordingName = recordingAdapter.getRecordingName(viewHolder.getAdapterPosition());
                Log.e(TAG, "Current Recording Name which is Swiped to Delete" + currentRecordingName );
                String MEDIA_PATH = new String(Environment.getExternalStorageDirectory() + "/ViewRecorder/Audios/" + currentRecordingName);
                Log.e(TAG, "Full Media Path " + MEDIA_PATH);
                File file = new File(MEDIA_PATH);
                //storage/emulated/0/ViewRecorder/Audios
                if(file.exists()) {
                    Log.e("INFO", "File exists and is ready to be deleted");
                    file.delete();
                } else {
                    Log.e("INFO", "File doesnot exists");
                }
                recordingArraylist.remove(viewHolder.getAdapterPosition());
                recordingAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerViewRecordings);
    }

    private void fetchRecordings() {

        File root = android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/ViewRecorder/Audios";
        Log.e("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
     //Log.e("Files", "Size: "+ files.length);
        if( files!=null ){

            for (int i = 0; i < files.length; i++) {

                Log.d("Files", "FileName:" + files[i].getName());
                String fileName = files[i].getName();
                String recordingUri = root.getAbsolutePath() + "/ViewRecorder/Audios/" + fileName;

                Recording recording = new Recording(recordingUri,fileName,false);
                if(!mHashMap.containsKey(fileName)) {
                    Log.e("INFO", "New Recording has been created");
                    mHashMap.put(fileName, 1);
                    recordingArraylist.add(0,recording);
                    setAdaptertoRecyclerView();
                }
            }

            textViewNoRecordings.setVisibility(View.GONE);
            recyclerViewRecordings.setVisibility(View.VISIBLE);

        }else{
            textViewNoRecordings.setVisibility(View.VISIBLE);
            recyclerViewRecordings.setVisibility(View.GONE);
        }

    }

    public static void refreshRecylerView() {
       // recordingAdapter.notifyDataSetChanged();
        recordingAdapter.notifyItemInserted(0);
        Log.e("INFO",  "Refrest Recycler View");
    }

    private void setAdaptertoRecyclerView() {
        recordingAdapter = new RecordingAdapter(getContext(),recordingArraylist);
        recyclerViewRecordings.setAdapter(recordingAdapter);
    }

    private void initViews(View view) {

        /** setting up the toolbar  **/
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Recording List");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
//       getContext().setSupportActionBar(toolbar);

        /** enabling back button ***/
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /** setting up recyclerView **/
        recyclerViewRecordings = view.findViewById(R.id.recyclerViewRecordings);
        recyclerViewRecordings.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL, false));
        recyclerViewRecordings.setHasFixedSize(true);
        recyclerViewRecordings.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
                int childCount = parent.getChildCount();


                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    if (parent.getAdapter() == null) return;

                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) child.getLayoutParams();

                    params.bottomMargin = parent.getChildAdapterPosition(child) != parent.getAdapter().getItemCount() - 1
                            ? 20
                            : 5;
                }

            }
        });

        textViewNoRecordings = view.findViewById(R.id.textViewNoRecordings);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
               getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}
