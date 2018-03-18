package com.tencent.qcloud.tlslibrary.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.qcloud.tlslibrary.helper.Util;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSSmsLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/7/9.
 * 处理短信登录的业务
 */
public class SmsLoginService {

    private EditText txtCountryCode;                // 包含国家码的EditView控件
    private EditText txtPhoneNumber;                // 包含手机号的EditView控件
    private EditText txtCheckCode;                  // 包含验证码的EditView控件
    private Button btn_requireCheckCode;            // 获取验证码的按钮
    private Button btn_login;                       // 登录按钮

    private SmsLoginListener smsLoginListener;      // 处理短信登录过程中遇到的各种情况
    private Context context;                        // Activity
    private TLSService tlsService;

    private String countrycode;
    private String phoneNumber;                     // 不包括国家码前缀
    private String checkCode;

    public SmsLoginService(Context context,
                           EditText txtCountryCode,
                           EditText txtPhoneNumber,
                           EditText txtCheckCode,
                           Button btn_requireCheckCode,
                           Button btn_login)
    {
        this.context = context;
        this.txtCountryCode = txtCountryCode;
        this.txtPhoneNumber = txtPhoneNumber;
        this.txtCheckCode = txtCheckCode;
        this.btn_requireCheckCode = btn_requireCheckCode;
        this.btn_login = btn_login;
        this.smsLoginListener = new SmsLoginListener();
        this.tlsService = TLSService.getInstance();

        this.btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countrycode = SmsLoginService.this.txtCountryCode.getText().toString();
                countrycode = countrycode.substring(countrycode.indexOf('+') + 1);        // 解析国家码
                phoneNumber = SmsLoginService.this.txtPhoneNumber.getText().toString();   // 获取手机号
                checkCode = SmsLoginService.this.txtCheckCode.getText().toString();       // 获取验证码

                // 1. 判断手机号是否有效
                if (!Util.validPhoneNumber(countrycode, phoneNumber)) {
                    Util.showToast(SmsLoginService.this.context, "请输入有效的手机号");
                    return;
                }

                // 2. 判断验证码是否为空（是否请求了验证码由上层控制）
                if (checkCode.length() == 0) {
                    Util.showToast(SmsLoginService.this.context, "请输入验证码");
                    return;
                }

                // 3. 向TLS验证手机号和验证码
                SmsLoginService.this.tlsService.smsLoginVerifyCode(checkCode, smsLoginListener);
            }
        });

        this.btn_requireCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countrycode = SmsLoginService.this.txtCountryCode.getText().toString();
                countrycode = countrycode.substring(countrycode.indexOf('+') + 1);        // 解析国家码
                phoneNumber = SmsLoginService.this.txtPhoneNumber.getText().toString();   // 获取手机号

                // 1. 判断手机号是否有效
                if (!Util.validPhoneNumber(countrycode, phoneNumber)) {
                    Util.showToast(SmsLoginService.this.context, "请输入有效的手机号");
                    return;
                }

                // 2. 请求验证码
                SmsLoginService.this.tlsService.smsLoginAskCode(countrycode, phoneNumber, smsLoginListener);
            }
        });
    }

    /**
     * 短信登录监听器
     * */
    public class SmsLoginListener implements TLSSmsLoginListener {

        // 请求下发短信成功，(reaskDuration s) 时间内不可以重新请求下发短信
        @Override
        public void OnSmsLoginAskCodeSuccess(int reaskDuration, int expireDuration) {
            Util.showToast(context, "请求下发短信成功,验证码" + expireDuration / 60 + "分钟内有效");

            // 在获取验证码按钮上显示重新获取验证码的时间间隔
            Util.startTimer(btn_requireCheckCode, "获取验证码", "重新发送", reaskDuration, 1);
        }

        // 重新请求下发短信成功
        @Override
        public void OnSmsLoginReaskCodeSuccess(int reaskDuration, int expireDuration) {
            Util.showToast(context, "登录短信重新下发,验证码" + expireDuration / 60 + "分钟内有效");
        }

        // 短信验证通过，下一步调用登录接口TLSSmsLogin完成登录
        @Override
        public void OnSmsLoginVerifyCodeSuccess() {
            tlsService.smsLogin(countrycode, phoneNumber, smsLoginListener);
        }

        // 登录成功了，在这里可以获取用户数据
        @Override
        public void OnSmsLoginSuccess(TLSUserInfo userSigInfo) {
            Util.showToast(context, "短信登录成功");
            TLSService.getInstance().setLastErrno(0);
            SmsLoginService.this.jumpToSuccActivity();
        }

        // 短信登录过程中任意一步都可以到达这里
        // 可以根据tlsErrInfo 中ErrCode, Title, Msg 给用户弹提示语，引导相关操作
        @Override
        public void OnSmsLoginFail(TLSErrInfo errInfo) { // 短信登录失败
            Util.notOK(context, errInfo);
            TLSService.getInstance().setLastErrno(-1);
            SmsLoginService.this.jumpToFailActivity();
        }

        // 短信登录过程中任意一步都可以到达这里，
        // 顾名思义，网络超时，可能是用户网络环境不稳定，一般让用户重试即可
        @Override
        public void OnSmsLoginTimeout(TLSErrInfo errInfo) {
            Util.notOK(context, errInfo);
        }
    }

    void jumpToSuccActivity() {
        String thirdappPackageNameSucc = Constants.thirdappPackageNameSucc;
        String thirdappClassNameSucc = Constants.thirdappClassNameSucc;

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.SMS_LOGIN);
        intent.putExtra(Constants.EXTRA_SMS_LOGIN, Constants.SMS_LOGIN_SUCCESS);
        if (thirdappPackageNameSucc != null && thirdappClassNameSucc != null) {
            intent.setClassName(thirdappPackageNameSucc, thirdappClassNameSucc);
            ((Activity) context).startActivity(intent);
        } else {
            ((Activity) context).setResult(Activity.RESULT_OK, intent);
        }
        ((Activity) context).finish();
    }

    void jumpToFailActivity() {
        String thirdappPackageNameFail = Constants.thirdappPackageNameFail;
        String thirdappClassNameFail = Constants.thirdappClassNameFail;

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.SMS_LOGIN);
        intent.putExtra(Constants.EXTRA_SMS_LOGIN, Constants.SMS_LOGIN_FAIL);
        if (thirdappPackageNameFail != null && thirdappClassNameFail != null) {
            intent.setClassName(thirdappPackageNameFail, thirdappClassNameFail);
            ((Activity) context).startActivity(intent);
            ((Activity) context).finish();
        }
    }

}
