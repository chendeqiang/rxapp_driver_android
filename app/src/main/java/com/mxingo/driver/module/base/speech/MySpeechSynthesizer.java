package com.mxingo.driver.module.base.speech;

import android.os.Bundle;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.mxingo.driver.MyApplication;
import com.mxingo.driver.module.base.log.LogUtils;
import com.mxingo.driver.utils.TextUtil;

import java.io.File;

/**
 * Created by zhouwei on 2016/11/4.
 */

public class MySpeechSynthesizer {
    private SpeechSynthesizer mTts;

    public MySpeechSynthesizer() {
        //1.创建 SpeechSynthesizer 对象
        mTts = SpeechSynthesizer.createSynthesizer(MyApplication.application, new InitListener() {

            @Override
            public void onInit(int i) {

            }
        });
        if (mTts == null || mTts.destroy()) {
            return;
        }
        //2.合成参数设置
        //设置引擎类型为本地
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
        //设置本地发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        File file = new File("./sdcard");
        LogUtils.d("isFile", file.isDirectory() + "，" + file.exists());
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        //加载本地合成资源，resPath为本地合成资源路径
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, "./sdcard/rx_driver.pcm");
        //设置合成音频保存位置(可自定义保存位置)，保存在“./sdcard/iflytek.pcm”
        // 保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
        // 如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/rx_driver_audio.pcm");


//        mTts.setParameter("engine_type", "cloud");
//        mTts.setParameter("speed", "50");//设置语速
//        mTts.setParameter("volume", "80");//设置音量，范围 0~100
//        //设置本地发音人
//        mTts.setParameter("voice_name", "xiaoyan");
//        //加载本地合成资源，resPath为本地合成资源路径
//        mTts.setParameter("tts_res_path", "./sdcard/iflytek.pcm");
//        //设置合成音频保存位置(可自定义保存位置)，保存在“./sdcard/iflytek.pcm”
//        // 保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
//        // 如果不需要保存合成音频，注释该行代码
//        mTts.setParameter("tts_audio_path", "./sdcard/iflytek_audio.pcm");


    }

    public void startSpeaking(String text) {
        if (TextUtil.isEmpty(text) || mTts == null) {
            return;
        }
        mTts.startSpeaking(text, mSynListener);
    }

    public void stop() {
        if (mTts.isSpeaking()) {
            mTts.stopSpeaking();
        }
    }

    public void destroy() {
        if (mTts == null) {
            return;
        }
        mTts.destroy();
    }

    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
        }

        //缓冲进度回调 //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在
        // 文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
        }

        //暂停播放
        public void onSpeakPaused() {
        }
        //播放进度回调 //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文

        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }


        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };
}
