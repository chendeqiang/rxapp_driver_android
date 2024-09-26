package com.mxingo.driver.module.base.speech;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.mxingo.driver.MyApplication;
import com.mxingo.driver.utils.TextUtil;
import com.mxingo.driver.widget.ShowToast;

import java.util.Locale;

/**
 * Created by deqiangchen on 2023/2/13.
 */
@SuppressWarnings("deprecation")
public class MySpeechUtils extends UtteranceProgressListener {
    private static final String TAG = "MySpeech";
    private TextToSpeech mySpeech;

    public MySpeechUtils(){
        mySpeech = new TextToSpeech(MyApplication.application, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                //初始化成功
                if (status == TextToSpeech.SUCCESS) {
                    Log.d(TAG, "init success");
                    int result = mySpeech.setLanguage(Locale.CHINA);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||result == TextToSpeech.LANG_NOT_SUPPORTED){
                        ShowToast.showCenter(MyApplication.application,"语言不支持");
                    }
                } else {
                    Log.d(TAG, "init fail");
                }
            }
        });
        mySpeech.setPitch(1.0f);
        mySpeech.setSpeechRate(1.5f);
        mySpeech.setOnUtteranceProgressListener(this);
    }

    @Override
    public void onStart(String utteranceId) {
        //播报开始
    }

    @Override
    public void onDone(String utteranceId) {
        //播报结束
    }

    @Override
    public void onError(String utteranceId) {
        //播报出错
    }

    public void startSpeaking(String text) {
        if (TextUtil.isEmpty(text) || mySpeech == null) {
            return;
        }
        //  第二个参数queueMode用于指定发音队列模式，两种模式选择。
        //（1）TextToSpeech.QUEUE_FLUSH：该模式下在有新任务时候会清除当前语音任务，执行新的语音任务
        //（2）TextToSpeech.QUEUE_ADD：该模式下会把新的语音任务放到语音任务之后，等前面的语音任务执行完了才会执行新的语音任务
        mySpeech.speak(text.toString(),TextToSpeech.QUEUE_FLUSH,null);
    }
    public void stopSpeaking(){
        if (mySpeech!=null){
            mySpeech.stop();
        }
    }
    public void destroy(){
        if (mySpeech==null){
            return;
        }
        mySpeech.stop();
        mySpeech.shutdown();
    }
}
