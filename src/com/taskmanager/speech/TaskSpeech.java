package com.taskmanager.speech;

import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

@SuppressLint("NewApi")
public class TaskSpeech implements OnInitListener, OnUtteranceCompletedListener{
	
	

	private static final String TAG = "TaskSpeech";
	private TextToSpeech tts;
	private Context context;
	
	public TaskSpeech(Context passedContext) {
		context = passedContext;
		setupSpeech();
	}
    
	public void setupSpeech() {
		tts = new TextToSpeech(context, this);
	    tts.setLanguage(Locale.US);
	}


	public void onDoneSpeaking(String utteranceId) {
		// TODO Auto-generated method stub
		Log.d(TAG, utteranceId);
	}
	
	@SuppressWarnings("deprecation")
	public void onInit(int arg0) {	
		if (Build.VERSION.SDK_INT >= 15){
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener(){
                @Override
                public void onDone(String utteranceId)
                {
                    onDoneSpeaking(utteranceId);
                }

                @Override
                public void onError(String utteranceId)
                {
                }

                @Override
                public void onStart(String utteranceId)
                {
                }
            });
        } else {
            Log.d(TAG, "set utternace completed listener");
            int res = tts.setOnUtteranceCompletedListener(this);
            if (res==TextToSpeech.ERROR) {
                Log.d("1TA", "Failed to set on utterance listener");
            }
        }
	}
	
	@Override
	public void onUtteranceCompleted(String utteranceId) {
		// TODO Auto-generated method stub
		onDoneSpeaking(utteranceId);
	}
	
	public void speak(String speakThis)
    {
        HashMap<String, String> ttsParams = new HashMap<String, String>();
		ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "User Notification");
		tts.speak(speakThis, TextToSpeech.QUEUE_FLUSH, ttsParams);
	}
}
