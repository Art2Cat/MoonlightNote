package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.art2cat.dev.moonlightnote.Model.BusEvent;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.AudioPlayerUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.UUID;

import static com.art2cat.dev.moonlightnote.R.drawable.ic_action_record;

/**
 * Created by Rorschach
 * on 2016/10/24 17:31.
 */

public class AudioRecordFragment extends DialogFragment {
    private MediaRecorder mRecorder = null;
    private static String mFileName = null;
    private boolean isRecord = true;
    private boolean isPlay = true;
    private MediaPlayer mPlayer = null;
    private String mFile;
    private AppCompatButton record;
    private AppCompatButton play;
    private ProgressBar mProgressBar;
    private AudioPlayerUtils audioPlayerUtils;

    private static final String TAG = "AudioRecordFragment";

    public AudioRecordFragment() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MoonlightNote/.audio/";
        mFile = UUID.randomUUID().toString() + ".aac";
        mFileName += mFile;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_audio_record, null);
        record = (AppCompatButton) view.findViewById(R.id.record_audio);
        record.setBackground(getResources().getDrawable(R.drawable.ic_action_record, null));
        play = (AppCompatButton) view.findViewById(R.id.play_audio_button);
        play.setBackground(getResources().getDrawable(R.drawable.ic_play_circle_outline_cyan_400_48dp, null));
        mProgressBar = (ProgressBar) view.findViewById(R.id.dialog_AR_progressBar);
        //audioPlayerUtils = new AudioPlayerUtils(mProgressBar);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecord) {
                    //start record voice
                    startRecording();
                    record.setBackground(getResources().getDrawable(R.drawable.ic_action_stop, null));
                } else {
                    //stop record voice
                    stopRecording();
                    record.setBackground(getResources().getDrawable(R.drawable.ic_action_record, null));
                    play.setVisibility(View.VISIBLE);
                }
                isRecord = !isRecord;
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlay) {
                    audioPlayerUtils.prepare(mFileName);
                    audioPlayerUtils.startPlaying(mFileName);
                    play.setBackground(getResources().getDrawable(R.drawable.ic_action_stop, null));
                } else {
                    audioPlayerUtils.stopPlaying();
                    play.setBackground(getResources().getDrawable(R.drawable.ic_play_circle_outline_cyan_400_48dp, null));
                }
                isPlay = !isPlay;
            }
        });

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //send data
                        BusEvent busEvent = new BusEvent();
                        busEvent.setFlag(Constants.BUS_FLAG_AUDIO_URL);
                        busEvent.setMessage(mFile);
                        EventBus.getDefault().post(busEvent);
                        dismiss();
                    }
                }).setNegativeButton("Cancel", null);
        return builder.create();
    }

    private void startRecording() {
        //新建MediaRecorder对象
        mRecorder = new MediaRecorder();
        //设置音频源
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置录音输出格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        //设置输出文件
        mRecorder.setOutputFile(mFileName);
        //设置音频编码器
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            //准备录音
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        //开始录音
        mRecorder.start();
        displayProgressBar();
    }

    private void stopRecording() {
        //停止录音
        mRecorder.stop();
        //解除录音器
        mRecorder.release();
        mRecorder = null;
    }

    private void startPlaying() {
        //新建音频播放器
        mPlayer = new MediaPlayer();
        try {
            //设置数据源
            mPlayer.setDataSource(mFileName);
            //准备播放
            mPlayer.prepare();
            //开始播放
            mPlayer.start();
            mProgressBar.setMax(mPlayer.getDuration());
            mProgressBar.setProgress(mPlayer.getCurrentPosition());
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        //释放播放器
        mPlayer.release();
        mPlayer = null;
    }

    private void displayProgressBar() {
        mProgressBar.setProgress(mRecorder.getMaxAmplitude());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        audioPlayerUtils.releasePlayer();
    }
}


