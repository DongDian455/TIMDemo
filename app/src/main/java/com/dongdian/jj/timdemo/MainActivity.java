package com.dongdian.jj.timdemo;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.dongdian.jj.timdemo.model.Conversation;
import com.dongdian.jj.timdemo.ui.ConversationFragment;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;

public class MainActivity extends FragmentActivity {

    private TIMConversation timConversation;
    private ConversationFragment conversationFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //为了生成对话，每次跳转到这里都发送一条消息给对方
        sendNormalMessage();
        setConversationFragment();
    }

    private void setConversationFragment() {
        conversationFragment=new ConversationFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmelayout,conversationFragment,conversationFragment.getTag()).commit();
    }

    //发送消息
    private void sendNormalMessage() {
        //获取单聊会话
        String peer = "admin2";  //获取与用户 "sample_user_1" 的会话
        timConversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                peer);                      //会话对方用户帐号//对方id


        //构造一条消息
        TIMMessage msg = new TIMMessage();

       //添加文本内容
        TIMTextElem elem = new TIMTextElem();
        elem.setText("a new msg");

       //将elem添加到消息
        if(msg.addElement(elem) != 0) {
            Log.d("tencentim", "addElement failed");
            return;
        }

         //发送消息
        timConversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                Log.d("tencentim", "send message failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Log.e("tencentim", "SendMsg ok");
            }
        });
    }
}
