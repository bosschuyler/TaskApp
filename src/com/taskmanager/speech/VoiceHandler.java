package com.taskmanager.speech;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

public class VoiceHandler { 
	private SpeechProcessor Processor = null;
	private SpeechRecognizer Recognizer = null;
	private Intent SpeechRecognizerIntent;
	
	private boolean isListening = false;
	
	private static String TAG = "Voice Handler";
			
	private static VoiceHandler instance = null;	
	public static VoiceHandler getInstance() {
		if(instance == null) {
			instance = new VoiceHandler();		
		}		
		return instance;
	}
	
	public VoiceHandler() {};
	
	public void init(Context tempContext) {
		Recognizer = SpeechRecognizer.createSpeechRecognizer(tempContext);
		Recognizer.setRecognitionListener(Listener);
		
		SpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		SpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		SpeechRecognizerIntent.putExtra("calling_package", "com.taskmanager.speech");
	}

	public boolean isListening() {
		return isListening;
	}
	
	public void startListening() {
		Recognizer.startListening(SpeechRecognizerIntent);
		isListening = true;	
	}
	
	public void stopListening() {
		Recognizer.stopListening();
		Recognizer.cancel();
		
		isListening = false;
	}
	
	
	/* Pass in the speech processor to use to handle the result */
	public void setSpeechProcessor (SpeechProcessor newProcessor) {
		Processor = newProcessor;
	}
	
	private RecognitionListener Listener = new RecognitionListener() {
        @Override
        public void onBufferReceived(byte[] buffer) {
                // TODO Auto-generated method stub
                //Log.d(TAG, "onBufferReceived");
        }

        @Override
        public void onError(int error) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onError: " + error);
                startListening();
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onEvent");
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onPartialResults");
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onReadyForSpeech");
                
        }

        @Override
        public void onResults(Bundle results) {
                Log.d(TAG, "onResults");
                
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                
                String voiceRequest = matches.get(0);

                Processor.processRequest(voiceRequest);
                startListening();
        }
        
        @Override
        public void onRmsChanged(float rmsdB) {
                // TODO Auto-generated method stub
                //Log.d(TAG, "onRmsChanged");
        }

        @Override
        public void onBeginningOfSpeech() {
                // TODO Auto-generated method stub
                Log.d(TAG, "onBeginningOfSpeech");
        }

        @Override
        public void onEndOfSpeech() {
                // TODO Auto-generated method stub
                Log.d(TAG, "onEndOfSpeech");
               
        }

	}; 
}