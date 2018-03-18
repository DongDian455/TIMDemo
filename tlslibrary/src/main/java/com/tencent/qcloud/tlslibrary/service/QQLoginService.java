package com.tencent.qcloud.tlslibrary.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.tencent.qcloud.tlslibrary.helper.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dgy on 15/7/23.
 */
public class QQLoginService {
    // 授权结果常量定义
    private static final int AUTHORIZATION_SUCC = 1;            // 授权成功
    private static final int AUTHORIZATION_FAIL = 2;            // 授权失败
    private static final int AUTHORIZATION_CANCEL = 3;          // 取消授权

    private Activity activity;
    private Tencent mTencent;
    private Handler handler;
    private String openid;
    private String access_token;
    private StringBuilder sBuilder = new StringBuilder();
    private String SCOPE = "all"; // 所有权限
    private Callback callback;

    public QQLoginService(Activity activity, Button btn_hostQQLogin) {
        this.activity = activity;
        callback = new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.arg1) {
                    case AUTHORIZATION_SUCC: { // 授权成功
                        JSONObject object = (JSONObject) msg.obj;
                        try {
                            openid = object.getString("openid").toString();
                            sBuilder.append("openid:\n" + openid + "\n");
                            access_token = object.getString("access_token").toString();
                            sBuilder.append("access_token:\n" + access_token);

//                            Util.showToast(QQLoginService.this.activity, sBuilder.toString());

                            QQLoginService.this.jumpToSuccActivity();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case AUTHORIZATION_FAIL: { // 授权失败
                        return false;
                    }
                    case AUTHORIZATION_CANCEL: { // 取消授权
                        return false;
                    }
                }
                return false;
            }
        };

        handler = new Handler(callback);

        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI
        // 其中APP_ID是分配给第三方应用的appid，类型为String
        mTencent = Tencent.createInstance(TLSConfiguration.QQ_APP_ID, activity.getApplicationContext());
        btn_hostQQLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickQQLogin();
            }
        });
    }

    private void onClickQQLogin() {
//        if (!mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {
                /** 授权失败的回调 */
                @Override
                public void onError(UiError error) {
                    Util.showToast(activity, "授权失败");
                    Message msg = new Message();
                    msg.arg1 = AUTHORIZATION_FAIL;
                    handler.sendMessage(msg);
                }
                /** 授权成功的回调*/
                @Override
                public void onComplete(Object response) {
                    Util.showToast(activity, "授权成功");
                    activity.finish();
                    Message msg = new Message();
                    msg.what = 2;
                    msg.arg1 = AUTHORIZATION_SUCC;
                    msg.obj = response;
                    handler.sendMessage(msg);
                }
                /** 取消授权的回调*/
                @Override
                public void onCancel() {
                    Util.showToast(activity, "取消授权");
                    Message msg = new Message();
                    msg.arg1 = AUTHORIZATION_CANCEL;
                    handler.sendMessage(msg);
                }
            };
            qqLogin(listener);
//        } else {
//            qqLogout();
//        }
    }

    public void qqLogin(IUiListener listener) {

        mTencent.login(activity, SCOPE, listener);
    }

    public void qqLogout() {
        mTencent.logout(activity);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data);
    }

    public boolean qqHasLogined() {
        return mTencent.isSessionValid();
    }

    void jumpToSuccActivity() {
//        activity.finish();
        String thirdappPackageNameSucc = Constants.thirdappPackageNameSucc;
        String thirdappClassNameSucc = Constants.thirdappClassNameSucc;

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.QQ_LOGIN);
        intent.putExtra(Constants.EXTRA_QQ_LOGIN, Constants.QQ_LOGIN_SUCCESS);
        intent.putExtra(Constants.EXTRA_QQ_OPENID, openid);
        intent.putExtra(Constants.EXTRA_QQ_ACCESS_TOKEN, access_token);
        if (thirdappPackageNameSucc != null && thirdappClassNameSucc != null) {
            intent.setClassName(thirdappPackageNameSucc, thirdappClassNameSucc);
            activity.startActivity(intent);
        } else {
            activity.setResult(Activity.RESULT_OK, intent);
        }
    }
}
